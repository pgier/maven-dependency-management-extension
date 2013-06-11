# Maven Dependency Management Extension

A Maven core extension (lifecycle participant) which adds dependency management features to Maven to allow dependency and plugin versions to be overridden using a command line property.

If any of the extension's options are used, the results are recorded in .properties format in META-INF/maven/groupId/artifactId/, the same place that maven copies the normal pom file to. An "effective pom" representation of the post-modification pom model is also written to this directory. These actions help mitigate loss of build repeatability.

This extension is compatible with Maven 3.  It has not been tested with Maven 2.x and will likely not work correctly.

## Installation

The extension jar can be downloaded from a maven repository, or it can be built from source.  Once the jar is downloaded, it must be added to the directory "${MAVEN_HOME}/lib/ext".  The next time Maven is started, you should see a command line message showing that the extension has been installed.

    [INFO] Init Maven Dependency Management Extension 1.0.0

If you wish to remove the extension after installing it, remover the jar from the lib/ext directory:

    rm ${MAVEN_HOME}/lib/ext/maven-dependency-management-extension*.jar


## Usage

Dependency and plugin versions are overridden using command line system properties.

### Overriding dependency versions

The version of a specific dependency can be overridden using a command line property of the form "version:<groupId>:<artifactId>=<version>".

For example, the version of **junit** can be set to **4.10** using a command line property.

    mvn install -Dversion:junit:junit=4.10

Multiple version overrides can be performed using multiple command line properties.

    mvn install -Dversion:junit:junit=4.10 -Dversion:commons-logging:commons-logging=1.1.1

If a large set of versions needs to be overridden, or the dependencies of the current project need to be tested with a matching set of 
dependencies from another project, a remote dependency management pom can be specified.

    mvn install -DdependencyManagement=org.foo:my-dep-pom:1.0

This has the effect of taking the <dependencyManagement/> from the remote pom, and applying the dependency versions to the current build.
By default, all dependencies listed in the remote pom will be added to the current build.  This has the effect of overriding matching 
transitive dependencies, as well as those specified directly in the pom.  If transitive dependencies should not be overridden, the option "overrideTransitive" can be set to false.

    mvn install -DdependencyManagement=org.foo:my-dep-pom:1.0 -DoverrideTransitive=false

Multiple remote dependency management poms can be specified using a comma separated list of GAVs (groupId, artifactId, version).

### Overriding plugin versions

Plugin versions can be overridden in the pom using a similar pattern to dependencies with the format "pluginVersion:<groupId>:<artifactId>=<version>".

    mvn install -DpluginVersion:org.apache.maven.plugins:maven-compiler-plugin=3.0

To override more than one Maven plugin version, multple override properties can be specified on the command line, or a remote plugin management pom can be specified.

    mvn install -DpluginManagement=org.jboss:jboss-parent:10

This will apply all <pluginManagement/> versions from the remote pom, to the local pom.


## Building from source

You must have Maven 3 or higher installed to build the extension.  The source repository can be downloaded from github.

    git clone git://github.com/jboss/maven-dependency-management-extension.git

After cloning the repo, just run a normal maven build.

    mvn install

The extension jar is created in the target directory.

### Run Integration Tests

The following command runs the integration tests as part of the build

    mvn install -Prun-its


## Known Issues

### Plugin Extensions

The plugin management feature will override plugin versions, but this will not work on plugins which are 
configured as build extensions.

    <extensions>true</extensions>

These plugins are loaded early in the build lifecycle, before the dependency management extension takes effect.


### Intermodule dependencies

It is common in a multi-module Maven build that one module has a dependency on another module.  If the remote
dependency management pom contains overrides for the modules of the current project,  Maven will attempt
to use the override versions instead of the local versions.  In version 1.0.1 of this extension, a change was made
to cause the overrides to ignore dependencies that are in the current reactor, however this problem could still
occur when attempting to build a single module of a multi-module build. 


