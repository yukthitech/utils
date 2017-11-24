package com.yukthitech.persistence;

/**
 * Enumeration of actions that can be taken by {@link IDataFilter} as part of data fetching.
 * @author akiran
 */
public enum FilterAction
{
	/**
	 * Indicates current data is accepted and should be part of return.
	 */
	ACCEPT(true, false),
	
	/**
	 * Indicates current data is rejected and should not be part of return.
	 */
	REJECT(false, false),
	
	/**
	 * Indicates current data should be accepted and further records need not be processed.
	 */
	ACCEPT_AND_STOP(true, true),

	/**
	 * Indicates current data should be rejected and further records need not be processed.
	 */
	REJECT_AND_STOP(false, true);
	
	/**
	 * Flag indicating if this constant indicates current data to be accepted.  
	 */
	private boolean dataAccepted;
	
	/**
	 * Flag indicating if this constant indicates further processing needs to be stopped.
	 */
	private boolean stopProcessing;

	/**
	 * Instantiates a new filter action.
	 *
	 * @param accept the accept
	 * @param stopProcessing the stop processing
	 */
	private FilterAction(boolean accept, boolean stopProcessing)
	{
		this.dataAccepted = accept;
		this.stopProcessing = stopProcessing;
	}

	/**
	 * Gets the flag indicating if this constant indicates current data to be accepted.
	 *
	 * @return the flag indicating if this constant indicates current data to be accepted
	 */
	public boolean isDataAccepted()
	{
		return dataAccepted;
	}

	/**
	 * Gets the flag indicating if this constant indicates further processing needs to be stopped.
	 *
	 * @return the flag indicating if this constant indicates further processing needs to be stopped
	 */
	public boolean isStopProcessing()
	{
		return stopProcessing;
	}
}
