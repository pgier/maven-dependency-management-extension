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
     * Writes String/String override maps to .properties format
     * 
     * @param model Model whose build to add the written file to
     * @param overrideName Primary part of the written file name
     * @param overrides Overrides to write as the file content
     */
    protected static void writeOverrideMap( Model model, String overrideName, Map<String, String> overrides )
    {
        try
        {
            MetaInfWriter.writeResource( model, new OverridePropertiesGenerator(overrides, overrideName) );
        }
        catch ( IOException e )
        {
            logger.error( "Could not write " + overrideName + " override map to file due to " + e );
            Logging.logAllCauses( logger, e.getCause() );
        }
    }

}
