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
	private static class StackElement
	{
		private IEntryPoint entryPoint;
		
		private ILocationBased element;
		
		private int entryPointLevel;

		public StackElement(IEntryPoint entryPoint, ILocationBased element, int entryPointLevel)
		{
			this.entryPoint = entryPoint;
			this.element = element;
			this.entryPointLevel = entryPointLevel;
		}
		
		public String getLocation()
		{
			return element.getLocation();
		}
		
		public String toString()
		{
			StringBuilder builder = new StringBuilder();
			builder.append(entryPoint.toText());
			
			String location = element.getLocation();
			builder.append("(").append(location).append(")");

			return builder.toString();
		}
	}
	
	/**
	 * Stack trace of the execution.
	 */
	private LinkedList<IEntryPoint> entryPointStack = new LinkedList<>();

	/**
	 * Stack trace of the execution.
	 */
	private LinkedList<StackElement> stackTrace = new LinkedList<>();
	
	public void push(Object object)
	{
		if(object instanceof IEntryPoint)
		{
			entryPointStack.push((IEntryPoint) object);
			return;
		}
		
		IEntryPoint entryPoint = entryPointStack.getFirst();
		ILocationBased element = (ILocationBased) object;
		
		StackElement stackElement = new StackElement(entryPoint, element, entryPointStack.size());
		stackTrace.push(stackElement);
		
		ThreadContext.put("xmlLoc", stackElement.getLocation());
	}
	
	public void pop(Object object)
	{
		if(object instanceof IEntryPoint)
		{
			entryPointStack.pop();
			return;
		}
		
		stackTrace.pop();
		
		if(stackTrace.isEmpty())
		{
			ThreadContext.put("xmlLoc", null);
		}
		else
		{
			ThreadContext.put("xmlLoc", stackTrace.peek().getLocation());
		}
			
	}
	
	public String toStackTrace()
	{
		StringBuilder builder = new StringBuilder("\n");
		int entryPointLevel = -1;
		
		for(StackElement element : stackTrace)
		{
			//if entry point level is same as that of prev element
			if(element.entryPointLevel == entryPointLevel)
			{
				//skip the element from stack trace, which would
				// be the case with loops, try-catch etc
				continue;
			}
			
			builder.append("\t").append(element).append("\n");
			entryPointLevel = element.entryPointLevel;
		}
		
		return builder.toString();
	}
	
	public String getCurrentLocation()
	{
		if(stackTrace.isEmpty())
		{
			return null;
		}
		
		return stackTrace.getFirst().getLocation();
	}
}
