package com.yukthitech.autox.ide.services;

import com.yukthitech.autox.ide.model.IdeState;

/**
 * Event that occurs before opening the ide. And after previous state is loaded 
 * by all components/services.
 * 
 * @author akiran
 */
public class IdeOpeningEvent implements IIdeEvent
{
	/**
	 * State of ide to be persisted.
	 */
	private IdeState ideState;

	public IdeOpeningEvent(IdeState ideState)
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
