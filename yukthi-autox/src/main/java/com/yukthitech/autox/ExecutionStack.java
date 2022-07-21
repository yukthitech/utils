package com.yukthitech.autox;

import java.util.LinkedList;

import org.apache.logging.log4j.ThreadContext;

import com.yukthitech.autox.test.IEntryPoint;
import com.yukthitech.utils.exceptions.InvalidStateException;

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
	
	/**
	 * Used internally to ensure, push and pop happens in same order.
	 */
	private LinkedList<Object> objectStack = new LinkedList<>();
	
	private Object unwrapStep(Object object)
	{
		if(object instanceof IStep)
		{
			IStep step = (IStep) object;
			
			if(step.getSourceStep() != null)
			{
				return step.getSourceStep();
			}
			
			return step;
		}

		return object;
	}
	
	public void push(Object object)
	{
		object = unwrapStep(object);
		
		//System.out.println("==========> Pushing object: " + object);
		objectStack.push(object);
		
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
		object = unwrapStep(object);
		
		Object peekObj = objectStack.peek();
		
		if(peekObj != object)
		{
			throw new InvalidStateException("Object being popped is not same as the one on stack. [Expected: {}, Actual:{}]", object, peekObj);
		}
		
		if(object instanceof IEntryPoint)
		{
			//Object popped = entryPointStack.pop();
			entryPointStack.pop();
			objectStack.pop();
			//System.out.println("==========> Popping entry object: " + object + "  =========> " + popped);
			return;
		}
		
		//StackElement popped = stackTrace.pop();
		stackTrace.pop();
		objectStack.pop();
		
		//System.out.println("==========> Popping normal object: " + object + "  =========> " + popped.element);
		
		if(stackTrace.isEmpty())
		{
			ThreadContext.put("xmlLoc", null);
		}
		else
		{
			ThreadContext.put("xmlLoc", stackTrace.peek().getLocation());
		}
			
	}
	
	public boolean isNotPeekElement(Object object)
	{
		object = unwrapStep(object);
		return (objectStack.peek() != object);
	}
	
	public boolean isPeekElement(Object object)
	{
		object = unwrapStep(object);
		return (objectStack.peek() == object);
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
