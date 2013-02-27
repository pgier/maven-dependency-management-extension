package org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.maven.model.Plugin;
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
     * Load dependency overrides list when the object is instantiated
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

        for ( Plugin plugin : result.getEffectiveModel().getBuild().getPlugins() )
        {
            PluginWrapper pluginWrapped = new PluginWrapper( plugin );
            result = applyVersionToTargetInModel( result, groupOverrideMap, pluginWrapped, OVERRIDE_NAME );
        }
        return result;
    }
}
