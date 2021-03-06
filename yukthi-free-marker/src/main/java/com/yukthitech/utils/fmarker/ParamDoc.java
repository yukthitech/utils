package com.yukthitech.utils.fmarker;

import java.lang.reflect.Parameter;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.utils.fmarker.annotaion.FmParam;

/**
 * Parameter documentation.
 * @author akiran
 */
public class ParamDoc implements Comparable<ParamDoc>
{
	/**
	 * Name of the parameter.
	 */
	private String name;
	
	/**
	 * Type of the parameter.
	 */
	private String type;
	
	/**
	 * Description about the parameter.
	 */
	private String description;
	
	public ParamDoc()
	{}

	public ParamDoc(Parameter param)
	{
		FmParam fmParam = param.getAnnotation(FmParam.class);
		
		this.name = (fmParam != null && StringUtils.isNotBlank(fmParam.name())) ? fmParam.name() : param.getName();
		this.type = param.getType().getName();
		this.description = (fmParam != null) ? fmParam.description() : "";
	}

	/**
	 * Gets the name of the parameter.
	 *
	 * @return the name of the parameter
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the parameter.
	 *
	 * @param name the new name of the parameter
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the type of the parameter.
	 *
	 * @return the type of the parameter
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Sets the type of the parameter.
	 *
	 * @param type the new type of the parameter
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * Gets the description about the parameter.
	 *
	 * @return the description about the parameter
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description about the parameter.
	 *
	 * @param description the new description about the parameter
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	@Override
	public int compareTo(ParamDoc o)
	{
		return name.compareTo(o.name);
	}
}
