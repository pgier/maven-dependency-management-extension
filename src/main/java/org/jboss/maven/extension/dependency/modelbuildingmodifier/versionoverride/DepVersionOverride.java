package org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;

/**
 * Overrides dependency versions in a model
 */
public class DepVersionOverride
    extends AbstractVersionOverride
{
    /**
     * A short description of the thing being overridden
     */
    private static final String OVERRIDE_NAME = "dependency";

    /**
     * Load dependency overrides list when the object is instantiated
     */
    public DepVersionOverride( Map<String, String> versionOverrides )
    {
        this.setVersionOverrides( versionOverrides );
    }

    public ModelBuildingResult updateModel( ModelBuildingRequest request, ModelBuildingResult result )
    {

        // If the model doesn't have any Dependency Management set by default, create one for it
        DependencyManagement dependencyManagement = result.getEffectiveModel().getDependencyManagement();
        if ( dependencyManagement == null )
        {
            dependencyManagement = new DependencyManagement();
            result.getEffectiveModel().setDependencyManagement( dependencyManagement );
            getLog().debug( "Created new Dependency Management for model" );
        }

        // Apply matching overrides to dependency management
        List<Dependency> dependencies = dependencyManagement.getDependencies();
        Map<String, String> nonMatchingVersionOverrides =
            this.applyOverrides( dependencies, this.getVersionOverrides() );

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
            getLog().debug( "New dependency added to Dependency Management: " + groupIdArtifactId + "=" +
                                artifactVersion );
        }

        // Apply overides to project dependencies
        List<Dependency> projectDependencies = result.getEffectiveModel().getDependencies();
        this.applyOverrides( projectDependencies, getVersionOverrides() );

        return result;
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
                getLog().debug( "Altered existing dependency in Dependency Management: " + groupIdArtifactId + "=" +
                                    artifactVersion );
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

}
