package org.jboss.maven.extension.dependency;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

    /**
     * The String that needs to be prepended a system property to make it a version override. <br />
     * ex: -Dversion:junit:junit=4.10
     */
    private static final String VERSION_PROPERTY_NAME = "version:";

    /**
     * Key: String of artifactID <br />
     * Value: String of desired override version number
     */
    private final Map<String, String> overrideMap;

    public ExtDepMgmtVersionResolver()
    {
        Map<String, String> overrideMap = new HashMap<String, String>();

        Properties jvmProperties = System.getProperties();
        List<?> propertyNameList = Collections.list( jvmProperties.propertyNames() );

        // Iterate through the JVM properties and pick out ones matching version syntax
        // Verbose due to the non-typed System.getProperties() returns
        for ( Object propertyNameObject : propertyNameList )
        {
            if ( propertyNameObject instanceof String )
            {
                String propertyName = (String) propertyNameObject;
                if ( propertyName.startsWith( VERSION_PROPERTY_NAME ) )
                {
                    Object propertyValueObject = jvmProperties.get( propertyNameObject );
                    if ( propertyValueObject instanceof String )
                    {
                        String propertyValue = (String) propertyValueObject;

                        // Split the name portion into parts (ex: version:junit:junit to {version, junit, junit})
                        String[] propertyNameParts = propertyName.split( ":" );

                        if ( propertyNameParts.length == 3 )
                        {
                            // Part 0 not used (the VERSION_PROPERTY_NAME)

                            // ex: org.apache.maven.plugins
                            String groupID = propertyNameParts[1];

                            // ex: junit
                            String artifactID = propertyNameParts[2];

                            // ex: 3.0
                            String version = propertyValue;

                            System.out.printf( ">> Detected version override property. Group: %s  Name: %s  Value: %s\n",
                                               groupID, artifactID, version );

                            // Not using groupID at the moment
                            overrideMap.put( artifactID, version );
                        }
                        else
                        {
                            // Error, don't know how to handle it properly yet
                            System.err.println( ">> Detected bad version override property." );
                        }

                    }
                }
            }
        }

        this.overrideMap = overrideMap;
    }

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
        String artifactVersion = artifact.getVersion();

        VersionResult returnVerRes;
        String overrideVersion;
        if ( overrideMap.containsKey( artifactID ) )
        {
            overrideVersion = overrideMap.get( artifactID );

            artifact = artifact.setVersion( overrideVersion );
            request.setArtifact( artifact );

            System.out.printf( ">> resolveVersion has overridden  ArtifactID: %s  Version: %s  to Version: %s",
                               artifactID, artifactVersion, overrideVersion );

            returnVerRes = super.resolveVersion( session, request );
        }
        else
        {
            System.out.printf( ">> resolveVersion did not override  ArtifactID: %s  Version: %s", artifactID,
                               artifactVersion );
            returnVerRes = super.resolveVersion( session, request );
        }

        System.out.printf( "  Returning: %s\n", returnVerRes.getVersion() );

        return returnVerRes;
    }
}
