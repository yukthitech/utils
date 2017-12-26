/*
 * 
 */
package com.yukthitech.autox.ide.model;

import java.io.Serializable;

/**
 * Represents executed step uniquely.
 * @author akiran
 */
public class ExecutedStep implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static int ID_TRACKER = 1;
	
	/**
	 * Id of the step.
	 */
	private int id;
	
	/**
	 * Text of the step.
	 */
	private String text;
	
	/**
	 * Rtf text.
	 */
	private String rtfText;

	public ExecutedStep(String text, String rtfText)
	{
		this.id = (ID_TRACKER++);
		this.text = text;
		this.rtfText = rtfText;
	}

	public int getId()
	{
		return id;
	}

	public String getText()
	{
		return text;
	}
	
	/**
	 * Gets the rtf text.
	 *
	 * @return the rtf text
	 */
	public String getRtfText()
	{
		return rtfText;
	}
}
