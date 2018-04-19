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
