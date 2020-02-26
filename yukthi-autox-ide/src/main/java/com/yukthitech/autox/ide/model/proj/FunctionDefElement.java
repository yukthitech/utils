package com.yukthitech.autox.ide.model.proj;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.ide.xmlfile.Element;
import com.yukthitech.autox.test.FunctionParamDef;
import com.yukthitech.utils.CommonUtils;

/**
 * Represents function element.
 * @author akiran
 */
public class FunctionDefElement extends CodeElementContainer
{
	/**
	 * Name of function.
	 */
	private String name;
	
	/**
	 * Description of function.
	 */
	private String description;
	
	/**
	 * Return description of function.
	 */
	private String returnDescription;
	
	/**
	 * The param map.
	 */
	private Map<String, FunctionParamDef> paramMap = new HashMap<>();

	/**
	 * Instantiates a new function element.
	 *
	 * @param file the file
	 * @param element the element
	 */
	public FunctionDefElement(File file, Element element)
	{
		super(file, element.getStartLocation().getStartOffset());

		Map<String, String> valMap = element.getChildValues(CommonUtils.toSet("name", "description", "returnDescription"));
		name = valMap.get("name");
		description = valMap.get("description");
		returnDescription = valMap.get("returnDescription");
		
		List<Element> paramElements = element.getElementsWithName("paramDef");
		
		for(Element paramElem : paramElements)
		{
			valMap = paramElem.getChildValues(CommonUtils.toSet("name", "description", "required"));
			FunctionParamDef paramDef = new FunctionParamDef();
			paramDef.setName(valMap.get("name"));
			paramDef.setDescription(valMap.get("description"));
			paramDef.setRequired("true".equalsIgnoreCase(valMap.get("description")));
			
			if(StringUtils.isBlank(paramDef.getName()))
			{
				continue;
			}
			
			paramMap.put(paramDef.getName(), paramDef);
		}
	}

	/**
	 * Gets the name of function.
	 *
	 * @return the name of function
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the description of function.
	 *
	 * @return the description of function
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Gets the return description of function.
	 *
	 * @return the return description of function
	 */
	public String getReturnDescription()
	{
		return returnDescription;
	}

	/**
	 * Gets the param map.
	 *
	 * @return the param map
	 */
	public Map<String, FunctionParamDef> getParamMap()
	{
		return paramMap;
	}
}
