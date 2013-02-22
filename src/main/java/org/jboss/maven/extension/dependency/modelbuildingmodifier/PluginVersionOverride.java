package org.jboss.maven.extension.dependency.modelbuildingmodifier;

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.codehaus.plexus.logging.Logger;
import org.jboss.maven.extension.dependency.util.Logging;
import org.jboss.maven.extension.dependency.util.SystemProperties;
import org.jboss.maven.extension.dependency.util.VersionOverride;

/**
 * Overrides plugin versions in a model
 */
public class PluginVersionOverride
    extends VersionOverridePropertyUser
    implements ModelBuildingModifier
{
    private static final Logger logger = Logging.getLogger();

    /**
     * The String that needs to be prepended a system property to make it a version override. <br />
     * ex: -Dversion:junit:junit=4.10
     */
    private static final String PLUGIN_VERSION_PROPERTY_NAME = "pluginVersion" + PROPERTY_NAME_SEPERATOR;

    /**
     * Key: String of artifactID <br />
     * Value: Map of overrides for a groupID Inner Map Key: String of groupID Inner Map Value: String of desired
     * override version number
     */
    private final Map<String, Map<String, VersionOverride>> groupOverrideMap;

    /**
     * Load dependency overrides list when the object is instantiated
     */
    public PluginVersionOverride()
    {
        Map<String, String> propertyMap = SystemProperties.getPropertiesByPrepend( PLUGIN_VERSION_PROPERTY_NAME );

        HashMap<String, Map<String, VersionOverride>> groupOverrideMap =
            new HashMap<String, Map<String, VersionOverride>>();
        Map<String, VersionOverride> artifactOverrideMap;

        for ( String propertyName : propertyMap.keySet() )
        {
            // Split the name portion into parts (ex: junit:junit to {junit, junit})
            String[] propertyNameParts = propertyName.split( PROPERTY_NAME_SEPERATOR );

            if ( propertyNameParts.length == 2 )
            {
                // Part 1 is the group name. ex: org.apache.maven.plugins
                String groupID = propertyNameParts[0];
                // Part 2 is the artifact ID. ex: maven-compiler-plugin
                String artifactID = propertyNameParts[1];

                // The value of the property is the desired version. ex: 3.0
                String version = propertyMap.get( propertyName );

                logger.debug( "Detected plugin override property. Group: " + groupID + "  ArtifactID: " + artifactID
                    + "  Target Version: " + version );

                // Create VersionOverride object
                VersionOverride versionOverride = new VersionOverride( groupID, artifactID, version );

                // Insert the override into override map
                if ( groupOverrideMap.containsKey( groupID ) )
                {
                    artifactOverrideMap = groupOverrideMap.get( groupID );
                    artifactOverrideMap.put( artifactID, versionOverride );
                }
                else
                {
                    artifactOverrideMap = new HashMap<String, VersionOverride>();
                    artifactOverrideMap.put( artifactID, versionOverride );
                    groupOverrideMap.put( groupID, artifactOverrideMap );
                }
            }
            else
            {
                logger.error( "Detected bad plugin version override property. Name: " + propertyName );
            }
        }

        if ( groupOverrideMap.size() == 0 )
        {
            logger.debug( "No plugin version overrides." );
        }

        this.groupOverrideMap = groupOverrideMap;
    }

    @Override
    public ModelBuildingResult modifyBuild( ModelBuildingRequest request, ModelBuildingResult result )
    {
        for ( Plugin plugin : result.getEffectiveModel().getBuild().getPlugins() )
        {
            String currGroupID = plugin.getGroupId();
            if ( groupOverrideMap.containsKey( currGroupID ) )
            {
                Map<String, VersionOverride> artifactOverrideMap = groupOverrideMap.get( currGroupID );
                String currArtifactID = plugin.getArtifactId();
                if ( artifactOverrideMap.containsKey( currArtifactID ) )
                {
                    String overrideVersion = artifactOverrideMap.get( currArtifactID ).getVersion();
                    String currVersion = plugin.getVersion();
                    if ( !currVersion.equals( overrideVersion ) )
                    {
                        plugin.setVersion( overrideVersion );
                        logger.debug( "Plugin version of ArtifactID " + currArtifactID + " was overridden from "
                            + currVersion + " to " + plugin.getVersion() + " (" + overrideVersion + ")" );
                    }
                    else
                    {
                        logger.debug( "Plugin version of ArtifactID " + currArtifactID
                            + " was the same as the override version (both are " + currVersion + ")" );
                    }
                    artifactOverrideMap.get( currArtifactID ).setOverriden( true );
                }
            }
        }
        return result;
    }
}
