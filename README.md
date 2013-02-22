# Maven Dependency Management Extension

Maven extension which allows additional dependency management features such as overriding a dependency version from the command line.

## Usage
Pass a property to the maven build in the form:

    version:<groupId>:<artifactId>=<version>

to override a **dependency version**. Pass a property to the maven build in the form:

    pluginVersion:<groupId>:<artifactId>=<version>

to override a **plugin version**.

### Examples
The following overrides **junit**  to version **4.10**

    mvn install -Dversion:junit:junit=4.10

The following overrides **plexus-component-metadata**  to version **1.5.5**

    mvn install -DpluginVersion:org.codehaus.plexus:plexus-component-metadata=1.5.5

## Install
After cloning the repo, you can make the extension active for all maven builds by running the following commands:

    mvn package && sudo cp target/maven-dependency-management-extension*.jar /usr/share/maven/lib/ext/

## Uninstall
If you wish to remove the extension after installing it, run the following command:

    sudo rm -i /usr/share/maven/lib/ext/maven-dependency-management-extension*.jar

## Run Integration Tests
The following command runs the integration tests as part of the build

    mvn install -Prun-its

