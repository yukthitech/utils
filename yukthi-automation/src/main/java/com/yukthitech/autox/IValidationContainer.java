package com.yukthitech.autox;

/**
 * Orchestrator of validations.
 * @author akiran
 */
public interface IValidationContainer
{
	/**
	 * Adds the validation to this orchestrator.
	 * @param validation Validation to be added.
	 */
	public void addValidation(IValidation validation);
}
