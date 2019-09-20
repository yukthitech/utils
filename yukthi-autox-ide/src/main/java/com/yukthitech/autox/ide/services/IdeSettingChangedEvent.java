package com.yukthitech.autox.ide.services;

import com.yukthitech.autox.ide.model.IdeSettings;

/**
 * Invoked when ide settings are changed.
 * @author akiran
 */
public class IdeSettingChangedEvent implements IIdeEvent
{
	/**
	 * Updated ide settings.
	 */
	private IdeSettings ideSettings;

	public IdeSettingChangedEvent(IdeSettings ideSettings)
	{
		this.ideSettings = ideSettings;
	}
	
	/**
	 * Gets the updated ide settings.
	 *
	 * @return the updated ide settings
	 */
	public IdeSettings getIdeSettings()
	{
		return ideSettings;
	}
}
