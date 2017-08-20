package com.yukthitech.autox;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Base abstract class for validations.
 * @author akiran
 */
public abstract class AbstractValidation implements IValidation, Validateable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Flag indicating if logging has to be disabled for current step.
	 */
	@Param(description = "Flag indicating if logging has to be disabled for current step. Default: false", required = false)
	private boolean disableLogging = false;

	/**
	 * Enables/disables current validation.\nDefault: true.
	 */
	@Param(description = "Enables/disables current validation.\nDefault: true", required = false)
	protected String enabled = "true";

	@Override
	public IValidation clone()
	{
		try
		{
			return (IValidation) super.clone();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating clone copy of current validation", ex);
		}
	}
	
	@Override
	public void validate() throws ValidateException
	{}
	
	/**
	 * Sets the enables/disables current validation.\nDefault: true.
	 *
	 * @param enabled the new enables/disables current validation
	 */
	public void setEnabled(String enabled)
	{
		this.enabled = enabled;
	}
	
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
