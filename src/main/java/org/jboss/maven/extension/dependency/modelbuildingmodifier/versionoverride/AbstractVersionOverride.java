package org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.logging.Logger;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.ModelBuildingModifier;
import org.jboss.maven.extension.dependency.util.log.Logging;

/**
 * Abstract class that provides fields and methods common to classes that need to override versions by groupID and
 * artifactID
 */
public abstract class AbstractVersionOverride
    implements ModelBuildingModifier
{
    /**
     * Logging abstraction
     */
    private static final Logger logger = Logging.getLogger();

    public static Logger getLog()
    {
        return logger;
    }

    /**
     * The character used to separate groupId:arifactId:version
     */
    protected static final String GAV_SEPERATOR = ":";

    /**
     * Key: String of artifactID <br />
     * Value: Map of overrides for a groupID Inner Map Key: String of groupID Inner Map Value: String of desired
     * override version number
     */
    private Map<String, String> versionOverrides;

    public Map<String, String> getVersionOverrides()
    {
        if ( this.versionOverrides == null )
        {
            this.versionOverrides = new HashMap<String, String>();
        }
        return versionOverrides;
    }

    public void setVersionOverrides( Map<String, String> versionOverrides )
    {
        this.versionOverrides = versionOverrides;
    }

}
