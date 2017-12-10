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

package com.yukthitech.excel.exporter.style;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * Specifies border styles for cell
 * @author akiran
 */
public class Border
{
	/**
	 * Left border style
	 */
	private BorderStyle left;
	
	/**
	 * Bottom border style
	 */
	private BorderStyle bottom;
	
	/**
	 * Right border style
	 */
	private BorderStyle right;

	/**
	 * Top border style
	 */
	private BorderStyle top;
	
	/**
	 * Instantiates a new border.
	 */
	public Border()
	{}

	/**
	 * Instantiates a new border.
	 *
	 * @param left the left
	 * @param bottom the bottom
	 * @param right the right
	 * @param top the top
	 */
	public Border(BorderStyle left, BorderStyle bottom, BorderStyle right, BorderStyle top)
	{
		this.left = left;
		this.bottom = bottom;
		this.right = right;
		this.top = top;
	}

	/**
	 * Gets the left border style.
	 *
	 * @return the left border style
	 */
	public BorderStyle getLeft()
	{
		return left;
	}

	/**
	 * Sets the left border style.
	 *
	 * @param left the new left border style
	 */
	public void setLeft(BorderStyle left)
	{
		this.left = left;
	}

	/**
	 * Gets the bottom border style.
	 *
	 * @return the bottom border style
	 */
	public BorderStyle getBottom()
	{
		return bottom;
	}

	/**
	 * Sets the bottom border style.
	 *
	 * @param bottom the new bottom border style
	 */
	public void setBottom(BorderStyle bottom)
	{
		this.bottom = bottom;
	}

	/**
	 * Gets the right border style.
	 *
	 * @return the right border style
	 */
	public BorderStyle getRight()
	{
		return right;
	}

	/**
	 * Sets the right border style.
	 *
	 * @param right the new right border style
	 */
	public void setRight(BorderStyle right)
	{
		this.right = right;
	}

	/**
	 * Gets the top border style.
	 *
	 * @return the top border style
	 */
	public BorderStyle getTop()
	{
		return top;
	}

	/**
	 * Sets the top border style.
	 *
	 * @param top the new top border style
	 */
	public void setTop(BorderStyle top)
	{
		this.top = top;
	}
	
	/**
	 * Applies current border to specified cell style
	 * @param cellStyle
	 */
	public void applyToCell(CellStyle cellStyle, short colorIndex)
	{
		if(left != null)
		{
			cellStyle.setBorderLeft(left);
			cellStyle.setLeftBorderColor(colorIndex);
		}
		
		if(bottom != null)
		{
			cellStyle.setBorderBottom(bottom);
			cellStyle.setBottomBorderColor(colorIndex);
		}

		if(right != null)
		{
			cellStyle.setBorderRight(right);
			cellStyle.setRightBorderColor(colorIndex);
		}
		
		if(top != null)
		{
			cellStyle.setBorderTop(top);
			cellStyle.setTopBorderColor(colorIndex);
		}
	}
}
