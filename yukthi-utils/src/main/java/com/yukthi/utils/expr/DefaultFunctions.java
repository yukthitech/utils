package com.yukthi.utils.expr;

/**
 * Provides the default functions fofr Default registry factory.
 * @author akiran
 */
public class DefaultFunctions
{
	/**
	 * A if function, if specified condition is true, ifVal will be returned
	 * otherwise elseVal will be returned.
	 * @param condition Condition to check.
	 * @param ifVal If value.
	 * @param elseVal Else Value.
	 * @return Result value
	 */
	@FunctionInfo(name = "IF", syntax = "IF(condition, ifVal, elseVal)", matchParameterTypes = {1, 2}, 
			description = "If specified condition is true, ifVal will be returned otherwise elseVal.")
	public static Object ifCondition(Boolean condition, Object ifVal, Object elseVal)
	{
		return condition ? ifVal : elseVal;
	}
	
	/**
	 * Not function.
	 * @param condition Condition.
	 * @return not of specified condition.
	 */
	@FunctionInfo(name = "NOT", syntax = "NOT(condition)", description = "Returns negation (inverse) of specified boolean condition.")
	public static Boolean not(Boolean condition)
	{
		return !condition;
	}
	
	/**
	 * Nvl.
	 * @param condition the condition
	 * @param trueVal the true val
	 * @param falseVal the false val
	 * @return If condition is null, trueVal will be returned otherwise falseVal.
	 */
	@FunctionInfo(name = "NVL", syntax = "NVL(conditionObj, trueVal, falseVal)",  matchParameterTypes = {1, 2}, 
			description = "Returns trueVal if condition object is null othwerwise falseVal.")
	public static Object nvl(Object condition, Object trueVal, Object falseVal)
	{
		return (condition == null) ? trueVal : falseVal;
	}
	
	/**
	 * Checks if specified string is blank.
	 *
	 * @param str the str
	 * @return true if string is blank.
	 */
	@FunctionInfo(name = "IS_BLANK", syntax = "IS_BLANK(str)", description = "Returns true if str is null or empty.")
	public static Boolean isBlank(String str)
	{
		return (str == null || str.trim().length() == 0);
	}
	
	/**
	 * Fetches the maximum values number amount specified values.
	 * @param firstVal mandatory first value
	 * @param values Values to be searched.
	 * @return Max value
	 */
	@FunctionInfo(name = "MAX", syntax = "MAX(num1, num2, num3...)", description = "Returns maximum value of the provided values.")
	public static Number max(Number firstVal, Number... values)
	{
		Number maxValue = firstVal;
		
		for(Number val : values)
		{
			if(maxValue.doubleValue() < val.doubleValue())
			{
				maxValue = val;
			}
		}
		
		return maxValue;
	}

	/**
	 * Fetches the minimum values number amound specified values.
	 * @param firstVal mandatory first value
	 * @param values Values to be searched.
	 * @return Max value
	 */
	@FunctionInfo(name = "MIN", syntax = "MIN(num1, num2, num3...)", description = "Returns minimum value of the provided values.")
	public static Number min(Number firstVal, Number... values)
	{
		Number minValue = firstVal;
		
		for(Number val : values)
		{
			if(minValue.doubleValue() > val.doubleValue())
			{
				minValue = val;
			}
		}
		
		return minValue;
	}

	/**
	 * Sum.
	 *
	 * @param firstVal mandatory first value
	 * @param values the values
	 * @return sum of provided values.
	 */
	@FunctionInfo(name = "SUM", syntax = "SUM(num1, num2, num3...)", description = "Returns sum of the provided values.")
	public static Number sum(Number firstVal, Number... values)
	{
		double sum = firstVal.doubleValue();
		
		for(Number val : values)
		{
			sum += val.doubleValue();
		}
		
		return sum;
	}
	
	/**
	 * Avg.
	 *
	 * @param firstVal mandatory first value
	 * @param values the values
	 * @return average of specified values.
	 */
	@FunctionInfo(name = "AVG", syntax = "AVG(num1, num2, num3...)", description = "Returns average of the provided values.")
	public static Number avg(Number firstVal, Number... values)
	{
		double avg = firstVal.doubleValue();
		
		for(Number val : values)
		{
			avg += val.doubleValue();
		}
		
		return avg / (values.length + 1);
	}
}
