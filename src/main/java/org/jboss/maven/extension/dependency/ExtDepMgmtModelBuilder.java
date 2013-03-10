package org.jboss.maven.extension.dependency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.maven.model.building.DefaultModelBuilder;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.jboss.maven.extension.dependency.metainf.OverrideMapWriter;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.ModelBuildingModifier;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride.AbstractVersionOverride;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride.DepVersionOverride;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride.PluginVersionOverride;
import org.jboss.maven.extension.dependency.util.VersionPropertyReader;
import org.jboss.maven.extension.dependency.util.log.Logging;
import org.sonatype.aether.impl.ArtifactResolver;
import org.sonatype.aether.impl.DependencyCollector;
import org.sonatype.aether.impl.internal.DefaultRepositorySystem;
import org.sonatype.aether.spi.log.NullLogger;

@Component( role = ModelBuilder.class )
public class ExtDepMgmtModelBuilder
    extends DefaultModelBuilder
    implements ModelBuilder
{

    @SuppressWarnings( "unused" )
    private static final Logger logger = Logging.getLogger();

    /**
     * The String that needs to be prepended a system property to make it a version override. <br />
     * ex: -Dversion:junit:junit=4.10
     */
    private static final String DEPENDENCY_VERSION_OVERRIDE_PREFIX = "version:";

    /**
     * The String that needs to be prepended a system property to make it a plugin version override. <br />
     * ex: -DpluginVersion:foo:maven-bar-plugin=1.0
     */
    private static final String PLUGIN_VERSION_OVERRIDE_PREFIX = "pluginVersion:";

    private final List<ModelBuildingModifier> buildModifierList;

    /**
     * Load the build modifiers at instantiation time
     */
    public ExtDepMgmtModelBuilder()
    {
        logger.info( "New ExtDepMgmtModelBuilder contructed" );
        List<ModelBuildingModifier> buildModifierList = new ArrayList<ModelBuildingModifier>();

        Map<String, String> propDepVersionOverrides =
            VersionPropertyReader.getVersionPropertiesByPrepend( DEPENDENCY_VERSION_OVERRIDE_PREFIX );
        Map<String, String> propPluginVersionOverrides =
            VersionPropertyReader.getVersionPropertiesByPrepend( PLUGIN_VERSION_OVERRIDE_PREFIX );

        // List is manually populated for now, though maybe reflection could be used.
        if ( propDepVersionOverrides.size() > 0 )
        {
            buildModifierList.add( new DepVersionOverride( propDepVersionOverrides ) );
        }
        if ( propPluginVersionOverrides.size() > 0 )
        {
            buildModifierList.add( new PluginVersionOverride( propPluginVersionOverrides ) );
        }

        this.buildModifierList = buildModifierList;
    }

    @Override
    public ModelBuildingResult build( ModelBuildingRequest request )
        throws ModelBuildingException
    {
        ModelBuildingResult buildResult = super.build( request );

        // If the pom file is not null, then this model is for the current project,
        // otherwise it's a repo pom and should be ignored
        if ( request.getPomFile() != null )
        {
            // Run the modifiers against the built model
            for ( ModelBuildingModifier currModifier : buildModifierList )
            {
                buildResult = currModifier.updateModel( request, buildResult );

                // TODO: Need to refactor writing xml of overrides based on better defined requirements
                if ( currModifier instanceof AbstractVersionOverride )
                {
                    Map<String, String> versionOverrides =
                        ( (AbstractVersionOverride) currModifier ).getVersionOverrides();
                    writeXmlMap( buildResult, currModifier.getName(), versionOverrides );
                }
            }
        }

        return buildResult;
    }

    @Override
    public ModelBuildingResult build( ModelBuildingRequest request, ModelBuildingResult result )
        throws ModelBuildingException
    {
        ModelBuildingResult buildResult = super.build( request, result );

        // If the pom file is not null, then this model is for the current project,
        // otherwise it's a repo pom and should be ignored
        if ( request.getPomFile() != null )
        {
            // Run the modifiers against the built model
            for ( ModelBuildingModifier currModifier : buildModifierList )
            {
                buildResult = currModifier.updateModel( request, buildResult );

                // TODO: Need to refactor writing xml of overrides based on better defined requirements
                if ( currModifier instanceof AbstractVersionOverride )
                {
                    Map<String, String> versionOverrides =
                        ( (AbstractVersionOverride) currModifier ).getVersionOverrides();
                    writeXmlMap( buildResult, currModifier.getName(), versionOverrides );
                }
            }
        }

        return buildResult;
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
            logger.error( "Could not write " + overrideName + " override map to XML file: " + e.toString() );
        }
        catch ( IOException e )
        {
            logger.error( "Could not write " + overrideName + " override map to XML file: " + e.toString() );
        }

    }
}
