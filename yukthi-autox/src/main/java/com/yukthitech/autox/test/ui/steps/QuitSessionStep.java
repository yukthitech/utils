package com.yukthitech.autox.test.ui.steps;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.config.SeleniumPluginSession;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Quits the driver. In order to user driver again it has to be initialized.
 * @author akiran
 */
@Executable(name = "uiQuitSession", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Quits the driver. In order to user driver again it has to be initialized.")
public class QuitSessionStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Closing current session");
		
		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		seleniumSession.getWebDriver(driverName).quit();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Quit Session");
		return builder.toString();
	}

}
