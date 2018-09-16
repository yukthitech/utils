package com.yukthitech.autox.ide.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents the state of the ide.
 * @author akiran
 */
public class IdeState implements Serializable
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default command line args if none is provided.
	 */
	private static final String DEF_CMD_ARGS[] = new String[] { "-rf", "./reports" };
	
	/**
	 * File used to persist ide state.
	 */
	private static File STATE_FILE = new File("./ide-state.dat");
	
	/**
	 * Application prop file for the application.
	 */
	private String applicationConfigFile;
	
	/**
	 * Command line arguments to be used.
	 */
	private String[] commandLineArguments;
	
	/**
	 * List of steps executed successfully.
	 */
	private List<ExecutedStep> steps = new LinkedList<>();
	
	/**
	 * Packages monitored for dynamic changes.
	 */
	private Set<String> monitoredPackages = new HashSet<>();

	/**
	 * Gets the application prop file for the application.
	 *
	 * @return the application prop file for the application
	 */
	public String getApplicationConfigFile()
	{
		return applicationConfigFile;
	}

	/**
	 * Sets the application prop file for the application.
	 *
	 * @param applicationPropertiesFile the new application prop file for the application
	 */
	public void setApplicationConfigFile(String applicationPropertiesFile)
	{
		this.applicationConfigFile = applicationPropertiesFile;
	}

	/**
	 * Gets the command line arguments to be used.
	 *
	 * @return the command line arguments to be used
	 */
	public String[] getCommandLineArguments()
	{
		return commandLineArguments;
	}

	/**
	 * Sets the command line arguments to be used.
	 *
	 * @param commandLineArguments the new command line arguments to be used
	 */
	public void setCommandLineArguments(String commandLineArguments[])
	{
		this.commandLineArguments = commandLineArguments;
	}

	/**
	 * Gets the list of steps executed successfully.
	 *
	 * @return the list of steps executed successfully
	 */
	public List<ExecutedStep> getSteps()
	{
		return steps;
	}

	/**
	 * Sets the list of steps executed successfully.
	 *
	 * @param steps the new list of steps executed successfully
	 */
	public void setSteps(List<ExecutedStep> steps)
	{
		this.steps = steps;
	}
	
	/**
	 * Add the execute steps text.
	 * @param step
	 */
	public ExecutedStep addStep(String step, String stepRtf)
	{
		ExecutedStep newStep = new ExecutedStep(step, stepRtf);
		this.steps.add(newStep);
		
		return newStep;
	}
	
	/**
	 * Removes the step.
	 *
	 * @param step the step
	 * @return true, if successful
	 */
	public boolean removeStep(ExecutedStep step)
	{
		return this.steps.remove(step);
	}
	
	/**
	 * Validates the current ide state is valid or not. If not an exception will be thrown.
	 */
	public void validate()
	{
		if(StringUtils.isEmpty(applicationConfigFile))
		{
			throw new InvalidStateException("No application configuration file is specified");
		}
		
		File configFile = new File(applicationConfigFile);
		
		if(!configFile.exists())
		{
			throw new InvalidStateException("Specified application config file does not exist: {}", configFile);
		}
		
		if(commandLineArguments == null || commandLineArguments.length == 0)
		{
			commandLineArguments = DEF_CMD_ARGS;
		}
	}
	
	/**
	 * Saves the current ide state to state file.
	 */
	public void save()
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(STATE_FILE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(this);
			
			oos.flush();
			fos.flush();
			
			oos.close();
			fos.close();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while saving ide state to file: {}\nError: {}", STATE_FILE, ex);
		}
	}
	
	/**
	 * Loads the previously persisted ide state if any.
	 * @return previously persisted ide state or null
	 */
	public static IdeState load()
	{
		if(!STATE_FILE.exists())
		{
			return null;
		}
		
		try
		{
			FileInputStream fis = new FileInputStream(STATE_FILE);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			IdeState state = (IdeState) ois.readObject();
			
			ois.close();
			fis.close();
			
			try
			{
				state.validate();
				
				if(CollectionUtils.isNotEmpty(state.getSteps()))
				{
					int maxId = 1;
					
					for(ExecutedStep step : state.getSteps())
					{
						if(step.getId() > maxId)
						{
							maxId = step.getId();
						}
					}
					
					ExecutedStep.setTrackerId(maxId + 1);
				}
				
				return state;
			}catch(Exception ex)
			{
				return null;
			}
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading state from state file: {}\nError: {}", STATE_FILE, ex);
		}
	}

	/**
	 * Gets the packages monitored for dynamic changes.
	 *
	 * @return the packages monitored for dynamic changes
	 */
	public Set<String> getMonitoredPackages()
	{
		return monitoredPackages;
	}

	/**
	 * Sets the packages monitored for dynamic changes.
	 *
	 * @param monitoredPackages the new packages monitored for dynamic changes
	 */
	public void setMonitoredPackages(Set<String> monitoredPackages)
	{
		this.monitoredPackages = monitoredPackages;
	}
	
	public int indexOf(ExecutedStep step)
	{
		return steps.indexOf(step);
	}
	
	public int getStepCount()
	{
		return steps.size();
	}
	
	public void moveStep(ExecutedStep step, int newIdx)
	{
		if(steps.remove(step))
		{
			steps.add(newIdx, step);
		}
	}
}
