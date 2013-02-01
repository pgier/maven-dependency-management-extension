package org.jboss.maven.extension.dependency;

import org.apache.maven.model.building.DefaultModelBuilder;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.codehaus.plexus.component.annotations.Component;

@Component(role = DefaultModelBuilder.class)
public class ExtDepMgmtModelBuilder extends DefaultModelBuilder implements ModelBuilder {

    @Override
    public ModelBuildingResult build(ModelBuildingRequest request) throws ModelBuildingException {
        System.out.println(">>>> build(ModelBuildingRequest) called: [" + request + "]");
        return super.build(request);
    }

    @Override
    public ModelBuildingResult build(ModelBuildingRequest request, ModelBuildingResult result) throws ModelBuildingException {
        System.out.println(">>>> build(ModelBuildingRequest, ModelBuildingResult) called: [" + request + "] [" + result + "]");
        return super.build(request, result);
    }
}
