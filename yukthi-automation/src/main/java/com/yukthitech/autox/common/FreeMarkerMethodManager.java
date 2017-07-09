package com.yukthitech.autox.common;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import com.yukthi.utils.fmarker.FreeMarkerEngine;
import com.yukthi.utils.fmarker.annotaion.FreeMarkerDirective;
import com.yukthi.utils.fmarker.annotaion.FreeMarkerMethod;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.utils.exceptions.InvalidStateException;

import freemarker.template.TemplateException;

public class FreeMarkerMethodManager
{
	private static Logger logger = LogManager.getLogger(FreeMarkerMethodManager.class);
	
	/**
	 * Freemarker template config used to parse expressions.
	 */
	private static FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
	
	static
	{
		try
		{
			freeMarkerEngine.getConfiguration().setSetting("number_format", "#");
		} catch(TemplateException ex)
		{
			throw new InvalidStateException("An error occurred while init freemarker context", ex);
		}
		
		loadFreeMarkerMethods();
	}
	
	private static void loadFreeMarkerMethods()
	{
		ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.getInstance();
		Set<String> basePackages = applicationConfiguration.getBasePackages();

		if(basePackages == null)
		{
			basePackages = new HashSet<>();
		}

		basePackages.add("com.yukthitech");

		Reflections reflections = null;
		Set<Method> freeMarkerMethods = null, freeMarkerDirectiveMethods = null;
		
		Set<Class<?>> fmClasses = new HashSet<>();

		for(String pack : basePackages)
		{
			logger.debug("Scanning for free marker methods in package - {}", pack);
			reflections = new Reflections(pack, new MethodAnnotationsScanner());

			freeMarkerMethods = reflections.getMethodsAnnotatedWith(FreeMarkerMethod.class);
			freeMarkerDirectiveMethods = reflections.getMethodsAnnotatedWith(FreeMarkerDirective.class);
			
			if(freeMarkerMethods != null)
			{
				for(Method method : freeMarkerMethods)
				{
					fmClasses.add(method.getDeclaringClass());
				}
			}

			if(freeMarkerDirectiveMethods != null)
			{
				for(Method method : freeMarkerDirectiveMethods)
				{
					fmClasses.add(method.getDeclaringClass());
				}
			}
		}
		
		logger.debug("Found free marker method container classes to be: {}", fmClasses);
		
		for(Class<?> cls : fmClasses)
		{
			logger.debug("Loading free marker class: {}", cls.getName());
			freeMarkerEngine.loadClass(cls);
		}
	}

	/**
	 * Treats provided template as freemarker template and processes them. The result will be returned.
	 * @param context Automation context which would be used as freemarker context for processing.
	 * @param templateStr Template in which expressions should be replaced
	 * @return Processed string
	 */
	public static String replaceExpressions(AutomationContext context, String templateStr)
	{
		try
		{
			return freeMarkerEngine.processTemplate("template", templateStr, context);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while processing template:\n" + templateStr, ex);
		}
	}
}