package com.yukthitech.autox;

import com.yukthitech.utils.exceptions.UtilsException;

/**
 * Exception to be thrown when validation fails.
 * @author akranthikiran
 */
public class AutoxValidationException extends UtilsException
{
	private static final long serialVersionUID = 1L;

	public AutoxValidationException(IStep step, String message, Object... args)
	{
		super(buildValidationErrorMssg(step, message), args);
	}
	
	private static String buildValidationErrorMssg(IStep step, String mssgTemp)
	{
		return getStepName(step) + ": " + mssgTemp;
	}
	
	private static String getStepName(IStep step)
	{
		Executable executable = step.getClass().getAnnotation(Executable.class);
		String stepName = null;
		
		if(executable == null)
		{
			stepName = step.getClass().getSimpleName();
			stepName = stepName.replaceAll("Step$", "");
		}
		else
		{
			stepName = executable.name();
		}

		stepName = stepName.replaceAll("([A-Z])", "-$1").toLowerCase();
		
		if(stepName.startsWith("-"))
		{
			stepName = stepName.substring(1);
		}
		
		return stepName;
	}
}
