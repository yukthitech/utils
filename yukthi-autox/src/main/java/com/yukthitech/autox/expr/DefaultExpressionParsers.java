package com.yukthitech.autox.expr;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.jxpath.JXPathContext;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Default expression parser methods.
 * @author akiran
 */
public class DefaultExpressionParsers
{
	@ExpressionParser(type = "prop", description = "Parses specified expression as bean property on context.", example = "prop: attr.bean.value1")
	public Object propertyParser(AutomationContext context, String expression)
	{
		try
		{
			return PropertyUtils.getProperty(context, expression);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while evaluating bean-property '{}' on context", expression, ex);
		}
	}

	@ExpressionParser(type = "ref", description = "Parses specified expression as bean property on context (same as 'prop').", example = "ref: attr.bean.value1")
	public Object refParser(AutomationContext context, String expression)
	{
		try
		{
			return PropertyUtils.getProperty(context, expression);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while evaluating bean-property '{}' on context", expression, ex);
		}
	}

	@ExpressionParser(type = "xpath", description = "Parses specified expression as xpath on context.", example = "xpath: /attr/bean/value1")
	public Object xpathParser(AutomationContext context, String expression)
	{
		try
		{
			return JXPathContext.newContext(context).getValue(expression);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while evaluating xpath '{}' on context", expression, ex);
		}
	}

	@ExpressionParser(type = "string", description = "Returns specified expression as stirng value after trimming.", example = "sring: str")
	public Object strParser(AutomationContext context, String expression)
	{
		return expression.trim();
	}

	@ExpressionParser(type = "int", description = "Parses specified expression into int.", example = "int: 10")
	public Object intParser(AutomationContext context, String expression)
	{
		expression = expression.trim();
		
		try
		{
			return Integer.parseInt(expression);
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while converting specified value into int. Value: " + expression, ex);
		}
	}

	@ExpressionParser(type = "long", description = "Parses specified expression into long.", example = "long: 10")
	public Object longParser(AutomationContext context, String expression)
	{
		expression = expression.trim();
		
		try
		{
			return Long.parseLong(expression);
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while converting specified value into long. Value: " + expression, ex);
		}
	}

	@ExpressionParser(type = "float", description = "Parses specified expression into float.", example = "float: 10.2")
	public Object floatParser(AutomationContext context, String expression)
	{
		expression = expression.trim();
		
		try
		{
			return Float.parseFloat(expression);
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while converting specified value into float. Value: " + expression, ex);
		}
	}

	@ExpressionParser(type = "double", description = "Parses specified expression into double.", example = "double: 10.2")
	public Object doubleParser(AutomationContext context, String expression)
	{
		expression = expression.trim();
		
		try
		{
			return Double.parseDouble(expression);
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while converting specified value into double. Value: " + expression, ex);
		}
	}

	@ExpressionParser(type = "boolean", description = "Parses specified expression into boolean. If expression value is true (case insensitive), then result will be true.", example = "boolean: True")
	public Object booleanParser(AutomationContext context, String expression)
	{
		expression = expression.trim();
		return "true".equalsIgnoreCase(expression);
	}

	@ExpressionParser(type = "condition", description = "Evaluates specified expression as condition and resultant boolean value will be returned", example = "condition: (attr.flag == true)")
	public Object conditionParser(AutomationContext context, String expression)
	{
		expression = expression.trim();
		return AutomationUtils.evaluateCondition(context, expression);
	}
}

