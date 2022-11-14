package com.yukthitech.autox.test.ui.steps;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.TestCaseFailedException;

/**
 * Waits for specified conditions for specified amount of time.
 * @author akiran
 */
@Executable(name = "uiWaitForConditions", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Waits for all specified conditions to be true")
public class WaitForConditionsStep extends BaseConditions
{
	private static final long serialVersionUID = 1L;

	/**
	 * Simulates the click event on the specified button.
	 * @param context Current automation context 
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Waiting for {} conditions", conditions.size());

		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();
		
		try
		{
			for(BaseCondition condition : super.conditions)
			{
				exeLogger.debug("Waiting for condition: {}", condition);
				
				WebDriverWait wait = new WebDriverWait(driver, condition.timeOutInSec, condition.timeGapMillis);
				wait.until(condition.condition);
			}
		} catch(Exception ex)
		{
			//exeLogger.error(ex, ex.getMessage());
			throw new TestCaseFailedException(this, ex.getMessage(), ex);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Waiting For Conditions [");
		builder.append(conditions);
		builder.append("]");
		return builder.toString();
	}
}
