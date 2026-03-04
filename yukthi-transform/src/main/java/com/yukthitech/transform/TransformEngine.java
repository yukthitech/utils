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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.yukthitech.transform.event.ITransformListener;
import com.yukthitech.transform.event.TransformEvent;
import com.yukthitech.transform.event.TransformEventType;
import com.yukthitech.transform.template.ExpressionUtils;
import com.yukthitech.transform.template.TransformTemplate;
import com.yukthitech.transform.template.TransformTemplate.Expression;
import com.yukthitech.transform.template.TransformTemplate.ForEachLoop;
import com.yukthitech.transform.template.TransformTemplate.Switch;
import com.yukthitech.transform.template.TransformTemplate.SwitchCase;
import com.yukthitech.transform.template.TransformTemplate.TransformList;
import com.yukthitech.transform.template.TransformTemplate.TransformObject;
import com.yukthitech.transform.template.TransformTemplate.TransformObjectField;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;
import com.yukthitech.utils.fmarker.FreeMarkerTemplate;

/**
 * Transformation engine to process transformation templates (JSON or XML).
 * @author akiran
 */
public class TransformEngine
{
	private static final ITransformListener DUMMY_LISTENER = new ITransformListener()
	{
		public void onTransform(TransformEvent event)
		{}
	};
	
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
	 * Listener for transform events.
	 */
	private ITransformListener listener = DUMMY_LISTENER;
	
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
		this.freeMarkerEngine.loadClass(TransformFmarkerMethods.class);
	}

	public void setListener(ITransformListener listener)
	{
		this.listener = listener == null ? DUMMY_LISTENER : listener;
	}
	
	/**
	 * Process the specified object with specified context and returns the result.
	 * @param template Template to use for transformation
	 * @param context context to be used
	 * @return processed object
	 */
	public Object process(TransformTemplate template, Object context)
	{
		TransformState transformState = new TransformState(template);
		
		//process and return the object
		return processObject(template.getRoot(), toTransformContext(context), transformState);
	}
	
	public String processAsString(TransformTemplate template, Object context)
	{
		TransformState transformState = new TransformState(template);
		
		//process and return the object
		Object res = processObject(template.getRoot(), toTransformContext(context), transformState);
		return transformState.formatObject(res);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ITransformContext toTransformContext(Object context)
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
	Object processObject(Object object, ITransformContext context, TransformState transformState)
	{
		try
		{
			if(object instanceof TransformList)
			{
				object = processList((TransformList) object, context, transformState);
			}
			else if(object instanceof TransformObject)
			{
				object = processMap((TransformObject) object, context, transformState);
			}
			else if(object instanceof Expression)
			{
				object = ExpressionUtils.processExpression(freeMarkerEngine, (Expression) object, context, listener);
			}
		} catch(TransformException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new TransformException(transformState.getLocation(), "An unhandled error occurred", ex);
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
	private List<Object> processList(TransformList lst, ITransformContext context, TransformState transformState)
	{
		if(!processCondition(lst, context))
		{
			return null;
		}
		
		List<Object> newLst = new ArrayList<Object>();
		int index = -1;
		
		// this will be true, if for-each loop resulted in zero elements
		boolean encounteredNullCondition = false;
		
		//loop through input list
		for(Object elem : lst.getObjects())
		{
			index++;
			
			//if the list element is map, check and process repetition approp
			if(elem instanceof TransformObject)
			{
				List<Object> newElems = procesRepeatedElement((TransformObject) elem, context, 
					null, transformState.forIndex(index), false);
				
				if(newElems == null)
				{
					encounteredNullCondition = true;
					continue;
				}
				
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
				Object newElem = processObject(elem, context, transformState.forIndex(index));
				
				if(newElem == null)
				{
					continue;
				}
				
				newLst.add(newElem);
			}
		}
		
		/*
		 * If for-loop is present and resulted in zero elements
		 * return null, so that overall key can be removed
		 */
		if(encounteredNullCondition)
		{
			return null;
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Object> procesRepeatedElement(TransformObject transformObject, 
		ITransformContext context, Expression nameExpression, TransformState transformState, boolean forMaps)
	{
		// if condition is present and evaluated to false, then return empty list
		if(!processCondition(transformObject, context))
		{
			return Collections.emptyList();
		}
		
		// check for repetition
		ForEachLoop forEachLoop = transformObject.getForEachLoop();

		//if repetition is not present, then return processed map as a single value list
		if(forEachLoop == null)
		{
			return Arrays.asList(processObject(transformObject, context, transformState));
		}

		//extract attr name from for-each expression
		String loopVariable = forEachLoop.getLoopVariable();

		//remove the pattern key, so that cloned maps will not have it
		Object valueLstExpr = forEachLoop.getListExpression();
		
		//fetch the value list, based on which template map has to be cloned and repeated
		Collection<Object> valueLst = null;

		// in case of string process it as an expression
		if(valueLstExpr instanceof Expression)
		{
			valueLstExpr = ExpressionUtils.processExpression(freeMarkerEngine, (Expression) valueLstExpr, context, listener);
		}
		
		if(valueLstExpr == null)
		{
			valueLst = Collections.emptyList();
		}
		else if(valueLstExpr instanceof Collection)
		{
			valueLst = (Collection<Object>) valueLstExpr;
		}
		else if(valueLstExpr instanceof Map)
		{
			valueLst = ((Map) valueLstExpr).entrySet();
		}
		else
		{
			valueLst = Arrays.asList(valueLstExpr);
		}

		listener.onTransform(new TransformEvent(transformObject.getLocation(), TransformEventType.LIST_EXPRESSION_EVALUATED, valueLst));
		
		List<Object> resLst = new ArrayList<>();
		Object processedMap = null;
		FreeMarkerTemplate forEachCond = forEachLoop.getCondition();

		//loop through value list and create repetitive object from template
		// and process them
		for(Object iterVal : valueLst)
		{
			context.setValue(loopVariable, iterVal);
			
			//if for each condition is specified
			if(forEachCond != null)
			{
				boolean condResult = FreemarkerUtil.evaluateCondition(freeMarkerEngine, transformObject.getLocation(), 
					forEachCond, context);

				listener.onTransform(new TransformEvent(transformObject.getLocation(), 
					TransformEventType.FOR_EACH_CONDITION_EVALUATED, condResult));

				//evaluate the condition. And if condition results in false
				//  ignore current iteration object
				if(!condResult)
				{
					continue;
				}
			}
			
			processedMap = processObject(transformObject, context, transformState.forClone());
			
			// for maps add name-value entry as a list object
			if(forMaps)
			{
				String name = nameExpression != null 
						? "" + ExpressionUtils.processExpression(freeMarkerEngine, nameExpression, context, listener) 
						: transformObject.getName();

				listener.onTransform(new TransformEvent(transformObject.getLocation(), TransformEventType.KEY_EXPRESSION_EVALUATED, name));

				resLst.add(new NameValueEntry(name, processedMap));
			}
			else
			{
				resLst.add(processedMap);
			}
		}

		listener.onTransform(new TransformEvent(transformObject.getLocation(), TransformEventType.LOOP_EVALUATED, resLst));
		
		// if for-loop result in zero elements, return null
		if(resLst.isEmpty())
		{
			return null;
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
	private Object processMap(TransformObject transformObject, ITransformContext context, TransformState transformState)
	{
		//if condition is present and evaluated to false, then return null
		ObjectWrapper<Object> resWrapper = new ObjectWrapper<Object>();

		if(!processCondition(transformObject, context))
		{
			if(processMapValue(transformObject, KEY_FALSE_VALUE, 
				transformObject.getFalseValue(), context, transformState, resWrapper))
			{
				//if value expression is present, return the result value
				return resWrapper.getValue();
			}

			return null;
		}
		
		//check if switch statement is present
		if(processSwitch(transformObject, context, transformState, resWrapper))
		{
			return resWrapper.getValue();
		}
		
		//check if value expression is present
		if(processMapValue(transformObject, KEY_VALUE, 
			transformObject.getValue(), context, transformState, resWrapper))
		{
			//if value expression is present, return the result value
			return resWrapper.getValue();
		}
		
		//check if current map is meant for resource loading.
		if(Conversions.processMapRes(freeMarkerEngine, transformObject, context, resWrapper, this, transformState, listener))
		{
			listener.onTransform(new TransformEvent(transformObject.getLocation(), 
				TransformEventType.RESOURCE_LOADED, resWrapper.getValue()));
			return resWrapper.getValue();
		}
		
		//check if current map is meant for including other resource or file
		if(Conversions.processInclude(transformObject, context, resWrapper, this, transformState))
		{
			listener.onTransform(new TransformEvent(transformObject.getLocation(), 
				TransformEventType.INCLUDE_PROCESSED, resWrapper.getValue()));
			return resWrapper.getValue();
		}

		Object resObj = transformState.newObject(transformObject);
		boolean setKeyPresent = false;
		boolean otherEntriesAdded = false;

		//loop through map entries
		for(TransformObjectField field : transformObject.getFields())
		{
			//if value is map
			if(field.getValue() instanceof TransformObject)
			{
				TransformObject submapTemplate = (TransformObject) field.getValue();
				
				//check if the map is having loop expression
				if(submapTemplate.getForEachLoop() != null)
				{
					// build name expression
					Expression nameExpression = submapTemplate.getForEachLoop().getNameExpression();
					
					//based on loop expression generate objects which should be added to res map
					List<NameValueEntry> processedValues = (List) procesRepeatedElement(submapTemplate, context, 
						nameExpression, transformState.forField(field), true); 
					processedValues = (processedValues == null) ? Collections.emptyList() : processedValues;
					
					//loop through res maps
					for(NameValueEntry nameValEntry : processedValues)
					{
						//add processed map on result
						transformState.setField(field, resObj, nameValEntry.getName(), nameValEntry.getValue());
						otherEntriesAdded = true;
					}
					
					//move to next entry
					continue;
				}
			}
			
			//if current key is a set key
			if(field.getAttributeName() != null)
			{
				transformState.executeInAttributeMode(field, attrState -> 
				{
					//execute the child content with attr state
					Object val = processObject(field.getValue(), context, attrState);

					listener.onTransform(new TransformEvent(field.getLocation(), 
						TransformEventType.SET_VARIABLE_EVALUATED, field.getAttributeName(), val));

					// if val is null, previous value will be removed (replaced by null)
					context.setValue(field.getAttributeName(), val);
				});
				
				//irrespective of value being null, if key is @set key, flag has to be set
				setKeyPresent = true;
				continue;
			}
			
			//for each entry, process the value and replace current value with processed value
			Object val = processObject(field.getValue(), context, transformState.forField(field));

			listener.onTransform(new TransformEvent(field.getLocation(), 
				TransformEventType.VALUE_EVALUATED, field.getName(), val));

			//if value resulted in null, ignore current entry
			if(val == null)
			{
				continue;
			}
			
			if(field.isReplaceEntry())
			{
				transformState.injectReplaceEntry(field, resObj, val);

				listener.onTransform(new TransformEvent(field.getLocation(), 
					TransformEventType.REPLACE_ENTRY_EVALUATED, val));
				otherEntriesAdded = true;
				continue;
			}

			//for normal key-value entry, process the key also for expressions
			String key = field.getName();

			if(field.getNameExpression() != null)
			{
				key = (String) ExpressionUtils.processExpression(freeMarkerEngine, field.getNameExpression(), context, listener);

				listener.onTransform(new TransformEvent(field.getLocation(), 
						TransformEventType.KEY_REPLACED, key));
			}

			transformState.setField(field, resObj, key, val);
			listener.onTransform(new TransformEvent(field.getLocation(), 
				TransformEventType.KEY_VALUE_SET, key, val));
			otherEntriesAdded = true;
		}
		
		//if set key is the only key in input map
		if(setKeyPresent && !otherEntriesAdded)
		{
			//return null, so that the current map is ignored
			return null;
		}
		
		if(transformState.isIgnorable(transformObject, resObj))
		{
			return null;
		}
		
		return resObj;
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
	private boolean processMapValue(TransformObject transformObject, String keyName, 
		Object valueExpr, ITransformContext context, TransformState transformState, ObjectWrapper<Object> value)
	{
		if(valueExpr == null)
		{
			return false;
		}
		
		//evaluate the condition and return the result
		Object res = processObject(valueExpr, context, transformState.forDynField(keyName));
		
		res = Conversions.checkForTransform(freeMarkerEngine, transformObject, res, context, transformState, listener);
		value.setValue(res);

		listener.onTransform(new TransformEvent(transformObject.getLocation(), TransformEventType.VALUE_EVALUATED, keyName, res));
		return true;
	}

	/**
	 * Processes the switch statement and returns the matching value.
	 * @param transformObject transform object containing switch statement
	 * @param context context to be used
	 * @param transformState transform state
	 * @param resWrapper wrapper to hold the result
	 * @return true if switch statement is present and processed
	 */
	private boolean processSwitch(TransformObject transformObject, ITransformContext context, 
		TransformState transformState, ObjectWrapper<Object> resWrapper)
	{
		Switch switchStatement = transformObject.getSwitchStatement();
		
		if(switchStatement == null)
		{
			return false;
		}
		
		List<SwitchCase> cases = switchStatement.getCases();
		
		// Evaluate cases in order
		for(int i = 0; i < cases.size(); i++)
		{
			SwitchCase switchCase = cases.get(i);
			FreeMarkerTemplate condition = switchCase.getCondition();
			
			// If condition is null, this is the default case
			boolean conditionMet = true;
			
			if(condition != null)
			{
				conditionMet = FreemarkerUtil.evaluateCondition(
					freeMarkerEngine,
					transformObject.getLocation(),
					condition,
					context);

				listener.onTransform(new TransformEvent(switchCase.getLocation(), 
					TransformEventType.SWITCH_CONDITION_EVALUATED, conditionMet));
			}
			
			if(conditionMet)
			{
				// Found matching case - process the value
				Object result = null;
				
				if(switchCase.getValue() != null) 
				{
					result = processObject(switchCase.getValue(), context, 
						transformState.forDynField("@switch[" + i + "]>@value"));
					
					// Apply transform if present
					result = Conversions.checkForTransform(freeMarkerEngine, transformObject, result, context, transformState, listener);
				}
				
				listener.onTransform(new TransformEvent(switchCase.getLocation(), 
					TransformEventType.SWITCH_VALUE_EVALUATED, result));
				resWrapper.setValue(result);
				return true;
			}
		}
		
		listener.onTransform(new TransformEvent(transformObject.getLocation(), TransformEventType.SWITCH_VALUE_EVALUATED, null));
		
		// No case matched (should not happen if default case is present, but handle gracefully)
		resWrapper.setValue(null);
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
		FreeMarkerTemplate condition = transformObject.getCondition();
			
		//if condition is not present, remove it
		if(condition == null)
		{
			return true;
		}

		//evaluate the condition and return the result
		boolean result = FreemarkerUtil.evaluateCondition(freeMarkerEngine, 
			transformObject.getLocation(), 
			condition, 
			context);

		listener.onTransform(new TransformEvent(transformObject.getLocation(), TransformEventType.CONDITION_EVALUATED, result));

		return result;
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
		boolean result = FreemarkerUtil.evaluateCondition(
			freeMarkerEngine, 
			transformList.getLocation(), 
			transformList.getCondition(), 
			context);

		listener.onTransform(new TransformEvent(transformList.getLocation(), TransformEventType.CONDITION_EVALUATED, result));

		return result;
	}
}
