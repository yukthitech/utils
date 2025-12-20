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
package com.yukthitech.transform;

import com.yukthitech.utils.exceptions.UtilsException;

/**
 * Used when an error occurs while parsing json expressions.
 * @author akiran
 */
public class TransformException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new json expression exception.
	 *
	 * @param path the path
	 * @param mssgTemplate the mssg template
	 * @param args the args
	 */
	public TransformException(String path, String mssgTemplate, Object... args)
	{
		super(String.format(mssgTemplate, args) + "\n Path: " + path, getRootCause(args));
	}
}
