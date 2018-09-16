package com.yukthitech.autox.ide.engine;

/**
 * Represents a step.
 * @author akiran
 */
public class StepDetails
{
	/**
	 * Step xml text.
	 */
	private String text;
	
	/**
	 * Step xml text in rtf format.
	 */
	private String rtfText;

	/**
	 * Instantiates a new step details.
	 *
	 * @param text the text
	 * @param rtfText the rtf text
	 */
	public StepDetails(String text, String rtfText)
	{
		this.text = text;
		this.rtfText = rtfText;
	}

	/**
	 * Gets the step xml text.
	 *
	 * @return the step xml text
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * Sets the step xml text.
	 *
	 * @param text the new step xml text
	 */
	public void setText(String text)
	{
		this.text = text;
	}

	/**
	 * Gets the step xml text in rtf format.
	 *
	 * @return the step xml text in rtf format
	 */
	public String getRtfText()
	{
		return rtfText;
	}

	/**
	 * Sets the step xml text in rtf format.
	 *
	 * @param rtfText the new step xml text in rtf format
	 */
	public void setRtfText(String rtfText)
	{
		this.rtfText = rtfText;
	}
}
