package com.yukthitech.autox.doc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.filter.ParserContentType;

/**
 * Expression parser details.
 * @author akiran
 */
public class ExpressionParserDoc extends AbstractDocInfo
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
	 * Name of the expression parser.
	 */
	private String name;
	
	/**
	 * Description about the parser.
	 */
	private String description;
	
	/**
	 * Expected content type of the parser.
	 */
	private ParserContentType contentType;
	
	/**
	 * Examples of the parser.
	 */
	private List<Example> examples = new ArrayList<>();
	
	/**
	 * Params supported by this parser.
	 */
	private List<Param> params;

	/**
	 * Instantiates a new expression parser doc.
	 */
	public ExpressionParserDoc()
	{}
	
	/**
	 * Instantiates a new expression parser doc.
	 *
	 * @param name the name
	 * @param description the description
	 */
	public ExpressionParserDoc(String name, String description, ParserContentType contentType)
	{
		this.name = name;
		this.description = description;
		this.contentType = contentType;
	}

	/**
	 * Gets the name of the expression parser.
	 *
	 * @return the name of the expression parser
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the expression parser.
	 *
	 * @param name the new name of the expression parser
	 */
	public void setName(String name)
	{
		this.name = name;
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
	 * Gets the examples of the parser.
	 *
	 * @return the examples of the parser
	 */
	public List<Example> getExamples()
	{
		return examples;
	}

	/**
	 * Sets the examples of the parser.
	 *
	 * @param examples the new examples of the parser
	 */
	public void setExamples(List<Example> examples)
	{
		this.examples = examples;
	}
	
	/**
	 * Adds the example.
	 *
	 * @param ex the ex
	 */
	public void addExample(Example ex)
	{
		this.examples.add(ex);
	}
	
	public void addExamples(Collection<Example> examples)
	{
		if(examples == null)
		{
			return;
		}
		
		this.examples.addAll(examples);
	}

	public boolean hasExamples()
	{
		return CollectionUtils.isNotEmpty(examples);
	}

	public ParserContentType getContentType()
	{
		return contentType;
	}

	public void setContentType(ParserContentType contentType)
	{
		this.contentType = contentType;
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
	
	public boolean hasParams()
	{
		return CollectionUtils.isNotEmpty(params);
	}
}
