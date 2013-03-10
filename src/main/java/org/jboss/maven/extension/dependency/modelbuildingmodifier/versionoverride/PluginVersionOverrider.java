package org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.jboss.maven.extension.dependency.metainf.OverrideMapWriter;
import org.jboss.maven.extension.dependency.util.VersionPropertyReader;

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

    private Map<String, String> pluginVersionOverrides;

    /**
     * Default constructor
     */
    public PluginVersionOverrider()
    {

    }

    public ModelBuildingResult updateModel( ModelBuildingRequest request, ModelBuildingResult result )
    {
        Map<String, String> versionOverrides = getVersionOverrides();
        if ( versionOverrides.size() == 0 )
        {
            return result;
        }

        // If the model doesn't have any plugin management set by default, create one for it
        PluginManagement pluginManagement = result.getEffectiveModel().getBuild().getPluginManagement();
        if ( pluginManagement == null )
        {
            pluginManagement = new PluginManagement();
            result.getEffectiveModel().getBuild().setPluginManagement( pluginManagement );
            getLog().debug( "Created new Plugin Management for model" );
        }

        // Override plugin management versions
        overridePluginVersions( pluginManagement.getPlugins(), versionOverrides );

        // Override plugin versions
        List<Plugin> projectPlugins = result.getEffectiveModel().getBuild().getPlugins();
        overridePluginVersions( projectPlugins, versionOverrides );

        writeXmlMap( result, getName(), versionOverrides );

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

    @Override
    public Map<String, String> getVersionOverrides()
    {
        if ( pluginVersionOverrides == null )
        {
            pluginVersionOverrides =
                VersionPropertyReader.getVersionPropertiesByPrepend( PLUGIN_VERSION_OVERRIDE_PREFIX );
        }
        return pluginVersionOverrides;
    }

    private void writeXmlMap( ModelBuildingResult result, String overrideName, Map<String, String> overrides )
    {
        OverrideMapWriter writeMapXML = new OverrideMapWriter( overrideName, overrides );
        try
        {
            writeMapXML.writeXMLTo( result.getEffectiveModel().getBuild() );
        }
        catch ( TransformerException e )
        {
            getLog().error( "Could not write " + overrideName + " override map to XML file: " + e.toString() );
        }
        catch ( IOException e )
        {
            getLog().error( "Could not write " + overrideName + " override map to XML file: " + e.toString() );
        }

    }

}
