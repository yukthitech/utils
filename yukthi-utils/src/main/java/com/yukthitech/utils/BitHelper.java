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
package com.yukthitech.utils;

/**
 * Helper to access bits.
 * @author akiran
 */
public class BitHelper
{
	/**
	 * Sets the flag bit with specified falg value.
	 * @param flags flags in which bit needs to be set
	 * @param flag flag bit to set
	 * @param value value to set
	 * @return resultant flags
	 */
	public static int setFlagValue(int flags, int flag, boolean value)
	{
		if(value)
		{
			return setFlag(flags, flag);
		}
		
		return unsetFlag(flags, flag);
	}
	
	/**
	 * Sets the flag in the flags.
	 * @param flags full flags integer
	 * @param flag flag to set
	 * @return flags after setting flag.
	 */
	public static int setFlag(int flags, int flag)
	{
		return (flags | flag);
	}

	/**
	 * Unsets the flag in the flags.
	 * @param flags full flags integer
	 * @param flag flag to unset
	 * @return flags after unsetting flag.
	 */
	public static int unsetFlag(int flags, int flag)
	{
		return (flags & (~flag));
	}

	/**
	 * Fetches flag indicating if flag is set or not.
	 * @param flags full flags integer
	 * @param flag flag to check
	 * @return true if flag was set otherwsie false.
	 */
	public static boolean isSet(int flags, int flag)
	{
		return ((flags & flag) == flag);
	}
}
