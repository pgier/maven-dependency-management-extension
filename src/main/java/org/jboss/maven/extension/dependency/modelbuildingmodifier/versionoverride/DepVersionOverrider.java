package org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.jboss.maven.extension.dependency.metainf.OverrideMapWriter;
import org.jboss.maven.extension.dependency.util.VersionPropertyReader;

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
     * A short description of the thing being overridden
     */
    private static final String OVERRIDE_NAME = "dependency";

    Map<String, String> depVersionOverrides;

    /**
     * Default constructor
     */
    public DepVersionOverrider()
    {

    }

    public ModelBuildingResult updateModel( ModelBuildingRequest request, ModelBuildingResult result )
    {
        Map<String, String> versionOverrides = getVersionOverrides();
        if ( versionOverrides.size() == 0 )
        {
            return result;
        }

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
        Map<String, String> nonMatchingVersionOverrides = this.applyOverrides( dependencies, versionOverrides );

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
        this.applyOverrides( projectDependencies, versionOverrides );

        writeXmlMap( result, getName(), versionOverrides );

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

    @Override
    public Map<String, String> getVersionOverrides()
    {
        if ( depVersionOverrides == null )
        {
            depVersionOverrides =
                VersionPropertyReader.getVersionPropertiesByPrepend( DEPENDENCY_VERSION_OVERRIDE_PREFIX );
        }
        return depVersionOverrides;
    }

    private void writeXmlMap( ModelBuildingResult result, String overrideName, Map<String, String> overrides )
    {
        OverrideMapWriter writeMapXML = new OverrideMapWriter( overrideName, overrides );
        try
        {
            writeMapXML.writeXMLTo( result.getEffectiveModel().getBuild() );
        }
        catch ( TransformerException e )
        {
            getLog().error( "Could not write " + overrideName + " override map to XML file: " + e.toString() );
        }
        catch ( IOException e )
        {
            getLog().error( "Could not write " + overrideName + " override map to XML file: " + e.toString() );
        }

    }

}
