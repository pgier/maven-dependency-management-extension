package org.jboss.maven.extension.dependency;

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.repository.internal.DefaultVersionResolver;
import org.codehaus.plexus.component.annotations.Component;
import org.jboss.maven.extension.dependency.util.StdoutLogger;
import org.jboss.maven.extension.dependency.util.SystemProperties;
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

//@Component( role = VersionResolver.class )
public class ExtDepMgmtVersionResolver
    extends DefaultVersionResolver
    implements VersionResolver, Service
{

    /**
     * The String separates parts of a Property name
     */
    private static final String PROPERTY_NAME_SEPERATOR = ":";

    /**
     * The String that needs to be prepended a system property to make it a version override. <br />
     * ex: -Dversion:junit:junit=4.10
     */
    private static final String VERSION_PROPERTY_NAME = "version" + PROPERTY_NAME_SEPERATOR;

    private StdoutLogger stdLogger = new StdoutLogger();

    /**
     * Key: String of artifactID <br />
     * Value: String of desired override version number
     */
    private final Map<String, String> overrideMap;

    /**
     * Load overrides list when the object is instantiated
     */
    public ExtDepMgmtVersionResolver()
    {
        Map<String, String> propertyMap = SystemProperties.getPropertiesByPrepend( VERSION_PROPERTY_NAME );

        HashMap<String, String> overrideMap = new HashMap<String, String>();

        for ( String propertyName : propertyMap.keySet() )
        {
            // Split the name portion into parts (ex: junit:junit to {junit, junit})
            String[] propertyNameParts = propertyName.split( PROPERTY_NAME_SEPERATOR );

            if ( propertyNameParts.length == 2 )
            {
                // Part 1 is the group name. ex: org.apache.maven.plugins
                String groupID = propertyNameParts[0];
                // Part 2 is the artifact ID. ex: junit
                String artifactID = propertyNameParts[1];

                // The value of the property is the desired version. ex: 3.0
                String version = propertyMap.get( propertyName );

                stdLogger.debug( "Detected version override property. Group: " + groupID + "  ArtifactID: "
                    + artifactID + "  Target Version: " + version );

                // Not using groupID at the moment
                overrideMap.put( artifactID, version );
            }
            else
            {
                stdLogger.warn( "Detected bad version override property. Name: " + propertyName );
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

            returnVerRes = super.resolveVersion( session, request );

            stdLogger.debug( "Version of ArtifactID: " + artifactID + " was overridden from " + artifactVersion
                + " to " + returnVerRes.getVersion() + " (" + overrideVersion + ")" );
        }
        else
        {
            // System.out.printf( ">> resolveVersion did not override  ArtifactID: %s  Version: %s", artifactID,
            // artifactVersion );
            returnVerRes = super.resolveVersion( session, request );
        }

        return returnVerRes;
    }
}
