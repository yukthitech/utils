package com.yukthitech.autox.ide.xmlfile;

public class XmlFileMessage
{
	private MessageType messageType;
	
	private String message;
	
	private int lineNo;
	
	private int startOffset = -1;
	
	private int endOffset;
	
	public XmlFileMessage()
	{}

	public XmlFileMessage(MessageType messageType, String message, int lineNo)
	{
		this.messageType = messageType;
		this.message = message;
		this.lineNo = lineNo;
	}
	
	public XmlFileMessage(MessageType messageType, String message, int lineNo, int startOffset, int endOffset)
	{
		this.messageType = messageType;
		this.message = message;
		this.lineNo = lineNo;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
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

	public int getStartOffset()
	{
		return startOffset;
	}

	public void setStartOffset(int startOffset)
	{
		this.startOffset = startOffset;
	}

	public int getEndOffset()
	{
		return endOffset;
	}
	
	public boolean hasValidOffsets()
	{
		if(startOffset < 0)
		{
			return false;
		}
		
		return (endOffset > startOffset);
	}

	public void setEndOffset(int endOffset)
	{
		this.endOffset = endOffset;
	}
}
