package com.yukthitech.autox.doc;

import java.util.List;

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
	public ValidationInfo(Class<? extends IValidation> validationClass, Executable executablAnnot, List<Example> examples)
	{
		super(validationClass, executablAnnot, examples);
	}

	public String getType()
	{
		return "Assertion";
	}
}
