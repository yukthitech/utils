package com.yukthitech.autox.doc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.IValidation;
import com.yukthitech.autox.Param;

/**
 * Represents information about validation.
 * @author akiran
 */
public class ValidationInfo implements Comparable<ValidationInfo>
{
	/**
	 * Name of the validation.
	 */
	private String name;
	
	/**
	 * Description about the validation.
	 */
	private String description;
	
	/**
	 * Java class representing this validation.
	 */
	private String javaType;
	
	/**
	 * List of params accepted by this validation.
	 */
	private Set<ParamInfo> params = new TreeSet<>();
	
	/**
	 * Plugins required by this validation.
	 */
	private Set<String> requiredPlugins = new TreeSet<>();

	/**
	 * Instantiates a new validation info.
	 *
	 * @param validationClass the step class
	 * @param executablAnnot the executabl annot
	 */
	public ValidationInfo(Class<? extends IValidation> validationClass, Executable executablAnnot)
	{
		this.name = Arrays.asList( executablAnnot.name() ).stream().collect(Collectors.joining(","));
		this.description = executablAnnot.message();
		this.javaType = validationClass.getName();
		
		Class<?> curType = validationClass;
		Param param = null;
		
		while(curType != null)
		{
			if(curType.getName().startsWith("java"))
			{
				break;
			}
			
			for(Field field : curType.getDeclaredFields())
			{
				if(Modifier.isStatic(field.getModifiers()))
				{
					continue;
				}
				
				param = field.getAnnotation(Param.class);
				
				if(param == null)
				{
					continue;
				}
				
				this.params.add( new ParamInfo(field, param) );
			}
			
			curType = curType.getSuperclass();
		}

		for(Class<?> pluginType : executablAnnot.requiredPluginTypes())
		{
			this.requiredPlugins.add(pluginType.getSimpleName());
		}
	}

	/**
	 * Gets the name of the validation.
	 *
	 * @return the name of the validation
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the description about the validation.
	 *
	 * @return the description about the validation
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Gets the java class representing this validation.
	 *
	 * @return the java class representing this validation
	 */
	public String getJavaType()
	{
		return javaType;
	}

	/**
	 * Gets the list of params accepted by this validation.
	 *
	 * @return the list of params accepted by this validation
	 */
	public Set<ParamInfo> getParams()
	{
		return params;
	}
	
	/**
	 * Gets the plugins required by this validation.
	 *
	 * @return the plugins required by this validation
	 */
	public Set<String> getRequiredPlugins()
	{
		return requiredPlugins;
	}

	@Override
	public int compareTo(ValidationInfo o)
	{
		return name.compareTo(o.name);
	}
}
