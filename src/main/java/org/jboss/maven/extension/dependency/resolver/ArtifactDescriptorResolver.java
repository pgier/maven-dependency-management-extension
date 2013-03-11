package org.jboss.maven.extension.dependency.resolver;

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;
import org.jboss.maven.extension.dependency.util.log.Logging;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactDescriptorException;
import org.sonatype.aether.resolution.ArtifactDescriptorRequest;
import org.sonatype.aether.resolution.ArtifactDescriptorResult;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * Class to resolve artifact descriptors (pom files) from a maven repository
 */
public class ArtifactDescriptorResolver
{

    private static final Logger logger = Logging.getLogger();

    private RepositorySystem repositorySystem;

    public Map<String, String> getRemoteVersionOverrides( String gav )
        throws ArtifactResolutionException, ArtifactDescriptorException
    {
        ArtifactDescriptorResult descResult = resolveRemotePom( gav );
        Map<String, String> versionOverrides = new HashMap<String, String>();

        for ( Dependency dep : descResult.getManagedDependencies() )
        {
            Artifact artifact = dep.getArtifact();
            String groupIdArtifactId = artifact.getGroupId() + ":" + artifact.getArtifactId();
            String version = artifact.getVersion();
            versionOverrides.put( groupIdArtifactId, version );
        }

        return versionOverrides;
    }

    public ArtifactDescriptorResult resolveRemotePom( String gav )
        throws ArtifactResolutionException, ArtifactDescriptorException

    {
        logger.debug( "Resolving remote POM: " + gav );

        try
        {
            repositorySystem = newRepositorySystem();
        }
        catch ( ComponentLookupException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch ( PlexusContainerException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        RepositorySystemSession session = newRepositorySystemSession( repositorySystem );

        Artifact artifact = new DefaultArtifact( gav );

        // String remoteRepoUrl = System.getProperty( "remoteRepoUrl" );
        String remoteRepoUrl = "http://repo1.maven.org/maven2/";
        RemoteRepository repo = new RemoteRepository( "central", "default", remoteRepoUrl );

        ArtifactDescriptorRequest descRequest = new ArtifactDescriptorRequest();
        descRequest.setArtifact( artifact );
        descRequest.addRepository( repo );

        ArtifactDescriptorResult descResult = repositorySystem.readArtifactDescriptor( session, descRequest );
        for ( Dependency dep : descResult.getManagedDependencies() )
        {
            logger.info( "Remote managed dep: " + dep );
        }

        System.out.println( artifact + " resolved to  " + artifact.getFile() );

        return descResult;
    }

    public static DefaultRepositorySystemSession newRepositorySystemSession( RepositorySystem system )
    {
        logger.info( "repo system: " + system );
        MavenRepositorySystemSession session = new MavenRepositorySystemSession();

        LocalRepository localRepo = new LocalRepository( "target/local-repo" );
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( localRepo ) );

        // session.setTransferListener( new ConsoleTransferListener() );
        // session.setRepositoryListener( new ConsoleRepositoryListener() );

        // uncomment to generate dirty trees
        // session.setDependencyGraphTransformer( null );

        return session;
    }

    private static RepositorySystem newRepositorySystem()
        throws ComponentLookupException, PlexusContainerException
    {
        return new DefaultPlexusContainer().lookup( RepositorySystem.class );
    }
}
