package com.yukthi.utils.expr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.yukthi.utils.exceptions.InvalidArgumentException;

/**
 * Registry for maintaining operators and functions that will be used in expression evaluation.
 * @author akiran
 */
public class ExpressionRegistry
{
	/**
	 * Expected operator pattern.
	 */
	public static final Pattern OP_PATTERN = Pattern.compile("[\\~\\!\\@\\#\\$\\%\\^\\&\\*\\<\\>\\?\\/\\:\\;\\+\\-\\=\\|]{1,3}");
	
	/**
	 * Expected function name pattern.
	 */
	public static final Pattern FUNC_PATTERN = Pattern.compile("\\w+");
	
	/**
	 * Operator map.
	 */
	private Map<String, IOperator> operatorMap = new HashMap<String, IOperator>();
	
	/**
	 * Function map.
	 */
	private Map<String, IFunction> functionMap = new HashMap<String, IFunction>();
	
	/**
	 * Adds specified operator to the registry.
	 * @param operatorStr Operator in string format.
	 * @param operator Operator to be added.
	 */
	public void addOperator(String operatorStr, IOperator operator)
	{
		if(operatorStr == null || operatorStr.trim().length() == 0)
		{
			throw new NullPointerException("Operator can not be null or empty");
		}
		
		if(!OP_PATTERN.matcher(operatorStr).matches())
		{
			throw new InvalidArgumentException("Specified operator string does not match required pattern: {}", operatorStr);
		}
		
		operatorMap.put(operatorStr, operator);
	}
	
	/**
	 * Gets operator matching specified string.
	 * @param str operator in string format.
	 * @return Matching operator.
	 */
	public IOperator getOperator(String str)
	{
		return operatorMap.get(str);
	}
	
	/**
	 * Adds specified function to registry.
	 * @param name Name of the function
	 * @param function Function to be added
	 */
	public void addFunction(String name, IFunction function)
	{
		if(name == null || name.trim().length() == 0)
		{
			throw new NullPointerException("Name can not be null or empty");
		}
		
		if(!FUNC_PATTERN.matcher(name).matches())
		{
			throw new InvalidArgumentException("Invalid function name specified - {}", name);
		}
		
		functionMap.put(name, function);
	}
	
	/**
	 * Gets function with specified name.
	 * @param name
	 * @return
	 */
	public IFunction getFunction(String name)
	{
		return functionMap.get(name);
	}
	
	/**
	 * Fetchs all operators.
	 * @return All operators.
	 */
	public Collection<IOperator> getAllOperators()
	{
		return operatorMap.values();
	}
	
	/**
	 * Fetches all available functions.
	 * @return All functions.
	 */
	public Collection<IFunction> getAllFunctions()
	{
		return functionMap.values();
	}
}
