package org.jboss.maven.extension.dependency.modelmodifier.versionoverride;

import java.io.IOException;
import java.util.Map;

import org.apache.maven.model.Model;
import org.codehaus.plexus.logging.Logger;
import org.jboss.maven.extension.dependency.metainf.MetaInfWriter;
import org.jboss.maven.extension.dependency.metainf.generator.OverridePropertiesGenerator;
import org.jboss.maven.extension.dependency.modelmodifier.ModelModifier;
import org.jboss.maven.extension.dependency.util.log.Logging;

/**
 * Abstract class that provides fields and methods common to classes that need to override versions by groupID and
 * artifactID
 */
public abstract class AbstractVersionOverrider
    implements ModelModifier
{
    /**
     * Logging abstraction
     */
    private static final Logger logger = Logging.getLogger();

    protected static Logger getLog()
    {
        return logger;
    }

    /**
     * The character used to separate groupId:arifactId:version
     */
    protected static final String GAV_SEPERATOR = ":";

    /**
     * Get the set of version overrides to be applied to the build
     */
    public abstract Map<String, String> getVersionOverrides();

    /**
     * Writes String/String override maps to XML
     * 
     * @param model
     * @param overrideName
     * @param overrides
     */
    protected void writeOverrideMap( Model model, String overrideName, Map<String, String> overrides )
    {
        try
        {
            MetaInfWriter.writeResource( model, new OverridePropertiesGenerator(overrides, overrideName) );
        }
        catch ( IOException e )
        {
            logger.error( "Could not write " + overrideName + " override map to file due to " + e );
        }
    }

}
