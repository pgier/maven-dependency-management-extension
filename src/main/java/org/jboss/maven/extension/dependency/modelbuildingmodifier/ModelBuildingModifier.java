package org.jboss.maven.extension.dependency.modelbuildingmodifier;

import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;

/**
 * Interface for classes that modify a built model in some way
 */
public interface ModelBuildingModifier
{
    /**
     * Modifies result in some way, possibly with information from request
     * 
     * @param request the ModelBuildingRequest to source information from
     * @param result the ModelBuildingResult to be modified
     * @return the modified ModelBuildingResult result
     */
    public ModelBuildingResult modifyBuild( ModelBuildingRequest request, ModelBuildingResult result );
}