package com.yukthitech.utils.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents command line options meta information. Which can be further be used to parse
 * beans from command line arguments.
 * 
 * @author akiran
 */
public class CommandLineOptions
{
	/**
	 * Command line parser to parse input arguments.
	 */
	private static CommandLineParser commandLineParser = new DefaultParser();
	
	/**
	 * Formatter to fetch help information.
	 */
	private static HelpFormatter helpFormatter = new HelpFormatter();
	
	/**
	 * Source from which option was obtained.
	 * @author akiran
	 */
	private static class OptionDetails
	{
		/**
		 * Type in which this option was found.
		 */
		private Class<?> type;
		
		/**
		 * Field on which option was defined.
		 */
		private Field field;
		
		/**
		 * Flag indicating if this is required option or not.
		 */
		private boolean required;
		
		/**
		 * Target option.
		 */
		private Option option;

		/**
		 * Instantiates a new option details.
		 *
		 * @param type the type
		 * @param field the field
		 * @param required the required
		 * @param option the option
		 */
		public OptionDetails(Class<?> type, Field field, boolean required, Option option)
		{
			this.type = type;
			this.field = field;
			this.required = required;
			this.option = option;
		}
	}
	
	/**
	 * Full list of options.
	 */
	private Options options = new Options();
	
	/**
	 * Name of the option to the source where option is specified.
	 */
	private Map<String, OptionDetails> nameToDetails = new HashMap<String, CommandLineOptions.OptionDetails>();
	
	/**
	 * Bean types involved.
	 */
	private Set<Class<?>> beanTypes = new HashSet<Class<?>>();
	
	/**
	 * Checks if the specified bean type is already loaded.
	 * @param beanType type to check
	 * @return true if loaded
	 */
	public boolean isBeanTypeLoaded(Class<?> beanType)
	{
		return beanTypes.contains(beanType);
	}
	
	/**
	 * Adds specified option with source details.
	 * @param type source type
	 * @param field source field
	 * @param option option to add.
	 */
	public void addOption(Class<?> type, Field field, Option option, boolean required)
	{
		if(nameToDetails.containsKey(option.getOpt()))
		{
			OptionDetails existingDetails = nameToDetails.get(option.getOpt());
			
			throw new InvalidStateException("Duplicate argument with name '{}' configuration encountered [{}.{}, {}.{}]", 
				option.getOpt(), type.getName(), field.getName(),
				existingDetails.type.getName(), existingDetails.field.getName()
			);
		}

		beanTypes.add(type);
		
		options.addOption(option);
		nameToDetails.put(option.getOpt(), new OptionDetails(type, field, required, option));
	}
	
	/**
	 * Should be used when options were configured using single bean type. Bean of configured type will
	 * be built, and properties will be to that bean properties and result will be returned.
	 * @param args Args to be parsed as target bean
	 * @return Bean of configured type
	 * @throws MissingArgumentException Thrown if any mandatory argument is not specified
	 */
	public Object parseBean(String... args) throws MissingArgumentException, UnrecognizedOptionException
	{
		if(beanTypes.size() > 1)
		{
			throw new InvalidStateException("Single bean parsing is requested when multiples beans are involved. Beans involved: {}", beanTypes);
		}
		
		if(beanTypes.isEmpty())
		{
			return null;
		}
		
		Map<Class<?>, Object> beans = parseBeans(args);
		return beans.values().iterator().next();
	}
	
	/**
	 * Bean of configured types will be built, and properties will be to that bean properties and results will be returned as map.
	 * Key would be configured bean type and value would be corresponding bean type instance mapped with properties from command line 
	 * arguments.
	 * @param args Args to be parsed as target beans
	 * @return Beans of configured types as map
	 * @throws MissingArgumentException Thrown if any mandatory argument is not specified
	 */
	public Map<Class<?>, Object> parseBeans(String... args) throws MissingArgumentException, UnrecognizedOptionException
	{
		Map<Class<?>, Object> beans = new HashMap<Class<?>, Object>();
		
		Object instance = null;
		OptionDetails optionDetails = null;
		String value = null;
		
		try
		{
			CommandLine commandLine = commandLineParser.parse(options, args);
			
			for(String name : nameToDetails.keySet())
			{
				optionDetails = nameToDetails.get(name);
				instance = beans.get(optionDetails.type);
				
				if(instance == null)
				{
					instance = optionDetails.type.newInstance();
					beans.put(optionDetails.type, instance);
				}
				
				
				if( boolean.class.equals(optionDetails.field.getType()) || Boolean.class.equals(optionDetails.field.getType()) )
				{
					value = "" + commandLine.hasOption(name);
				}
				else
				{
					value = commandLine.getOptionValue(name);
				}
				
				if(value == null && optionDetails.required)
				{
					if(optionDetails.option.getLongOpt() != null)
					{
						throw new MissingArgumentException("Required command line argument '-{}' (--{}) is not specified.", 
							optionDetails.option.getOpt(), optionDetails.option.getLongOpt());
					}
					else
					{
						throw new MissingArgumentException("Required command line argument '-{}' is not specified.", 
								optionDetails.option.getOpt());
					}
				}
					
				BeanUtils.setProperty(instance, optionDetails.field.getName(), value);
			}
			
			return beans;
		} catch(MissingArgumentException | UnrecognizedOptionException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while loading bean of from command line arguments");
		}
	}
	
	/**
	 * Fetches help information about command line options.
	 * @param commandSyntax Basic command syntax
	 * @return Help info
	 */
	public String fetchHelpInfo(String commandSyntax)
	{
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		
		helpFormatter.printHelp(printWriter, Integer.MAX_VALUE, commandSyntax, "Arguments:", options, 5, 10, null);
		
		printWriter.flush();
		writer.flush();
		
		return writer.toString();
	}
	
}
