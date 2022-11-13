package com.yukthitech.autox;

import java.io.File;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Base abstract class for steps.
 * @author akiran
 */
public abstract class AbstractStep implements IStep, Validateable
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Flag indicating if logging has to be disabled for current step.
	 */
	@Param(description = "Flag indicating if logging has to be disabled for current step. Default: false", required = false)
	private boolean disableLogging = false;
	
	/**
	 * Used to maintain the location of step.
	 */
	protected File location;
	
	/**
	 * Line number where this step is defined.
	 */
	protected int lineNumber = -1;
	
	@SkipParsing
	protected IStep sourceStep;
	
	@Override
	public void setSourceStep(IStep sourceStep)
	{
		this.sourceStep = sourceStep;
	}
	
	@Override
	public IStep getSourceStep()
	{
		return sourceStep;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
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
	
	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.Validateable#validate()
	 */
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
	
	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#isLoggingDisabled()
	 */
	@Override
	public boolean isLoggingDisabled()
	{
		return disableLogging;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.ILocationBased#setLocation(java.lang.String, int)
	 */
	@Override
	public void setLocation(File location, int lineNumber)
	{
		this.location = location;
		this.lineNumber = lineNumber;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#getLocation()
	 */
	@Override
	public File getLocation()
	{
		return location;
	}
	
	@Override
	public int getLineNumber()
	{
		return lineNumber;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE, true, (Class) this.getClass());
	}
}
