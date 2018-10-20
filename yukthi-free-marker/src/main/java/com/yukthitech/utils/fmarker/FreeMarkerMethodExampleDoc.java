package com.yukthitech.utils.fmarker;

import com.yukthitech.utils.fmarker.annotaion.ExampleDoc;

/**
 * Represents method example documentation.
 * @author akiran
 */
public class FreeMarkerMethodExampleDoc
{
	/**
	 * Usage example.
	 */
	private String usage;
	
	/**
	 * Result of the usage.
	 */
	private String result;
	
	/**
	 * Instantiates a new free marker method example doc.
	 */
	public FreeMarkerMethodExampleDoc()
	{}
	
	/**
	 * Instantiates a new free marker method example doc.
	 *
	 * @param doc the doc
	 */
	public FreeMarkerMethodExampleDoc(ExampleDoc doc)
	{
		this.usage = doc.usage();
		this.result = doc.result();
	}

	/**
	 * Gets the usage example.
	 *
	 * @return the usage example
	 */
	public String getUsage()
	{
		return usage;
	}

	/**
	 * Gets the result of the usage.
	 *
	 * @return the result of the usage
	 */
	public String getResult()
	{
		return result;
	}
}
