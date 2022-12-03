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
package com.yukthitech.autox.ide;

/**
 * Functionality that optionally be implemented by {@link IIdeFileManager} to support links to other files. 
 * @author akiran
 */
public interface IHyperlinkSupport
{
	/**
	 * If any hperlink is suppose to come, then 
	 * @param position
	 * @param fileContentObject
	 * @return
	 */
	public LinkWithLocation getLinkLocation(int position, Object fileContentObject);
}
