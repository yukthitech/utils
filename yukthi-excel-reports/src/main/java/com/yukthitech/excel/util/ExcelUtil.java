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
package com.yukthitech.excel.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelUtil
{

	public static void copySheets(HSSFSheet newSheet, HSSFSheet sheet)
	{
		copySheets(newSheet, sheet, true);
	}

	public static void copySheets(HSSFSheet newSheet, HSSFSheet sheet, boolean copyStyle)
	{
		int maxColumnNum = 0;
		Map<Integer, HSSFCellStyle> styleMap = (copyStyle)? new HashMap<Integer, HSSFCellStyle>(): null;
		for(int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++)
		{
			HSSFRow srcRow = sheet.getRow(i);
			HSSFRow destRow = newSheet.createRow(i);
			if(srcRow != null)
			{
				ExcelUtil.copyRow(sheet, newSheet, srcRow, destRow, styleMap);
				if(srcRow.getLastCellNum() > maxColumnNum)
				{
					maxColumnNum = srcRow.getLastCellNum();
				}
			}
		}
		for(int i = 0; i <= maxColumnNum; i++)
		{
			newSheet.setColumnWidth(i, sheet.getColumnWidth(i));
		}
	}

	public static void copyRow(HSSFSheet srcSheet, HSSFSheet destSheet, HSSFRow srcRow, HSSFRow destRow, Map<Integer, HSSFCellStyle> styleMap)
	{
		Set<CellRangeAddress> mergedRegions = new TreeSet<CellRangeAddress>();
		destRow.setHeight(srcRow.getHeight());
		for(int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++)
		{
			HSSFCell oldCell = srcRow.getCell(j);
			HSSFCell newCell = destRow.getCell(j);
			if(oldCell != null)
			{
				if(newCell == null)
				{
					newCell = destRow.createCell(j);
				}
				copyCell(oldCell, newCell, styleMap);
				CellRangeAddress mergedRegion = getMergedRegion(srcSheet, srcRow.getRowNum(), (short)oldCell.getColumnIndex());
				if(mergedRegion != null)
				{
					CellRangeAddress newMergedRegion = new CellRangeAddress(mergedRegion.getFirstRow(), mergedRegion.getFirstColumn(), mergedRegion.getLastRow(), mergedRegion.getLastColumn());
					if(isNewMergedRegion(newMergedRegion, mergedRegions))
					{
						mergedRegions.add(newMergedRegion);
						destSheet.addMergedRegion(newMergedRegion);
					}
				}
			}
		}

	}

	public static void copyCell(HSSFCell oldCell, HSSFCell newCell, Map<Integer, HSSFCellStyle> styleMap)
	{
		if(styleMap != null)
		{
			if(oldCell.getSheet().getWorkbook() == newCell.getSheet().getWorkbook())
			{
				newCell.setCellStyle(oldCell.getCellStyle());
			}
			else
			{
				int stHashCode = oldCell.getCellStyle().hashCode();
				HSSFCellStyle newCellStyle = styleMap.get(stHashCode);
				if(newCellStyle == null)
				{
					newCellStyle = newCell.getSheet().getWorkbook().createCellStyle();
					newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
					styleMap.put(stHashCode, newCellStyle);
				}
				newCell.setCellStyle(newCellStyle);
			}
		}
		
		switch(oldCell.getCellType())
		{
			case STRING:
				newCell.setCellValue(oldCell.getStringCellValue());
				break;
			case NUMERIC:
				newCell.setCellValue(oldCell.getNumericCellValue());
				break;
			case BLANK:
				newCell.setCellType(CellType.BLANK);
				break;
			case BOOLEAN:
				newCell.setCellValue(oldCell.getBooleanCellValue());
				break;
			case ERROR:
				newCell.setCellType(CellType.ERROR);
				break;
			case FORMULA:
				newCell.setCellFormula(oldCell.getCellFormula());
				break;
			default:
				break;
		}

	}

	public static CellRangeAddress getMergedRegion(HSSFSheet sheet, int rowNum, short cellNum)
	{
		for(int i = 0; i < sheet.getNumMergedRegions(); i++)
		{
			CellRangeAddress merged = sheet.getMergedRegion(i);
			if(merged.isInRange(rowNum, cellNum))
			{
				return merged;
			}
		}
		return null;
	}

	private static boolean isNewMergedRegion(CellRangeAddress newMergedRegion, Collection<CellRangeAddress> mergedRegions)
	{
		return !mergedRegions.contains(newMergedRegion);
	}
}
