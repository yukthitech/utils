package com.yukthitech.autox.common;

import com.yukthitech.utils.exceptions.UtilsException;

/**
 * Base class for exceptions which are used for providing information.
 * And when handling, expected not to be wrapped by other runtime exceptions, instead should
 * be used at high level to display infomration in proper format.
 * 
 * @author akranthikiran
 */
public class AutoxInfoException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	public AutoxInfoException(String message, Object... args)
	{
		super(message, args);
	}
}
