package com.yukthitech.autox.ide.xmlfile;

import com.yukthitech.utils.exceptions.UtilsException;

public class XmlParseException extends UtilsException
{
	private static final long serialVersionUID = 1L;
	
	private XmlFile xmlFile;
	
	private int offset;
	
	private int lineNumber;
	
	private int columnNumber;
	
	public XmlParseException(XmlFile xmlFile, int offset, int lineNo, int colNo, String mssg, Object... params)
	{
		super(getPositionString(lineNo, colNo) + mssg, params);
		
		this.offset = offset;
		this.xmlFile = xmlFile;
		this.lineNumber = lineNo;
		this.columnNumber = colNo;
	}
	
	private static String getPositionString(int lineNo, int colNo)
	{
		return "[Line: " + lineNo + ", Column: " + colNo + "] "; 
	}
	
	public int getOffset()
	{
		return offset;
	}
	
	public XmlFile getXmlFile()
	{
		return xmlFile;
	}
	
	public int getLineNumber()
	{
		return lineNumber;
	}
	
	public int getColumnNumber()
	{
		return columnNumber;
	}
}
