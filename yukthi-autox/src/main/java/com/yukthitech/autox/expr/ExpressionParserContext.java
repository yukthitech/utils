package com.yukthitech.autox.expr;

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.autox.AutomationContext;

/**
 * Context used by expression parsers.
 * @author akiran
 */
public class ExpressionParserContext
{
	/**
	 * Automation context being used.
	 */
	private AutomationContext automationContext;
	
	/**
	 * Last expression value in expression chain.
	 */
	private Object currentValue;
	
	/**
	 * Expression type parameters specified as part of expression.
	 */
	private String expressionTypeParameters[];
	
	/**
	 * Current parser in use.
	 */
	private ExpressionParserDetails currentParser;
	
	private Map<String, String> parameters = new HashMap<>();

	/**
	 * Instantiates a new expression parser context.
	 *
	 * @param context the context
	 */
	public ExpressionParserContext(AutomationContext context)
	{
		this.automationContext = context;
	}

	/**
	 * Sets the last expression value in expression chain.
	 *
	 * @param currentValue the new last expression value in expression chain
	 */
	void setCurrentValue(Object currentValue)
	{
		this.currentValue = currentValue;
	}

	/**
	 * Gets the automation context being used.
	 *
	 * @return the automation context being used
	 */
	public AutomationContext getAutomationContext()
	{
		return automationContext;
	}
	
	/**
	 * Gets the last expression value in expression chain.
	 *
	 * @return the last expression value in expression chain
	 */
	public Object getCurrentValue()
	{
		return currentValue;
	}
	
	/**
	 * Fetches effective context to be used for parsing expressions.
	 * @return effective context
	 */
	public Object getEffectiveContext()
	{
		return (currentValue == null) ? automationContext : currentValue;
	}
	
	/**
	 * Sets the expression type parameters specified as part of expression.
	 *
	 * @param expressionTypeParameters the new expression type parameters specified as part of expression
	 */
	void setExpressionTypeParameters(String[] expressionTypeParameters)
	{
		this.expressionTypeParameters = expressionTypeParameters;
	}
	
	/**
	 * Gets the expression type parameters specified as part of expression.
	 *
	 * @return the expression type parameters specified as part of expression
	 */
	public String[] getExpressionTypeParameters()
	{
		return expressionTypeParameters;
	}

	/**
	 * Gets the current parser in use.
	 *
	 * @return the current parser in use
	 */
	public ExpressionParserDetails getCurrentParser()
	{
		return currentParser;
	}

	/**
	 * Sets the current parser in use.
	 *
	 * @param currentParser the new current parser in use
	 */
	void setCurrentParser(ExpressionParserDetails currentParser)
	{
		this.currentParser = currentParser;
	}
	
	/**
	 * Clears the current parameters from the context.
	 */
	public void clearParameters()
	{
		parameters.clear();
	}
	
	/**
	 * Adds the parameter to the context.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public void addParameter(String name, String value)
	{
		parameters.put(name.toLowerCase(), value);
	}
	
	/**
	 * Fetches the parameter value with specified name.
	 * @param name name of param to fetch
	 * @return matching param value
	 */
	public String getParameter(String name)
	{
		return parameters.get(name.toLowerCase());
	}
}
