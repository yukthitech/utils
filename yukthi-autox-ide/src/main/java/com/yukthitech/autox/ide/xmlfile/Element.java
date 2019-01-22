package com.yukthitech.autox.ide.xmlfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.doc.ElementInfo;
import com.yukthitech.autox.doc.StepInfo;
import com.yukthitech.autox.doc.ValidationInfo;
import com.yukthitech.autox.ide.FileParseCollector;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.editor.FileParseMessage;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.test.TestDataFile;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.XMLConstants;
import com.yukthitech.utils.beans.BeanProperty;
import com.yukthitech.utils.beans.BeanPropertyInfoFactory;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class Element implements INode
{
	private static DefaultParserHandler defaultParserHandler = new DefaultParserHandler();
	
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
	 * Step info corresponding to this element.
	 */
	private StepInfo stepInfo;
	
	/**
	 * Prefix to namespace mapping.
	 */
	private Map<String, String> prefixToNamespace;
	
	/**
	 * Prefix to namespace mapping.
	 */
	private Map<String, String> namespaceToPrefix;
	
	private BeanProperty beanProperty;

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
	
	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
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
	
	public StepInfo getStepInfo()
	{
		return stepInfo;
	}
	
	public Set<String> getChildNames()
	{
		Set<String> names = new HashSet<>();
		
		for(Attribute attr : attributes.values())
		{
			names.add(attr.getName());
			names.add( attr.getName().replaceAll("([A-Z])", "-$1").toLowerCase() );
		}
		
		for(INode node : nodes)
		{
			if(!(node instanceof Element))
			{
				continue;
			}
			
			Element elem = (Element) node;
			names.add(elem.getName());
			names.add( elem.getName().replaceAll("([A-Z])", "-$1").toLowerCase() );
		}
		
		return names;
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
	
	public boolean hasLine(int line)
	{
		if(line < startLocation.getStartLineNumber())
		{
			return false;
		}
		
		if(endLocation == null || line <= endLocation.getEndLineNumber())
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
			
			if(elem.hasLine(curLineNo))
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
			
			if(celem.getEndLocation() == null || celem.getEndLocation().getEndOffset() >= offset)
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
	
	private void populateTypesForReserved(Project project, FileParseCollector collector, boolean recursive)
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
					populateChildren(project, collector);
				}
			}catch(Exception ex)
			{
				Attribute attr = getAttribute(DefaultParserHandler.ATTR_BEAN_TYPE);
				
				collector.addMessage(
						new FileParseMessage(MessageType.ERROR, "Invalid bean type specified: " + beanTypeStr, 
								startLocation.getStartLineNumber(), 
								attr.getNameLocation().getStartOffset(), attr.getValueLocation().getEndOffset()
							)
						);
			}
			
			return;
		}
		
		StepInfo stepInfo = docInformation.getStep(name);
		
		if(stepInfo != null)
		{
			try
			{
				this.stepInfo = stepInfo;
				this.elementType = Class.forName(stepInfo.getJavaType(), false, project.getProjectClassLoader());
				
				if(recursive)
				{
					populateChildren(project, collector);
				}
			}catch(Exception ex)
			{
				collector.addMessage(
						new FileParseMessage(
								MessageType.ERROR, 
								"Failed to load step type class: " + stepInfo.getJavaType(), 
								startLocation.getStartLineNumber(),
								this.startLocation.getStartOffset(),
								this.startLocation.getEndOffset()
								)
						);
			}
			
			return;
		}
		
		ValidationInfo validationInfo = docInformation.getValidation(name);
		
		if(validationInfo != null)
		{
			try
			{
				this.stepInfo = validationInfo;
				this.elementType = Class.forName(validationInfo.getJavaType(), false, project.getProjectClassLoader());

				if(recursive)
				{
					populateChildren(project, collector);
				}
			}catch(Exception ex)
			{
				collector.addMessage(
						new FileParseMessage(
								MessageType.ERROR, 
								"Failed to load validation type class: " + validationInfo.getJavaType(), 
								startLocation.getStartLineNumber(),
								this.startLocation.getStartOffset(),
								this.startLocation.getEndOffset()
								)
						);
			}
			
			return;
		}
		
		//if reserve node is supported by default handler, ignore
		if(defaultParserHandler.isValidReserverNode(this.name))
		{
			return;
		}

		collector.addMessage(
				new FileParseMessage(MessageType.ERROR, 
						"No matching step or validation found with name: " + name, 
						startLocation.getStartLineNumber(),
						this.startLocation.getStartOffset(),
						this.startLocation.getEndOffset()
						)
				);
	}
	
	public void populateTestFileTypes(Project project, FileParseCollector collector)
	{
		this.populateTypes(null, project, collector, true);
	}
	
	public void populateTypes(Class<?> parentElementType, Project project, FileParseCollector collector, boolean recursive)
	{
		BeanPropertyInfoFactory beanInfoFactory = project.getBeanPropertyInfoFactory();
		
		if(XMLConstants.CCG_WRAP_URI.equals(namespace))
		{
			this.elementType = parentElementType;
		}
		else if(XMLConstants.CCG_URI.equals(namespace))
		{
			populateTypesForReserved(project, collector, recursive);
		}
		else if(parentElementType == null)
		{
			this.elementType = TestDataFile.class;
		}
		else
		{
			StepInfo parentStepInfo = parentElement.getStepInfo();
			ElementInfo childElementInfo = (parentStepInfo != null) ? parentStepInfo.getChildElement(name) : null;
			
			if(childElementInfo != null)
			{
				this.stepInfo = childElementInfo;
				
				try
				{
					this.elementType = Class.forName(childElementInfo.getType());
				}catch(Exception ex)
				{
					throw new InvalidStateException("An error occurred while loading class of type: {}", childElementInfo.getType(), ex);
				}
			}
			
			if(this.elementType == null)
			{
				String name = IdeUtils.removeHyphens(this.name);
				BeanProperty propInfo = beanInfoFactory.getBeanPropertyInfo(parentElementType).getProperty(name);
				
				if(propInfo == null)
				{
					collector.addMessage(
							new FileParseMessage(
									MessageType.ERROR, 
									String.format("No matching property '%s' under parent-element bean type: %s", this.name, parentElementType.getName()), 
									startLocation.getStartLineNumber(),
									this.startLocation.getStartOffset(),
									this.startLocation.getEndOffset()
									)
							);
					return;
				}
				
				if(propInfo.getWriteMethod() == null)
				{
					collector.addMessage(
							new FileParseMessage(
									MessageType.ERROR, 
									String.format("No writeable property '%s' under parent-element bean type: %s", this.name, parentElementType.getName()),
									startLocation.getStartLineNumber(),
									this.startLocation.getStartOffset(),
									this.startLocation.getEndOffset()
									)
							);
					return;
				}
				
				this.elementType = propInfo.getType();
				this.beanProperty = propInfo;

				String beanTypeStr = getReservedAttribute(DefaultParserHandler.ATTR_BEAN_TYPE);
				
				if(StringUtils.isNotBlank(beanTypeStr))
				{
					try
					{
						this.elementType = Class.forName(beanTypeStr);
					}catch(Exception ex)
					{
						collector.addMessage(
								new FileParseMessage(
										MessageType.ERROR, 
										String.format("An error occurred while fetching dynamic type: {}", beanTypeStr),
										startLocation.getStartLineNumber(),
										this.startLocation.getStartOffset(),
										this.startLocation.getEndOffset()
										)
								);
						return;
					}
				}
			}
		}
		
		
		//if failed to determine the current element type
		// move to next element
		if(this.elementType == null)
		{
			return;
		}

		//TODO: handle bean copy property properly
		String beanCopy = getReservedAttribute("beanCopy");
		
		//if bean copy is present, for temp fix, dont process children or attributes
		if(StringUtils.isNotBlank(beanCopy))
		{
			return;
		}

		if(recursive)
		{
			populateChildren(project, collector);
		}
	}
	
	private void populateChildren(Project project, FileParseCollector collector)
	{
		for(INode node : this.nodes)
		{
			if(!(node instanceof Element))
			{
				continue;
			}
			
			Element selem = (Element) node;
			selem.populateTypes(elementType, project, collector, true);
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
				//if this id based node ignore it
				if(this.beanProperty != null && this.beanProperty.isKeyProperty())
				{
					continue;
				}
				
				//if step info is present and it is of key based, ignore missing attribute prop
				if(this.stepInfo != null && (this.stepInfo instanceof ElementInfo))
				{
					ElementInfo elemInfo = (ElementInfo) this.stepInfo;
					
					if(StringUtils.isNotBlank(elemInfo.getKeyName()))
					{
						continue;
					}
				}
				
				collector.addMessage(
						new FileParseMessage(
								MessageType.ERROR, 
								String.format("No matching property found for attribute '%s' under bean-type: %s'", attr.getName(), elementType.getName()), 
								startLocation.getStartLineNumber(),
								attr.getNameLocation().getStartOffset(), attr.getValueLocation().getEndOffset()
								)
						);
				continue;
			}
			
			if(propInfo.getWriteMethod() == null)
			{
				collector.addMessage(new FileParseMessage(
							MessageType.ERROR, "No writeable property found for attribute with name: " + attr.getName(), 
							startLocation.getStartLineNumber(),
							attr.getNameLocation().getStartOffset(), attr.getValueLocation().getEndOffset()
							)
						);
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
	public Class<?> findElementType(Project project, FileParseCollector collector)
	{
		if(elementType != null)
		{
			return elementType;
		}
		
		if(parentElement == null)
		{
			return TestDataFile.class;
		}
		
		Class<?> parentElemType = parentElement.findElementType(project, collector);
		
		if(parentElemType == null)
		{
			return null;
		}
		
		populateTypes(parentElemType, project, collector, false);
		
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
