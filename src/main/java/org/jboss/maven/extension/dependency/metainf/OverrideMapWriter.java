package org.jboss.maven.extension.dependency.metainf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import org.codehaus.plexus.logging.Logger;
import org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride.VersionOverrideInfo;
import org.jboss.maven.extension.dependency.util.log.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Converts in-memory override maps to a persistent form
 */
public class OverrideMapWriter
{
    private static final Logger logger = Logging.getLogger();

    /**
     * Directory underneath the general build target dir where the files get written
     */
    private static final String OUTPUT_DIR_NAME = "extdepmgmt";

    private final String fileName;

    private final Map<String, Map<String, VersionOverrideInfo>> overrideMap;

    /**
     * Ignore write requests when this flag is true
     */
    private boolean mapWritten;

    /**
     * Clear the location where the file would have been written on write requests if this is true
     */
    private final boolean clearMapOnDisk;

    /**
     * @param fileName Name for the xml file
     * @param map The override map to use
     */
    public OverrideMapWriter( String fileName, Map<String, Map<String, VersionOverrideInfo>> map )
    {
        this.fileName = fileName;
        this.overrideMap = map;

        mapWritten = false;

        if ( map.size() <= 0 )
        {
            clearMapOnDisk = true;
        }
        else
        {
            clearMapOnDisk = false;
        }
    }

    /**
     * Convert the map into XML, write it out, and include it in the META-INF directory of the jar resources of build.
     * 
     * @param build The Build to write to and add the written map file to
     * @throws TransformerException
     * @throws IOException
     * @throws InvalidPathException
     */
    public void writeXMLTo( Build build )
        throws TransformerException, IOException
    {
        if ( mapWritten )
        {
            return;
        }

        // Where to write the file
        File buildDir = new File( build.getDirectory() );
        File outputDir = new File( buildDir.getPath() + "/classes/META-INF/" + OUTPUT_DIR_NAME );
        File file = new File( outputDir.getPath() + "/" + fileName + "-version.xml" );

        if ( clearMapOnDisk )
        {
            boolean fileWasDeleted = file.delete();
            outputDir.delete();

            if ( fileWasDeleted )
            {
                logger.debug( "Cleared map at path " + file );
            }
        }
        else
        {
            // Create directories if needed
            outputDir.mkdirs();

            // Generate XML
            Document xml = generateXMLDocument( overrideMap );
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

            logger.debug( "Wrote map at path " + file );
        }

        mapWritten = true;
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
    private static void writeXMLDocument( DOMSource xmlSource, File file )
        throws IOException, TransformerException
    {
        // Get a Transformer in a long-winded manner
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
        transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "2" );

        // Actually write the file
        FileWriter writer = null;
        try
        {
            writer = new FileWriter( file );
            StreamResult output = new StreamResult( writer );
            transformer.transform( xmlSource, output );
        }
        finally
        {
            if ( writer != null )
            {
                writer.close();
            }
        }
    }

    /**
     * Perform the conversion from a Map to XML
     * 
     * @param map The Map to source entries from
     * @return The generated XML Document
     */
    private static Document generateXMLDocument( Map<String, Map<String, VersionOverrideInfo>> map )
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
        Element root = doc.createElement( "override" );
        doc.appendChild( root );

        for ( Entry<String, Map<String, VersionOverrideInfo>> groupEntry : map.entrySet() )
        {
            Element groupElement = doc.createElement( "group" );
            groupElement.setAttribute( "id", groupEntry.getKey() );
            for ( Entry<String, VersionOverrideInfo> artifactEntry : groupEntry.getValue().entrySet() )
            {
                Element artifactElement = doc.createElement( "artifact" );
                artifactElement.setAttribute( "id", artifactEntry.getKey() );

                Element valueElement = doc.createElement( "version" );
                valueElement.setTextContent( artifactEntry.getValue().getVersion() );

                artifactElement.appendChild( valueElement );
                groupElement.appendChild( artifactElement );
            }
            root.appendChild( groupElement );
        }

        return doc;
    }
}
