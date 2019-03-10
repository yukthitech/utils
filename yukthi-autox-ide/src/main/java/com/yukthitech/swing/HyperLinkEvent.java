package com.yukthitech.swing;

/**
 * Hyper link click event object. 
 * @author akiran
 */
public class HyperLinkEvent
{
	/**
	 * Href of the hyperlink.
	 */
	private String href;

	/**
	 * Instantiates a new hyper link event.
	 *
	 * @param href the href
	 */
	public HyperLinkEvent(String href)
	{
		this.href = href;
	}
	
	/**
	 * Gets the href of the hyperlink.
	 *
	 * @return the href of the hyperlink
	 */
	public String getHref()
	{
		return href;
	}
}
