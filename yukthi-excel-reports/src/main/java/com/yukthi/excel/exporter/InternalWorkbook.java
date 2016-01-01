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

package com.yukthi.excel.exporter;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

/**
 * Wrapper over internal workbook
 * 
 * @author akiran
 */
public class InternalWorkbook
{
	/**
	 * Actual workbook
	 */
	private HSSFWorkbook workbook;

	/**
	 * Index at which next color needs to be added
	 */
	private short nextColorIndex = PaletteRecord.FIRST_COLOR_INDEX;

	/**
	 * Color to index mapping
	 */
	private Map<Color, Integer> colorToIndex = new HashMap<>();

	public InternalWorkbook(HSSFWorkbook workbook)
	{
		this.workbook = workbook;
	}
	
	/**
	 * @return the {@link #workbook workbook}
	 */
	public HSSFWorkbook getWorkbook()
	{
		return workbook;
	}

	/**
	 * Gets the index of specified color in the current workbook
	 * 
	 * @param color
	 * @return
	 */
	public HSSFColor getColor(Color color)
	{
		Integer index = colorToIndex.get(color);
		HSSFPalette palette = workbook.getCustomPalette();

		// if color was already added get color based on found index
		if(index != null)
		{
			return palette.getColor(index);
		}

		//add color to the workbook and return the same
		HSSFColor hssfColor = null;

		try
		{
			palette.setColorAtIndex(nextColorIndex, (byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());
			hssfColor = palette.getColor(nextColorIndex);

			nextColorIndex++;
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while adding color to workbook", ex);
		}

		return hssfColor;
	}
}
