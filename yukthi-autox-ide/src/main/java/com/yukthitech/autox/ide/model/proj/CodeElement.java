package com.yukthitech.autox.ide.model.proj;

import java.io.File;
import java.util.Set;

import com.yukthitech.autox.ide.FileParseCollector;

/**
 * Base class for elements.
 * @author akiran
 */
public abstract class CodeElement
{
	/**
	 * File in which element is defined.
	 */
	protected File file;
	
	/**
	 * Position at which element is defined.
	 */
	protected int position;
	
	protected CodeElement parent;

	/**
	 * Instantiates a new element.
	 *
	 * @param file the file
	 * @param position the position
	 */
	public CodeElement(File file, int position)
	{
		this.file = file;
		this.position = position;
	}

	/**
	 * Gets the file in which element is defined.
	 *
	 * @return the file in which element is defined
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * Gets the position at which element is defined.
	 *
	 * @return the position at which element is defined
	 */
	public int getPosition()
	{
		return position;
	}
	
	void setParent(CodeElement parent)
	{
		this.parent = parent;
	}
	
	protected void addFileElement(CodeElement element)
	{
		if(parent != null)
		{
			parent.addFileElement(element);
		}
	}
	
	/**
	 * Checks for reference errors recursively.
	 * @param collector
	 */
	public abstract void compile(FileParseCollector collector);
	
	public boolean isValidAppProperty(String name)
	{
		return parent.isValidAppProperty(name);
	}
	
	/**
	 * Fetches the def of specified attr into specified set.
	 * @param name name of attr being searched
	 * @param attrDefElem set to collect defs
	 */
	public void getAttrDef(String name, Set<AttrDefElement> attrDefElem)
	{}

	public void getFunctionDef(String name, Set<FunctionDefElement> funcDefSet)
	{}
}
