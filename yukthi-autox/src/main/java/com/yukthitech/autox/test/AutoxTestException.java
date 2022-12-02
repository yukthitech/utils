package com.yukthitech.autox.test;

import com.yukthitech.autox.IStep;
import com.yukthitech.utils.exceptions.UtilsException;

public class AutoxTestException extends UtilsException
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Step which is throwing this exception.
	 */
	private IStep sourceStep;

	/**
	 * Instantiates a new autox exception.
	 *
	 * @param sourceStep the source step
	 * @param message the message
	 * @param args the args
	 */
	public AutoxTestException(IStep sourceStep, String message, Object... args)
	{
		super(message, args);
		this.sourceStep = sourceStep;
	}
	
	/**
	 * Gets the step which is throwing this exception.
	 *
	 * @return the step which is throwing this exception
	 */
	public IStep getSourceStep()
	{
		return sourceStep;
	}
}
