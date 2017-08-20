package com.yukthitech.autox;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Base abstract class for steps.
 * @author akiran
 */
public abstract class AbstractStep implements IStep, Validateable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Flag indicating if logging has to be disabled for current step.
	 */
	@Param(description = "Flag indicating if logging has to be disabled for current step. Default: false", required = false)
	private boolean disableLogging = false;
	
	@Override
	public IStep clone()
	{
		try
		{
			return (IStep) super.clone();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating clone copy of current step", ex);
		}
	}
	
	@Override
	public void validate() throws ValidateException
	{}
	
	/**
	 * Sets the flag indicating if logging has to be disabled for current step.
	 *
	 * @param disableLogging the new flag indicating if logging has to be disabled for current step
	 */
	public void setDisableLogging(boolean disableLogging)
	{
		this.disableLogging = disableLogging;
	}
	
	@Override
	public boolean isLoggingDisabled()
	{
		return disableLogging;
	}
}
