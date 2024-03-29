/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
