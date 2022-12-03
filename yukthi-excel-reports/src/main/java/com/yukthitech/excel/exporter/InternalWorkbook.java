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
