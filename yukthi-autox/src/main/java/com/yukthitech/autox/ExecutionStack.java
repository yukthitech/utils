package com.yukthitech.autox;

import java.util.LinkedList;

import org.apache.logging.log4j.ThreadContext;

import com.yukthitech.autox.test.IEntryPoint;

/**
 * Keeps track of execution, which in turn will be used to display stack trace
 * on error.
 * @author akiran
 */
public class ExecutionStack
{
	/**
	 * Stack trace of the execution.
	 */
	private LinkedList<IEntryPoint> entryPointStack = new LinkedList<>();

	/**
	 * Stack trace of the execution.
	 */
	private LinkedList<String> stackTrace = new LinkedList<>();
	
	/**
	 * Locations stack trace.
	 */
	private LinkedList<String> locationStackTrace = new LinkedList<>();

	public void push(Object object)
	{
		System.out.println("**************Pushing entry: " + object);
		if(object instanceof IEntryPoint)
		{
			entryPointStack.push((IEntryPoint) object);
			return;
		}
		
		IEntryPoint entryPoint = entryPointStack.getFirst();
		ILocationBased element = (ILocationBased) object;
		
		StringBuilder builder = new StringBuilder();
		builder.append(entryPoint.toText());
		
		String location = element.getLocation();
		builder.append("(").append(location).append(")");
		
		locationStackTrace.push(location);
		stackTrace.push(builder.toString());
		
		ThreadContext.put("xmlLoc", location);
	}
	
	public void pop(Object object)
	{
		System.out.println("**************Popping entry: " + object);
		
		if(object instanceof IEntryPoint)
		{
			entryPointStack.pop();
			return;
		}
		
		locationStackTrace.pop();
		stackTrace.pop();
		
		if(locationStackTrace.isEmpty())
		{
			ThreadContext.put("xmlLoc", null);
		}
		else
		{
			ThreadContext.put("xmlLoc", locationStackTrace.peek());
		}
			
	}
	
	public String toStackTrace()
	{
		StringBuilder builder = new StringBuilder("\n");
		
		for(String element : stackTrace)
		{
			builder.append("\t").append(element).append("\n");
		}
		
		return builder.toString();
	}
	
	public String getCurrentLocation()
	{
		if(locationStackTrace.isEmpty())
		{
			return null;
		}
		
		return locationStackTrace.getFirst();
	}
}
