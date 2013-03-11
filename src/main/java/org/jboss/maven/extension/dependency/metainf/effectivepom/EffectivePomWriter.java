package org.jboss.maven.extension.dependency.metainf.effectivepom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.logging.Logger;
import org.jboss.maven.extension.dependency.util.log.Logging;

/**
 * This class writes out the effective pom from the in-memory model, to be included alongside the copied pom (pom.xml)
 * at META-INF/maven/group/project/ with the name effective-pom.xml This is a similar functionality to the help plugin's
 * help:effective-pom goal, except it is meant to be included in a built jar
 */
@Component( role = AbstractMavenLifecycleParticipant.class, hint = "pomwriter" )
public class EffectivePomWriter
    extends AbstractMavenLifecycleParticipant
{
    private Logger logger = Logging.getLogger();

    // TODO: get this value from somewhere (probably needs to be set to an on state from one or more modelbuildingmodifiers)
    private boolean extIsBeingUsed = true;

    /**
     * A unique name to use for a prefix in temp output
     */
    private static String OUTPUT_DIR_PREFIX = "mvndepext";

    @Override
    public void afterProjectsRead( MavenSession session )
        throws MavenExecutionException
    {
        // Write out the effective pom and include it in the model resources, but only if the extension is being used
        if ( extIsBeingUsed )
        {
            MavenProject currentProject = session.getCurrentProject();
            Model model = currentProject.getModel();

            // Paths
            String projectArtifactID = model.getArtifactId();
            String projectGroupID = model.getGroupId();
            String outputPath = TempDirectory.generateDir( OUTPUT_DIR_PREFIX ).toString();
            String groupPath = outputPath + File.separator + projectGroupID;
            String artifactPath = groupPath + File.separator + projectArtifactID;

            File outputFile = new File( artifactPath + File.separator + "effective-pom.xml" );

            // Generate POM XML
            String pomContent;
            try
            {
                pomContent = EffectivePomGenerator.generatePom( model );
            }
            catch ( IOException e )
            {
                throw new MavenExecutionException( "Couldn't generate effective pom from internal model", e );
            }

            // Write POM to file
            try
            {
                writeStringToFile( pomContent, outputFile );
            }
            catch ( IOException e )
            {
                throw new MavenExecutionException( "Couldn't write effective pom", e );
            }

            // Add outputPath directory tree to model build resources
            Resource effPomResource = new Resource();
            effPomResource.setDirectory( outputPath );
            effPomResource.setTargetPath( "META-INF/" + "maven" );

            model.getBuild().addResource( effPomResource );

            // Done
            logger.debug( "Effective pom written" );
        }
    }

    /**
     * Write the entirety of a string to a file, replacing any existing contents.
     * 
     * @param content String to use as the entire contents for the file
     * @param file File location to write or overwrite. Must not be a directory.
     * @throws IOException If the file exists and is a directory, or if the file does not exist but cannot be created,
     *             or cannot be opened for any other reason.
     */
    private static void writeStringToFile( String content, File file )
        throws IOException
    {
        if ( file.isDirectory() )
        {
            throw new IOException( "File '" + file + "' is a directory" );
        }

        File dirname = file.getParentFile();
        dirname.mkdirs();

        FileWriter writer = null;
        try
        {
            writer = new FileWriter( file );
            writer.write( content );
        }
        finally
        {
            if ( writer != null )
            {
                writer.close();
            }
        }
    }

    private static class TempDirectory
    {
        private static final File tmpdir = new File( System.getProperty( "java.io.tmpdir" ) );

        private static final SecureRandom random = new SecureRandom();

        /**
         * Create a random directory in the temp directory using prefix as the start of the new directory name.
         * 
         * @param prefix
         * @return File representing the new directory
         */
        static File generateDir( String prefix )
        {
            long suffix = random.nextLong();
            if ( suffix == Long.MIN_VALUE )
            {
                // Long.MIN_VALUE will not be changed by Math.abs
                suffix = 0;
            }
            else
            {
                suffix = Math.abs( suffix );
            }
            return new File( tmpdir, prefix + String.valueOf( suffix ) );
        }
    }
}
