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
package com.yukthitech.jexpr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Helps in customizing resource/file loading for JEL engine.
 */
public interface IContentLoader
{
	public default String loadResource(String resource) throws IOException
	{
		return IOUtils.resourceToString((String) resource, Charset.defaultCharset());
	}

	public default String loadFile(String file) throws IOException
	{
		return FileUtils.readFileToString(new File((String)file), Charset.defaultCharset());
	}
}
