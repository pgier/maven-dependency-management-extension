package org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride;

import java.util.Map;

import org.codehaus.plexus.logging.Logger;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.ModelBuildingModifier;
import org.jboss.maven.extension.dependency.util.log.Logging;

/**
 * Abstract class that provides fields and methods common to classes that need to override versions by groupID and
 * artifactID
 */
public abstract class AbstractVersionOverrider
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
     * Get the set of version overrides to be applied to the build
     */
    public abstract Map<String, String> getVersionOverrides();

}
