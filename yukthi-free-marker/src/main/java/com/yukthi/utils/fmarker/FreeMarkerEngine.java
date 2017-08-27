package com.yukthi.utils.fmarker;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthi.utils.fmarker.directives.IndentDirective;
import com.yukthi.utils.fmarker.directives.InitCapDirective;
import com.yukthi.utils.fmarker.directives.TrimDirective;
import com.yukthitech.utils.exceptions.InvalidStateException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveModel;

/**
 * Free marker engine where methods can be registered as template directive or direct methods
 * that can be used in free marker templates processed by underlying configuration.
 * 
 * For ease {@link #processTemplate(String, String, Object)} can be used to process free marker templates.
 * @author akiran
 */
public class FreeMarkerEngine
{
	private static Logger logger = LogManager.getLogger(FreeMarkerEngine.class);
	
	/**
	 * Singleton configuration.
	 */
	private Configuration configuration = new Configuration(Configuration.getVersion());
	
	/**
	 * Registry of free marker methods.
	 */
	private Map<String, Method> freeMarkerMethodRegistry = new HashMap<String, Method>();
	
	public FreeMarkerEngine()
	{
		registerDirective("trim", new TrimDirective());
		registerDirective("indent", new IndentDirective());
		registerDirective("initcap", new InitCapDirective());
		
		loadClass(DefaultMethods.class);
	}

	/**
	 * Registers the specified directive with specified template directive model.
	 * @param name name of the directive
	 * @param directive directive executor
	 */
	public void registerDirective(String name, TemplateDirectiveModel directive)
	{
		configuration.setSharedVariable(name, directive);
	}
	
	/**
	 * Registers specified method into registry.
	 * Made into default for test cases usage.
	 * @param method Method to register.
	 */
	public void registerMethod(String name, Method method)
	{
		if(!Modifier.isStatic(method.getModifiers()) || !Modifier.isPublic(method.getModifiers()))
		{
			throw new InvalidStateException("A non-public-static Method {}.{}() is registered as freemarker-method", method.getDeclaringClass().getName(), method.getName());
		}
		
		if(void.class.equals(method.getReturnType()))
		{
			throw new InvalidStateException("A void method {}.{}() is registered as freemarker-method", method.getDeclaringClass().getName(), method.getName());
		}
		
		logger.debug("Registering method {}.{} with name - {}", method.getDeclaringClass().getName(), method.getName(), name);
		
		if(StringUtils.isBlank(name))
		{
			name = method.getName();
		}
		
		if(freeMarkerMethodRegistry.containsKey(name))
		{
			Method duplicateMethod = freeMarkerMethodRegistry.get(name);
			
			throw new InvalidStateException("Multiple free marker methods are found with same name - [{}.{}(), {}.{}()]", 
					duplicateMethod.getDeclaringClass().getName(), duplicateMethod.getName(),
					method.getDeclaringClass().getName(), method.getName());
		}
		
		configuration.setSharedVariable(name, new FreeMarkerMethodModel(method, name));
		freeMarkerMethodRegistry.put(name, method);
	}
	
	/**
	 * Loads free marker methods from specified class.
	 * @param clazz class to load.
	 */
	public void loadClass(Class<?> clazz)
	{
		MethodLoader.loadClass(clazz, this);
	}

	/**
	 * Fetches default configuration.
	 * 
	 * @return default configuration.
	 */
	public Configuration getConfiguration()
	{
		return configuration;
	}

	/**
	 * Utility method to process templates.
	 * 
	 * @param name
	 *            Name of the template, used for debugging.
	 * @param templateString
	 *            Template string to be processed.
	 * @param context
	 *            Context to be used for processing.
	 * @return Processed string.
	 */
	public String processTemplate(String name, String templateString, Object context)
	{
		if(templateString == null)
		{
			return null;
		}
		
		try
		{
			Template template = new Template(name, templateString, getConfiguration());

			StringWriter writer = new StringWriter();
			template.process(context, writer);

			writer.flush();
			return writer.toString();
		} catch(Exception ex)
		{
			throw new IllegalStateException("An exception occurred while processing template: " + name, ex);
		}
	}

}
