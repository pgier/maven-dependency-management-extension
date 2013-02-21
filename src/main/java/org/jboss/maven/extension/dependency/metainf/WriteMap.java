package org.jboss.maven.extension.dependency.metainf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import org.apache.maven.model.Build;
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
     * Directory underneath the general build target dir where the files get written
     */
    private static final String OUTPUT_DIR_NAME = "extdepmgmt";

    /**
     * Convert the map into XML, write it out, and include it in the META-INF directory of the jar resources
     * 
     * @param result ModelBuildingResult to write and add the map file to
     * @param fileName Name for the xml file
     * @param map Map whose keys and values are both type String
     * @throws TransformerException
     * @throws IOException
     */
    public static void toJarResources( ModelBuildingResult result, String fileName, Map<String, Map<String, String>> map )
        throws TransformerException, IOException
    {
        // Don't do anything if the map has nothing in it
        if ( map == null || map.size() <= 0 )
        {
            return;
        }

        Build build = result.getEffectiveModel().getBuild();

        // Test if the build directory is a path that we should work with
        Path buildDir = null;
        try
        {
            buildDir = Paths.get( build.getDirectory() );

            // The result of getBuild().getDirectory() will be absolute only once, on the first call after loading (or
            // so it seems empirically).
            if ( !buildDir.isAbsolute() || Files.notExists( buildDir ) )
            {
                return;
            }
        }
        catch ( InvalidPathException x )
        {
            return;
        }

        // Where to write the file
        Path outputDir = buildDir.resolve( OUTPUT_DIR_NAME );
        Files.createDirectories( outputDir );
        Path file = outputDir.resolve( fileName + ".xml" );

        // Generate XML
        Document xml = generateXMLDocument( map );
        DOMSource xmlSource = new DOMSource( xml );

        // Write the file
        writeXMLDocument( xmlSource, file );

        // Include the output directory in the model resources if it isn't already there.
        boolean outputDirInResources = false;
        for ( Resource currResource : build.getResources() )
        {
            if ( currResource.getDirectory() == OUTPUT_DIR_NAME )
            {
                outputDirInResources = true;
                break;
            }
        }

        if ( !outputDirInResources )
        {
            Resource resource = new Resource();
            resource.setDirectory( outputDir.toString() );
            resource.setTargetPath( "META-INF/" + OUTPUT_DIR_NAME );
            // By default includes everything there

            build.addResource( resource );
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
        transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "2" );

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
        Element root = doc.createElement( "override" );
        doc.appendChild( root );

        for ( Entry<String, Map<String, String>> groupEntry : map.entrySet() )
        {
            Element groupElement = doc.createElement( "group" );
            groupElement.setAttribute( "id", groupEntry.getKey() );
            for ( Entry<String, String> artifactEntry : groupEntry.getValue().entrySet() )
            {
                Element artifactElement = doc.createElement( "artifact" );
                artifactElement.setAttribute( "id", artifactEntry.getKey() );
                artifactElement.setTextContent( artifactEntry.getValue() );
                groupElement.appendChild( artifactElement );
            }
            root.appendChild( groupElement );
        }

        return doc;
    }
}
