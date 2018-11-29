package com.yukthitech.autox.ide.xmlfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.doc.StepInfo;
import com.yukthitech.autox.doc.ValidationInfo;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.test.TestDataFile;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.XMLConstants;
import com.yukthitech.utils.beans.BeanProperty;
import com.yukthitech.utils.beans.BeanPropertyInfoFactory;

public class Element implements INode
{
	private Element parentElement;
	
	private String prefix;
	
	private String namespace;
	
	private String name;
	
	private List<INode> nodes = new ArrayList<>();
	
	private Map<String, Attribute> attributes = new HashMap<>();

	private LocationRange startLocation;
	
	private LocationRange endLocation;
	
	private Class<?> elementType;
	
	/**
	 * Prefix to namespace mapping.
	 */
	private Map<String, String> prefixToNamespace;
	
	/**
	 * Prefix to namespace mapping.
	 */
	private Map<String, String> namespaceToPrefix;

	public Element()
	{}

	public Element(Element parentElement, String prefix, String namespace, String name, LocationRange startLocation)
	{
		this.prefix = prefix;
		this.parentElement = parentElement;
		this.namespace = namespace;
		this.name = name;
		this.startLocation = startLocation;
	}

	public void addNameSpaceMapping(String prefix, String namespace)
	{
		if(this.prefixToNamespace == null)
		{
			this.prefixToNamespace = new HashMap<>();
			this.namespaceToPrefix = new HashMap<>();
		}
		
		this.prefixToNamespace.put(prefix, namespace);
		this.namespaceToPrefix.put(namespace, prefix);
	}
	
	public LocationRange getStartLocation()
	{
		return startLocation;
	}

	public void setStartLocation(LocationRange startLocation)
	{
		this.startLocation = startLocation;
	}

	public LocationRange getEndLocation()
	{
		return endLocation;
	}

	public void setEndLocation(LocationRange endLocation)
	{
		this.endLocation = endLocation;
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
	
	public String getNormalizedName()
	{
		return name.toLowerCase().replaceAll("\\W+", "");
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<INode> getNodes()
	{
		return nodes;
	}

	public void setNodes(List<INode> nodes)
	{
		this.nodes = nodes;
	}

	public void addNode(INode element)
	{
		this.nodes.add(element);
	}

	public void addAttribute(Attribute attr)
	{
		this.attributes.put(attr.getName(), attr);
	}
	
	public String getPrefix()
	{
		return prefix;
	}
	
	public Attribute getAttribute(String name)
	{
		return this.attributes.get(name);
	}
	
	public Class<?> getElementType()
	{
		return elementType;
	}
	
	public void toText(String indent, StringBuilder builder)
	{
		builder.append("\n").append(indent)
			.append(startLocation).append(" - ").append(endLocation).append(" ")
			.append(name).append(attributes);
		
		for(INode element : this.nodes)
		{
			element.toText(indent + "\t", builder);
		}
	}
	
	public boolean hasOffset(int offset)
	{
		if(offset < startLocation.getStartOffset())
		{
			return false;
		}
		
		if(endLocation == null || offset <= endLocation.getEndOffset())
		{
			return true;
		}
		
		return false;
	}
	
	public boolean isSameName(String name)
	{
		String elemName = this.name.toLowerCase().replaceAll("\\W+", "");
		name = name.toLowerCase().replaceAll("\\W+", "");
		
		return elemName.equals(name);
	}
	
	public Element getElement(String withName, int curLineNo)
	{
		String elemName = name.toLowerCase().replaceAll("\\W+", "");
		
		if(elemName.equals(withName))
		{
			return this;
		}

		for(INode node : this.nodes)
		{
			if(!(node instanceof Element))
			{
				continue;
			}
			
			Element elem = (Element) node;
			
			if(elem.hasOffset(curLineNo))
			{
				return elem.getElement(withName, curLineNo);
			}
			
			if(elem.getEndLocation() == null || curLineNo <= elem.getEndLocation().getEndLineNumber())
			{
				break;
			}
		}
		
		return null;
	}
	
	public Element getElementWithName(String withName)
	{
		String elemName = withName.toLowerCase().replaceAll("\\W+", "");
		
		for(INode node : this.nodes)
		{
			if(!(node instanceof Element))
			{
				continue;
			}
			
			Element elem = (Element) node;

			if(elemName.equals(elem.getNormalizedName()))
			{
				return elem;
			}
		}
		
		return null;
	}

	public Element getLastElement(int offset)
	{
		if(!hasOffset(offset))
		{
			return null;
		}
		
		Element finalElem = null;
		
		for(INode node : this.nodes)
		{
			if(!(node instanceof Element))
			{
				continue;
			}
			
			Element celem = (Element) node;
			
			finalElem = celem.getLastElement(offset);
			
			if(finalElem != null)
			{
				return finalElem;
			}
			
			if(celem.getEndLocation() == null || celem.getEndLocation().getEndOffset() < offset)
			{
				break;
			}
		}
		
		return this;
	}
	
	public String getReservedAttribute(String name)
	{
		Attribute attr = this.attributes.get(name);
		
		if(attr == null)
		{
			return null;
		}
		
		if(!XMLConstants.CCG_URI.equals(attr.getNamespace()))
		{
			return null;
		}
		
		return attr.getValue();
	}
	
	private void populateTypesForReserved(Project project, List<XmlFileMessage> messages, boolean recursive)
	{
		String beanTypeStr = getReservedAttribute(DefaultParserHandler.ATTR_BEAN_TYPE);
		DocInformation docInformation = project.getDocInformation();
		
		if(beanTypeStr != null)
		{
			try
			{
				this.elementType = Class.forName(beanTypeStr, false, project.getProjectClassLoader());
				
				if(recursive)
				{
					populateChildren(project, messages);
				}
			}catch(Exception ex)
			{
				messages.add(new XmlFileMessage(MessageType.ERROR, "Invalid bean type specified: " + beanTypeStr, startLocation.getStartLineNumber()));
			}
			
			return;
		}
		
		StepInfo stepInfo = docInformation.getStep(name);
		
		if(stepInfo != null)
		{
			try
			{
				this.elementType = Class.forName(stepInfo.getJavaType(), false, project.getProjectClassLoader());
				
				if(recursive)
				{
					populateChildren(project, messages);
				}
			}catch(Exception ex)
			{
				messages.add(new XmlFileMessage(MessageType.ERROR, "Failed to load step type class: " + stepInfo.getJavaType(), startLocation.getStartLineNumber()));
			}
			
			return;
		}
		
		ValidationInfo validationInfo = docInformation.getValidation(name);
		
		if(validationInfo != null)
		{
			try
			{
				this.elementType = Class.forName(validationInfo.getJavaType(), false, project.getProjectClassLoader());

				if(recursive)
				{
					populateChildren(project, messages);
				}
			}catch(Exception ex)
			{
				messages.add(new XmlFileMessage(MessageType.ERROR, "Failed to load validation type class: " + validationInfo.getJavaType(), startLocation.getStartLineNumber()));
			}
			
			return;
		}

		messages.add(new XmlFileMessage(MessageType.ERROR, "No matching step or validation found for this element.", startLocation.getStartLineNumber()));
	}
	
	public void populateTestFileTypes(Project project, List<XmlFileMessage> messages)
	{
		this.populateTypes(null, project, messages, true);
	}
	
	public void populateTypes(Class<?> parentElementType, Project project, List<XmlFileMessage> messages, boolean recursive)
	{
		BeanPropertyInfoFactory beanInfoFactory = project.getBeanPropertyInfoFactory();
		
		if(XMLConstants.CCG_WRAP_URI.equals(namespace))
		{
			this.elementType = parentElementType;
		}
		else if(XMLConstants.CCG_URI.equals(namespace))
		{
			populateTypesForReserved(project, messages, recursive);
		}
		else if(parentElementType == null)
		{
			this.elementType = TestDataFile.class;
		}
		else
		{
			BeanProperty propInfo = beanInfoFactory.getBeanPropertyInfo(parentElementType).getProperty(name);
			
			if(propInfo == null)
			{
				messages.add(new XmlFileMessage(MessageType.ERROR, "No matching property found with name: " + name, startLocation.getStartLineNumber()));
				return;
			}
			
			if(propInfo.getWriteMethod() == null)
			{
				messages.add(new XmlFileMessage(MessageType.ERROR, "No writeable property found with name: " + name, startLocation.getStartLineNumber()));
				return;
			}
			
			this.elementType = propInfo.getType();
		}
		
		
		//if failed to determine the current element type
		// move to next element
		if(this.elementType == null)
		{
			return;
		}

		if(recursive)
		{
			populateChildren(project, messages);
		}
	}
	
	private void populateChildren(Project project, List<XmlFileMessage> messages)
	{
		for(INode node : this.nodes)
		{
			if(!(node instanceof Element))
			{
				continue;
			}
			
			Element selem = (Element) node;
			selem.populateTypes(elementType, project, messages, true);
		}
		
		BeanPropertyInfoFactory beanInfoFactory = project.getBeanPropertyInfoFactory();

		for(Attribute attr : this.attributes.values())
		{
			if(XMLConstants.CCG_URI.equals(attr.getNamespace()))
			{
				continue;
			}
			
			BeanProperty propInfo = beanInfoFactory.getBeanPropertyInfo(elementType).getProperty(attr.getName());
			
			if(propInfo == null)
			{
				messages.add(new XmlFileMessage(MessageType.ERROR, "No matching property found for attribute with name: " + attr.getName(), startLocation.getStartLineNumber()));
				continue;
			}
			
			if(propInfo.getWriteMethod() == null)
			{
				messages.add(new XmlFileMessage(MessageType.ERROR, "No writeable property found for attribute with name: " + attr.getName(), startLocation.getStartLineNumber()));
				continue;
			}
			
			attr.setAttributeType(propInfo.getType());
		}
	}
	
	/**
	 * Finds the element type by recursively finding parent element types as needed.
	 * @param project
	 * @param messages
	 * @return
	 */
	public Class<?> findElementType(Project project, List<XmlFileMessage> messages)
	{
		if(elementType != null)
		{
			return elementType;
		}
		
		if(parentElement == null)
		{
			return TestDataFile.class;
		}
		
		Class<?> parentElemType = parentElement.findElementType(project, messages);
		
		if(parentElemType == null)
		{
			return null;
		}
		
		populateTypes(parentElemType, project, messages, false);
		
		return this.elementType;
	}
	
	public String getFullElementName()
	{
		if(prefix == null)
		{
			return name;
		}
		
		return prefix + ":" + name;
	}

	public String getNamespaceWithPrefix(String prefix)
	{
		if(prefixToNamespace == null || !prefixToNamespace.containsKey(prefix))
		{
			return parentElement != null ? parentElement.getNamespaceWithPrefix(prefix) : null;
		}
		
		return prefixToNamespace.get(prefix);
	}
	
	public String getPrefixForNamespace(String namespace)
	{
		if(namespaceToPrefix == null || !namespaceToPrefix.containsKey(namespace))
		{
			return parentElement != null ? parentElement.getPrefixForNamespace(namespace) : null;
		}
		
		return namespaceToPrefix.get(namespace);
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
