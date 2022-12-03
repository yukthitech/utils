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
package com.yukthitech.ccg.xml.writer;

/**
 * Expected to be implemented by beans which can self convert themselves into DOM element.
 * @author akranthikiran
 */
public interface IWriteableBean
{
	/**
	 * Expectes the implementation to populate attributes and subnodes of current-element specified by context.
	 * @param context
	 */
	public void writeTo(XmlWriterContext context);
}
