package com.yukthitech.autox.ide.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.model.IdeState;

@Service
public class IdeStateManager
{
	private static Logger logger = LogManager.getLogger(IdeStateManager.class);
	
	private IdeState currentState;
	
	private IdeState loadState()
	{
		currentState = IdeState.load();
		return currentState;
	}
	
	public IdeState getState()
	{
		if(currentState == null)
		{
			loadState();
		}
		
		return currentState;
	}
	
	public void saveState(IdeState ideState)
	{
		logger.debug("Saving ide state..");
		ideState.save();
	}
}
