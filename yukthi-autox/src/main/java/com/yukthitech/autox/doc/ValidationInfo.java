package com.yukthitech.autox.doc;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.IValidation;

/**
 * Represents information about validation.
 * @author akiran
 */
public class ValidationInfo extends StepInfo
{
	/**
	 * Instantiates a new validation info.
	 *
	 * @param validationClass the step class
	 * @param executablAnnot the executabl annot
	 */
	public ValidationInfo(Class<? extends IValidation> validationClass, Executable executablAnnot)
	{
		super(validationClass, executablAnnot);
	}
}
