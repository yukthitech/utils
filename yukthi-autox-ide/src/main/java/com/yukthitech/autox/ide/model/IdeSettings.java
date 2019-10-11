package com.yukthitech.autox.ide.model;

import java.awt.Font;
import java.io.Serializable;

/**
 * Setting of ide.
 * @author akiran
 */
public class IdeSettings implements Serializable
{
	
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Font to be used for editors.
	 */
	private Font editorFont;
	
	/**
	 * Flag indicating if text wrapping should be enabled.
	 */
	private boolean enableTextWrapping;

	/**
	 * Gets the font to be used for editors.
	 *
	 * @return the font to be used for editors
	 */
	public Font getEditorFont()
	{
		return editorFont;
	}

	/**
	 * Sets the font to be used for editors.
	 *
	 * @param editorFont the new font to be used for editors
	 */
	public void setEditorFont(Font editorFont)
	{
		this.editorFont = editorFont;
	}

	/**
	 * Gets the flag indicating if text wrapping should be enabled.
	 *
	 * @return the flag indicating if text wrapping should be enabled
	 */
	public boolean isEnableTextWrapping()
	{
		return enableTextWrapping;
	}

	/**
	 * Sets the flag indicating if text wrapping should be enabled.
	 *
	 * @param enableTextWrapping the new flag indicating if text wrapping should be enabled
	 */
	public void setEnableTextWrapping(boolean enableTextWrapping)
	{
		this.enableTextWrapping = enableTextWrapping;
	}
}
