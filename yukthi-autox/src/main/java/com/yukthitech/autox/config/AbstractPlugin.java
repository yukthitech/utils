package com.yukthitech.autox.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.InvalidArgumentException;

import com.yukthitech.autox.Param;

public abstract class AbstractPlugin<AT, S extends IPluginSession> implements IPlugin<AT, S>
{
	private static Logger logger = LogManager.getLogger(AbstractPlugin.class);
	
	@Param(description = "Maximum number of sessions that can be opened simultaneously. Defaults to 10.")
	private int maxSessions = 10;
	
	private PluginCache<S> sessionCache;
	
	public void setMaxSessions(int maxSessions)
	{
		if(maxSessions < 1)
		{
			throw new InvalidArgumentException("Invalid number of max sessions specified: " + maxSessions);
		}
		
		this.maxSessions = maxSessions;
	}

	@Override
	public void initialize(AT args)
	{
		logger.debug("Creating cache with size: {}", maxSessions);
		sessionCache = new PluginCache<>(this::createSession, maxSessions);
	}

	@Override
	public S newSession()
	{
		return sessionCache.getSession();
	}
	
	@Override
	public void releaseSession(S session)
	{
		sessionCache.release(session);
	}
	
	@Override
	public void close()
	{
		if(sessionCache != null)
		{
			sessionCache.close();
		}
	}
	
	protected abstract S createSession();
}
