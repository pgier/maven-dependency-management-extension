package org.jboss.maven.extension.dependency.modelmodifier;

import org.apache.maven.model.Model;

/**
 * Interface for classes that modify a built model in some way
 */
public interface ModelModifier
{
    /**
     * Possibly updates a model in some way (may do nothing).
     * 
     * @param model the Model to be modified
     * @return the possibly updated Model model
     */
    public Model updateModel( Model model );

    /**
     * Get simple name of this modifier
     * 
     * @return Simple name of this modifier
     */
    public String getName();
}