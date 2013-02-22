package org.jboss.maven.extension.dependency.util;

public class VersionOverride
{
	
	private String groupID;
	
	private String artifactID;
	
	private String version;
	
	private boolean overriden;
	
	public VersionOverride( String groupID, String artifactID, String version )
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
	
	public void setOverriden (boolean overriden)
	{
		this.overriden = overriden;
	}
	
	public String toString()
	{
		return ""; //TODO
	}

}
