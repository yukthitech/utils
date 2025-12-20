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

import com.yukthitech.utils.fmarker.annotaion.FmParam;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

public class TestMethods
{
	@FreeMarkerMethod(
			description = "Method to check if value is null",
			returnDescription = "True if not null."
			)
	public static boolean isNullTest(
			@FmParam(name = "value", description = "Value to be checked") Object value)
	{
		return (value != null);
	}

	@FreeMarkerMethod(
			description = "Method to throw error",
			returnDescription = ""
			)
	public static boolean errorMethod(
			@FmParam(name = "value", description = "Value to be checked") Object value)
	{
		throw new IllegalStateException("Test error");
	}

}
