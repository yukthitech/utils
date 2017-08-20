package com.yukthitech.autox.doc;

import java.util.Set;
import java.util.TreeSet;

/**
 * Represents the information required to generate documentation.
 * @author akiran
 */
public class DocInformation
{
	/**
	 * List of plugin's information.
	 */
	private Set<PluginInfo> plugins = new TreeSet<>();
	
	/**
	 * List of step information.
	 */
	private Set<StepInfo> steps = new TreeSet<>();
	
	/**
	 * List of validation information.
	 */
	private Set<ValidationInfo> validations = new TreeSet<>();
	
	/**
	 * Adds specified plugin.
	 * @param plugin plugin to add.
	 */
	public void addPlugin(PluginInfo plugin)
	{
		plugins.add(plugin);
	}
	
	/**
	 * Adds specified step.
	 * @param step step to add.
	 */
	public void addStep(StepInfo step)
	{
		steps.add(step);
	}
	
	/**
	 * Adds specified validation.
	 * @param validation validation to add.
	 */
	public void addValidation(ValidationInfo validation)
	{
		validations.add(validation);
	}
	
	/**
	 * Gets the list of plugin's information.
	 *
	 * @return the list of plugin's information
	 */
	public Set<PluginInfo> getPlugins()
	{
		return plugins;
	}

	/**
	 * Gets the list of step information.
	 *
	 * @return the list of step information
	 */
	public Set<StepInfo> getSteps()
	{
		return steps;
	}

	/**
	 * Gets the list of validation information.
	 *
	 * @return the list of validation information
	 */
	public Set<ValidationInfo> getValidations()
	{
		return validations;
	}
}
