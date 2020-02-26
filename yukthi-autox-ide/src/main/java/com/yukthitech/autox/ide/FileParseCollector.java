package com.yukthitech.autox.ide;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.yukthitech.autox.ide.editor.FileParseMessage;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.xmlfile.Element;
import com.yukthitech.autox.ide.xmlfile.MessageType;

/**
 * Collector to collect the file parsing results.
 * @author akiran
 */
public class FileParseCollector
{
	private List<LinkWithLocation> links = new LinkedList<>();
	
	private List<FileParseMessage> messages = new LinkedList<>();
	
	private int errorCount;
	
	private int warningCount;
	
	/**
	 * Project under which parsing is being done.
	 */
	private Project project;
	
	/**
	 * File being parsed.
	 */
	private File file;
	
	public FileParseCollector(Project project, File file)
	{
		this.project = project;
		this.file = file;
	}

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
	
	public void addLink(LinkWithLocation link)
	{
		this.links.add(link);
	}
	
	public List<LinkWithLocation> getLinks()
	{
		return links;
	}
	
	public void elementStarted(Element element)
	{
		//project.getProjectElementTracker().elementStarted(file, element, this);
	}
	
	public void elementEnded(Element element)
	{
		//project.getProjectElementTracker().elementEnded(file, element, this);
	}
	
	public void addFunctionRef(Element element)
	{
		//project.getProjectElementTracker().addFunctionRef(file, element, this);
	}
}
