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

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yukthi.persistence.freemarker.ParamCollectorDirective;
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
	
	/**
	 * Directive to collect parameter values that needs to be passed during query execution
	 */
	private ParamCollectorDirective paramCollectorDirective = new ParamCollectorDirective();
	
	
	public NativeQueryFactory()
	{
		configuration.setSharedVariable("trim", new TrimDirective());
		configuration.setSharedVariable("param", paramCollectorDirective);
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
			//reset param directive
			paramCollectorDirective.reset(context);
			
			//process the template into query
			StringWriter writer = new StringWriter();
			template.process(context, writer);
			
			if(paramValues != null)
			{
				paramValues.addAll(paramCollectorDirective.getParamValues());
			}
			
			writer.flush();
			return writer.toString();
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An exception occurred while building query: " + name);
		}
	}
}
