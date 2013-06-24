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
package org.jboss.maven.extension.dependency.metainf;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.model.Model;

public class OverridePropertiesGenerator
    implements MetaInfGenerator
{
    private String description;

    private Map<String, String> overrides;

    public OverridePropertiesGenerator( Map<String, String> overrides, String description )
    {
        this.description = description + " overrides";
        this.overrides = overrides;
    }

    @Override
    public String generateContent( Model model )
        throws IOException
    {
        StringBuilder content = new StringBuilder();

        for ( Entry<String, String> override : overrides.entrySet() )
        {
            // Add a line per entry in the format of key=value\n
            content.append( override.getKey() );
            content.append( "=" );
            content.append( override.getValue() );
            content.append( "\n" );
        }

        return content.toString();
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public String getDesiredFileExtension()
    {
        return "properties";
    }

}
