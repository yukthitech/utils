package com.yukthitech.autox.doc;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
	private Map<String, PluginInfo> plugins = new TreeMap<>();
	
	/**
	 * List of step information.
	 */
	private Map<String, StepInfo> steps = new TreeMap<>();
	
	/**
	 * List of validation information.
	 */
	private Map<String, ValidationInfo> validations = new TreeMap<>();
	
	/**
	 * SEt of free method documentations.
	 */
	private Map<String, FreeMarkerMethodDoc> freeMarkerMethods = new TreeMap<>();
	
	/**
	 * Adds specified plugin.
	 * @param plugin plugin to add.
	 */
	public void addPlugin(PluginInfo plugin)
	{
		plugins.put(plugin.getName(), plugin);
	}
	
	/**
	 * Adds specified step.
	 * @param step step to add.
	 */
	public void addStep(StepInfo step)
	{
		steps.put(step.getName(), step);
	}
	
	/**
	 * Adds specified validation.
	 * @param validation validation to add.
	 */
	public void addValidation(ValidationInfo validation)
	{
		validations.put(validation.getName(), validation);
	}
	
	/**
	 * Gets the list of plugin's information.
	 *
	 * @return the list of plugin's information
	 */
	public Collection<PluginInfo> getPlugins()
	{
		return plugins.values();
	}

	/**
	 * Gets the list of step information.
	 *
	 * @return the list of step information
	 */
	public Collection<StepInfo> getSteps()
	{
		return steps.values();
	}
	
	public StepInfo getStep(String name)
	{
		return this.steps.get(name);
	}

	/**
	 * Gets the list of validation information.
	 *
	 * @return the list of validation information
	 */
	public Collection<ValidationInfo> getValidations()
	{
		return validations.values();
	}
	
	public ValidationInfo getValidation(String name)
	{
		return validations.get(name);
	}

	/**
	 * Gets the sEt of free method documentations.
	 *
	 * @return the sEt of free method documentations
	 */
	public Collection<FreeMarkerMethodDoc> getFreeMarkerMethods()
	{
		return freeMarkerMethods.values();
	}

	/**
	 * Sets the sEt of free method documentations.
	 *
	 * @param freeMarkerMethods the new sEt of free method documentations
	 */
	public void setFreeMarkerMethods(Set<FreeMarkerMethodDoc> freeMarkerMethods)
	{
		for(FreeMarkerMethodDoc met : freeMarkerMethods)
		{
			this.freeMarkerMethods.put(met.getName(), met);
		}
	}
}
