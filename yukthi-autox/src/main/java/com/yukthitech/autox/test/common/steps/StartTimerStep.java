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
package com.yukthitech.autox.test.common.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Starts time tracking with specified name. Stopping timer would keep elaspsed time on context which can used for logging.
 * @author akiran
 */
@Executable(name = "startTimer", group = Group.Common, message = "Starts time tracking with specified name. Stopping timer would keep elaspsed time on context which can used for logging.")
public class StartTimerStep extends AbstractStep 
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of the timer.
	 */
	@Param(description = "Name of the timer.")
	private String name;
	
	/**
	 * Sets the name of the timer.
	 *
	 * @param name the new name of the timer
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Override
	public void execute(AutomationContext context, IExecutionLogger logger) throws Exception
	{
		logger.debug("Started timer with name: {}", name);
		context.setAttribute(name + ".startTime", System.currentTimeMillis());
	}
}
