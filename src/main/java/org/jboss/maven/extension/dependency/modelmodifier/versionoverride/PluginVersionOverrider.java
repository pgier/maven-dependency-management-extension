package org.jboss.maven.extension.dependency.modelmodifier.versionoverride;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.building.ModelBuildingException;
import org.jboss.maven.extension.dependency.resolver.EffectiveModelBuilder;
import org.jboss.maven.extension.dependency.util.VersionPropertyReader;
import org.sonatype.aether.resolution.ArtifactDescriptorException;
import org.sonatype.aether.resolution.ArtifactResolutionException;

/**
 * Overrides plugin versions in a model
 */
public class PluginVersionOverrider
    extends AbstractVersionOverrider
{
    /**
     * A short description of the thing being overridden
     */
    private static final String OVERRIDE_NAME = "plugin";

    /**
     * The String that needs to be prepended a system property to make it a plugin version override. <br />
     * ex: -DpluginVersion:foo:maven-bar-plugin=1.0
     */
    private static final String PLUGIN_VERSION_OVERRIDE_PREFIX = "pluginVersion:";

    /**
     * The name of the property which contains the GAV of the remote pom from which to retrieve plugin management
     * information. <br />
     * ex: -DpluginManagement:org.foo:bar-plugin-mgmt:1.0
     */
    private static final String PLUGIN_MANAGEMENT_POM_PROPERTY = "pluginManagement";

    private Map<String, String> pluginVersionOverrides;

    /**
     * Default constructor
     */
    public PluginVersionOverrider()
    {

    }

    @Override
    public boolean updateModel( Model model )
    {
        Map<String, String> versionOverrides = getVersionOverrides();
        if ( versionOverrides.size() == 0 )
        {
            return false;
        }

        // If the model doesn't have any plugin management set by default, create one for it
        PluginManagement pluginManagement = model.getBuild().getPluginManagement();
        if ( pluginManagement == null )
        {
            pluginManagement = new PluginManagement();
            model.getBuild().setPluginManagement( pluginManagement );
            getLog().debug( "Created new Plugin Management for model" );
        }

        // Override plugin management versions
        overridePluginVersions( pluginManagement.getPlugins(), versionOverrides );

        // Override plugin versions
        List<Plugin> projectPlugins = model.getBuild().getPlugins();
        overridePluginVersions( projectPlugins, versionOverrides );

        writeOverrideMap( model, getName(), versionOverrides );

        // Assuming the Model changed since overrides were given
        return true;
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
                getLog().debug( "Plugin " + groupIdArtifactId + " was overridden from " + currVersion + " to "
                                    + overrideVersion );
            }
        }
    }

    @Override
    public String getName()
    {
        return OVERRIDE_NAME;
    }

    /**
     * Get the set of versions which will be used to override local plugin versions.
     */
    public Map<String, String> getVersionOverrides()
    {
        if ( pluginVersionOverrides == null )
        {
            pluginVersionOverrides = new HashMap<String, String>();

            Map<String, String> remoteDepOverrides = loadRemotePluginVersionOverrides();
            pluginVersionOverrides.putAll( remoteDepOverrides );

            Map<String, String> propPluginOverrides =
                VersionPropertyReader.getVersionPropertiesByPrepend( PLUGIN_VERSION_OVERRIDE_PREFIX );
            pluginVersionOverrides.putAll( propPluginOverrides );
        }
        return pluginVersionOverrides;
    }

    /**
     * Get plugin management version properties from a remote POM
     * 
     * @return Map between the GA of the plugin and the version of the plugin.
     */
    private Map<String, String> loadRemotePluginVersionOverrides()
    {
        Properties systemProperties = System.getProperties();
        String pluginMgmtPomGAV = systemProperties.getProperty( PLUGIN_MANAGEMENT_POM_PROPERTY );

        Map<String, String> versionOverrides = new HashMap<String, String>( 0 );

        if ( pluginMgmtPomGAV != null )
        {
            try
            {
                EffectiveModelBuilder resolver = EffectiveModelBuilder.getInstance();
                versionOverrides = resolver.getRemotePluginVersionOverrides( pluginMgmtPomGAV );
            }
            catch ( ArtifactResolutionException e )
            {
                getLog().warn( "Unable to resolve remote pom: " + e );
            }
            catch ( ArtifactDescriptorException e )
            {
                getLog().warn( "Unable to resolve remote pom: " + e );
            }
            catch ( ModelBuildingException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return versionOverrides;
    }


}
