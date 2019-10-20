package com.yukthitech.autox.ide.services;

import com.yukthitech.autox.ide.model.IdeState;

/**
 * Event that occurs after ide is started.
 * @author akiran
 */
public class IdeStartedEvent implements IIdeEvent
{
	/**
	 * State of ide to be persisted.
	 */
	private IdeState ideState;

	public IdeStartedEvent(IdeState ideState)
	{
		this.ideState = ideState;
	}
	
	/**
	 * Gets the state of ide to be persisted.
	 *
	 * @return the state of ide to be persisted
	 */
	public IdeState getIdeState()
	{
		return ideState;
	}
}
