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
package com.yukthitech.autox.debug.server;

import java.io.Serializable;

/**
 * Handler to handle async data received from client.
 * @author akiran
 */
public interface IServerDataHandler<D extends Serializable>
{
	/**
	 * Should return the type of data this handler can handle.
	 * @return
	 */
	public Class<D> getSupportedDataType();
	
	/**
	 * Invoked when a data is received from client.
	 * @param data
	 */
	public void processData(D data);
}
