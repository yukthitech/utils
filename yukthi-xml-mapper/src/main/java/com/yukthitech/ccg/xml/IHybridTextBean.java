package com.yukthitech.ccg.xml;

/**
 * Bean implementing this interface indicates parser this bean supports hybrid content (nodes and intermediate text).
 * {@link #addText(String)} method will be called by parser whenever it encounters non empty text between the nodes 
 * and directly under this bean node.
 * 
 * @author akiran
 */
public interface IHybridTextBean
{
	public void addText(String text);
}
