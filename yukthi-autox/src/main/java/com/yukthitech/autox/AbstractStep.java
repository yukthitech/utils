package com.yukthitech.autox;

import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.test.IDataProvider;
import com.yukthitech.autox.test.ListDataProvider;
import com.yukthitech.autox.test.RangeDataProvider;
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
	protected String location;
	
	/**
	 * Optional data provider for the step.
	 */
	@SkipParsing
	private IDataProvider dataProvider;
	
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
	 * @see com.yukthitech.autox.IStep#setLocation(java.lang.String)
	 */
	@Override
	public void setLocation(String location)
	{
		this.location = location;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#getLocation()
	 */
	@Override
	public String getLocation()
	{
		return location;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStep#getDataProvider()
	 */
	public IDataProvider getDataProvider()
	{
		return dataProvider;
	}

	/**
	 * Sets the optional data provider for the step.
	 *
	 * @param dataProvider the new optional data provider for the step
	 */
	public void setDataProvider(IDataProvider dataProvider)
	{
		this.dataProvider = dataProvider;
	}

	/**
	 * Sets the specified list data provider as data-provider for this test case.
	 * @param dataProvider data provider to set
	 */
	public void setListDataProvider(ListDataProvider dataProvider)
	{
		this.setDataProvider(dataProvider);
	}
	
	/**
	 * Sets the specified range data provider as data-provider for this test case.
	 * @param dataProvider data provider to set
	 */
	public void setRangeDataProvider(RangeDataProvider dataProvider)
	{
		this.setDataProvider(dataProvider);
	}
}
