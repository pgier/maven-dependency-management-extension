package org.jboss.maven.extension.dependency.modelmodifier.versionoverride;

public class VersionOverrideInfo
{

    private String groupID;

    private String artifactID;

    private String version;

    private boolean overriden;

    public VersionOverrideInfo( String groupID, String artifactID, String version )
    {
        this.groupID = groupID;
        this.artifactID = artifactID;
        this.version = version;
        this.overriden = false;
    }

    public String getGroupID()
    {
        return groupID;
    }

    public String getArtifactID()
    {
        return artifactID;
    }

    public String getVersion()
    {
        return version;
    }

    public boolean isOverriden()
    {
        return overriden;
    }

    public void setOverriden( boolean overriden )
    {
        this.overriden = overriden;
    }

    public String toString()
    {
        return ""; // TODO
    }

}
