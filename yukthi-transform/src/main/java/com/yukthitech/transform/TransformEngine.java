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
package com.yukthitech.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.transform.template.TransformTemplate;
import com.yukthitech.transform.template.TransformTemplate.Expression;
import com.yukthitech.transform.template.TransformTemplate.ExpressionType;
import com.yukthitech.transform.template.TransformTemplate.ForEachLoop;
import com.yukthitech.transform.template.TransformTemplate.TransforObjectField;
import com.yukthitech.transform.template.TransformTemplate.TransformList;
import com.yukthitech.transform.template.TransformTemplate.TransformObject;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

/**
 * Json expression engine to process Json Expression Language (JEL).
 * @author akiran
 */
public class TransformEngine
{
	/**
	 * Key used to specify value for the enclosing map. Useful when condition has to be specified for simple attribute.
	 */
	private static final String KEY_VALUE = "@value";
	
	/**
	 * Key used to specify value for the enclosing map when condition fails. Useful when condition has to be specified for simple attribute.
	 */
	private static final String KEY_FALSE_VALUE = "@falseValue";

	/**
	 * Free marker engine for expression processing.
	 */
	private FreeMarkerEngine freeMarkerEngine;
	
	/**
	 * Conversion functionality.
	 */
	private Conversions conversions;
	
	public TransformEngine()
	{
		this(new FreeMarkerEngine());
	}

	public TransformEngine(FreeMarkerEngine freeMarkerEngine)
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
		
		this.freeMarkerEngine.loadClass(TransformFmarkerMethods.class);
	}
	
	/**
	 * Process the specified object with specified context and returns the result.
	 * @param template Template to use for transformation
	 * @param context context to be used
	 * @return processed object
	 */
	public Object process(TransformTemplate template, Object context)
	{
		//process and return the object
		return processObject(template.getRoot(), toJsonExprContext(context), "");
	}
	
	public String processAsString(TransformTemplate template, Object context)
	{
		//process and return the object
		Object res = processObject(template.getRoot(), toJsonExprContext(context), "");
		return template.getTemplateFactory().formatObject(res);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ITransformContext toJsonExprContext(Object context)
	{
		if(context instanceof ITransformContext)
		{
			return (ITransformContext) context;
		}
		
		if(context instanceof Map)
		{
			return new MapExprContext((Map) context);
		}

		return new PojoExprContext(context);
	}

	
	/**
	 * Based on the input object type approp process method will be called.
	 * @param object input object to process
	 * @param context context to be used
	 * @param path current path where current object is found
	 * @return processed object.
	 */
	Object processObject(Object object, ITransformContext context, String path)
	{
		try
		{
			if(object instanceof TransformList)
			{
				object = processList((TransformList) object, context);
			}
			else if(object instanceof TransformObject)
			{
				object = processMap((TransformObject) object, context);
			}
			else if(object instanceof Expression)
			{
				object = conversions.processExpression((Expression) object, context, path);
			}
		} catch(TransformException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new TransformException(path, "An unhandled error occurred", ex);
		}
		
		return object;
	}
	
	/**
	 * Process the list. In case a object is found under list directly, the repetition factor
	 * will be considered for that object.
	 * @param lst list to be processed
	 * @param context context to be used
	 * @return processed list.
	 */
	private List<Object> processList(TransformList lst, ITransformContext context)
	{
		if(!processCondition(lst, context))
		{
			return null;
		}
		
		List<Object> newLst = new ArrayList<Object>();
		int index = -1;
		String path = lst.getPath();
		
		//loop through input list
		for(Object elem : lst.getObjects())
		{
			index++;
			
			//if the list element is map, check and process repetition approp
			if(elem instanceof TransformObject)
			{
				List<Object> newElems = procesRepeatedElement((TransformObject) elem, context, null, path + "[" + index + "]");
				
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
				Object newElem = processObject(elem, context, lst.getPath() + "[" + index + "]");
				
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
	 * @return processed list of values
	 */
	@SuppressWarnings("unchecked")
	private List<Object> procesRepeatedElement(TransformObject transformObject, ITransformContext context, Expression nameExpression, String path)
	{
		// if condition is present and evaluated to false, then return empty list 
		// which in turn would remove current object (map template) on final list
		if(!processCondition(transformObject, context))
		{
			return Collections.emptyList();
		}
		
		// check for repetition
		ForEachLoop forEachLoop = transformObject.getForEachLoop();

		//if repetition is not present, then return processed map as a single value list
		if(forEachLoop == null)
		{
			return Arrays.asList(processObject(transformObject, context, path));
		}

		//extract attr name from for-each expression
		String loopVariable = forEachLoop.getLoopVariable();

		//remove the pattern key, so that cloned maps will not have it
		Object valueLstExpr = forEachLoop.getListExpression();
		
		//fetch the value list, based on which template map has to be cloned and repeated
		List<Object> valueLst = null;
		
		if(valueLstExpr instanceof String)
		{
			valueLst = (List<Object>) ExpressionUtil.processValueExpression(freeMarkerEngine, transformObject.getPath(), "jel-valueLst-expr", (String) valueLstExpr, context);
			
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
		Object processedMap = null;
		String forEachCond = forEachLoop.getCondition();

		//loop through value list and create repetitive object from template
		// and process them
		for(Object iterVal : valueLst)
		{
			context.setValue(loopVariable, iterVal);
			
			//if for each condition is specified
			if(StringUtils.isNotBlank(forEachCond))
			{
				//evaluate the condition. And if condition results in false
				//  ignore current iteration object
				if(!ExpressionUtil.evaluateCondition(freeMarkerEngine, transformObject.getPath(), "for-each-condition", forEachCond, context))
				{
					continue;
				}
			}
			
			processedMap = processObject(transformObject, context, path + "{clone}");
			
			// for maps add name-value entry as a list object
			if(nameExpression != null)
			{
				String name = "" + conversions.processExpression(nameExpression, context, path + "#name");
				resLst.add(new NameValueEntry(name, processedMap));
			}
			else
			{
				resLst.add(processedMap);
			}
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
	private Object processMap(TransformObject transformObject, ITransformContext context)
	{
		//if condition is present and evaluated to false, then return null
		ObjectWrapper<Object> resWrapper = new ObjectWrapper<Object>();

		if(!processCondition(transformObject, context))
		{
			if(processMapValue(transformObject, KEY_FALSE_VALUE, 
				transformObject.getFalseValue(), context, resWrapper))
			{
				//if value expression is present, return the result value
				return resWrapper.getValue();
			}

			return null;
		}
		
		//check if value expression is present
		if(processMapValue(transformObject, KEY_VALUE, 
			transformObject.getValue(), context, resWrapper))
		{
			//if value expression is present, return the result value
			return resWrapper.getValue();
		}
		
		//check if current map is meant for resource loading.
		if(conversions.processMapRes(transformObject, context, resWrapper, this))
		{
			return resWrapper.getValue();
		}
		
		//check if current map is meant for including other resource or file
		if(conversions.processInclude(transformObject, context, resWrapper, this))
		{
			return resWrapper.getValue();
		}

		Map<String, Object> resMap = new LinkedHashMap<String, Object>();
		boolean setKeyPresent = false;
		String path = transformObject.getPath();

		//loop through map entries
		for(TransforObjectField field : transformObject.getFields())
		{
			String fieldName = field.getName();

			//if value is map
			if(field.getValue() instanceof TransformObject)
			{
				TransformObject submapTemplate = (TransformObject) field.getValue();
				
				//check if the map is having loop expression
				if(submapTemplate.getForEachLoop() != null)
				{
					// build name expression
					Expression nameExpression = field.getNameExpression();
					
					if(nameExpression == null)
					{
						nameExpression = new Expression(ExpressionType.STRING, fieldName);
					}
					
					//based on loop expression generate objects which should be added to res map
					List<NameValueEntry> processedValues = (List) procesRepeatedElement(submapTemplate, context, nameExpression, path + ">" + fieldName); 
					
					//loop through res maps
					for(NameValueEntry nameValEntry : processedValues)
					{
						//add processed map on result
						resMap.put(nameValEntry.getName(), nameValEntry.getValue());
					}
					
					//move to next entry
					continue;
				}
			}
			
			//for each entry, process the value and replace current value with processed value
			Object val = processObject(field.getValue(), context, transformObject.getPath() + ">" + field.getName());
			
			//if current key is a set key
			if(field.getAttributeName() != null)
			{
				//irrespective of value being null, if key is @set key, flag has to be set
				setKeyPresent = true;
				// if val is null, previous value will be removed (replaced by null)
				context.setValue(field.getAttributeName(), val);
				continue;
			}
			
			//if value resulted in null, ignore current entry
			if(val == null)
			{
				continue;
			}
			
			if(field.isReplaceEntry())
			{
				//if result value is not map
				if(!(val instanceof Map))
				{
					throw new TransformException(path, "Value of @replace key must be a map but found: {}", val.getClass().getName());
				}
				
				Map<String, Object> valMap = (Map<String, Object>) val;
				resMap.putAll(valMap);
				continue;
			}

			//for normal key-value entry, process the key also for expressions
			String key = field.getName();

			if(field.getNameExpression() != null)
			{
				key = (String) conversions.processExpression(field.getNameExpression(), context, path + ">" + fieldName + "#key");
			}

			resMap.put(key, val);
		}
		
		//if set key is the only key in input map
		if(setKeyPresent && resMap.isEmpty())
		{
			//return null, so that the current map is ignored
			return null;
		}
		
		return resMap;
	}
	
	/**
	 * Processes the value expression under specified map using specified context. And result will be copied
	 * to specified wrapper.
	 * @param transformObject transform object in which value-expression has to be checked
	 * @param keyName key name to be used to fetch the value expression
	 * @param valueExpr value expression to be evaluated
	 * @param context context to be used
	 * @param value wrapper to hold the value
	 * @return true if value expression is present
	 */
	private boolean processMapValue(TransformObject transformObject, String keyName, Object valueExpr, ITransformContext context, ObjectWrapper<Object> value)
	{
		if(valueExpr == null)
		{
			return false;
		}
		
		//evaluate the condition and return the result
		String path = transformObject.getPath();
		Object res = processObject(valueExpr, context, path + ">" + keyName);
		
		res = conversions.checkForTransform(transformObject, res, context);
				
		value.setValue(res);
		return true;
	}

	/**
	 * Processes the condition and returns the condition evaluation.
	 * @param transformObject transform object in which condition has to be checked
	 * @param context context to be used
	 * @return true if condition is not present or evaluate to true
	 */
	private boolean processCondition(TransformObject transformObject, ITransformContext context)
	{
		String condition = transformObject.getCondition();
			
		//if condition is not present, remove it
		if(StringUtils.isBlank(condition))
		{
			return true;
		}

		//evaluate the condition and return the result
		return ExpressionUtil.evaluateCondition(freeMarkerEngine, 
			transformObject.getPath(), 
			"Json-condition", 
			condition, 
			context);
	}

	/**
	 * Processes the condition and returns the condition evaluation.
	 * @param lst list in which condition has to be checked
	 * @param context context to be used
	 * @return true if condition is not present or evaluate to true
	 */
	private boolean processCondition(TransformList transformList, ITransformContext context)
	{
		//if condition is not present, evaluate condition as true
		if(transformList.getCondition() == null)
		{
			return true;
		}
		
		//evaluate the condition and return the result
		return ExpressionUtil.evaluateCondition(
			freeMarkerEngine, 
			transformList.getPath(), 
			"Json-condition", 
			transformList.getCondition(), 
			context);
	}
}
