Maven Dependency Management Extension
=====================================

Maven extension which allows additional dependency management features such as overriding a dependency version from the command line.

Install
-------
After cloning the repo, you can make the extension active for all maven builds by running the following commands:

    mvn package && sudo cp target/maven-dependency-management-extension*.jar /usr/share/maven/lib/ext/

Uninstall
---------
If you wish to remove the extension after installing it, run the following command:

    sudo rm -i /usr/share/maven/lib/ext/maven-dependency-management-extension*.jar

