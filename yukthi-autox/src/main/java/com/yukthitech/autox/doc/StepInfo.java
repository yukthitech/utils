package com.yukthitech.autox.doc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.yukthitech.autox.ChildElement;
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
	private Map<String, ParamInfo> params = new TreeMap<>();
	
	/**
	 * Child elements of this step.
	 */
	private Map<String, ElementInfo> childElements = new TreeMap<>();
	
	/**
	 * Plugins required by this step.
	 */
	private Set<String> requiredPlugins = new TreeSet<>();
	
	/**
	 * Name to be used with hyphens.
	 */
	private String nameWithHyphens;
	
	/**
	 * Instantiates a new step info.
	 *
	 * @param stepClass the step class
	 * @param executablAnnot the executabl annot
	 */
	public StepInfo(Class<? extends IStep> stepClass, Executable executablAnnot)
	{
		setDetails(executablAnnot.name()[0], executablAnnot.message());

		this.javaType = stepClass.getName();

		loadParams(stepClass);

		for(Class<?> pluginType : executablAnnot.requiredPluginTypes())
		{
			this.requiredPlugins.add(pluginType.getSimpleName());
		}
	}
	
	protected StepInfo()
	{}
	
	protected void setDetails(String name, String description)
	{
		this.name = name;
		this.description = description;
		this.nameWithHyphens = name.replaceAll("([A-Z])", "-$1").toLowerCase();
	}

	protected void loadParams(Class<?> stepClass)
	{
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
				
				ParamInfo paramInfo = new ParamInfo(field, param); 
				this.params.put(paramInfo.getName(), paramInfo);
			}
			
			curType = curType.getSuperclass();
		}
		
		ChildElement childElement = null;
		
		for(Method method : stepClass.getMethods())
		{
			if(Modifier.isStatic(method.getModifiers()))
			{
				continue;
			}
			
			childElement = method.getAnnotation(ChildElement.class);
			
			if(childElement == null)
			{
				continue;
			}
			
			ElementInfo elemInfo = new ElementInfo(method, childElement);
			this.childElements.put(elemInfo.getName(), elemInfo);
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
	public Collection<ParamInfo> getParams()
	{
		return params.values();
	}
	
	/**
	 * Fetches param info for specified name.
	 * @param name name of param to fetch
	 * @return matching param
	 */
	public ParamInfo getParam(String name)
	{
		return params.get(name);
	}
	
	/**
	 * Gets the child elements of this step.
	 *
	 * @return the child elements of this step
	 */
	public Collection<ElementInfo> getChildElements()
	{
		return childElements.values();
	}
	
	/**
	 * Fetches child element info with specified name.
	 * @param name name of child fetch.
	 * @return matching child element.
	 */
	public ElementInfo getChildElement(String name)
	{
		return childElements.get(name);
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
	
	/**
	 * Gets the name to be used with hyphens.
	 *
	 * @return the name to be used with hyphens
	 */
	public String getNameWithHyphens()
	{
		return nameWithHyphens;
	}
	
	/**
	 * Returns flag indicating if this is executable step.
	 * @return
	 */
	public boolean isExecutable()
	{
		return true;
	}

	@Override
	public int compareTo(StepInfo o)
	{
		return name.compareTo(o.name);
	}
}
