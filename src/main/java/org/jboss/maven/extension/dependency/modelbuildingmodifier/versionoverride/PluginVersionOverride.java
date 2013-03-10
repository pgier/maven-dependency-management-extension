package org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride;

import java.util.List;
import java.util.Map;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;

/**
 * Overrides plugin versions in a model
 */
public class PluginVersionOverride
    extends AbstractVersionOverride
{
    /**
     * A short description of the thing being overridden
     */
    private static final String OVERRIDE_NAME = "plugin";

    /**
     * Load plugin overrides list when the object is instantiated
     */
    public PluginVersionOverride( Map<String, String> pluginVersionOverrides )
    {
        this.setVersionOverrides( pluginVersionOverrides );
    }

    public ModelBuildingResult updateModel( ModelBuildingRequest request, ModelBuildingResult result )
    {
        // If the model doesn't have any plugin management set by default, create one for it
        PluginManagement pluginManagement = result.getEffectiveModel().getBuild().getPluginManagement();
        if ( pluginManagement == null )
        {
            pluginManagement = new PluginManagement();
            result.getEffectiveModel().getBuild().setPluginManagement( pluginManagement );
            getLog().debug( "Created new Plugin Management for model" );
        }

        // Override plugin management versions
        overridePluginVersions( pluginManagement.getPlugins(), this.getVersionOverrides() );

        // Override plugin versions
        List<Plugin> projectPlugins = result.getEffectiveModel().getBuild().getPlugins();
        overridePluginVersions( projectPlugins, this.getVersionOverrides() );

        return result;
    }

    /**
     * Set the versions of any plugins which match the contents of the list of plugin overrides
     * 
     * @param plugins The list of plugins to modify
     * @param pluginVersionOverrides The list of version overrides to apply to the plugins
     */
    private void overridePluginVersions( List<Plugin> plugins, Map<String, String> pluginVersionOverrides )
    {
        for ( Plugin plugin : plugins )
        {
            String groupIdArtifactId = plugin.getGroupId() + GAV_SEPERATOR + plugin.getArtifactId();
            if ( pluginVersionOverrides.containsKey( groupIdArtifactId ) )
            {
                String currVersion = plugin.getVersion();
                String overrideVersion = pluginVersionOverrides.get( groupIdArtifactId );
                plugin.setVersion( pluginVersionOverrides.get( groupIdArtifactId ) );
                getLog().debug( "Plugin " + groupIdArtifactId + " was overridden from " + currVersion + " to " +
                                    overrideVersion );
            }
        }
    }

    @Override
    public String getName()
    {
        return OVERRIDE_NAME;
    }

}
