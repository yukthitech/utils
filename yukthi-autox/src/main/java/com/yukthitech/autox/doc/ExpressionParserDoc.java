package com.yukthitech.autox.doc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.expr.ParserContentType;

/**
 * Expression parser details.
 * @author akiran
 */
public class ExpressionParserDoc
{
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
}
