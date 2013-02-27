package org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.TransformerException;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.codehaus.plexus.logging.Logger;
import org.jboss.maven.extension.dependency.metainf.OverrideMapWriter;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.ModelBuildingModifier;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride.target.PluginWrapper;
import org.jboss.maven.extension.dependency.util.log.Logging;

/**
 * Overrides plugin versions in a model
 */
public class PluginVersionOverride
    extends VersionOverrider
    implements ModelBuildingModifier
{
    private static final Logger logger = Logging.getLogger();

    /**
     * A short description of the thing being overridden
     */
    private static final String OVERRIDE_NAME = "plugin";

    /**
     * The String that needs to be prepended a system property to make it an applicable override. <br />
     * ex: -Dversion:junit:junit=4.10
     */
    private static final String PROPERTY_PREPEND = OVERRIDE_NAME + "Version" + PROPERTY_NAME_SEPERATOR;

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
     * Load plugin overrides list when the object is instantiated
     */
    public PluginVersionOverride()
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

        // Main plugins
        for ( Plugin plugin : result.getEffectiveModel().getBuild().getPlugins() )
        {
            PluginWrapper pluginWrapped = new PluginWrapper( plugin );
            result = applyVersionToTargetInModel( result, groupOverrideMap, pluginWrapped, OVERRIDE_NAME );
        }

        // Transitive plugins
        result = addPluginManagementForUnusedOverrides( result, groupOverrideMap );

        return result;
    }

    /**
     * For all overrides in the overrideMap, if they haven't been used, add them as plugins in the model's
     * PlguinManagement, to allow them to affect Transitive Plugins.
     * 
     * @param result The model to modify
     * @param overrideMap The Map of overrides to that may or may not have been used previously
     * @return The modified model
     */
    private static ModelBuildingResult addPluginManagementForUnusedOverrides( ModelBuildingResult result,
                                                                              Map<String, Map<String, VersionOverrideInfo>> overrideMap )
    {
        // TODO: Explore de-duplication of code between this method and addDependencyManagementForUnusedOverrides

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
                    // PluginManagement entries target possible future plugins (Transitive Plugin).

                    // If the model doesn't have any Plugin Management set by default, create one for it
                    PluginManagement pluginManagement = result.getEffectiveModel().getBuild().getPluginManagement();
                    if ( pluginManagement == null )
                    {
                        pluginManagement = new PluginManagement();
                        result.getEffectiveModel().getBuild().setPluginManagement( pluginManagement );
                        logger.debug( "Created new Plugin Management for model" );
                    }

                    // Just alter the version of the existing Plugin Management entry if one exists already for the
                    // groupID and artifactID
                    boolean existsAlready = false;
                    for ( Plugin currPlugin : pluginManagement.getPlugins() )
                    {
                        if ( groupID == currPlugin.getGroupId() && artifactID == currPlugin.getArtifactId() )
                        {
                            currPlugin.setVersion( artifactVersion );

                            logger.debug( "Altered existing plugin in Plugin Management: " + groupID + ":" + artifactID
                                + "=" + artifactVersion );
                            existsAlready = true;
                            break;
                        }
                    }

                    // No existing entry to alter, so make and add a new one
                    if ( !existsAlready )
                    {
                        Plugin newPlugin = new Plugin();
                        newPlugin.setGroupId( groupID );
                        newPlugin.setArtifactId( artifactID );
                        newPlugin.setVersion( artifactVersion );
                        pluginManagement.addPlugin( newPlugin );

                        logger.debug( "New plugin added to Plugin Management: " + groupID + ":" + artifactID + "="
                            + artifactVersion );
                    }

                    artifactVersionOverrideInfo.setOverriden( true );
                }
            }
        }

        return result;
    }
}
