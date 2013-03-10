package org.jboss.maven.extension.dependency;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.building.DefaultModelBuilder;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.logging.Logger;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.ModelBuildingModifier;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride.DepVersionOverrider;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride.PluginVersionOverrider;
import org.jboss.maven.extension.dependency.util.log.Logging;

@Component( role = ModelBuilder.class )
public class ExtDepMgmtModelBuilder
    extends DefaultModelBuilder
    implements ModelBuilder
{

    @SuppressWarnings( "unused" )
    private static final Logger logger = Logging.getLogger();

    private final List<ModelBuildingModifier> buildModifierList = new ArrayList<ModelBuildingModifier>();

    /**
     * Load the build modifiers at instantiation time
     */
    public ExtDepMgmtModelBuilder()
    {
        logger.debug( "New ExtDepMgmtModelBuilder contructed" );

        buildModifierList.add( new DepVersionOverrider() );
        buildModifierList.add( new PluginVersionOverrider() );
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
            }
        }

        return buildResult;
    }

}
