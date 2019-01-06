package com.yukthitech.autox.expr;

import java.lang.reflect.Method;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Expression parser details.
 * 
 * @author akiran
 */
public class ExpressionParserDetails
{
	/**
	 * Type of the expression supported.
	 */
	private String type;
	
	/**
	 * Description about the parser.
	 */
	private String description;
	
	/**
	 * Example of the parser.
	 */
	private String example;
	
	/**
	 * Enclosing object.
	 */
	private Object enclosingObject;
	
	/**
	 * Parser method.
	 */
	private Method method;
	
	/**
	 * Instantiates a new expression parser details.
	 */
	public ExpressionParserDetails()
	{}
	
	public ExpressionParserDetails(String type, String description, String example, Object enclosingObject, Method method)
	{
		this.type = type;
		this.description = description;
		this.example = example;
		this.enclosingObject = enclosingObject;
		this.method = method;
	}

	/**
	 * Gets the type of the expression supported.
	 *
	 * @return the type of the expression supported
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Sets the type of the expression supported.
	 *
	 * @param type the new type of the expression supported
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * Gets the description about the parser.
	 *
	 * @return the description about the parser
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description about the parser.
	 *
	 * @param description the new description about the parser
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets the example of the parser.
	 *
	 * @return the example of the parser
	 */
	public String getExample()
	{
		return example;
	}

	/**
	 * Sets the example of the parser.
	 *
	 * @param example the new example of the parser
	 */
	public void setExample(String example)
	{
		this.example = example;
	}

	/**
	 * Gets the enclosing object.
	 *
	 * @return the enclosing object
	 */
	public Object getEnclosingObject()
	{
		return enclosingObject;
	}

	/**
	 * Sets the enclosing object.
	 *
	 * @param enclosingObject the new enclosing object
	 */
	public void setEnclosingObject(Object enclosingObject)
	{
		this.enclosingObject = enclosingObject;
	}

	/**
	 * Gets the parser method.
	 *
	 * @return the parser method
	 */
	public Method getMethod()
	{
		return method;
	}

	/**
	 * Sets the parser method.
	 *
	 * @param method the new parser method
	 */
	public void setMethod(Method method)
	{
		this.method = method;
	}
	
	/**
	 * Invokes underlying parser method and returns the result.
	 * @param context context to be used
	 * @param expression expression to be executed
	 * @return result
	 */
	public Object invoke(AutomationContext context, String expression)
	{
		try
		{
			return method.invoke(enclosingObject, context, expression);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while parsing expression '{}' of type '{}' using parser method: {}.{}", 
					expression, type, method.getDeclaringClass().getName(), method.getName());
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Type: ").append(type);
		builder.append(",").append("Method: ").append(method.getDeclaringClass().getName()).append(".").append(method.getName()).append("()");

		builder.append("]");
		return builder.toString();
	}

}
