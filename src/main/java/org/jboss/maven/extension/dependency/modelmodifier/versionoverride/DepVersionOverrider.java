package org.jboss.maven.extension.dependency.modelmodifier.versionoverride;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingException;
import org.jboss.maven.extension.dependency.resolver.EffectiveModelBuilder;
import org.jboss.maven.extension.dependency.util.VersionPropertyReader;
import org.sonatype.aether.resolution.ArtifactDescriptorException;
import org.sonatype.aether.resolution.ArtifactResolutionException;

/**
 * Overrides dependency versions in a model
 */
public class DepVersionOverrider
    extends AbstractVersionOverrider
{
    /**
     * The String that needs to be prepended a system property to make it a version override. <br />
     * ex: -Dversion:junit:junit=4.10
     */
    private static final String DEPENDENCY_VERSION_OVERRIDE_PREFIX = "version:";

    /**
     * The name of the property which contains the GAV of the remote pom from which to retrieve dependency management
     * information. <br />
     * ex: -DdependencyManagement:org.foo:bar-dep-mgmt:1.0
     */
    private static final String DEPENDENCY_MANAGEMENT_POM_PROPERTY = "dependencyManagement";

    /**
     * A short description of the thing being overridden
     */
    private static final String OVERRIDE_NAME = "dependency";

    private Map<String, String> dependencyVersionOverrides;

    /**
     * Default constructor
     */
    public DepVersionOverrider()
    {

    }

    public boolean updateModel( Model model )
    {
        Map<String, String> versionOverrides = getVersionOverrides();
        if ( versionOverrides.size() == 0 )
        {
            return false;
        }

        // If the model doesn't have any Dependency Management set by default, create one for it
        DependencyManagement dependencyManagement = model.getDependencyManagement();
        if ( dependencyManagement == null )
        {
            dependencyManagement = new DependencyManagement();
            model.setDependencyManagement( dependencyManagement );
            getLog().debug( "Created new Dependency Management for model" );
        }

        // Apply matching overrides to dependency management
        List<Dependency> dependencies = dependencyManagement.getDependencies();
        Map<String, String> nonMatchingVersionOverrides = applyOverrides( dependencies, versionOverrides );

        // Add dependencies which did not match previously
        for ( String groupIdArtifactId : nonMatchingVersionOverrides.keySet() )
        {
            String[] groupIdArtifactIdParts = groupIdArtifactId.split( ":" );
            Dependency dependency = new Dependency();
            dependency.setGroupId( groupIdArtifactIdParts[0] );
            dependency.setArtifactId( groupIdArtifactIdParts[1] );
            String artifactVersion = nonMatchingVersionOverrides.get( groupIdArtifactId );
            dependency.setVersion( artifactVersion );
            dependencyManagement.getDependencies().add( dependency );
            getLog().debug( "New dependency added to Dependency Management: " + groupIdArtifactId + "="
                                + artifactVersion );
        }

        // Apply overides to project dependencies
        List<Dependency> projectDependencies = model.getDependencies();
        applyOverrides( projectDependencies, versionOverrides );

        writeOverrideMap( model, getName(), versionOverrides );

        // Assuming the Model changed since overrides were given
        return true;
    }

    /**
     * Apply a set of version overrides to a list of dependencies. Return a list of the overrides which were not
     * applied.
     * 
     * @param dependencies The list of dependencies
     * @param overrides The map of dependency version overrides
     * @return The map of overrides that were not matched in the dependencies
     */
    public Map<String, String> applyOverrides( List<Dependency> dependencies, Map<String, String> overrides )
    {
        // Apply matching overrides to dependency management
        Map<String, String> nonMatchingVersionOverrides = new HashMap<String, String>();
        nonMatchingVersionOverrides.putAll( overrides );

        for ( Dependency dependency : dependencies )
        {
            String groupIdArtifactId = dependency.getGroupId() + GAV_SEPERATOR + dependency.getArtifactId();
            if ( overrides.containsKey( groupIdArtifactId ) )
            {
                String artifactVersion = overrides.get( groupIdArtifactId );
                dependency.setVersion( artifactVersion );
                getLog().debug( "Altered existing dependency in Dependency Management: " + groupIdArtifactId + "="
                                    + artifactVersion );
                nonMatchingVersionOverrides.remove( groupIdArtifactId );
            }
        }

        return nonMatchingVersionOverrides;
    }

    @Override
    public String getName()
    {
        return OVERRIDE_NAME;
    }

    public Map<String, String> getVersionOverrides()
    {
        if ( dependencyVersionOverrides == null )
        {
            dependencyVersionOverrides = new HashMap<String, String>();

            Map<String, String> remoteDepOverrides = loadRemoteDepVersionOverrides();
            dependencyVersionOverrides.putAll( remoteDepOverrides );

            Map<String, String> propDepOverrides =
                VersionPropertyReader.getVersionPropertiesByPrepend( DEPENDENCY_VERSION_OVERRIDE_PREFIX );
            dependencyVersionOverrides.putAll( propDepOverrides );
        }
        return dependencyVersionOverrides;
    }

    private Map<String, String> loadRemoteDepVersionOverrides()
    {
        Properties systemProperties = System.getProperties();
        String depMgmtPomGAV = systemProperties.getProperty( DEPENDENCY_MANAGEMENT_POM_PROPERTY );

        Map<String, String> versionOverrides = new HashMap<String, String>( 0 );

        if ( depMgmtPomGAV != null )
        {
            try
            {
                EffectiveModelBuilder resolver = EffectiveModelBuilder.getInstance();
                versionOverrides = resolver.getRemoteDependencyVersionOverrides( depMgmtPomGAV );
            }
            catch ( ArtifactResolutionException e )
            {
                getLog().warn( "Unable to resolve remote pom: " + e );
            }
            catch ( ArtifactDescriptorException e )
            {
                getLog().warn( "Unable to resolve remote pom: " + e );
            }
            catch ( ModelBuildingException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return versionOverrides;
    }

}
