/*
 * The MIT License (MIT)
 * Copyright (c) 2016 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
