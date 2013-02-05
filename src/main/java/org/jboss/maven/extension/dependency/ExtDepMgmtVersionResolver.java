package org.jboss.maven.extension.dependency;

import org.apache.maven.repository.internal.DefaultVersionResolver;
import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.impl.MetadataResolver;
import org.sonatype.aether.impl.SyncContextFactory;
import org.sonatype.aether.impl.VersionResolver;
import org.sonatype.aether.resolution.VersionRequest;
import org.sonatype.aether.resolution.VersionResolutionException;
import org.sonatype.aether.resolution.VersionResult;
import org.sonatype.aether.spi.locator.Service;
import org.sonatype.aether.spi.locator.ServiceLocator;
import org.sonatype.aether.spi.log.Logger;

@Component( role = VersionResolver.class )
public class ExtDepMgmtVersionResolver
    extends DefaultVersionResolver
    implements VersionResolver, Service
{

    @Override
    public void initService( ServiceLocator locator )
    {
        super.initService( locator );
    }

    @Override
    public DefaultVersionResolver setLogger( Logger logger )
    {
        return super.setLogger( logger );
    }

    @Override
    public DefaultVersionResolver setMetadataResolver( MetadataResolver metadataResolver )
    {
        return super.setMetadataResolver( metadataResolver );
    }

    @Override
    public DefaultVersionResolver setSyncContextFactory( SyncContextFactory syncContextFactory )
    {
        return super.setSyncContextFactory( syncContextFactory );
    }

    @Override
    public VersionResult resolveVersion( RepositorySystemSession session, VersionRequest request )
        throws VersionResolutionException
    {
        Artifact artifact = request.getArtifact();

        String artifactID = artifact.getArtifactId();
        String version = artifact.getVersion();

        VersionResult returnVerRes = super.resolveVersion( session, request );

        System.out.printf( ">> resolveVersion called for  ArtifactID: %-30s  Version: %-15s  and it Resolved: %-15s\n",
                           artifactID, version, returnVerRes.getVersion() );
        return returnVerRes;
    }

}
