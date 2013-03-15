package org.jboss.maven.extension.dependency.metainf.generator;

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
        this.description = description;
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
