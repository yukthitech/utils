package com.yukthitech.autox.ide.model;

import java.awt.Font;
import java.io.Serializable;

/**
 * Setting of ide.
 * @author akiran
 */
public class IdeSettings implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Font to be used for editors.
	 */
	private Font editorFont;

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
}
