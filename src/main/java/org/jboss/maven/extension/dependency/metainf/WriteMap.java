package org.jboss.maven.extension.dependency.metainf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.model.Resource;
import org.apache.maven.model.building.ModelBuildingResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class containing static methods that deal with converting in-memory override maps to another form
 */
public class WriteMap
{
    /**
     * Charset for writing text files
     */
    private static final Charset CHARSET = Charset.forName( "UTF-8" );

    /**
     * Convert the map into XML, write it out, and include it in the META-INF directory of the jar resources
     * 
     * @param result ModelBuildingResult to write and add the map file to
     * @param map Map whose keys and values are both type String
     * @throws TransformerException
     * @throws IOException
     */
    public static void toJarResources( ModelBuildingResult result, Map<String, Map<String, String>> map )
        throws TransformerException, IOException
    {
        // Don't do anything if the map has nothing in it
        if ( map == null || map.size() <= 0 )
        {
            return;
        }

        // Test if the build directory is a path that we should work with
        try
        {
            Path buildDir = Paths.get( result.getEffectiveModel().getBuild().getDirectory() );

            if ( !buildDir.isAbsolute() )
            {
                return;
            }

            // Where to write the file
            Path file = buildDir.resolve( "version-overrides.xml" );

            // Generate XML
            Document xml = generateXMLDocument( map );
            DOMSource xmlSource = new DOMSource( xml );

            // Write the file
            writeXMLDocument( xmlSource, file );

            // Include the file in the model resources
            Resource resource = new Resource();
            resource.setDirectory( buildDir.toString() );
            resource.setTargetPath( "META-INF" );

            List<String> includes = new ArrayList<String>( 1 );
            includes.add( file.getFileName().toString() );
            resource.setIncludes( includes );

            result.getEffectiveModel().getBuild().addResource( resource );
        }
        catch ( InvalidPathException x )
        {
        }
    }

    /**
     * Write XML to a file
     * 
     * @param xmlSource XML to write
     * @param file File to write to
     * @throws IOException If an I/O error occurs opening or creating the file
     * @throws TransformerException When it is not possible to create a Transformer instance, or if an unrecoverable
     *             error occurs during the course of the transformation.
     */
    private static void writeXMLDocument( DOMSource xmlSource, Path file )
        throws IOException, TransformerException
    {
        // Get a Transformer in a long-winded manner
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty( OutputKeys.ENCODING, CHARSET.toString() );
        transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
        transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "4" );

        // Actually write the file
        try (BufferedWriter writer = Files.newBufferedWriter( file, CHARSET ))
        {
            StreamResult output = new StreamResult( writer );
            transformer.transform( xmlSource, output );
        }
    }

    /**
     * Perform the conversion from a Map to XML
     * 
     * @param map The Map to source entries from
     * @return The generated XML Document
     */
    private static Document generateXMLDocument( Map<String, Map<String, String>> map )
    {
        // Get a Document in a long-winded manner
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
        }
        catch ( ParserConfigurationException x )
        {
            // This state should never happen, since all we want is the default config
            x.printStackTrace();
        }
        Document doc = builder.newDocument();

        // Add the entries from the map into the XML tree
        // TODO Needs to be more intelligent once final map format is known
        Element root = doc.createElement( "overrides" );
        doc.appendChild( root );

        for ( Entry<String, Map<String, String>> groupEntry : map.entrySet() )
        {
            Element groupElement = doc.createElement( groupEntry.getKey() );
            for ( Entry<String, String> artifactEntry : groupEntry.getValue().entrySet() )
            {
                Element artifactElement = doc.createElement( artifactEntry.getKey() );
                artifactElement.setTextContent( artifactEntry.getValue() );
                groupElement.appendChild( artifactElement );
            }
            root.appendChild( groupElement );
        }

        return doc;
    }
}
