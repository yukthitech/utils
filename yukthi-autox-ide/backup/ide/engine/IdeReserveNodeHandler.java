package com.yukthitech.autox.ide.engine;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.AutomationReserveNodeHandler;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.ccg.xml.reserved.NodeName;

/**
 * Custom ide reserve node handler which gives ability to reload the step types
 * dynamically.
 * @author akiran
 */
@NodeName(namePattern = ".*")
public class IdeReserveNodeHandler extends AutomationReserveNodeHandler
{
	/**
	 * Ide engine to be used.
	 */
	private IdeEngine ideEngine;
	
	private IdeEngineClassLoader ideEngineClassLoader;
	
	public IdeReserveNodeHandler(AutomationContext context, ApplicationConfiguration appConfiguraion, IdeEngine ideEngine)
	{
		super(context, appConfiguraion);
		this.ideEngine = ideEngine;
		
		ideEngineClassLoader = new IdeEngineClassLoader(ideEngine.getState());
	}
	
	public IdeEngineClassLoader getIdeEngineClassLoader()
	{
		return ideEngineClassLoader;
	}
	
	/**
	 * Reloads all the step classes.
	 */
	public void reload()
	{
		ideEngineClassLoader = new IdeEngineClassLoader(ideEngine.getState());
		super.loadStepTypes(ideEngineClassLoader);
	}
}
