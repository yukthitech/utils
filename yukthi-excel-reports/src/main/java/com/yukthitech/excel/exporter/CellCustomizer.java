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
package com.yukthitech.excel.exporter;

import java.awt.Color;

import com.yukthitech.excel.exporter.style.Border;

/**
 * Used to customize the heading cell of the generated excel sheets
 * @author akiran
 */
public class CellCustomizer
{
	/**
	 * Background color of the target cell
	 */
	private Color background;

	/**
	 * Border for the style
	 */
	private Border border;
	
	/**
	 * Color for border
	 */
	private Color borderColor = Color.BLACK;
	
	/**
	 * Gets the background color of the target cell.
	 *
	 * @return the background color of the target cell
	 */
	public Color getBackground()
	{
		return background;
	}

	/**
	 * Sets the background color of the target cell.
	 *
	 * @param background the new background color of the target cell
	 */
	public void setBackground(Color background)
	{
		this.background = background;
	}

	/**
	 * Gets the border for the style.
	 *
	 * @return the border for the style
	 */
	public Border getBorder()
	{
		return border;
	}

	/**
	 * Sets the border for the style.
	 *
	 * @param border the new border for the style
	 */
	public void setBorder(Border border)
	{
		this.border = border;
	}

	/**
	 * Gets the color for border.
	 *
	 * @return the color for border
	 */
	public Color getBorderColor()
	{
		return borderColor;
	}

	/**
	 * Sets the color for border.
	 *
	 * @param borderColor the new color for border
	 */
	public void setBorderColor(Color borderColor)
	{
		this.borderColor = borderColor;
	}
	
	
}
