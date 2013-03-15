package org.jboss.maven.extension.dependency.metainf.generator;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;

/**
 * Roughly emulate the generation behaviour of help:effective-pom. This will not produce an exact duplicate of
 * help:effective-pom's pom, but should be close.
 */
public class EffectivePomGenerator
    implements MetaInfGenerator
{
    /**
     * Convert an in-memory model to POM XML in string format.
     * 
     * @param model The model to convert
     * @return The POM XML representation of the project's model
     * @throws IOException If MavenXpp3Writer fails to write the raw xml to the internal buffer.
     */
    @Override
    public String generateContent( Model model )
        throws IOException
    {
        String encoding = model.getModelEncoding();

        // Used for taking data out of the library methods that require a writer.
        StringWriter xmlBuffer;

        // Extract the basic XML data out of maven's MavenXpp3Writer
        xmlBuffer = new StringWriter();
        MavenXpp3Writer pomWriter = new MavenXpp3Writer();
        pomWriter.write( xmlBuffer, model );
        String basicPomXML = xmlBuffer.toString();

        // Convert the basic XML into something aesthetically pleasing
        xmlBuffer = new StringWriter();
        XMLWriter xmlWriter = new PrettyPrintXMLWriter( xmlBuffer, encoding, null );
        xmlWriter.writeMarkup( basicPomXML );
        String effectivePom = xmlBuffer.toString();

        // TODO: remove duplicate XML header in output, ex:
        // <?xml version="1.0" encoding="UTF-8"?>
        // <?xml version="1.0" encoding="UTF-8"?>

        return effectivePom;
    }

    @Override
    public String getDescription()
    {
        return "effective pom";
    }

    @Override
    public String getDesiredFileExtension()
    {
        return "xml";
    }
}
