package com.yukthitech.autox.expr;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Expression parser details.
 * 
 * @author akiran
 */
public class ExpressionParserDetails
{
	/**
	 * Parameter of the parser.
	 * @author akiran
	 */
	public static class Param
	{
		/**
		 * Name of the param.
		 */
		private String name;
		
		/**
		 * Type of the param.
		 */
		private String type;
		
		/**
		 * Default value.
		 */
		private String defaultValue;
		
		/**
		 * Description.
		 */
		private String description;
		
		public Param()
		{}

		public Param(String name, String type, String defaultValue, String description)
		{
			this.name = name;
			this.type = type;
			this.defaultValue = defaultValue;
			this.description = description;
		}



		/**
		 * Gets the name of the param.
		 *
		 * @return the name of the param
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Sets the name of the param.
		 *
		 * @param name the new name of the param
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 * Gets the type of the param.
		 *
		 * @return the type of the param
		 */
		public String getType()
		{
			return type;
		}

		/**
		 * Sets the type of the param.
		 *
		 * @param type the new type of the param
		 */
		public void setType(String type)
		{
			this.type = type;
		}

		/**
		 * Gets the default value.
		 *
		 * @return the default value
		 */
		public String getDefaultValue()
		{
			return defaultValue;
		}

		/**
		 * Sets the default value.
		 *
		 * @param defaultValue the new default value
		 */
		public void setDefaultValue(String defaultValue)
		{
			this.defaultValue = defaultValue;
		}

		/**
		 * Gets the description.
		 *
		 * @return the description
		 */
		public String getDescription()
		{
			return description;
		}

		/**
		 * Sets the description.
		 *
		 * @param description the new description
		 */
		public void setDescription(String description)
		{
			this.description = description;
		}
	}
	
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
	 * Expected content type of the expression.
	 */
	private ParserContentType contentType;
	
	/**
	 * Params supported by this parser.
	 */
	private List<Param> params;
	
	/**
	 * Instantiates a new expression parser details.
	 */
	public ExpressionParserDetails()
	{}
	
	/**
	 * Instantiates a new expression parser details.
	 *
	 * @param type the type
	 * @param description the description
	 * @param example the example
	 * @param enclosingObject the enclosing object
	 * @param method the method
	 * @param contentType the content type
	 */
	public ExpressionParserDetails(String type, String description, String example, Object enclosingObject, Method method, ParserContentType contentType)
	{
		this.type = type;
		this.description = description;
		this.example = example;
		this.enclosingObject = enclosingObject;
		this.method = method;
		this.contentType = contentType;
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
	 * Fetches if the parser can take of expression value conversion.
	 * @return
	 */
	public boolean isConversionHandled()
	{
		return (method.getParameterTypes().length == 3);
	}
	
	/**
	 * Invokes underlying parser method and returns the result.
	 * @param context context to be used
	 * @param expression expression to be executed
	 * @return result
	 */
	public IPropertyPath invoke(ExpressionParserContext context, String expression, String expectedType[])
	{
		try
		{
			if(method.getParameterTypes().length == 2)
			{
				return (IPropertyPath) method.invoke(enclosingObject, context, expression);
			}
			else
			{
				return (IPropertyPath) method.invoke(enclosingObject, context, expression, expectedType);
			}
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while parsing expression '{}' of type '{}' using parser method: {}.{}", 
					expression, type, method.getDeclaringClass().getName(), method.getName());
		}
	}
	
	/**
	 * Sets the expected content type of the expression.
	 *
	 * @param contentType the new expected content type of the expression
	 */
	public void setContentType(ParserContentType contentType)
	{
		this.contentType = contentType;
	}
	
	/**
	 * Gets the expected content type of the expression.
	 *
	 * @return the expected content type of the expression
	 */
	public ParserContentType getContentType()
	{
		return contentType;
	}

	/**
	 * Adds the param.
	 *
	 * @param param the param
	 */
	public void addParam(Param param)
	{
		if(this.params == null)
		{
			this.params = new ArrayList<>();
		}
		
		this.params.add(param);
	}
	
	/**
	 * Gets the params supported by this parser.
	 *
	 * @return the params supported by this parser
	 */
	public List<Param> getParams()
	{
		return params;
	}

	/**
	 * Sets the params supported by this parser.
	 *
	 * @param params the new params supported by this parser
	 */
	public void setParams(List<Param> params)
	{
		this.params = params;
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
