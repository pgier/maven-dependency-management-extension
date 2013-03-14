package org.jboss.maven.extension.dependency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.logging.Logger;
import org.jboss.maven.extension.dependency.metainf.effectivepom.EffectivePomWriter;
import org.jboss.maven.extension.dependency.modelmodifier.ModelModifier;
import org.jboss.maven.extension.dependency.modelmodifier.versionoverride.DepVersionOverrider;
import org.jboss.maven.extension.dependency.modelmodifier.versionoverride.PluginVersionOverrider;
import org.jboss.maven.extension.dependency.util.log.Logging;

/**
 * Main executor. Operates at the point defined by superclass as "afterProjectsRead", which is "after all MavenProject
 * instances have been created". This should allow access to the model(s) after they are built, but before they are
 * used.
 */
@Component( role = AbstractMavenLifecycleParticipant.class, hint = "modifymodel" )
public class ModifyModelLifecycleParticipant
    extends AbstractMavenLifecycleParticipant
{
    private static final Logger logger = Logging.getLogger();

    private final List<ModelModifier> buildModifierList = new ArrayList<ModelModifier>();

    /**
     * Load the build modifiers at instantiation time
     */
    public ModifyModelLifecycleParticipant()
    {
        logger.debug( "New ModifyModelLifecycleParticipant contructed" );

        buildModifierList.add( new DepVersionOverrider() );
        buildModifierList.add( new PluginVersionOverrider() );
    }

    @Override
    public void afterProjectsRead( MavenSession session )
        throws MavenExecutionException
    {
        // Apply model modifiers to the projects' models
        for ( MavenProject project : session.getProjects() )
        {
            logger.debug( "Checking project '" + project.getId() + "'" );

            int modelChangeCount = 0;
            Model currModel = project.getModel();

            // Run the modifiers against the built model
            for ( ModelModifier currModifier : buildModifierList )
            {
                boolean modelChanged = currModifier.updateModel( currModel );
                if (modelChanged) {
                    modelChangeCount++;
                }
            }

            // Iff something changed, then it will be useful to output extra info
            if ( modelChangeCount >= 1 )
            {
                logger.debug( "Model changed at least once, writing informational files" );
                try
                {
                    EffectivePomWriter.writeEffectivePOM( currModel );
                }
                catch ( IOException e )
                {
                    logger.error( "Could not write the effective POM of model '" + currModel.getId() + "' due to " + e );
                    logAllCauses( e.getCause() );
                }
            }
        }

    }

    /**
     * Recursively log all causes in a Throwable chain
     * 
     * @param cause Will be null in the base case
     */
    private void logAllCauses( Throwable cause )
    {
        if ( cause == null )
        {
            return;
        }
        logger.error( "Cause: " + cause );
        logAllCauses( cause.getCause() );
    }
}
