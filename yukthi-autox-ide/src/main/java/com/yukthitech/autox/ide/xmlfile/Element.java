package com.yukthitech.autox.ide.xmlfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Element
{
	private Element parentElement;
	
	private String namespace;
	
	private String name;
	
	private List<Element> elements = new ArrayList<>();
	
	private Map<String, Attribute> attributes = new HashMap<>();
	
	private int startLineNo;
	
	private int endLineNo;
	
	public Element()
	{}

	public Element(Element parentElement, String namespace, String name, int startLineNo)
	{
		this.parentElement = parentElement;
		this.namespace = namespace;
		this.name = name;
		this.startLineNo = startLineNo;
	}
	
	public Element getParentElement()
	{
		return parentElement;
	}

	public void setParentElement(Element parentElement)
	{
		this.parentElement = parentElement;
	}

	public String getNamespace()
	{
		return namespace;
	}

	public void setNamespace(String namespace)
	{
		this.namespace = namespace;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<Element> getElements()
	{
		return elements;
	}

	public void setElements(List<Element> elements)
	{
		this.elements = elements;
	}
	
	public void addElement(Element element)
	{
		this.elements.add(element);
	}

	public int getStartLineNo()
	{
		return startLineNo;
	}

	public void setStartLineNo(int startLineNo)
	{
		this.startLineNo = startLineNo;
	}

	public int getEndLineNo()
	{
		return endLineNo;
	}

	public void setEndLineNo(int endLineNo)
	{
		this.endLineNo = endLineNo;
	}
	
	public void addAttribute(Attribute attr)
	{
		this.attributes.put(attr.getName(), attr);
	}
	
	public Attribute getAttribute(String name)
	{
		return this.attributes.get(name);
	}
	
	private void toText(String indent, StringBuilder builder)
	{
		builder.append(indent).append(name).append(attributes).append("\n");
		
		for(Element element : this.elements)
		{
			element.toText(indent + "\t", builder);
		}
	}
	
	public boolean hasLineNumber(int lineNo)
	{
		return (lineNo >= startLineNo && lineNo <= endLineNo);
	}
	
	public Element getElement(String withName, int curLineNo)
	{
		String elemName = name.toLowerCase().replaceAll("\\W+", "");
		
		if(elemName.equals(withName))
		{
			return this;
		}

		for(Element elem : this.elements)
		{
			if(elem.hasLineNumber(curLineNo))
			{
				return elem.getElement(withName, curLineNo);
			}
			
			if(curLineNo <= elem.getEndLineNo())
			{
				break;
			}
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("\n");
		toText("\t", builder);
		
		return builder.toString();
	}

}
