package com.yukthitech.autox.config;

public abstract class AbstractPluginSession<S extends IPluginSession, P extends IPlugin<?, S>> implements IPluginSession
{
	protected P parentPlugin;

	public AbstractPluginSession(P parentPlugin)
	{
		this.parentPlugin = parentPlugin;
	}
	
	@Override
	public P getParentPlugin()
	{
		return parentPlugin;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void release()
	{
		parentPlugin.releaseSession((S) this);
	}
}
