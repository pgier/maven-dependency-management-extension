package org.jboss.maven.extension.dependency.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

/**
 * Class containing static methods that deal with the JVM system properties
 */
public class SystemProperties
{
    /**
     * Filter System.getProperties() by accepting only properties with names that start with prepend. Trims prepend from
     * the property names when inserting them into the returned Map.
     * 
     * @param prepend The String that must be at the start of the property names
     * @return (String, String) typed LinkedHashMap of matching properties' (name, value)
     */
    public static LinkedHashMap<String, String> getPropertiesByPrepend( String prepend )
    {
        LinkedHashMap<String, String> acceptedProperties = new LinkedHashMap<String, String>();

        Properties jvmProperties = System.getProperties();
        List<?> rawPropertyNames = Collections.list( jvmProperties.propertyNames() );

        // Examine each of the JVM property names and pick out ones that match
        for ( Object propertyNameObject : rawPropertyNames )
        {
            if ( propertyNameObject instanceof String )
            {
                String propertyName = (String) propertyNameObject;
                if ( propertyName.startsWith( prepend ) )
                {
                    Object propertyValueObject = jvmProperties.get( propertyNameObject );
                    if ( propertyValueObject instanceof String )
                    {
                        String propertyValue = (String) propertyValueObject;

                        // Remove the prepend from the property name before adding it to the accepted Map
                        acceptedProperties.put( propertyName.substring( prepend.length() ), propertyValue );
                    }
                }
            }
        }

        return acceptedProperties;
    }
}
