package org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.maven.model.Dependency;
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

        for ( Dependency dependency : result.getEffectiveModel().getDependencies() )
        {
            DependencyWrapper dependencyWrapped = new DependencyWrapper( dependency );
            result = applyVersionToTargetInModel( result, groupOverrideMap, dependencyWrapped, OVERRIDE_NAME );
        }

        // TODO: Move into VersionOverrider
        // Add dependencies not already in model
        for ( String groupID : groupOverrideMap.keySet() )
        {
            for ( String artifactID : groupOverrideMap.get( groupID ).keySet() )
            {
                if ( !groupOverrideMap.get( groupID ).get( artifactID ).isOverriden() )
                {
                    String version = groupOverrideMap.get( groupID ).get( artifactID ).getVersion();
                    Dependency dependency = new Dependency();
                    dependency.setGroupId( groupID );
                    dependency.setArtifactId( artifactID );
                    dependency.setVersion( version );
                    result.getEffectiveModel().addDependency( dependency );
                    groupOverrideMap.get( groupID ).get( artifactID ).setOverriden( true );
                    logger.debug( "New dependency added: " + groupID + ":" + artifactID + "=" + version );
                }
            }
        }
        return result;
    }
}
