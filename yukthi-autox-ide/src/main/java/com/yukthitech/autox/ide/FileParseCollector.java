package com.yukthitech.autox.ide;

import java.util.LinkedList;
import java.util.List;

import com.yukthitech.autox.ide.editor.FileParseMessage;
import com.yukthitech.autox.ide.xmlfile.MessageType;

/**
 * Collector to collect the file parsing results.
 * @author akiran
 */
public class FileParseCollector
{
	private List<FileParseMessage> messages = new LinkedList<>();
	
	private int errorCount;
	
	private int warningCount;
	
	public void load(FileParseCollector collector)
	{
		for(FileParseMessage mssg : collector.messages)
		{
			addMessage(mssg);
		}
	}
	
	public void addMessage(FileParseMessage mssg)
	{
		this.messages.add(mssg);
		
		if(mssg.getMessageType() == MessageType.ERROR)
		{
			errorCount++;
		}
		
		if(mssg.getMessageType() == MessageType.WARNING)
		{
			warningCount++;
		}
	}
	
	public List<FileParseMessage> getMessages()
	{
		return this.messages;
	}
	
	public int getErrorCount()
	{
		return errorCount;
	}
	
	public int getWarningCount()
	{
		return warningCount;
	}
}
