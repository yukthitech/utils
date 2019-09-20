package com.yukthitech.autox.ide.services;

import com.yukthitech.autox.ide.model.IdeState;

/**
 * Event that occurs before closing the ide.
 * @author akiran
 */
public class IdeClosingEvent implements IIdeEvent
{
	/**
	 * State of ide to be persisted.
	 */
	private IdeState ideState;

	public IdeClosingEvent(IdeState ideState)
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
