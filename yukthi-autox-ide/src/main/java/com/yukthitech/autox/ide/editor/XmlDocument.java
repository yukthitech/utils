package com.yukthitech.autox.ide.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlDocument
{
	public static class Attribute
	{
		private String prefix;
		
		private String name;
		
		private String value;

		public Attribute(String prefix, String name)
		{
			this.prefix = prefix;
			this.name = name;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getPrefix()
		{
			return prefix;
		}
		
		public void setValue(String value)
		{
			this.value = value;
		}
		
		public String getValue()
		{
			return value;
		}
	}
	
	public static class Node
	{
		private Node parent;
		
		private String prefix;
		
		private String name;
		
		private List<Attribute> attributes = new ArrayList<>();
		
		private List<Node> childNodes = new ArrayList<>();

		public Node(Node parent, String prefix, String name)
		{
			this.parent = parent;
			this.prefix = prefix;
			this.name = name;
		}
		
		public String getPrefix()
		{
			return prefix;
		}
		
		public String getName()
		{
			return name;
		}
		
		public Node getParent()
		{
			return parent;
		}
		
		public void addAttribute(Attribute attr)
		{
			this.attributes.add(attr);
		}
		
		public Attribute getAttribute(String name)
		{
			for(Attribute attr : this.attributes)
			{
				if(name.equals(attr.name))
				{
					return attr;
				}
			}
			
			return null;
		}
		
		public void addChildNode(Node node)
		{
			this.childNodes.add(node);
		}
	}
	
	private Map<String, String> nameSpaces = new HashMap<>();
	
	private Node root;
	
	private Node currentNode;
	
	public void addNameSpace(String prefix, String uri)
	{
		nameSpaces.put(prefix, uri);
	}
	
	public String getNameSpaceUri(String prefix)
	{
		return nameSpaces.get(prefix);
	}
	
	public void nodeStarted(Node node)
	{
		if(root == null)
		{
			root = node;
			currentNode = node;
			return;
		}
		
		if(currentNode != null)
		{
			currentNode.addChildNode(node);
		}
	}
	
	public void nodeEnded()
	{
		
	}
}
