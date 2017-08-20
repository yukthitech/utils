package com.yukthitech.autox.doc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.TreeSet;

import com.yukthitech.autox.Executable;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.Param;

/**
 * Information about a step.
 * @author akiran
 */
public class StepInfo implements Comparable<StepInfo>
{
	/**
	 * Name of the step.
	 */
	private String name;
	
	/**
	 * Description about the step.
	 */
	private String description;
	
	/**
	 * Java class representing this step.
	 */
	private String javaType;
	
	/**
	 * List of params accepted by this step.
	 */
	private Set<ParamInfo> params = new TreeSet<>();
	
	/**
	 * Plugins required by this step.
	 */
	private Set<String> requiredPlugins = new TreeSet<>();
	
	/**
	 * Instantiates a new step info.
	 *
	 * @param stepClass the step class
	 * @param executablAnnot the executabl annot
	 */
	public StepInfo(Class<? extends IStep> stepClass, Executable executablAnnot)
	{
		this.name = executablAnnot.name();
		this.description = executablAnnot.message();
		this.javaType = stepClass.getName();
		
		Class<?> curType = stepClass;
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
	 * Gets the name of the step.
	 *
	 * @return the name of the step
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the description about the step.
	 *
	 * @return the description about the step
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Gets the java class representing this step.
	 *
	 * @return the java class representing this step
	 */
	public String getJavaType()
	{
		return javaType;
	}

	/**
	 * Gets the list of params accepted by this step.
	 *
	 * @return the list of params accepted by this step
	 */
	public Set<ParamInfo> getParams()
	{
		return params;
	}
	
	/**
	 * Gets the plugins required by this step.
	 *
	 * @return the plugins required by this step
	 */
	public Set<String> getRequiredPlugins()
	{
		return requiredPlugins;
	}

	@Override
	public int compareTo(StepInfo o)
	{
		return name.compareTo(o.name);
	}
}
