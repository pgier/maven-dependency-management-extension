package org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride;

import java.util.Map;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.ModelBuildingModifier;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride.target.PluginWrapper;

/**
 * Overrides plugin versions in a model
 */
public class PluginVersionOverride
    extends VersionOverrider
    implements ModelBuildingModifier
{

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
     * Load dependency overrides list when the object is instantiated
     */
    public PluginVersionOverride()
    {
        groupOverrideMap = getOverrideMap( PROPERTY_PREPEND, OVERRIDE_NAME );
    }

    @Override
    public ModelBuildingResult modifyBuild( ModelBuildingRequest request, ModelBuildingResult result )
    {
        for ( Plugin plugin : result.getEffectiveModel().getBuild().getPlugins() )
        {
            result = applyVersionToTargetInModel( result, groupOverrideMap, new PluginWrapper(plugin), OVERRIDE_NAME );
        }
        return result;
    }
}
