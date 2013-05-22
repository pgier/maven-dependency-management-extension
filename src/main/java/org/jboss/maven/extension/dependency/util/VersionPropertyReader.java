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
