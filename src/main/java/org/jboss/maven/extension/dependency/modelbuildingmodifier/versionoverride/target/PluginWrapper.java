package org.jboss.maven.extension.dependency.modelbuildingmodifier.versionoverride.target;

import org.apache.maven.model.Plugin;

public class PluginWrapper
    implements OverrideTarget
{

    private final Plugin delegate;

    public PluginWrapper( Plugin delegate )
    {
        this.delegate = delegate;
    }

    public Class<?> getWrappedClass()
    {
        return delegate.getClass();
    }

    // All these methods have to be redefined so they are part of OverrideTarget

    public String getGroupId()
    {
        return delegate.getGroupId();
    }

    public String getArtifactId()
    {
        return delegate.getArtifactId();
    }

    public String getVersion()
    {
        return delegate.getVersion();
    }

    public void setGroupId( String groupID )
    {
        delegate.setGroupId( groupID );
    }

    public void setArtifactId( String artifactID )
    {
        delegate.setArtifactId( artifactID );
    }

    public void setVersion( String version )
    {
        delegate.setVersion( version );
    }

}
