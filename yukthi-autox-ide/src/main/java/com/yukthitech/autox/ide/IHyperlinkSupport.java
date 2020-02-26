package com.yukthitech.autox.ide;

/**
 * Functionality that optionally be implemented by {@link IIdeFileManager} to support links to other files. 
 * @author akiran
 */
public interface IHyperlinkSupport
{
	/**
	 * If any hperlink is suppose to come, then 
	 * @param position
	 * @param fileContentObject
	 * @return
	 */
	public LinkWithLocation getLinkLocation(int position, Object fileContentObject);
}
