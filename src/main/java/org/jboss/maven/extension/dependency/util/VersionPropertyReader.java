package org.jboss.maven.extension.dependency.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Class containing static methods that deal with the JVM system properties for version overrides
 */
public class VersionPropertyReader
{
    /**
     * Filter System.getProperties() by accepting only properties with names that start with prepend. Trims prepend from
     * the property names when inserting them into the returned Map.
     * 
     * @param prepend The String that must be at the start of the property names
     * @return Map<String, String> map of properties with matching prepend and their values
     */
    public static Map<String, String> getVersionPropertiesByPrepend( String prepend )
    {

        Properties systemProperties = System.getProperties();
        Map<String, String> matchedProperties = new HashMap<String, String>();

        for ( String propertyName : systemProperties.stringPropertyNames() )
        {
            if ( propertyName.startsWith( prepend ) )
            {
                String trimmedPropertyName = propertyName.substring( prepend.length() );
                matchedProperties.put( trimmedPropertyName, systemProperties.getProperty( propertyName ) );
            }

        }

        return matchedProperties;
    }
}
