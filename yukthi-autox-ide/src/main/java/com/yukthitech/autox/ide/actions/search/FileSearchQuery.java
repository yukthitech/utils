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
package com.yukthitech.autox.ide.actions.search;

import java.io.File;

public class FileSearchQuery
{
	/**
	 * String to be searched.
	 */
	private String searchString;
	
	/**
	 * Folders in which search should be done.
	 */
	private File searchFolders[];
	
	/**
	 * Whether search string should be search in case sensitive way. 
	 */
	private boolean caseSensitive;
	
	/**
	 * Whether search string should be used as regular expression.
	 */
	private boolean regularExpression;
	
	/**
	 * File name pattern.
	 */
	private String fileNamePattern;
	
}
