package com.yukthitech.autox.test.lang.steps;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.test.StepGroup;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Loops through specified collection or string tokens and for each iteration executed underlying steps.
 * 
 * @author akiran
 */
@Executable(name = "forEach", message = "Loops through specified collection or string tokens and for each iteration executed underlying steps")
public class ForEachLoopStep extends AbstractStep implements IStepContainer
{
	private static final long serialVersionUID = 1L;

	/**
	 * Group of steps/validations to be executed when condition evaluated to be
	 * true.
	 */
	@SkipParsing
	@Param(description = "Group of steps/validations to be executed in loop.")
	private StepGroup steps;

	/**
	 * Expression which will be evaluated to collection or map or String.
	 */
	@Param(description = "Expression which will be evaluated to collection or map or String")
	private String expression;
	
	/**
	 * If expression evaluated to string, delimiter to be used to split the string.
	 */
	@Param(description = "If expression evaluated to string, delimiter to be used to split the string. Default Value: comma (\\s*\\,\\s*)", required = false)
	private String delimiter = "\\s*\\,\\s*";
	
	/**
	 * Loop variable that will be used to set loop iteration object on context. Default: loopVar.
	 */
	@Param(description = "Loop variable that will be used to set loop iteration object on context. Default: loopVar", required = false)
	private String loopVar = "loopVar";
	
	/**
	 * Loop index variable that will be used to set loop iteration index on context. Default: loopIdxVar.
	 */
	@Param(description = "Loop index variable that will be used to set loop iteration index on context. Default: loopIdxVar", required = false)
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
	@Override
	public void addStep(IStep step)
	{
		if(steps == null)
		{
			steps = new StepGroup();
		}
		
		steps.addStep(step);
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
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger) throws Exception
	{
		Object exprValue = null;
		
		try
		{
			exprValue = PropertyUtils.getProperty(context, expression);
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while evaluating expression: {}", expression, ex);
		}
		
		if(exprValue == null)
		{
			exeLogger.debug(this, "Expression '{}' evaluated to null", expression);
			return true;
		}
		
		Collection<Object> collection = null;
		
		if(exprValue instanceof String)
		{
			collection = Arrays.asList( (Object[]) ((String) exprValue).split(delimiter) );
			
			exeLogger.debug(this, "Expression '{}' evaluated to string and after split got collection as: {}", expression, collection);
		}
		else if(exprValue instanceof Collection)
		{
			collection = (Collection<Object>) exprValue;
			exeLogger.debug(this, "Expression '{}' evaluated to collection of size: {}", expression, collection.size());
		}
		else if(exprValue instanceof Map)
		{
			collection = (Collection) ((Map<Object, Object>) exprValue).entrySet();
			exeLogger.debug(this, "Expression '{}' evaluated to map of size: {}", expression, collection.size());
		}
		else
		{
			throw new InvalidStateException("Expression {} evaluated to non-string, non-collection, non-map. Value: {}", expression, exprValue);
		}
		
		int idx = 0;
		
		for(Object val : collection)
		{
			context.setAttribute(loopVar, val);
			context.setAttribute(loopIdxVar, idx);
			
			try
			{
				steps.execute(context, exeLogger);
			} catch(BreakException ex)
			{
				break;
			} catch(ContinueException ex)
			{
				continue;
			} catch(RuntimeException ex)
			{
				if(ignoreError)
				{
					exeLogger.error(ForEachLoopStep.this, "Iteration resulted in error, which is being ignore.", ex);
				}
				
				throw ex;
			}
			
			idx++;
		}
		
		return true;
	}
}
