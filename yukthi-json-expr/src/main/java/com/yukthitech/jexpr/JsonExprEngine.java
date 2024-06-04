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
package com.yukthitech.jexpr;

import static com.yukthitech.jexpr.JelFmarkerMethods.OBJECT_MAPPER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.utils.ExecutionUtils;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

/**
 * Json expression engine to process Json Expression Language (JEL).
 * @author akiran
 */
public class JsonExprEngine
{
	/**
	 * Key used to specify condition for an object/map inclusion.
	 */
	private static final String KEY_CONDITION = "@condition";
	
	/**
	 * Key used to specify value for the enclosing map. Useful when condition has to be specified for simple attribute.
	 */
	private static final String KEY_VALUE = "@value";
	
	/**
	 * Key used to specify value for the enclosing map when condition fails. Useful when condition has to be specified for simple attribute.
	 */
	private static final String KEY_FALSE_VALUE = "@falseValue";

	/**
	 * Internal key used to set key expression in cases where sub-map has loop expression.
	 */
	private static final String KEY_KEY_EXPRESSION = "@key-expression";

	/**
	 * Pattern used by keys to define repetition.
	 */
	private static final Pattern FOR_EACH_PATTERN = Pattern.compile("^\\@for\\-each\\((\\w+)\\)$");
	
	/**
	 * Loop condition to exclude objects being generated.
	 */
	private static final String KEY_FOR_EACH_CONDITION = "@for-each-condition";
	
	/**
	 * Pattern used by keys to set complex object (post processing) on context.
	 */
	private static final Pattern SET_PATTERN = Pattern.compile("^\\@set\\((\\w+)\\)$");
	
	/**
	 * Used to replace current map entry, with entries with entries of value map (of current entry). Mainly
	 * expected to be used with @includeResource or @includeFile. 
	 * 
	 * Note: Though param string is supported, the param itself is not in use. It is added to support multiple replacements
	 * in single map (in simple terms as key differentiators).
	 */
	private static final Pattern REPLACE_PATTERN = Pattern.compile("^\\@replace\\((\\w+)\\)$");

	/**
	 * Free marker engine for expression processing.
	 */
	private FreeMarkerEngine freeMarkerEngine;
	
	/**
	 * Conversion functionality.
	 */
	private Conversions conversions;
	
	public JsonExprEngine()
	{
		this(new FreeMarkerEngine());
	}

	public JsonExprEngine(FreeMarkerEngine freeMarkerEngine)
	{
		this.setFreeMarkerEngine(freeMarkerEngine);
	}
	
	/**
	 * Sets the free marker engine for expression processing.
	 *
	 * @param freeMarkerEngine
	 *            the new free marker engine for expression processing
	 */
	public void setFreeMarkerEngine(FreeMarkerEngine freeMarkerEngine)
	{
		if(freeMarkerEngine == null)
		{
			throw new NullPointerException("Free marker engine cannot be set to null.");
		}
		
		this.freeMarkerEngine = freeMarkerEngine;
		this.conversions = new Conversions(freeMarkerEngine);
		
		this.freeMarkerEngine.loadClass(JelFmarkerMethods.class);
	}
	
	/**
	 * Process the specified json with specified context and returns the result as json string.
	 * @param json json to process
	 * @param context context to be used
	 * @return processed json
	 */
	public String processJson(String json, Object context)
	{
		Object jsonObj = null;
		
		try
		{
			jsonObj = OBJECT_MAPPER.readValue(json, Object.class);
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while parsing json template.", ex);
		}
		
		Object res = processObject(jsonObj, toJsonExprContext(context), "");
		
		return ExecutionUtils.executeWithReturn(() -> 
		{
			return OBJECT_MAPPER.writeValueAsString(res);
		}, "An error occurred while writing json content.");
	}
	
	
	/**
	 * Process the specified json with specified context and returns the result as object (not json).
	 * @param json json to process
	 * @param context context to be used
	 * @return processed json
	 */
	public Object processJsonAsObject(String json, Object context)
	{
		Object jsonObj = null;
		
		try
		{
			jsonObj = OBJECT_MAPPER.readValue(json, Object.class);
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while parsing json template.", ex);
		}
		
		return processObject(jsonObj, toJsonExprContext(context), "");
	}
	
	/**
	 * Process the specified object with specified context and returns the result.
	 * @param object object to process
	 * @param context context to be used
	 * @return processed object
	 */
	public Object processObject(Object object, Object context)
	{
		//Convert object into json object
		String json = ExecutionUtils.executeWithReturn(() -> 
		{
			return OBJECT_MAPPER.writeValueAsString(object);
		}, "An error occurred while writing json content.");

		Object jsonObj = null;
		
		try
		{
			jsonObj = OBJECT_MAPPER.readValue(json, Object.class);
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while parsing json.", ex);
		}

		//process and return the object
		return processObject(jsonObj, toJsonExprContext(context), "");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private IJsonExprContext toJsonExprContext(Object context)
	{
		if(context instanceof Map)
		{
			return new MapJsonExprContext((Map) context);
		}

		return new PojoJsonExprContext(context);
	}

	
	/**
	 * Based on the input object type approp process method will be called.
	 * @param object input object to process
	 * @param context context to be used
	 * @param path current path where current object is found
	 * @return processed object.
	 */
	@SuppressWarnings("unchecked")
	private Object processObject(Object object, IJsonExprContext context, String path)
	{
		try
		{
			if(object instanceof List)
			{
				object = processList((List<Object>) object, context, path);
			}
			else if(object instanceof Map)
			{
				object = processMap((Map<String, Object>) object, context, path);
			}
			else if(object instanceof String)
			{
				object = conversions.processString((String) object, context, path);
			}
		} catch(JsonExpressionException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new JsonExpressionException(path, "An unhandled error occurred", ex);
		}
		
		return object;
	}
	
	/**
	 * Process the list. In case a object is found under list directly, the repetition factor
	 * will be considered for that object.
	 * @param lst list to be processed
	 * @param context context to be used
	 * @param path current path where current list is found
	 * @return processed list.
	 */
	@SuppressWarnings("unchecked")
	private List<Object> processList(List<Object> lst, IJsonExprContext context, String path)
	{
		if(!processCondition(lst, context, path))
		{
			return null;
		}
		
		List<Object> newLst = new ArrayList<Object>();
		int index = -1;
		
		//loop through input list
		for(Object elem : lst)
		{
			index++;
			
			//if the list element is map, check and process repetition approp
			if(elem instanceof Map)
			{
				List<Object> newElems = procesRepeatedElement((Map<String, Object>) elem, context, path + "[" + index + "]");
				
				//Note: because of repetition single object may result in multiple objects
				for(Object nelem: newElems)
				{
					if(nelem == null)
					{
						continue;
					}
					
					newLst.add(nelem);
				}
				
			}
			//if not an object call process object recursively
			else
			{
				Object newElem = processObject(elem, context, path + "[" + index + "]");
				
				if(newElem == null)
				{
					continue;
				}
				
				newLst.add(newElem);
			}
		}
		
		return newLst;
	}
	
	/**
	 * Process single element to check for repetition factor. And in case present repeats current object as many
	 * times as required and returns processed list.
	 * 
	 * @param mapTemplate list element to be processed, which will be used as template
	 * @param context context to be used for processing
	 * @param path path where current element is found
	 * @return processed list of values
	 */
	@SuppressWarnings("unchecked")
	private List<Object> procesRepeatedElement(Map<String, Object> mapTemplate, IJsonExprContext context, String path)
	{
		// if condition is present and evaluated to false, then return empty list 
		// which in turn would remove current object (map template) on final list
		if(!processCondition(mapTemplate, context, path))
		{
			return Collections.emptyList();
		}
		
		// check for repetition
		String patternKey = getPatternKey(mapTemplate, FOR_EACH_PATTERN);
		
		//if repetition is not present, then return processed map as a single value list
		if(patternKey == null)
		{
			return Arrays.asList(processObject(mapTemplate, context, path));
		}

		//extract attr name from for-each expression
		Matcher matcher = FOR_EACH_PATTERN.matcher(patternKey);
		matcher.matches();
		String attrName = matcher.group(1);

		//remove the pattern key, so that cloned maps will not have it
		Object valueLstExpr = mapTemplate.remove(patternKey);
		
		//fetch the value list, based on which template map has to be cloned and repeated
		List<Object> valueLst = null;
		
		if(valueLstExpr instanceof String)
		{
			valueLst = (List<Object>) ExpressionUtil.processValueExpression(freeMarkerEngine, path, "jel-valueLst-expr", (String) valueLstExpr, context);
			
			if(valueLst == null || valueLst.isEmpty())
			{
				return Collections.emptyList();
			}
		}
		else if(valueLstExpr instanceof List)
		{
			valueLst = (List<Object>) valueLstExpr;
		}
		else
		{
			
			if(valueLstExpr == null)
			{
				return Collections.emptyList();
			}

			valueLst = Arrays.asList(valueLstExpr);
		}
		
		List<Object> resLst = new ArrayList<>();
		Object cloneMap = null;
		Object processedMap = null;
		String forEachCond = (String) mapTemplate.remove(KEY_FOR_EACH_CONDITION);

		//create json of current template to create clone.
		// this is needed, as during some updates may happen to current object
		//  like removing condition etc.
		String templateJson = ExecutionUtils.executeWithErrorAndReturn(() -> 
		{
			return OBJECT_MAPPER.writeValueAsString(mapTemplate);
		}, "An error occurred while processing object for iteration");

		//loop through value list and create repetitive object from template
		// and process them
		for(Object iterVal : valueLst)
		{
			context.setValue(attrName, iterVal);
			
			//if for each condition is specified
			if(StringUtils.isNotBlank(forEachCond))
			{
				//evaluate the condition. And if condition results in false
				//  ignore current iteration object
				if(!ExpressionUtil.evaluateCondition(freeMarkerEngine, path, "for-each-condition", forEachCond, context))
				{
					continue;
				}
			}
			
			cloneMap = ExecutionUtils.executeWithErrorAndReturn(() ->
			{
				return  OBJECT_MAPPER.readValue(templateJson, Object.class);
			}, "An error occurred while processing object for iteration");
			
			processedMap = processObject(cloneMap, context, path + "{clone}");
			resLst.add(processedMap);
		}

		return resLst;
	}
	
	/**
	 * Process the map recursively and returns the processed map.
	 * @param map Map to be processed
	 * @param context context to be used
	 * @param path current path where current map is found
	 * @return processed map.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object processMap(Map<String, Object> map, IJsonExprContext context, String path)
	{
		//if condition is present and evaluated to false, then return null
		ObjectWrapper<Object> resWrapper = new ObjectWrapper<Object>();

		if(!processCondition(map, context, path))
		{
			if(processMapValue(map, context, path, resWrapper, KEY_FALSE_VALUE))
			{
				//if value expression is present, return the result value
				return resWrapper.getValue();
			}

			return null;
		}
		
		//check if value expression is present
		if(processMapValue(map, context, path, resWrapper, KEY_VALUE))
		{
			//if value expression is present, return the result value
			return resWrapper.getValue();
		}
		
		//check if current map is meant for resource loading.
		if(conversions.processMapRes(map, context, path, resWrapper))
		{
			return resWrapper.getValue();
		}
		
		//check if current map is meant for including other resource or file
		if(conversions.processInclude(map, context, path, resWrapper, this))
		{
			return resWrapper.getValue();
		}

		Map<String, Object> resMap = new LinkedHashMap<String, Object>();
		boolean setKeyPresent = false;
		
		//loop through map entries
		for(Map.Entry<String, Object> entry : map.entrySet())
		{
			//if value is map
			if(entry.getValue() instanceof Map)
			{
				Map<String, Object> submapTemplate = (Map) entry.getValue();
				
				//check if the map is having loop expression
				if(getPatternKey(submapTemplate, FOR_EACH_PATTERN) != null)
				{
					//set the current key as the key expression on the template map
					submapTemplate.put(KEY_KEY_EXPRESSION, entry.getKey());
					
					//based on loop expression generate objects which should be added to res map
					List<Map<String, Object>> processedValues = (List) procesRepeatedElement(submapTemplate, context, path + ">" + entry.getKey()); 
					
					//loop through res maps
					for(Map<String, Object> pmap : processedValues)
					{
						//for each map get newly derived key from key expression
						String pkey = (String) pmap.remove(KEY_KEY_EXPRESSION);
						
						//add processed map on result
						resMap.put(pkey, pmap);
					}
					
					//move to next entry
					continue;
				}
			}
			
			//for each entry, process the value and replace current value with processed value
			Object val = processObject(entry.getValue(), context, path + ">" + entry.getKey());
			Matcher setMatcher = SET_PATTERN.matcher(entry.getKey());
			
			//if current key is a set key
			if(setMatcher.matches())
			{
				//irrespective of value being null, if key is @set key, flag has to be set
				setKeyPresent = true;
				
				if(val != null)
				{
					context.setValue(setMatcher.group(1), val);
				}
				
				continue;
			}
			
			//if value resulted in null, ignore current entry
			if(val == null)
			{
				continue;
			}
			
			Matcher replaceMatcher = REPLACE_PATTERN.matcher(entry.getKey());
			
			if(replaceMatcher.matches())
			{
				//if result value is not map
				if(!(val instanceof Map))
				{
					throw new JsonExpressionException(path, "Value of @replace key must be a map but found: {}", val.getClass().getName());
				}
				
				Map<String, Object> valMap = (Map<String, Object>) val;
				resMap.putAll(valMap);
				continue;
			}

			//for normal key-value entry, process the key also for expressions
			String key = String.valueOf(conversions.processString(entry.getKey(), context, path + "#key"));
			resMap.put(key, val);
		}
		
		//if set key is the only key in input map
		if(setKeyPresent && map.size() == 1)
		{
			//return null, so that the current map is ignored
			return null;
		}
		
		return resMap;
	}
	
	/**
	 * Fetches key from input map matching with specified pattern.
	 * @param map map to search
	 * @param pattern pattern to check
	 * @return matching key
	 */
	private String getPatternKey(Map<String, Object> map, Pattern pattern)
	{
		for(String key : map.keySet())
		{
			if(pattern.matcher(key).matches())
			{
				return key;
			}
		}
		
		return null;
	}
	
	/**
	 * Processes the value expression under specified map using specified context. And result will be copied
	 * to specified wrapper.
	 * @param map map in which value-expression has to be checked
	 * @param context context to be used
	 * @param path path where current map is found
	 * @param value wrapper to hold the value
	 * @param keyName Key name to be used to fetch the value expression
	 * @return true if value expression is present
	 */
	private boolean processMapValue(Map<String, Object> map, IJsonExprContext context, String path, ObjectWrapper<Object> value, String keyName)
	{
		Object valueExpr = map.get(keyName);
		
		//if value is not present, remove it
		if(valueExpr == null)
		{
			map.remove(keyName);
			return false;
		}

		map.remove(keyName);
		
		//evaluate the condition and return the result
		Object res = (valueExpr instanceof String) ? 
				conversions.processString((String) valueExpr, context, path + ">" + keyName) : 
					processObject(valueExpr, context, path + ">" + keyName);
		
		res = conversions.checkForTransform(map, res, context, path);
				
		value.setValue(res);
		return true;
	}

	/**
	 * Processes the condition and returns the condition evaluation.
	 * @param map map in which condition has to be checked
	 * @param context context to be used
	 * @param path path where current map is found
	 * @return true if condition is not present or evaluate to true
	 */
	private boolean processCondition(Map<String, Object> map, IJsonExprContext context, String path)
	{
		String condition = (String) map.get(KEY_CONDITION);
			
		//if condition is not present, remove it
		if(StringUtils.isBlank(condition))
		{
			map.remove(KEY_CONDITION);
			return true;
		}

		map.remove(KEY_CONDITION);
		
		//evaluate the condition and return the result
		return ExpressionUtil.evaluateCondition(freeMarkerEngine, path, "Json-condition", condition, context);
	}

	/**
	 * Processes the condition and returns the condition evaluation.
	 * @param lst list in which condition has to be checked
	 * @param context context to be used
	 * @param path path where current map is found
	 * @return true if condition is not present or evaluate to true
	 */
	private boolean processCondition(List<Object> lst, IJsonExprContext context, String path)
	{
		//if empty list is found, evaluate condition as true
		if(CollectionUtils.isEmpty(lst))
		{
			return true;
		}
		
		Object firstElem = lst.get(0);
		
		//if empty element is not string, evaluate condition as true
		if(!(firstElem instanceof String))
		{
			return true;
		}
		
		Matcher matcher = Conversions.EXPR_PATTERN.matcher((String) firstElem);
		
		//if empty list is not expression, evaluate condition as true
		if(!matcher.matches())
		{
			return true;
		}

		String exprType = matcher.group(1);
		
		//if empty list is not condition expression, evaluate condition as true
		if(!"condition".equals(exprType))
		{
			return true;
		}

		//remove the condition element
		lst.remove(0);
		
		//extract the condition part
		String condition = matcher.group(2);
		
		//evaluate the condition and return the result
		return ExpressionUtil.evaluateCondition(freeMarkerEngine, path, "Json-condition", condition, context);
	}
}
