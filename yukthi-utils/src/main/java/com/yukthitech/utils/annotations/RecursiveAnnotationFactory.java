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
package com.yukthitech.utils.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * @author akiran
 *
 */
public class RecursiveAnnotationFactory
{
	private static Logger logger = LogManager.getLogger(RecursiveAnnotationFactory.class);
	
	private static Pattern ARR_IDX_PATTERN = Pattern.compile("\\[(.+)\\]");
	
	/**
	 * Method handler for proxy annotations
	 * @author akiran
	 *
	 * @param <A> Annotation for which handler needs to be created
	 */
	private class AnnotationMethodHandler<A extends Annotation> implements InvocationHandler
	{
		/**
		 * Proxy annotation created as part of this handler
		 */
		private A proxyAnnotation;
		
		/**
		 * Actual annotation over which wrapper annotation is being built
		 */
		private Annotation actualAnnotation;
		
		/**
		 * Map to keep track overridden properties
		 */
		private Map<String, Object> overriddenProp = new HashMap<String, Object>();
		
		public AnnotationMethodHandler(Annotation actualAnnotation)
		{
			this.actualAnnotation = actualAnnotation;
		}

		/* (non-Javadoc)
		 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
		 */
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			//if property is overridden, return overridden value
			if(overriddenProp.containsKey(method.getName()))
			{
				return overriddenProp.get(method.getName());
			}
			
			//if property is not overridden return actual value
			return method.invoke(actualAnnotation);
		}
		
		/**
		 * Parse array index string and return specified indexes
		 * @param methodDet Method for which parsing is in progress
		 * @param arrStr Array string which needs to be parsed
		 * @return Parsed indexes
		 */
		private Integer[] getArrayIndexes(String methodDet, String arrStr)
		{
			Matcher matcher = ARR_IDX_PATTERN.matcher(arrStr);
			List<Integer> indexes = new ArrayList<Integer>();
			
			while(matcher.find())
			{
				try
				{
					indexes.add(Integer.parseInt(matcher.group(1)));
				}catch(Exception ex)
				{
					throw new InvalidArgumentException("An error occurred while parsing overriding parsing string of method - {}. Invalid index specified - {}", methodDet, matcher.group(1));
				}
			}
			
			return indexes.toArray(new Integer[0]);
		}
		
		/**
		 * @param annotation Annotation on which method search needs to be done. If not specified, it will be "actualAnnotation"
		 * @param methodStr Method string which needs parsing
		 * @param propertyTokens Function expression for which is being overridden
		 * @param idx Index of property token. Used to keep track of recursion
		 * @param value Value to be used for overriding
		 * @param expression Expression under progress
		 */
		public void setPropertyValue(Annotation annotation, String methodStr, String propertyTokens[], int idx, Object value, StringBuilder expression)
		{
			if(annotation == null)
			{
				annotation = this.actualAnnotation;
			}
			
			String methodName = propertyTokens[idx];
			int arrStartIdx = propertyTokens[idx].indexOf("[");
			Integer arrIndexes[] = null;
			
			//if array operator is used, parse array indexes
			if(arrStartIdx > 0)
			{
				methodName = propertyTokens[idx].substring(0, arrStartIdx);
				arrIndexes = getArrayIndexes(methodStr, propertyTokens[idx].substring(arrStartIdx));
				
				if(arrIndexes.length == 0)
				{
					throw new InvalidArgumentException("Invalid override expression specified on method - {}. Expression - {}", methodStr, propertyTokens[idx]);
				}
			}
			
			//add method name to expression progress
			expression.append(methodName);
			
			Method method = null;
			
			try
			{
				method = annotation.annotationType().getMethod(methodName);
			}catch(Exception ex)
			{
				throw new InvalidArgumentException(ex, "Invalid override expression specified on method - '{}'. Method - {}", methodStr, expression);
			}
			
			Object methodVal = null;
			
			try
			{
				methodVal = method.invoke(annotation);
			}catch(Exception ex)
			{
				throw new InvalidArgumentException(ex, "An error occurred while invoking method - {}.{}()", annotation.annotationType().getName(), method.getName());
			}
			
			//if the value is overridden then use overridden value
			if(overriddenProp.containsKey(methodName))
			{
				methodVal = overriddenProp.get(methodName);
			}
			
			//if array indexes are present
			if(arrIndexes != null)
			{
				Object parentArr = null;
				Object finalVal = methodVal;
				int parentArrIdx = 0;
				
				//loop through array indexes
				for(int i = 0; i < arrIndexes.length; i++)
				{
					if(!finalVal.getClass().isArray())
					{
						throw new InvalidArgumentException("Invalid override expression specified on method - '{}'. Array operator used on non-array property - {}", methodStr, expression);
					}
					
					//keep track of parent element on which array operator should be applied
					parentArr = finalVal;
					parentArrIdx = arrIndexes[i];
					
					//keep track of final value
					finalVal = Array.get(finalVal, arrIndexes[i]);
					expression.append("[").append(arrIndexes[i]).append("]");
				}
				
				//if the final overriding is on array element (no nested property, overriding value is array)
				if(idx == (propertyTokens.length - 1))
				{
					//check for compatibility
					if(!CommonUtils.isAssignable(value.getClass(), finalVal.getClass()))
					{
						throw new InvalidArgumentException("Invalid override expression specified on method - {}. Expression type {} and overridden value types are not matching", methodStr, finalVal.getClass().getName(), value.getClass());
					}
					
					//set the overriden array element
					Array.set(parentArr, parentArrIdx, value);
					overriddenProp.put(methodName, methodVal);
					return;
				}
				
				//if sub properties are present
				if(!Annotation.class.isAssignableFrom(finalVal.getClass()))
				{
					throw new InvalidArgumentException("Invalid override expression specified on method - {}. Nested operator used on non-annotation expression {}", methodStr, expression);				
				}

				//if nested operation is specified, create sub annotation wrapper and go for recursion
				AnnotationMethodHandler<Annotation> subAnnotHandler = newAnnotation((Annotation)finalVal);
				Array.set(parentArr, parentArrIdx, subAnnotHandler.proxyAnnotation);
				overriddenProp.put(methodName, methodVal);
				
				subAnnotHandler.setPropertyValue((Annotation)finalVal, methodStr, propertyTokens, idx + 1, value, expression.append("."));
			}
			//if array operator is not involved
			else
			{
				//if this represents final token
				if(idx == (propertyTokens.length - 1))
				{
					//if types are not compatible
					if(!CommonUtils.isAssignable(value.getClass(), method.getReturnType()))
					{
						throw new InvalidArgumentException("Invalid override expression specified on method - {}. Expression type {} and overridden value types are not matching", methodStr, method.getReturnType().getName(), value.getClass());
					}

					//set overridden value and return
					overriddenProp.put(methodName, value);
					return;
				}
				
				//if sub properties are present
				if(!Annotation.class.isAssignableFrom(methodVal.getClass()))
				{
					throw new InvalidArgumentException("Invalid override expression specified on method - {}. Nested operator used on non-annotation expression {}", methodStr, expression);				
				}

				//create handler for sub annotation and create handler
				AnnotationMethodHandler<Annotation> subAnnotHandler = newAnnotation((Annotation)methodVal);
				overriddenProp.put(methodName, subAnnotHandler.proxyAnnotation);
				subAnnotHandler.setPropertyValue((Annotation)methodVal, methodStr, propertyTokens, idx + 1, value, expression.append("."));
			}
		}
		
	}
	
	/**
	 * Creates proxy wrapper annotation. The result annotation would be wrapper over actualAnnotation. 
	 * For non-overridden methods, method call will be delegated to actual annotation
	 * @param actualAnnotation Actual annotation which should be wrapped
	 * @return Wrapped proxy annotation
	 */
	@SuppressWarnings("unchecked")
	private <A extends Annotation> AnnotationMethodHandler<A> newAnnotation(A actualAnnotation)
	{
		//create handler and proxy class
		AnnotationMethodHandler<A> handler = new AnnotationMethodHandler<A>(actualAnnotation);
		A proxy = (A)Proxy.newProxyInstance(RecursiveAnnotationFactory.class.getClassLoader(), new Class[]{actualAnnotation.annotationType()}, handler);
		
		handler.proxyAnnotation = proxy;
		
		return handler;
	}
	
	/**
	 * This method loop through parent annotation methods and checks if they override property of target annotation using {@link OverrideProperty}
	 * annotation. If any property is overridden a proxy annotation wrapper will be created over actual annotation which helps in overriding actual
	 * property values.
	 *  
	 * @param targetAnnotation Target annotation found
	 * @param parentAnnotation Annotation on which target annotation is found
	 * @return Target annotation wit overridden properties, if any
	 */
	@SuppressWarnings("unchecked")
	private <A extends Annotation> A overrideProperties(A targetAnnotation, Annotation parentAnnotation)
	{
		Method methods[] = parentAnnotation.annotationType().getMethods();
		OverrideProperty overrideProperty = null;
		OverrideProperties overrideProperties = null;
		Class<?> targetAnnotationType = targetAnnotation.annotationType(); 

		String propertyTokens[] = null;
		Object value = null;
		AnnotationMethodHandler<Annotation> handler = null;
		List<OverrideProperty> overridePropertyLst = new ArrayList<OverrideProperty>();
		String methodName = null;
		
		//loop through parent annotation methods and find which methods are overriding target annotation properties
		for(Method method : methods)
		{
			overridePropertyLst.clear();
			
			overrideProperty = method.getAnnotation(OverrideProperty.class);
			overrideProperties = method.getAnnotation(OverrideProperties.class);
			
			//if annotation is not present or 
			if(overrideProperty != null && targetAnnotationType.equals(overrideProperty.targetAnnotationType()) )
			{
				overridePropertyLst.add(overrideProperty);
			}
			
			//if @OverrideProperties is specified
			if(overrideProperties != null)
			{
				//extract matching overrides
				for(OverrideProperty overProp : overrideProperties.value())
				{
					if(targetAnnotationType.equals(overProp.targetAnnotationType()) )
					{
						overridePropertyLst.add(overProp);
					}
				}
			}

			//if no overrides are specified
			if(overridePropertyLst.isEmpty())
			{
				continue;
			}
			
			//fetch the value and set it on map
			try
			{
				value =  method.invoke(parentAnnotation);
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while fetching '{}' value from annotation - {}", method.getName(), parentAnnotation.annotationType().getName());
			}
		
			//if no proxy was defined earlier
			if(handler == null)
			{
				//create new handler and proxy annotation
				handler = newAnnotation((Annotation) targetAnnotation);
			}
			
			methodName = parentAnnotation.annotationType().getName() + "." + method.getName() + "()";
			
			//add overridden property value
			for(OverrideProperty overProp : overridePropertyLst)
			{
				propertyTokens = overProp.property().split("\\.");
				handler.setPropertyValue(null, methodName, propertyTokens, 0, value, new StringBuilder());
			}
		}
		
		return (handler != null) ? (A)handler.proxyAnnotation : targetAnnotation;
	}
	
	/**
	 * Tries to find target annotation on "parentAnnotation". If not found, further annotations are added to queue
	 * @param parentAnnotation Annotation on which target annotation needs to be searched
	 * @param annotationQueue Queue to which sub-annotations (annotation on parent-annotation) needs to be added
	 * @param targetAnnotationType Annotation being searched
	 * @return Target annotation if found, otherwise null
	 */
	private <A extends Annotation> A findRecursiveAnnotation(Annotation parentAnnotation, LinkedList<Annotation> annotationQueue, Class<A> targetAnnotationType)
	{
		//ignore java core annotations
		if(parentAnnotation.annotationType().getName().startsWith("java"))
		{
			return null;
		}
		
		//logger.trace("Trying to find annotation '{}' on annotation - {}", targetAnnotationType.getName(), parentAnnotation.annotationType().getName());
		
		//check if annotation is defined directly.
		A targetAnnotation = parentAnnotation.annotationType().getAnnotation(targetAnnotationType);
		
		//if target annotation is found, override the annotation properties as required
		if(targetAnnotation != null)
		{
			targetAnnotation = overrideProperties(targetAnnotation, parentAnnotation);
			return targetAnnotation;
		}

		//find annotations on parent annotations and add it to queue at end for further processing
		Annotation annotations[] = parentAnnotation.annotationType().getAnnotations();
		
		if(annotations != null)
		{
			annotationQueue.addAll(Arrays.asList(annotations));
		}
		
		return null;
	}
	
	/**
	 * Searches and returns annotation of type "targetAnnotationType" defined on "annotatedElement" recursively.
	 * @param annotatedElement Element annotation search needs to be done
	 * @param targetAnnotationType Annotation type to be searched
	 * @return Found annotaion, if not null
	 */
	public <A extends Annotation> A findAnnotationRecursively(AnnotatedElement annotatedElement, Class<A> targetAnnotationType)
	{
		//check if annotation is defined directly.
		A targetAnnotation = annotatedElement.getAnnotation(targetAnnotationType);
		
		//if direct annotation is found, return the same
		if(targetAnnotation != null)
		{
			logger.trace("Direct annotation of type '{}' found on '{}'", targetAnnotationType.getName(), annotatedElement);
			return targetAnnotation;
		}
		
		/*
		 * If direct annotation is not found, try to find target annotation on annotations defined on annotate-element.
		 * If not found at that level (level 1) try to find at next level
		 */
		
		//queue to maintain annotations order level wise
		//next level annotation are appended, and annotation are popped from start for processing
		LinkedList<Annotation> annotationQueue = new LinkedList<Annotation>();
		
		//find other annotations and add them to queue for further recursive processing
		Annotation annotations[] = annotatedElement.getAnnotations();

		//add annotations found to queue
		if(annotations != null)
		{
			annotationQueue.addAll(Arrays.asList(annotations));
		}
		
		//loop through queue and recursively try to find 
		while(!annotationQueue.isEmpty())
		{
			//try to find target annotation on current annotation
			targetAnnotation = findRecursiveAnnotation(annotationQueue.removeFirst(), annotationQueue, targetAnnotationType);
			
			//if target annotation found
			if(targetAnnotation != null)
			{
				break;
			}
		}
		
		return targetAnnotation;
	}
	
	/**
	 * Checks if the specified annotation is suppressed on specified annotated element.
	 * @param annotatedElement Element to be checked
	 * @param targetAnnotationType Annotation which needs to be checked
	 * @return true if suppressed
	 */
	private boolean isRecursionSuppressed(AnnotatedElement annotatedElement, Class<?> targetAnnotationType)
	{
		SuppressRecursiveSearch suppressRecursiveSearch = annotatedElement.getAnnotation(SuppressRecursiveSearch.class);
		
		if(suppressRecursiveSearch == null)
		{
			return false;
		}
		
		for(Class<?> type : suppressRecursiveSearch.value())
		{
			if(type.equals(targetAnnotationType))
			{
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Searches and returns all annotations of type "targetAnnotationType" defined on "annotatedElement" recursively.
	 * @param annotatedElement Element annotation search needs to be done
	 * @param targetAnnotationType Annotation type to be searched
	 * @return Found matching annotaions, if not null
	 */
	public <A extends Annotation> List<A> findAllAnnotationsRecursively(AnnotatedElement annotatedElement, Class<A> targetAnnotationType)
	{
		List<A> matchingAnnot = new ArrayList<A>();
		
		//check if annotation is defined directly.
		A targetAnnotation = annotatedElement.getAnnotation(targetAnnotationType);
		
		//if direct annotation is found, return the same
		if(targetAnnotation != null)
		{
			logger.trace("Direct annotation of type '{}' found on '{}'", targetAnnotationType.getName(), annotatedElement);
			matchingAnnot.add(targetAnnotation);
		}

		//if annotation recursion is suppressed return direct finding alone
		if(isRecursionSuppressed(annotatedElement, targetAnnotationType))
		{
			return matchingAnnot.isEmpty() ? null : matchingAnnot;
		}
		
		/*
		 * find target annotation on annotations defined on annotate-element.
		 */
		
		//queue to maintain annotations order level wise
		//next level annotation are appended, and annotation are popped from start for processing
		LinkedList<Annotation> annotationQueue = new LinkedList<Annotation>();
		
		//find other annotations and add them to queue for further recursive processing
		Annotation annotations[] = annotatedElement.getAnnotations();

		//add annotations found to queue
		if(annotations != null)
		{
			annotationQueue.addAll(Arrays.asList(annotations));
		}
		
		//loop through queue and recursively try to find 
		while(!annotationQueue.isEmpty())
		{
			//try to find target annotation on current annotation
			targetAnnotation = findRecursiveAnnotation(annotationQueue.removeFirst(), annotationQueue, targetAnnotationType);
			
			//if target annotation found
			if(targetAnnotation != null)
			{
				matchingAnnot.add(targetAnnotation);
			}
		}
		
		if(matchingAnnot.isEmpty())
		{
			return null;
		}
		
		return matchingAnnot;
	}
}
