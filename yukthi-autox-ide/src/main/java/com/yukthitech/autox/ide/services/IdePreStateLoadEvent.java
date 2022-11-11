package com.yukthitech.autox.ide.services;

import com.yukthitech.autox.ide.model.IdeState;

/**
 * Event that occurs before state is loaded by all components.
 * This needs to be listened by components/services which needs to pre work before loading the state.
 * 
 * @author akiran
 */
public class IdePreStateLoadEvent implements IIdeEvent
{
	/**
	 * State of ide to be persisted.
	 */
	private IdeState ideState;

	public IdePreStateLoadEvent(IdeState ideState)
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
