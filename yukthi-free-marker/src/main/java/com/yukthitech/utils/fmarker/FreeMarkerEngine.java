/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.utils.fmarker;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.doc.FreeMarkerDirectiveDoc;
import com.yukthitech.utils.fmarker.doc.FreeMarkerMethodDoc;
import com.yukthitech.utils.fmarker.met.CollectionMethods;
import com.yukthitech.utils.fmarker.met.DateMethods;
import com.yukthitech.utils.fmarker.met.CommonDirectives;
import com.yukthitech.utils.fmarker.met.CommonMethods;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import freemarker.template.utility.DeepUnwrap;

/**
 * Free marker engine where methods can be registered as template directive or direct methods
 * that can be used in free marker templates processed by underlying configuration.
 * 
 * For ease {@link #processTemplate(String, String, Object)} can be used to process free marker templates.
 * @author akiran
 */
public class FreeMarkerEngine
{
	private static Logger logger = Logger.getLogger(FreeMarkerEngine.class.getName());
	
	private static ThreadLocal<FreeMarkerEngine> currentInstance = new ThreadLocal<>();
	
	/**
	 * Singleton configuration.
	 */
	private Configuration configuration;
	
	/**
	 * Registry of free marker methods.
	 */
	private Map<String, Method> freeMarkerMethodRegistry;
	
	/**
	 * Registry of free marker method documentations.
	 */
	private Map<String, FreeMarkerMethodDoc> freeMarkerMethodDocRegistry = new TreeMap<String, FreeMarkerMethodDoc>();
	
	/**
	 * Registry of free marker directive documentations.
	 */
	private Map<String, FreeMarkerDirectiveDoc> freeMarkerDirectiveDocRegistry = new TreeMap<>();
	
	public FreeMarkerEngine()
	{
		reset();
	}
	
	public static FreeMarkerEngine getCurrentInstance()
	{
		return currentInstance.get();
	}
	
	/**
	 * Resets this free marker engine with defaults.
	 */
	public void reset()
	{
		configuration = new Configuration(Configuration.getVersion());
		configuration.setNumberFormat("#");
		
		freeMarkerMethodRegistry = new HashMap<String, Method>();
		
		loadClass(CommonMethods.class);
		loadClass(DateMethods.class);
		loadClass(CollectionMethods.class);
		
		loadClass(CommonDirectives.class);
	}

	/**
	 * Registers the specified directive with specified template directive model.
	 * @param name name of the directive
	 * @param directive directive executor
	 */
	public void registerDirective(String name, Method method)
	{
		if(freeMarkerMethodRegistry.containsKey(name))
		{
			Method regMethod = freeMarkerMethodRegistry.get(name);
			String methodName = regMethod.getDeclaringClass().getName() + "." + regMethod.getName();
			String dirMethodName = method.getDeclaringClass().getName() + "." + method.getName();

			throw new InvalidStateException("A directive and method are found with same name - [Directive: {}(), Method: {}()]", 
					dirMethodName, methodName);
		}
		
		if(freeMarkerDirectiveDocRegistry.containsKey(name))
		{
			Method duplicateMethod = freeMarkerDirectiveDocRegistry.get(name).getMethod();
			
			String method1 = duplicateMethod.getDeclaringClass().getName() + "." + duplicateMethod.getName();
			String method2 = method.getDeclaringClass().getName() + "." + method.getName();
			
			//if duplicate method and current methods are same, ignore
			//	this can happen when same class is loaded multiple times
			if(method1.equals(method2))
			{
				logger.log(Level.FINE, String.format("Ignoring duplicate registration of directive: %s()", method1));
				return;
			}
			
			throw new InvalidStateException("Multiple free marker directives are found with same name - [{}(), {}()]", 
					method1, method2);
		}

		FreeMarkerDirectiveDoc dirDoc = new FreeMarkerDirectiveDoc(method);
		configuration.setSharedVariable(name, new DirectiveProxy(method, dirDoc));
		freeMarkerDirectiveDocRegistry.put(name, dirDoc);
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
		
		logger.log(Level.FINE, String.format("Registering method %s.%s with name - %s", method.getDeclaringClass().getName(), method.getName(), name));
		
		if(StringUtils.isBlank(name))
		{
			name = method.getName();
		}
		
		if(freeMarkerDirectiveDocRegistry.containsKey(name))
		{
			Method dirMethod = freeMarkerDirectiveDocRegistry.get(name).getMethod();
			String dirMethodName = dirMethod.getDeclaringClass().getName() + "." + dirMethod.getName();
			String methodName = method.getDeclaringClass().getName() + "." + method.getName();

			throw new InvalidStateException("A directive and method are found with same name - [Directive: {}(), Method: {}()]", 
					dirMethodName, methodName);
		}
		
		if(freeMarkerMethodRegistry.containsKey(name))
		{
			Method duplicateMethod = freeMarkerMethodRegistry.get(name);
			
			String method1 = duplicateMethod.getDeclaringClass().getName() + "." + duplicateMethod.getName();
			String method2 = method.getDeclaringClass().getName() + "." + method.getName();
			
			//if duplicate method and current methods are same, ignore
			//	this can happen when same class is loaded multiple times
			if(method1.equals(method2))
			{
				logger.log(Level.FINE, String.format("Ignoring duplicate registration of method: %s()", method1));
				return;
			}
			
			throw new InvalidStateException("Multiple free marker methods are found with same name - [{}(), {}()]", 
					method1, method2);
		}
		
		configuration.setSharedVariable(name, new MethodProxy(method, name));
		freeMarkerMethodRegistry.put(name, method);
		freeMarkerMethodDocRegistry.put(name, new FreeMarkerMethodDoc(method));
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
		
		// Prev value tracking is done, to support internal expr evaluation
		//  multiple times in single expression.
		FreeMarkerEngine prevVal = currentInstance.get();
		currentInstance.set(this);
		
		try
		{
			Template template = new Template(name, templateString, getConfiguration());

			StringWriter writer = new StringWriter();
			template.process(context, writer);

			writer.flush();
			return writer.toString();
		} catch(Exception ex)
		{
			templateString = (templateString != null && templateString.length() > 1000) ? (templateString.substring(0, 1000) + "...") : templateString;
			throw new InvalidStateException("An exception occurred while processing template: {}\nTemplate String: {}", name, templateString, ex);
		} finally
		{
			currentInstance.set(prevVal);
		}
	}

	public Collection<Method> getRegisteredMethods()
	{
		return Collections.unmodifiableCollection( freeMarkerMethodRegistry.values() );
	}
	
	public Collection<FreeMarkerMethodDoc> getRegisterMethodDocuments()
	{
		return Collections.unmodifiableCollection( freeMarkerMethodDocRegistry.values() );
	}

	public Collection<FreeMarkerDirectiveDoc> getRegisterDirectiveDocuments()
	{
		return Collections.unmodifiableCollection( freeMarkerDirectiveDocRegistry.values() );
	}

	/**
	 * Evaluates specified condition in specified context and returns the result.
	 * @param name name of condition template useful for debugging.
	 * @param condition condition to be evaluated.
	 * @param context context to be used for processing.
	 * @return result of condition evaluation.
	 */
	public boolean evaluateCondition(String name, String condition, Object context)
	{
		if("true".equalsIgnoreCase(condition))
		{
			return true;
		}

		if("false".equalsIgnoreCase(condition))
		{
			return false;
		}

		String ifCondition = String.format("<#if %s>true<#else>false</#if>", condition);
		String res = processTemplate(name, ifCondition, context);
		
		return "true".equals(res);
	}

	/**
	 * Evaluates specified condition in specified context and returns the result.
	 * @param name name of condition template useful for debugging.
	 * @param valueExpression Value expression whose value needs to be fetched.
	 * @param context context to be used for processing.
	 * @return result fetched value.
	 */
	public Object fetchValue(String name, String valueExpression, Object context)
	{
		try
		{
			String collectorExpr = String.format("${__fmarker_collect(%s)}", valueExpression);
			processTemplate(name, collectorExpr, context);
			
			
			Object res = CommonMethods.getCollectedValue();
			
			if(res instanceof TemplateModel)
			{
				res = DeepUnwrap.unwrap((TemplateModel)res);
			}

			//return collected value
			return res;
		} catch(Exception ex)
		{
			throw new InvalidStateException("Template processing resulted in error.\n\tName: {}\n\tError: {}\n\tTemplate: {}", 
					name, CommonUtils.getRootCauseMessages(ex), valueExpression, ex);
		}
	}
}

