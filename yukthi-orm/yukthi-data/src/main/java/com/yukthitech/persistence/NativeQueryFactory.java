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
package com.yukthitech.persistence;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

/**
 * Factory of native queries. Loads the input query resources into a map and provides
 * factory method to fetch native queries based on name and context object.
 * 
 * @author akiran
 */
public class NativeQueryFactory
{
	private static Logger logger = LogManager.getLogger(NativeQueryFactory.class);
	
	/**
	 * Pattern for query parameters in queries
	 */
	private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("\\?\\{([\\w\\.]+)\\}");
	
	/**
	 * Map to maintain raw queries with expressions
	 */
	private Map<String, String> queryMap = new HashMap<>();

	/**
	 * Configuration for free marker
	 */
	private FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
	
	public NativeQueryFactory()
	{
	}
	
	/**
	 * Loads queries from specified input stream
	 * @param is Input stream to be loaded
	 */
	public void addResourceStream(InputStream is)
	{
		XMLBeanParser.parse(is, this);
	}
	
	/**
	 * Loads queries from specified resource file. Specified resource path should be accessible in classpath.
	 * @param resource Resource to be loaded
	 */
	public void addResource(String resource)
	{
		try
		{
			logger.debug("Loading native query resource: {}", resource);
			InputStream is = NativeQueryFactory.class.getResourceAsStream(resource);
			addResourceStream(is);
			is.close();
		}catch(Exception ex)
		{
			throw new InvalidArgumentException(ex, "An error occurred while loading resource file - {}\nError: {}", resource, ex);
		}
	}
	
	/**
	 * Adds list of resources to this factory.
	 * @param resources Resource files to add.
	 */
	public void setResources(List<String> resources)
	{
		for(String resource : resources)
		{
			addResource(resource);
		}
	}
	
	/**
	 * Loads queries from specified file
	 * @param file File to be loaded
	 */
	public void addFile(String file)
	{
		try
		{
			logger.debug("Loading native query file: {}", file);
			InputStream is = new FileInputStream(file);
			addResourceStream(is);
			is.close();
		}catch(Exception ex)
		{
			throw new InvalidArgumentException(ex, "An error occurred while loading file - {}", file);
		}
	}
	
	/**
	 * Adds list of specified files to this factory.
	 * @param files Files to add.
	 */
	public void setFiles(List<String> files)
	{
		for(String file : files)
		{
			addFile(file);
		}
	}
	
	/**
	 * Loads the free marker methods from specified class.
	 * @param clazz
	 */
	public void setFreeMarkerClass(Class<?> clazz)
	{
		freeMarkerEngine.loadClass(clazz);
	}

	/**
	 * Adder method to add raw query string with free marker template and expressions
	 * @param name Name of the query
	 * @param query Raw query string
	 */
	public void addQuery(String name, String query)
	{
		queryMap.put(name, query);
	}

	/**
	 * Fetches the query template with specified "name" and builds the query using specified "context".
	 * If the query uses "param" directive, corresponding values will be collected into "paramValues".
	 * 
	 * @param name Name of the query to be fetched
	 * @param outputParamValues Collected param values that needs to be passed to query as prepared statement params
	 * @param context Context to be used to parse template into query
	 * @return Built query
	 */
	public String buildQuery(String name, List<Object> outputParamValues, Object context)
	{
		String rawQuery = queryMap.get(name);
		
		//if no query found with specified name
		if(rawQuery == null)
		{
			throw new InvalidArgumentException("No query found with specified name - {}", name);
		}
		
		try
		{
			//build the final query (replacing the param expressions)
			String query = freeMarkerEngine.processTemplate(name, rawQuery, context);
			StringBuffer finalQuery = new StringBuffer();
			Matcher matcher = QUERY_PARAM_PATTERN.matcher(query);
			String property = null;
			
			while(matcher.find())
			{
				property = matcher.group(1);
				matcher.appendReplacement(finalQuery, "?");
				
				outputParamValues.add(PropertyUtils.getProperty(context, property));
			}
			
			matcher.appendTail(finalQuery);
			
			return finalQuery.toString();
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An exception occurred while building query: " + name);
		}
	}
}
