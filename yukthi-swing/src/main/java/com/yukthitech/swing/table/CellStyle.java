/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.swing.table;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.border.Border;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Basic attributes to render a cell with style.
 * @author akranthikiran
 */
public class CellStyle implements Cloneable
{
	/**
	 * Background for cell.
	 */
	private Color background;
	
	/**
	 * Foreground for cell.
	 */
	private Color foreground;
	
	/**
	 * Font to be used.
	 */
	private Font font;
	
	/**
	 * Border to be used.
	 */
	private Border border;
	
	/**
	 * Gets the background for cell.
	 *
	 * @return the background for cell
	 */
	public Color getBackground()
	{
		return background;
	}

	/**
	 * Sets the background.
	 *
	 * @param background the background
	 * 
	 * @return the cell style
	 */
	public CellStyle setBackground(Color background)
	{
		this.background = background;
		return this;
	}

	/**
	 * Gets the foreground for cell.
	 *
	 * @return the foreground for cell
	 */
	public Color getForeground()
	{
		return foreground;
	}

	/**
	 * Sets the foreground.
	 *
	 * @param foreground the foreground
	 * 
	 * @return the cell style
	 */
	public CellStyle setForeground(Color foreground)
	{
		this.foreground = foreground;
		return this;
	}

	/**
	 * Gets the font to be used.
	 *
	 * @return the font to be used
	 */
	public Font getFont()
	{
		return font;
	}

	/**
	 * Sets the font.
	 *
	 * @param font the font
	 * 
	 * @return the cell style
	 */
	public CellStyle setFont(Font font)
	{
		this.font = font;
		return this;
	}
	
	/**
	 * Gets the border to be used.
	 *
	 * @return the border to be used
	 */
	public Border getBorder()
	{
		return border;
	}

	/**
	 * Sets the border to be used.
	 *
	 * @param border the new border to be used
	 */
	public CellStyle setBorder(Border border)
	{
		this.border = border;
		return this;
	}

	/**
	 * Invoked to customize given element with current style attributes.
	 *
	 * @param c the c
	 */
	protected void customize(JComponent c)
	{
		if(background != null)
		{
			c.setBackground(background);
		}
		
		if(foreground != null)
		{
			c.setForeground(foreground);
		}
		
		if(border != null)
		{
			c.setBorder(border);
		}
		
		if(font != null)
		{
			c.setFont(font);
		}
	}
	
	@Override
	public CellStyle clone()
	{
		try
		{
			return (CellStyle) super.clone();
		} catch(CloneNotSupportedException ex)
		{
			throw new InvalidStateException("An error occurred during style cloning", ex);
		}
	}
}
