package com.yukthitech.autox.test.ui.steps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.config.SeleniumPlugin;
import com.yukthitech.autox.config.SeleniumPluginSession;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.ui.common.IUiConstants;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Stores the current session cookies into specified file. Helpful in storing the web stored cookies to retain for following
 * sessions.
 * 
 * @author akiran
 */
@Executable(name = "uiStoreCookies", group = Group.Ui, requiredPluginTypes = SeleniumPlugin.class, message = "Stores the current session cookies into specified file.")
public class StoreCookiesStep extends AbstractParentUiStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Path of the file where cookies should be persisted.
	 */
	@Param(description = "Path of the file where cookies should be persisted. Default: " + IUiConstants.COOKIE_FILE, required = false)
	private String path = IUiConstants.COOKIE_FILE;

	/**
	 * Sets the path of the file where cookies should be persisted.
	 *
	 * @param path the new path of the file where cookies should be persisted
	 */
	public void setPath(String path)
	{
		this.path = path;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.trace("Stroring current cookies into file: {}", path);

		SeleniumPluginSession seleniumSession = ExecutionContextManager.getInstance().getPluginSession(SeleniumPlugin.class);
		WebDriver driver = seleniumSession.getWebDriver(driverName);

		Set<Cookie> cookies = driver.manage().getCookies();
		
		if(cookies == null || cookies.isEmpty())
		{
			exeLogger.debug("No cookies found for persisting.");
			return;
		}
		
		File cookieFile = new File(path);

		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cookieFile));
			oos.writeObject(cookies);
			oos.flush();
			oos.close();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while storing cookie file: {}", cookieFile.getPath(), ex);
		}
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
		builder.append("Store Cookies [");

		builder.append("Path: ").append(path);

		builder.append("]");
		return builder.toString();
	}
}
