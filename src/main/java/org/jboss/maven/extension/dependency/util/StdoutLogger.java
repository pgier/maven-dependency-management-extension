package org.jboss.maven.extension.dependency.util;

import org.sonatype.aether.spi.log.Logger;

/**
 * Haven't been successful so far hooking into the real log system, this class should hopefully just be a placeholder.
 */
public class StdoutLogger
    implements Logger
{

    public boolean isDebugEnabled()
    {
        return true;
    }

    public void debug( String msg )
    {
        System.out.println("[DEBUG] " + msg);
    }

    public void debug( String msg, Throwable error )
    {
        System.out.println("[DEBUG] " + msg);
    }

    public boolean isWarnEnabled()
    {
        return true;
    }

    public void warn( String msg )
    {
        System.out.println("[WARNING] " + msg);
    }

    public void warn( String msg, Throwable error )
    {
        System.out.println("[WARNING] " + msg);
    }

}
