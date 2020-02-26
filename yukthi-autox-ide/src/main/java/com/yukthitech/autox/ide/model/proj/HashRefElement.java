package com.yukthitech.autox.ide.model.proj;

import java.io.File;

import com.yukthitech.autox.ide.FileParseCollector;
import com.yukthitech.autox.ide.editor.FileParseMessage;
import com.yukthitech.autox.ide.xmlfile.MessageType;

/**
 * Element where attribute is referred.
 * @author akiran
 */
public class HashRefElement extends ReferenceElement
{
	/**
	 * Hash property being referred.
	 */
	private String name;

	public HashRefElement(File file, int lineNo, int pos, int end, String name)
	{
		super(file, lineNo, pos, end);
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	@Override
	public void compile(FileParseCollector collector)
	{
		if(!super.isValidAppProperty(name))
		{
			collector.addMessage(new FileParseMessage(MessageType.ERROR, 
					"No app-property found with refered name: " + name, super.lineNo, super.position, super.end));
		}
	}
}
