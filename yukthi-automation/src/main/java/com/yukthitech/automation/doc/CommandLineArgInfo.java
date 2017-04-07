package com.yukthitech.automation.doc;

import java.lang.reflect.Field;

import com.yukthitech.utils.cli.CliArgument;

/**
 * Represents information about the command line argument.
 * 
 * @author akiran
 */
public class CommandLineArgInfo implements Comparable<CommandLineArgInfo>
{
	/**
	 * Short name of the argument.
	 */
	private String shortName;

	/**
	 * Long name of the argument.
	 */
	private String longName;

	/**
	 * Description about the plugin.
	 */
	private String description;

	/**
	 * Flag indicating if this argument is mandatory or not.
	 */
	private boolean mandatory;

	/**
	 * Java type of the argument.
	 */
	private String type;

	/**
	 * Instantiates a new command line arg info.
	 *
	 * @param field the field
	 * @param argAnnot the arg annot
	 */
	public CommandLineArgInfo(Field field, CliArgument argAnnot)
	{
		this.shortName = argAnnot.name();
		this.longName = argAnnot.longName();
		this.description = argAnnot.description();
		this.mandatory = argAnnot.required();

		this.type = field.getType().getName();
	}

	/**
	 * Gets the short name of the argument.
	 *
	 * @return the short name of the argument
	 */
	public String getShortName()
	{
		return shortName;
	}

	/**
	 * Gets the long name of the argument.
	 *
	 * @return the long name of the argument
	 */
	public String getLongName()
	{
		return longName;
	}

	/**
	 * Gets the description about the plugin.
	 *
	 * @return the description about the plugin
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Checks if is flag indicating if this argument is mandatory or not.
	 *
	 * @return the flag indicating if this argument is mandatory or not
	 */
	public boolean isMandatory()
	{
		return mandatory;
	}

	/**
	 * Gets the java type of the argument.
	 *
	 * @return the java type of the argument
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Compare to.
	 *
	 * @param o the o
	 * @return the int
	 */
	@Override
	public int compareTo(CommandLineArgInfo o)
	{
		return shortName.compareTo(o.shortName);
	}
}
