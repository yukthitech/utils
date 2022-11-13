package com.yukthitech.autox.test.ui.steps;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.IExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.test.ui.common.UiAutomationUtils;
import com.yukthitech.autox.test.ui.common.UiFreeMarkerMethods;

public abstract class AbstractPostCheckStep extends AbstractUiStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Post click the locator to be used to check for visibility. If this locator is not visible, click will be retried till this locator is visible or timeout.
	 */
	@Param(description = "Post click the locator to be used to check for visibility. If this locator is not visible, click will be retried till this locator is visible or timeout. "
			+ "Note: Polling for visibility will be done every 100 millis.", 
			sourceType = SourceType.UI_LOCATOR, required = false)
	private String postVisibilityLocator;
	
	/**
	 * Post click the locator to be used to check for invisibility. If this locator is not visible, click will be retried till this locator is visible or timeout.
	 */
	@Param(description = "Post click the locator to be used to check for hidden. If this locator is visible, click will be retried till this locator is visible or timeout. "
			+ "Note: Polling for visibility will be done every 100 millis.", 
			sourceType = SourceType.UI_LOCATOR, required = false)
	private String postHideLocator;
	
	@Param(description = "Time to wait to perform post verification in millis. Default: 2000", required = false)
	private int postVerificationDelay = 2000;

	
	public void setPostVisibilityLocator(String postVisibilityLocator)
	{
		this.postVisibilityLocator = postVisibilityLocator;
	}

	public void setPostHideLocator(String postHideLocator)
	{
		this.postHideLocator = postHideLocator;
	}
	
	public void setPostVerificationDelay(int postVerificationDelay)
	{
		this.postVerificationDelay = postVerificationDelay;
	}
	
	protected boolean isPostCheckAvailable()
	{
		return (postVisibilityLocator != null || postHideLocator != null);
	}

	protected boolean doPostCheck(AutomationContext context, IExecutionLogger exeLogger, String purpose)
	{
		if(postVisibilityLocator != null)
		{
			exeLogger.debug("{} - Performing post check, checking if specified locator is visible. Locator: {}", purpose, postVisibilityLocator);
			
			boolean isVisible = UiAutomationUtils.waitWithPoll(() ->
			{
				return UiFreeMarkerMethods.uiIsVisible(postVisibilityLocator, null);
			}, postVerificationDelay, 100);
			
			//if visible locator is not visible, return false
			if(!isVisible)
			{
				exeLogger.debug("{} - Post check failed. Specified locator is not visible yet. Locator: {}", purpose, postVisibilityLocator);
				return false;
			}
			else
			{
				exeLogger.debug("{} - Post check Successful. Specified locator is visible. Locator: {}", purpose, postVisibilityLocator);
			}
		}
		
		if(postHideLocator != null)
		{
			exeLogger.debug("{} - Performing post check, checking if specified locator is hidden. Locator: {}", purpose, postHideLocator);
			
			boolean isVisible = UiAutomationUtils.waitWithPoll(() ->
			{
				return UiFreeMarkerMethods.uiIsVisible(postHideLocator, null);
			}, postVerificationDelay, 100);
			
			//if hide locator is visible 
			if(isVisible)
			{
				exeLogger.debug("{} - Post check failed. Specified locator is not hidden yet. Locator: {}", purpose, postHideLocator);
				return false;
			}
			else
			{
				exeLogger.debug("{} - Post check Successful. Specified locator is hidden. Locator: {}", purpose, postHideLocator);
			}
		}

		return true;
	}

}
