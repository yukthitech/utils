package com.yukthitech.autox.test.lang.steps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ChildElement;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.exec.AutomationExecutor;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Loops through specified collection or string tokens and for each iteration executed underlying steps.
 * 
 * @author akiran
 */
@Executable(name = "forEach", group = Group.Lang, message = "Loops through specified collection, map or string tokens and for each iteration executed underlying steps")
public class ForEachLoopStep extends AbstractStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;
	
	private static final String VAR_LIST = "list";
	
	private static final String VAR_INDEX = "index";

	/**
	 * Group of steps/validations to be executed when condition evaluated to be
	 * true.
	 */
	@SkipParsing
	@Param(description = "Group of steps/validations to be executed in loop.")
	private List<IStep> steps = new ArrayList<IStep>();

	/**
	 * Expression which will be evaluated to collection or map or String.
	 */
	@Param(description = "Expression which will be evaluated to collection or map or String", sourceType = SourceType.EXPRESSION, required = true)
	private Object expression;
	
	/**
	 * If expression evaluated to string, delimiter to be used to split the string.
	 */
	@Param(description = "If expression evaluated to string, delimiter to be used to split the string. Default Value: comma (\\s*\\,\\s*)", required = false)
	private String delimiter = "\\s*\\,\\s*";
	
	/**
	 * Loop variable that will be used to set loop iteration object on context. Default: loopVar.
	 */
	@Param(description = "Loop variable that will be used to set loop iteration object on context. Default: loopVar", required = false, 
			attrName = true, defaultValue = "loopVar")
	private String loopVar = "loopVar";
	
	/**
	 * Loop index variable that will be used to set loop iteration index on context. Default: loopIdxVar.
	 */
	@Param(description = "Loop index variable that will be used to set loop iteration index on context. Default: loopIdxVar", required = false,
			attrName = true, defaultValue = "loopIdxVar")
	private String loopIdxVar = "loopIdxVar";
	
	/**
	 * Ignores error during iteration and continues to next iteration.
	 */
	@Param(description = "Ignores error during iteration and continues to next iteration.", required = false)
	private boolean ignoreError = false;

	/**
	 * Sets the expression which will be evaluated to collection or map or String.
	 *
	 * @param expression the new expression which will be evaluated to collection or map or String
	 */
	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	/**
	 * Sets the if expression evaluated to string, delimiter to be used to split the string.
	 *
	 * @param delimiter the new if expression evaluated to string, delimiter to be used to split the string
	 */
	public void setDelimiter(String delimiter)
	{
		this.delimiter = delimiter;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.autox.IStepContainer#addStep(com.yukthitech.autox.IStep)
	 */
	@ChildElement(description = "Steps to be executed")
	@Override
	public void addStep(IStep step)
	{
		steps.add(step);
	}
	
	@Override
	public List<IStep> getSteps()
	{
		return steps;
	}
	
	/**
	 * Sets the loop variable that will be used to set loop iteration object on context. Default: loopVar.
	 *
	 * @param loopVar the new loop variable that will be used to set loop iteration object on context
	 */
	public void setLoopVar(String loopVar)
	{
		this.loopVar = loopVar;
	}
	
	/**
	 * Sets the loop index variable that will be used to set loop iteration index on context. Default: loopIdxVar.
	 *
	 * @param loopIdxVar the new loop index variable that will be used to set loop iteration index on context
	 */
	public void setLoopIdxVar(String loopIdxVar)
	{
		this.loopIdxVar = loopIdxVar;
	}
	
	/**
	 * Sets the ignores error during iteration and continues to next iteration.
	 *
	 * @param ignoreError the new ignores error during iteration and continues to next iteration
	 */
	public void setIgnoreError(boolean ignoreError)
	{
		this.ignoreError = ignoreError;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Object> fetchExpressionList(ExecutionLogger exeLogger)
	{
		List<Object> collection = null;
		
		if(expression instanceof String)
		{
			collection = Arrays.asList( (Object[]) ((String) expression).split(delimiter) );
			
			exeLogger.debug("Collection expression '{}' evaluated to string and after split got collection as: {}", expression, collection);
		}
		else if(expression instanceof Collection)
		{
			collection =  new ArrayList<Object>( (Collection<Object>) expression );
			exeLogger.debug("Collection expression '{}' evaluated to collection of size: {}", expression, collection.size());
		}
		else if(expression instanceof Map)
		{
			collection = new ArrayList<Object>( (Collection) ((Map<Object, Object>) expression).entrySet() );
			exeLogger.debug("Collection expression '{}' evaluated to map of size: {}", expression, collection.size());
		}
		else
		{
			throw new InvalidStateException("Collection expression {} evaluated to non-string, non-collection, non-map. Value: {}", expression, expression);
		}
		
		return collection;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		if(expression == null)
		{
			exeLogger.debug("Collection expression evaluated to null");
			return;
		}
		
		AutomationExecutor executor = context.getAutomationExecutor();
		
		executor.newSteps("for-each-steps", this, steps)
			.onInit(entry -> 
			{
				List<Object> collection = fetchExpressionList(exeLogger);
				
				if(CollectionUtils.isEmpty(collection))
				{
					ForEachLoopStep parentStep = (ForEachLoopStep) sourceStep;
					exeLogger.debug("Collection expression '{}' evaluated to empty collection", parentStep.expression);
					return false;
				}
				
				collection = CollectionUtils.isEmpty(collection) ? Collections.emptyList() : collection;
				
				AtomicInteger index = new AtomicInteger(0);
				
				entry.setVariable(VAR_LIST, collection)
					.setVariable(VAR_INDEX, index);
				
				return true;
			})
			.onPreexecute(entry -> 
			{
				AtomicInteger loopIndex = (AtomicInteger) entry.getVariable(VAR_INDEX);
				List<Object> loopCollection = (List<Object>) entry.getVariable(VAR_LIST);
				
				int idx = loopIndex.get();
				
				Object val = loopCollection.get(idx);
				context.setAttribute(loopVar, val);
				context.setAttribute(loopIdxVar, idx);
			})
			.exceptionHandler((entry, ex) -> 
			{
				AtomicInteger loopIndex = (AtomicInteger) entry.getVariable(VAR_INDEX);
				List<Object> loopCollection = (List<Object>) entry.getVariable(VAR_LIST);
				
				if(ex instanceof BreakException)
				{
					entry.skipChildSteps();
					loopIndex.set(loopCollection.size());
					return true;
				}
				
				if(ex instanceof ContinueException)
				{
					entry.resetChildIndex();
					return true;
				}
				
				return false;
			})
			.onSuccess(entry -> 
			{
				AtomicInteger loopIndex = (AtomicInteger) entry.getVariable(VAR_INDEX);
				loopIndex.incrementAndGet();
			})
			.isReexecutionNeeded(entry -> 
			{
				AtomicInteger loopIndex = (AtomicInteger) entry.getVariable(VAR_INDEX);
				List<Object> loopCollection = (List<Object>) entry.getVariable(VAR_LIST);
				
				int idx = loopIndex.incrementAndGet();
				
				return (idx < loopCollection.size());
			})
			.execute();
		;
	}
}
