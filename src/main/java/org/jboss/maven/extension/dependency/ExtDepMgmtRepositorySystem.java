package org.jboss.maven.extension.dependency;

import java.util.Collection;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.SyncContext;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.CollectResult;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.deployment.DeployRequest;
import org.sonatype.aether.deployment.DeployResult;
import org.sonatype.aether.deployment.DeploymentException;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.installation.InstallRequest;
import org.sonatype.aether.installation.InstallResult;
import org.sonatype.aether.installation.InstallationException;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.LocalRepositoryManager;
import org.sonatype.aether.resolution.ArtifactDescriptorException;
import org.sonatype.aether.resolution.ArtifactDescriptorRequest;
import org.sonatype.aether.resolution.ArtifactDescriptorResult;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.resolution.DependencyResult;
import org.sonatype.aether.resolution.MetadataRequest;
import org.sonatype.aether.resolution.MetadataResult;
import org.sonatype.aether.resolution.VersionRangeRequest;
import org.sonatype.aether.resolution.VersionRangeResolutionException;
import org.sonatype.aether.resolution.VersionRangeResult;
import org.sonatype.aether.resolution.VersionRequest;
import org.sonatype.aether.resolution.VersionResolutionException;
import org.sonatype.aether.resolution.VersionResult;

/**
 * Extended dependency management repository system.
 * Allows dependency versions to be overridden
 *
 */
@Component( role = RepositorySystem.class )
public class ExtDepMgmtRepositorySystem
    implements RepositorySystem
{

    @Requirement( hint = "aether-internal" )
    private RepositorySystem repoSystemDelegate;

    public VersionRangeResult resolveVersionRange( RepositorySystemSession session, VersionRangeRequest request )
        throws VersionRangeResolutionException
    {
        return repoSystemDelegate.resolveVersionRange( session, request );
    }

    public VersionResult resolveVersion( RepositorySystemSession session, VersionRequest request )
        throws VersionResolutionException
    {
        System.out.println("resolving version: " + request );
        return repoSystemDelegate.resolveVersion( session, request );
    }

    public ArtifactDescriptorResult readArtifactDescriptor( RepositorySystemSession session,
                                                            ArtifactDescriptorRequest request )
        throws ArtifactDescriptorException
    {
        return repoSystemDelegate.readArtifactDescriptor( session, request );
    }

    public CollectResult collectDependencies( RepositorySystemSession session, CollectRequest request )
        throws DependencyCollectionException
    {
        System.out.println("collecting dependencies");
        return repoSystemDelegate.collectDependencies( session, request );
    }

    public DependencyResult resolveDependencies( RepositorySystemSession session, DependencyRequest request )
        throws DependencyResolutionException
    {
        System.out.println("resolving dependencies");
        return repoSystemDelegate.resolveDependencies( session, request );
    }

    @Deprecated
    public List<ArtifactResult> resolveDependencies( RepositorySystemSession session, DependencyNode node,
                                                     DependencyFilter filter )
        throws ArtifactResolutionException
    {
        return repoSystemDelegate.resolveDependencies( session, node, filter );
    }

    @Deprecated
    public List<ArtifactResult> resolveDependencies( RepositorySystemSession session, CollectRequest request,
                                                     DependencyFilter filter )
        throws DependencyCollectionException, ArtifactResolutionException
    {
        return repoSystemDelegate.resolveDependencies( session, request, filter );
    }

    public ArtifactResult resolveArtifact( RepositorySystemSession session, ArtifactRequest request )
        throws ArtifactResolutionException
    {
        System.out.println( "resolving artifact: " + request.getArtifact() );
        return repoSystemDelegate.resolveArtifact( session, request );
    }

    public List<ArtifactResult> resolveArtifacts( RepositorySystemSession session,
                                                  Collection<? extends ArtifactRequest> requests )
        throws ArtifactResolutionException
    {
        for ( ArtifactRequest request : requests )
        {
            System.out.println( "resolving: " + request.getArtifact() );
        }
        return repoSystemDelegate.resolveArtifacts( session, requests );
    }

    public List<MetadataResult> resolveMetadata( RepositorySystemSession session,
                                                 Collection<? extends MetadataRequest> requests )
    {
        return repoSystemDelegate.resolveMetadata( session, requests );
    }

    public InstallResult install( RepositorySystemSession session, InstallRequest request )
        throws InstallationException
    {
        return repoSystemDelegate.install( session, request );
    }

    public DeployResult deploy( RepositorySystemSession session, DeployRequest request )
        throws DeploymentException
    {
        return repoSystemDelegate.deploy( session, request );
    }

    public LocalRepositoryManager newLocalRepositoryManager( LocalRepository localRepository )
    {
        System.out.println( "creating local repo mgr: " + localRepository );
        System.out.println( repoSystemDelegate.getClass() );
        return repoSystemDelegate.newLocalRepositoryManager( localRepository );
    }

    public SyncContext newSyncContext( RepositorySystemSession session, boolean shared )
    {
        return repoSystemDelegate.newSyncContext( session, shared );
    }



}
