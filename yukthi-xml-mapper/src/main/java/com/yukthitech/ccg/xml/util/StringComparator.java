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
package com.yukthitech.ccg.xml.util;

import java.util.Comparator;

public class StringComparator implements Comparator<String>
{
	private boolean ignoreCase = true;

	public StringComparator()
	{}

	public StringComparator(boolean ignoreCase)
	{
		this.ignoreCase = ignoreCase;
	}

	public int compare(String str1, String str2)
	{
		if(str1 == str2)
			return 0;

		if(str1 == null)
			return -1;

		if(str2 == null)
			return 1;

		if(ignoreCase)
			return str1.compareToIgnoreCase(str2);

		return str1.compareTo(str2);
	}

}
