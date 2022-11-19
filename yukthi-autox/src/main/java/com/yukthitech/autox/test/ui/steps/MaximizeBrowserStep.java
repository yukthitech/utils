package com.yukthitech.autox.test.ui.steps;

import org.openqa.selenium.WebDriver.Window;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.config.SeleniumPluginSession;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Simulates the click event on the specified button.
 * @author akiran
 */
@Executable(name = "uiMaximizeBrowser", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Maximizes the current browser window.")
public class MaximizeBrowserStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		Window window = seleniumSession.getWebDriver(driverName).manage().window();
		
		exeLogger.debug("Maximizing the browser window. Before maximize browser details are: [Position: {}, Size: {}]",
				window.getPosition(), window.getSize());
		
		window.maximize();
		
		exeLogger.debug("Waiting for 5 Sec for maximize to take affect");
		
		try
		{
			Thread.sleep(5000);
		}catch(Exception ex)
		{}
		
		exeLogger.debug("Post maximizing the browser window browser details are: [Position: {}, Size: {}]",
				window.getPosition(), window.getSize());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Maximize Browser");
		return builder.toString();
	}

}
