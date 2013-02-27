package org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride.target;

/**
 * Unfortunately the Plugin and Dependency classes don't extend or implement a common class or interface, despite having
 * many identical methods. We have to work around this to use them generically. This interface defines the methods
 * common to the classes Plugin and Dependency.
 */
public interface OverrideTarget
{
    /**
     * Which class does the class implementing this interface extend?
     * 
     * @return A class reference
     */
    public Class<?> getWrappedClass();

    public String getGroupId();

    public String getArtifactId();

    public String getVersion();

    public void setGroupId( String groupID );

    public void setArtifactId( String artifactID );

    public void setVersion( String version );
}
