package org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.TransformerException;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.codehaus.plexus.logging.Logger;
import org.jboss.maven.extension.dependency.metainf.OverrideMapWriter;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.ModelBuildingModifier;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride.target.DependencyWrapper;
import org.jboss.maven.extension.dependency.util.log.Logging;

/**
 * Overrides dependency versions in a model
 */
public class DepVersionOverride
    extends VersionOverrider
    implements ModelBuildingModifier
{
    private static final Logger logger = Logging.getLogger();

    /**
     * A short description of the thing being overridden
     */
    private static final String OVERRIDE_NAME = "dependency";

    /**
     * The String that needs to be prepended a system property to make it a version override. <br />
     * ex: -Dversion:junit:junit=4.10
     */
    private static final String PROPERTY_PREPEND = "version" + PROPERTY_NAME_SEPERATOR;

    /**
     * Key: String of artifactID <br />
     * Value: Map of overrides for a groupID Inner Map Key: String of groupID Inner Map Value: String of desired
     * override version number
     */
    private final Map<String, Map<String, VersionOverrideInfo>> groupOverrideMap;

    /**
     * Handles writing the override map to the jar resources when needed
     */
    private OverrideMapWriter writeMapXML;

    /**
     * Load dependency overrides list when the object is instantiated
     */
    public DepVersionOverride()
    {
        groupOverrideMap = getOverrideMap( PROPERTY_PREPEND, OVERRIDE_NAME );
        writeMapXML = new OverrideMapWriter( OVERRIDE_NAME, groupOverrideMap );
    }

    @Override
    public ModelBuildingResult modifyBuild( ModelBuildingRequest request, ModelBuildingResult result )
    {
        try
        {
            writeMapXML.writeXMLTo( result.getEffectiveModel().getBuild() );
        }
        catch ( InvalidPathException | TransformerException | IOException e )
        {
            logger.error( "Could not write " + OVERRIDE_NAME + " override map to XML file: " + e.toString() );
        }

        // Main dependencies
        for ( Dependency dependency : result.getEffectiveModel().getDependencies() )
        {
            DependencyWrapper dependencyWrapped = new DependencyWrapper( dependency );
            result = applyVersionToTargetInModel( result, groupOverrideMap, dependencyWrapped, OVERRIDE_NAME );
        }

        // Transitive dependencies
        result = addDependencyManagementForUnusedOverrides( result, groupOverrideMap );

        return result;
    }

    /**
     * For all overrides in the overrideMap, if they haven't been used, add them as dependencies in the model's
     * DependencyManagement, to allow them to affect Transitive Dependencies.
     * 
     * @param result The model to modify
     * @param overrideMap The Map of overrides to that may or may not have been used previously
     * @return The modified model
     */
    private static ModelBuildingResult addDependencyManagementForUnusedOverrides( ModelBuildingResult result,
                                                                                  Map<String, Map<String, VersionOverrideInfo>> overrideMap )
    {
        // TODO: Explore de-duplication of code between this method and addPluginManagementForUnusedOverrides

        for ( Entry<String, Map<String, VersionOverrideInfo>> groupEntry : overrideMap.entrySet() )
        {
            String groupID = groupEntry.getKey();
            Map<String, VersionOverrideInfo> artifactMap = groupEntry.getValue();

            for ( Entry<String, VersionOverrideInfo> artifactEntry : artifactMap.entrySet() )
            {
                String artifactID = artifactEntry.getKey();
                VersionOverrideInfo artifactVersionOverrideInfo = artifactEntry.getValue();

                // Was the override used?
                if ( !artifactVersionOverrideInfo.isOverriden() )
                {
                    String artifactVersion = artifactVersionOverrideInfo.getVersion();

                    // https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Transitive_Dependencies
                    // DependencyManagement entries target possible future dependencies (Transitive Dependencies).

                    // If the model doesn't have any Dependency Management set by default, create one for it
                    DependencyManagement dependencyManagement = result.getEffectiveModel().getDependencyManagement();
                    if ( dependencyManagement == null )
                    {
                        dependencyManagement = new DependencyManagement();
                        result.getEffectiveModel().setDependencyManagement( dependencyManagement );
                        logger.debug( "Created new Dependency Management for model" );
                    }

                    // Just alter the version of the existing Dependency Management entry if one exists already for the
                    // groupID and artifactID
                    boolean existsAlready = false;
                    for ( Dependency currDependency : dependencyManagement.getDependencies() )
                    {
                        if ( groupID == currDependency.getGroupId() && artifactID == currDependency.getArtifactId() )
                        {
                            currDependency.setVersion( artifactVersion );

                            logger.debug( "Altered existing dependency in Dependency Management: " + groupID + ":"
                                + artifactID + "=" + artifactVersion );
                            existsAlready = true;
                            break;
                        }
                    }

                    // No existing entry to alter, so make and add a new one
                    if ( !existsAlready )
                    {
                        Dependency newDependency = new Dependency();
                        newDependency.setGroupId( groupID );
                        newDependency.setArtifactId( artifactID );
                        newDependency.setVersion( artifactVersion );
                        dependencyManagement.addDependency( newDependency );

                        logger.debug( "New dependency added to Dependency Management: " + groupID + ":" + artifactID
                            + "=" + artifactVersion );
                    }

                    artifactVersionOverrideInfo.setOverriden( true );
                }
            }
        }

        return result;
    }
}
