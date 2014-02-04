
import java.util.Properties

Properties versionProps = new Properties()
File itDirectory = new File("target/it/configured-version-properties")

new File(itDirectory, "target/classes/versions.properties").withReader { reader -> 
  versionProps.load( reader )
}
assert( "4.1".equals( versionProps.getProperty( "theJunitVersion" ) ) )

