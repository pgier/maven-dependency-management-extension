package org.jboss.maven.extension.dependency;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.building.DefaultModelBuilder;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.codehaus.plexus.component.annotations.Component;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.ModelBuildingModifier;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride.DepVersionOverride;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride.PluginVersionOverride;
import org.jboss.maven.extension.dependency.util.log.Logging;
import org.codehaus.plexus.logging.Logger;

@Component( role = ModelBuilder.class )
public class ExtDepMgmtModelBuilder
    extends DefaultModelBuilder
    implements ModelBuilder
{
    @SuppressWarnings( "unused" )
    private static final Logger logger = Logging.getLogger();

    private final List<ModelBuildingModifier> buildModifierList;

    /**
     * Load the build modifiers at instantiation time
     */
    public ExtDepMgmtModelBuilder()
    {
        List<ModelBuildingModifier> buildModifierList = new ArrayList<ModelBuildingModifier>();

        // List is manually populated for now, though maybe reflection could be used.
        buildModifierList.add( new DepVersionOverride() );
        buildModifierList.add( new PluginVersionOverride() );

        this.buildModifierList = buildModifierList;
    }

    @Override
    public ModelBuildingResult build( ModelBuildingRequest request )
        throws ModelBuildingException
    {
        ModelBuildingResult buildResult = super.build( request );

        // Run the modifiers against the built model
        for ( ModelBuildingModifier currModifier : buildModifierList )
        {
            buildResult = currModifier.modifyBuild( request, buildResult );
        }

        return buildResult;
    }

    @Override
    public ModelBuildingResult build( ModelBuildingRequest request, ModelBuildingResult result )
        throws ModelBuildingException
    {
        ModelBuildingResult buildResult = super.build( request, result );

        // Run the modifiers against the built model
        for ( ModelBuildingModifier currModifier : buildModifierList )
        {
            buildResult = currModifier.modifyBuild( request, buildResult );
        }

        return buildResult;
    }
}
