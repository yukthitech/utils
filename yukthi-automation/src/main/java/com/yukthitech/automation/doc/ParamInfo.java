package com.yukthitech.automation.doc;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.yukthitech.automation.Param;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Information about the parameter.
 * @author akiran
 */
public class ParamInfo implements Comparable<ParamInfo>
{
	/**
	 * Name of the parameter.
	 */
	private String name;
	
	/**
	 * Description about the parameter.
	 */
	private String description;
	
	/**
	 * Flag indicating if parameter is mandatory or not.
	 */
	private boolean mandatory;
	
	/**
	 * Type of the parameter.
	 */
	private String type;
	
	/**
	 * Instantiates a new param info.
	 *
	 * @param field the field
	 * @param paramAnnot the param annot
	 */
	public ParamInfo(Field field, Param paramAnnot)
	{
		this.name = field.getName();
		this.description = paramAnnot.description();
		this.mandatory = paramAnnot.required();
		
		Type genericType = field.getType();
		
		if(genericType instanceof Class)
		{
			type = ((Class<?>) genericType).getName();
		}
		else if(genericType instanceof ParameterizedType)
		{
			ParameterizedType parameterizedType = (ParameterizedType) genericType;
			StringBuilder builder = new StringBuilder( ((Class<?>) parameterizedType.getRawType()).getName());
			
			builder.append("<");
			
			for(Type type : parameterizedType.getActualTypeArguments())
			{
				builder.append(type.getTypeName());
			}
			
			builder.append(">");
		}
		else
		{
			throw new InvalidStateException("Unsupported field type encountered for field {}.{}", field.getDeclaringClass().getName(), field.getName());
		}
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
	 * Gets the description about the parameter.
	 *
	 * @return the description about the parameter
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Checks if is flag indicating if parameter is mandatory or not.
	 *
	 * @return the flag indicating if parameter is mandatory or not
	 */
	public boolean isMandatory()
	{
		return mandatory;
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

	@Override
	public int compareTo(ParamInfo o)
	{
		return this.name.compareTo(o.name);
	}
}
