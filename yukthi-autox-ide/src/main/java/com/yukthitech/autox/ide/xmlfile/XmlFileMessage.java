package com.yukthitech.autox.ide.xmlfile;

public class XmlFileMessage
{
	private MessageType messageType;
	
	private String message;
	
	private int lineNo;
	
	private int startColumn;
	
	private int endColumn;
	
	public XmlFileMessage()
	{}

	public XmlFileMessage(MessageType messageType, String message, int lineNo)
	{
		this.messageType = messageType;
		this.message = message;
		this.lineNo = lineNo;
	}
	
	public XmlFileMessage(MessageType messageType, String message, int lineNo, int startColumn, int endColumn)
	{
		this.messageType = messageType;
		this.message = message;
		this.lineNo = lineNo;
		this.startColumn = startColumn;
		this.endColumn = endColumn;
	}

	public MessageType getMessageType()
	{
		return messageType;
	}

	public void setMessageType(MessageType messageType)
	{
		this.messageType = messageType;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public int getLineNo()
	{
		return lineNo;
	}

	public void setLineNo(int lineNo)
	{
		this.lineNo = lineNo;
	}

	public int getStartColumn()
	{
		return startColumn;
	}

	public void setStartColumn(int startColumn)
	{
		this.startColumn = startColumn;
	}

	public int getEndColumn()
	{
		return endColumn;
	}

	public void setEndColumn(int endColumn)
	{
		this.endColumn = endColumn;
	}
}
