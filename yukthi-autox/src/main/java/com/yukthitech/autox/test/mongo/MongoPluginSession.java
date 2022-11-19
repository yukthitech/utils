package com.yukthitech.autox.test.mongo;

import com.yukthitech.autox.config.IPluginSession;

public class MongoPluginSession implements IPluginSession
{
	private MongoPlugin parentPlugin;
	
	public MongoPluginSession(MongoPlugin parentPlugin)
	{
		this.parentPlugin = parentPlugin;
	}
	
	@Override
	public MongoPlugin getParentPlugin()
	{
		return parentPlugin;
	}

	/**
	 * Fetches a mongo resource with specified name.
	 * @param name
	 * @return
	 */
	public MongoResource getMongoResource(String name)
	{
		return parentPlugin.getMongoResource(name);
	}

}
