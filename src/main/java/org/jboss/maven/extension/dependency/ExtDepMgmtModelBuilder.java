package org.jboss.maven.extension.dependency;

import org.apache.maven.model.building.DefaultModelBuilder;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

@Component(role = DefaultModelBuilder.class)
public class ExtDepMgmtModelBuilder implements ModelBuilder {

    // Seems that having this requirement may cause issues, since jars in lib/ext/ are loaded before the main jars
    // Need to see this works if we add this as a build extension in the pom (the other way of using an extension)
    // See http://maven.apache.org/examples/maven-3-lifecycle-extensions.html
    // It's also possible that we could insert this jar after the main jars are loaded? This isn't documented though.
    // See /usr/share/maven/bin/m2.conf 's [plexus.core] section
    
    //@Requirement(hint = "model-builder-internal")
    //private DefaultModelBuilder modelBuilderDelegate;

    public ModelBuildingResult build(ModelBuildingRequest request) throws ModelBuildingException {
        System.out.println(">>>> build 1 called <<<<");
        return null;
    }

    public ModelBuildingResult build(ModelBuildingRequest request, ModelBuildingResult result) throws ModelBuildingException {
        System.out.println(">>>> build 2 called <<<<");
        return null;
    }
}
