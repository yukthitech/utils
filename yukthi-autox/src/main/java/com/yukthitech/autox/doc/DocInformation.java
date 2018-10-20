package com.yukthitech.autox.doc;

import java.util.Set;
import java.util.TreeSet;

import com.yukthitech.utils.fmarker.FreeMarkerMethodDoc;

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
	 * SEt of free method documentations.
	 */
	private Set<FreeMarkerMethodDoc> freeMarkerMethods = new TreeSet<>();
	
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

	/**
	 * Gets the sEt of free method documentations.
	 *
	 * @return the sEt of free method documentations
	 */
	public Set<FreeMarkerMethodDoc> getFreeMarkerMethods()
	{
		return freeMarkerMethods;
	}

	/**
	 * Sets the sEt of free method documentations.
	 *
	 * @param freeMarkerMethods the new sEt of free method documentations
	 */
	public void setFreeMarkerMethods(Set<FreeMarkerMethodDoc> freeMarkerMethods)
	{
		this.freeMarkerMethods = new TreeSet<>( freeMarkerMethods );
	}
}
