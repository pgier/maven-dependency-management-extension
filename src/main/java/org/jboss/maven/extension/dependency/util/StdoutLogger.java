package org.jboss.maven.extension.dependency.util;

import org.codehaus.plexus.logging.Logger;

/**
 * Haven't been successful so far hooking into the real log system, this class should hopefully just be a placeholder.
 */
public class StdoutLogger
    implements Logger
{
    private static String DEBUG_TICKER = "[DEBUG] ";

    private static String INFO_TICKER = "[INFO] ";

    private static String WARN_TICKER = "[WARNING] ";

    private static String ERROR_TICKER = "[ERROR] ";

    private static String FATAL_ERROR_TICKER = "[FATAL] ";

    public void debug( String message )
    {
        System.out.println( DEBUG_TICKER + message );
    }

    public void debug( String message, Throwable throwable )
    {
        System.out.println( DEBUG_TICKER + message );
        throwable.printStackTrace();
    }

    public boolean isDebugEnabled()
    {
        return true;
    }

    public void info( String message )
    {
        System.out.println( INFO_TICKER + message );
    }

    public void info( String message, Throwable throwable )
    {
        System.out.println( INFO_TICKER + message );
        throwable.printStackTrace();
    }

    public boolean isInfoEnabled()
    {
        return true;
    }

    public void warn( String message )
    {
        System.out.println( WARN_TICKER + message );
    }

    public void warn( String message, Throwable throwable )
    {
        System.out.println( WARN_TICKER + message );
        throwable.printStackTrace();
    }

    public boolean isWarnEnabled()
    {
        return true;
    }

    public void error( String message )
    {
        System.out.println( ERROR_TICKER + message );
    }

    public void error( String message, Throwable throwable )
    {
        System.out.println( ERROR_TICKER + message );
        throwable.printStackTrace();
    }

    public boolean isErrorEnabled()
    {
        return true;
    }

    public void fatalError( String message )
    {
        System.out.println( FATAL_ERROR_TICKER + message );
    }

    public void fatalError( String message, Throwable throwable )
    {
        System.out.println( FATAL_ERROR_TICKER + message );
        throwable.printStackTrace();
    }

    public boolean isFatalErrorEnabled()
    {
        return true;
    }

    public int getThreshold()
    {
        return LEVEL_DEBUG;
    }

    public void setThreshold( int threshold )
    {
    }

    public Logger getChildLogger( String name )
    {
        return null;
    }

    public String getName()
    {
        return null;
    }

}
