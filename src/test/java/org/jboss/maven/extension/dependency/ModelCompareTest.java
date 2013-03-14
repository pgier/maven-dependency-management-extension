package org.jboss.maven.extension.dependency;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingResult;
import org.jboss.maven.extension.dependency.modelmodifier.ModelCompare;
import org.junit.Before;
import org.junit.Test;

public class ModelCompareTest
{

    @Before
    public void setUp()
    {
        // Set debug flag inside the class
        try
        {
            Field field = ModelCompare.class.getDeclaredField( "DEBUG" );
            // Strip private
            field.setAccessible( true );
            // Strip final
            Field modifiersField = Field.class.getDeclaredField( "modifiers" );
            modifiersField.setAccessible( true );
            modifiersField.setInt( field, field.getModifiers() & ~Modifier.FINAL );
            // Set value
            field.set( null, 1 );
        }
        catch ( Exception x )
        {
            fail( "Exception '" + x + "' when setting debug mode on for the class under test" );
        }
    }

    private static Model getModel( String pomName )
        throws ModelBuildingException
    {
        File pom = new File( "src/test/resources/poms/" + pomName + ".xml" ).getAbsoluteFile();
        ModelBuilder builder = new DefaultModelBuilderFactory().newInstance();
        DefaultModelBuildingRequest request = new DefaultModelBuildingRequest();
        request.setPomFile( pom );
        ModelBuildingResult result = builder.build( request );
        return result.getEffectiveModel();
    }

    @Test
    public void testAreEqualModelModel()
        throws ModelBuildingException
    {
        // Make a model
        Model model = getModel( "simple" );

        // Should equal self
        assertTrue( "Didn't equal self", ModelCompare.areEqual( model, model ) );

        // Should equal a clone (and may have some identicality, depending on the depth of cloning implemented)
        Model modelClone = model.clone();
        assertTrue( "Didn't equal clone", ModelCompare.areEqual( model, modelClone ) );

        // Should equal a reread of the same file (but should have no identicality)
        Model rereadModel = getModel( "simple" );
        assertTrue( "Didn't equal reread model", ModelCompare.areEqual( model, rereadModel ) );

        // Test various changes to the model file
        String[] pomlist = new String[] { "depchanged", "pluginchanged", "pluginconfchanged", "propertychanged" };
        for ( String pom : pomlist )
        {
            // Difference
            Model otherModel = getModel( "simple-" + pom );
            otherModel.setPomFile( model.getPomFile() );
            assertFalse( "Didn't detect difference between different models (" + pom + ")",
                         ModelCompare.areEqual( model, otherModel ) );
            // Self
            assertTrue( "Didn't equal itself (" + pom + ")", ModelCompare.areEqual( otherModel, otherModel ) );
            // Clone
            Model otherClone = otherModel.clone();
            assertTrue( "Didn't equal clone (" + pom + ")", ModelCompare.areEqual( otherModel, otherClone ) );
            // Reread
            Model otherRereadModel = getModel( "simple-" + pom );
            otherRereadModel.setPomFile( model.getPomFile() );
            assertTrue( "Didn't equal reread model (" + pom + ")", ModelCompare.areEqual( otherModel, otherRereadModel ) );
        }

        // Test various modifications on a clone
        modelClone = rereadModel.clone();
        for ( Dependency dep : modelClone.getDependencies() )
        {
            if ( dep.getArtifactId() == "junit" )
            {
                dep.setVersion( "4.9" );
                break;
            }
        }
        assertFalse( "Didn't detect modifications in memory", ModelCompare.areEqual( model, modelClone ) );
    }
}
