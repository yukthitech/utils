package com.yukthitech.autox.test.mongo;

import com.yukthitech.autox.config.AbstractPluginSession;

public class MongoPluginSession extends AbstractPluginSession<MongoPluginSession, MongoPlugin>
{
	public MongoPluginSession(MongoPlugin parentPlugin)
	{
		super(parentPlugin);
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
