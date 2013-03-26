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
     * Filter System.getProperties() by accepting only properties with names that start with prefix. Trims the prefix
     * from the property names when inserting them into the returned Map.
     * 
     * @param prepend The String that must be at the start of the property names
     * @return Map<String, String> map of properties with matching prepend and their values
     */
    public static Map<String, String> getPropertiesByPrefix( String prefix )
    {
        Properties systemProperties = System.getProperties();
        Map<String, String> matchedProperties = new HashMap<String, String>();
        int prefixLength = prefix.length();

        for ( String propertyName : systemProperties.stringPropertyNames() )
        {
            if ( propertyName.startsWith( prefix ) )
            {
                String trimmedPropertyName = propertyName.substring( prefixLength );
                matchedProperties.put( trimmedPropertyName, systemProperties.getProperty( propertyName ) );
            }

        }

        return matchedProperties;
    }
}
