package com.yukthitech.autox.test.ui.steps;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.SeleniumPlugin;

/**
 * Simulates the click event on the specified button.
 * 
 * @author akiran
 */
@Executable(name = "uiMoveMouse", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Moves the mouse to specified target and optionally clicks the element.")
public class MoveMouseStep extends AbstractUiStep
{
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Mouse mouse in x-direction by specified amount.
	 */
	@Param(description = "Mouse mouse in x-direction by specified amount.")
	private int xoffset;
	
	/**
	 * Mouse mouse in y-direction by specified amount.
	 */
	@Param(description = "Mouse mouse in y-direction by specified amount.")
	private int yoffset;
	
	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.trace("Moving mouse by specified offset: [x: {}, y: {}]", xoffset, yoffset);

		SeleniumPlugin seleniumConfiguration = context.getPlugin(SeleniumPlugin.class);
		WebDriver driver = seleniumConfiguration.getWebDriver();

		Actions actions = new Actions(driver);
		actions.moveByOffset(xoffset, yoffset);
		actions.build().perform();
		
		return true;
	}

	/**
	 * Sets the mouse mouse in x-direction by specified amount.
	 *
	 * @param xoffset the new mouse mouse in x-direction by specified amount
	 */
	public void setXoffset(int xoffset)
	{
		this.xoffset = xoffset;
	}

	/**
	 * Sets the mouse mouse in y-direction by specified amount.
	 *
	 * @param yoffset the new mouse mouse in y-direction by specified amount
	 */
	public void setYoffset(int yoffset)
	{
		this.yoffset = yoffset;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Move Mouse [");

		builder.append("x: ").append(xoffset);
		builder.append(", y: ").append(yoffset);

		builder.append("]");
		return builder.toString();
	}
}
