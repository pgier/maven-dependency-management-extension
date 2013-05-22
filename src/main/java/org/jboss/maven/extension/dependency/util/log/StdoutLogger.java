/**
 * Copyright (C) 2013 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.maven.extension.dependency.util.log;

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

    private int threshold;

    public StdoutLogger()
    {
        this( LEVEL_DEBUG );
    }

    public StdoutLogger( int threshold )
    {
        setThreshold( threshold );
    }

    public void debug( String message )
    {
        if ( isDebugEnabled() )
        {
            System.out.println( DEBUG_TICKER + message );
        }
    }

    public void debug( String message, Throwable throwable )
    {
        if ( isDebugEnabled() )
        {
            System.out.println( DEBUG_TICKER + message );
            throwable.printStackTrace();
        }
    }

    public boolean isDebugEnabled()
    {
        return threshold != LEVEL_DISABLED && threshold <= LEVEL_DEBUG;
    }

    public void info( String message )
    {
        if ( isInfoEnabled() )
        {
            System.out.println( INFO_TICKER + message );
        }
    }

    public void info( String message, Throwable throwable )
    {
        if ( isInfoEnabled() )
        {
            System.out.println( INFO_TICKER + message );
            throwable.printStackTrace();
        }
    }

    public boolean isInfoEnabled()
    {
        return threshold != LEVEL_DISABLED && threshold <= LEVEL_INFO;
    }

    public void warn( String message )
    {
        if ( isWarnEnabled() )
        {
            System.out.println( WARN_TICKER + message );
        }
    }

    public void warn( String message, Throwable throwable )
    {
        if ( isWarnEnabled() )
        {
            System.out.println( WARN_TICKER + message );
            throwable.printStackTrace();
        }
    }

    public boolean isWarnEnabled()
    {
        return threshold != LEVEL_DISABLED && threshold <= LEVEL_WARN;
    }

    public void error( String message )
    {
        if ( isErrorEnabled() )
        {
            System.out.println( ERROR_TICKER + message );
        }
    }

    public void error( String message, Throwable throwable )
    {
        if ( isErrorEnabled() )
        {
            System.out.println( ERROR_TICKER + message );
            throwable.printStackTrace();
        }
    }

    public boolean isErrorEnabled()
    {
        return threshold != LEVEL_DISABLED && threshold <= LEVEL_ERROR;
    }

    public void fatalError( String message )
    {
        if ( isFatalErrorEnabled() )
        {
            System.out.println( FATAL_ERROR_TICKER + message );
        }
    }

    public void fatalError( String message, Throwable throwable )
    {
        if ( isFatalErrorEnabled() )
        {
            System.out.println( FATAL_ERROR_TICKER + message );
            throwable.printStackTrace();
        }
    }

    public boolean isFatalErrorEnabled()
    {
        return threshold != LEVEL_DISABLED && threshold <= LEVEL_FATAL;
    }

    public int getThreshold()
    {
        return threshold;
    }

    public void setThreshold( int threshold )
    {
        if (threshold < LEVEL_DEBUG || threshold > LEVEL_DISABLED) {
            return;
        }
        this.threshold = threshold;
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
