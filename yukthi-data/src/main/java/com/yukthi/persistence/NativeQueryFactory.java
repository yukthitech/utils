/*
 * The MIT License (MIT)
 * Copyright (c) 2016 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.persistence;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthi.ccg.xml.XMLBeanParser;
import com.yukthi.persistence.freemarker.TrimDirective;
import com.yukthi.utils.exceptions.InvalidArgumentException;
import com.yukthi.utils.exceptions.InvalidStateException;

import freemarker.template.Configuration;
import freemarker.template.Template;

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
	 * Map to maintain query templates
	 */
	private Map<String, Template> templateMap = new HashMap<>();
	
	/**
	 * Configuration for free marker
	 */
	private Configuration configuration = new Configuration();
	
	public NativeQueryFactory()
	{
		configuration.setSharedVariable("trim", new TrimDirective());
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
			throw new InvalidArgumentException(ex, "An error occurred while loading resource file - {}", resource);
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
	 * @param paramValues Collected param values that needs to be passed to query as prepared statement params
	 * @param context Context to be used to parse template into query
	 * @return Built query
	 */
	public String buildQuery(String name, List<Object> paramValues, Object context)
	{
		Template template = templateMap.get(name);
		
		//if template is not found build it from raw query
		if(template == null)
		{
			String rawQuery = queryMap.get(name);
			
			//if no query found with specified name
			if(rawQuery == null)
			{
				throw new InvalidArgumentException("No query found with specified name - {}", name);
			}
			
			//build the free marker template from raw query
			try
			{
				template = new Template(name, rawQuery, configuration);
				templateMap.put(name, template);
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while loading query template - {}", name);
			}
		}
		
		try
		{
			//process the template into query
			StringWriter writer = new StringWriter();
			template.process(context, writer);
			
			writer.flush();

			//build the final query (replacing the param expressions)
			String query = writer.toString();
			StringBuffer finalQuery = new StringBuffer();
			Matcher matcher = QUERY_PARAM_PATTERN.matcher(query);
			String property = null;
			
			while(matcher.find())
			{
				property = matcher.group(1);
				matcher.appendReplacement(finalQuery, "?");
				
				paramValues.add(PropertyUtils.getProperty(context, property));
			}
			
			matcher.appendTail(finalQuery);
			
			return finalQuery.toString();
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An exception occurred while building query: " + name);
		}
	}
}
