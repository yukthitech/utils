package com.yukthitech.autox.test.ui.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Resets the driver for usage.
 * @author akiran
 */
@Executable(name = "uiResetSession", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Resets the driver for usage.")
public class ResetSessionStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Resetting current session");
		
		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		
		seleniumConfiguration.resetDriver();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("Reset Session");
		return builder.toString();
	}

}
