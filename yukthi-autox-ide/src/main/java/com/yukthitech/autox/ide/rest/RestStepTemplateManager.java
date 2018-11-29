package com.yukthitech.autox.ide.rest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.openqa.selenium.InvalidArgumentException;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Used to manage rest step templates.
 * @author akiran
 */
@Component
public class RestStepTemplateManager
{
	private Map<String, String> templates = new HashMap<>();
	
	@PostConstruct
	private void init()
	{
		try
		{
			InputStream is = RestStepTemplateManager.class.getResourceAsStream("/rest-step-templates.xml");
			XMLBeanParser.parse(is, this);
			is.close();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading rest step templates", ex);
		}
	}
	
	public void addStepTemplate(String name, String value)
	{
		templates.put(name, value);
	}
	
	/**
	 * Generates step code for specified step name with specified context.
	 * @param stepName step name to execute
	 * @param context context to be used to process step.
	 * @return generated step code.
	 */
	public String generateStep(String stepName, Object context)
	{
		String template = templates.get(stepName);
		
		if(template == null)
		{
			throw new InvalidArgumentException("Invalid rest step name specified for template processing: " + stepName);
		}
		
		return FreeMarkerMethodManager.replaceExpressions("rest-step-template:" + stepName, context, template);
	}
}
