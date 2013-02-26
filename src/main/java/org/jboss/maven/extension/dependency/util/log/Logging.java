package org.jboss.maven.extension.dependency.util.log;

import org.codehaus.plexus.logging.Logger;

/**
 * Provides a way for classes in the local packages to perform central logging
 */
public class Logging
{
    private static Logger logger;

    /**
     * Yields the central logger object.
     * @return A Logger object, never null.
     */
    public static Logger getLogger()
    {
        // First access creates the logger (on-demand)
        if ( logger == null )
        {
            logger = new StdoutLogger();
        }
        return logger;
    }
}
