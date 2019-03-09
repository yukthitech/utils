package com.yukthitech.autox;

import java.util.LinkedList;

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
		if(object instanceof IEntryPoint)
		{
			entryPointStack.push((IEntryPoint) object);
			return;
		}
		
		IEntryPoint entryPoint = entryPointStack.getFirst();
		ILocationBased element = (ILocationBased) object;
		
		StringBuilder builder = new StringBuilder();
		builder.append(".").append(entryPoint.toText());
		
		String location = element.getLocation();
		builder.append("(").append(location).append(")");
		
		locationStackTrace.push(location);
		stackTrace.push(builder.toString());
	}
	
	public void pop(Object object)
	{
		if(object instanceof IEntryPoint)
		{
			entryPointStack.pop();
			return;
		}
		
		locationStackTrace.pop();
		stackTrace.pop();
	}
	
	public String toStackTrace()
	{
		StringBuilder builder = new StringBuilder("\n");
		
		for(String element : locationStackTrace)
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
