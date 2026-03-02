package com.yukthitech.transform.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.collections.CollectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.yukthitech.transform.ITransformConstants;
import com.yukthitech.transform.TransformException;
import com.yukthitech.transform.TransformState;
import com.yukthitech.transform.TransformXmlUtils;
import com.yukthitech.transform.template.TransformTemplate.FieldType;
import com.yukthitech.transform.template.TransformTemplate.TransformObject;
import com.yukthitech.transform.template.TransformTemplate.TransformObjectField;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class XmlGenerator implements IGenerator
{
	private Document document;
	
	public XmlGenerator()
	{
		try
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	
	        // Step 2: Create a new Document
	        document = dBuilder.newDocument();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating new document object", ex);
		}
	}
	
	@Override
	public String getRootPath()
	{
		return "";
	}
	
	@Override
	public String getSubPath(TransformObjectField field)
	{
		return "/" + field.getName();
	}

	@Override
	public Object generateObject(TransformState state, TransformObject transformObj)
	{
		if(state.isAttributeMode())
		{
			return new LinkedHashMap<String, Object>();
		}
		
		try
		{
			Element element = document.createElement(transformObj.getName());
			return element;
		}catch(Exception ex)
		{
			throw new TransformException(state.getLocation(), "Failed to create new DOM element with name: {}", transformObj.getName(), ex);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setField(TransformState state, TransformObjectField field, Object object, String name, Object fieldValue)
	{
		if(fieldValue instanceof List)
		{
			List<Object> lst = (List<Object>) fieldValue;
			
			for(Object val : lst)
			{
				if(state.isAttributeMode())
				{
					setFieldSingleValueForAttr(state, field, object, name, val);
					continue;
				}
				
				this.setFieldSingleValue(state, field, object, name, val);
			}
			
			return;
		}
		
		if(state.isAttributeMode())
		{
			setFieldSingleValueForAttr(state, field, object, name, fieldValue);
			return;
		}

		setFieldSingleValue(state, field, object, name, fieldValue);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setFieldSingleValueForAttr(TransformState state, TransformObjectField field, Object object, String name, Object fieldValue)
	{
		// Note: In set mode, text-content will not come as it would be turned into expression
		//    xml-template-factory. So only attributes and subnodes is being taken care here
		
		LinkedHashMap<String, Object> objMap = (LinkedHashMap<String, Object>) object;
		
		Object existingValue = objMap.get(name);
		
		if(existingValue == null)
		{
			objMap.put(name, fieldValue);
			return;
		}
		
		if(existingValue instanceof List)
		{
			((List) existingValue).add(fieldValue);
			return;
		}
		
		List<Object> valLst = new ArrayList<>(Arrays.asList(existingValue));
		valLst.add(fieldValue);
		objMap.put(name, valLst);
	}
	
	private void setFieldSingleValue(TransformState state, TransformObjectField field, Object object, String name, Object fieldValue)
	{
		Element element = (Element) object;
		
		if(field.getType() == FieldType.ATTRIBUTE)
		{
			try
			{
				element.setAttribute(name, fieldValue.toString());
			}catch(Exception ex)
			{
				throw new TransformException(state.getLocation(), "Failed to add attribute: [Name: {}, Value: {}]", name, fieldValue, ex);
			}
			
			return;
		}
		
		if(field.getType() == FieldType.NODE)
		{
			if(!(fieldValue instanceof Element))
			{
				try
				{
					Element nameElem = document.createElement(name);
					Text textVal = document.createTextNode(fieldValue.toString());
					
					nameElem.appendChild(textVal);
					element.appendChild(nameElem);
				}catch(Exception ex)
				{
					throw new TransformException(state.getLocation(), "Failed to add text-node: [Name: {}, Value: {}]", name, fieldValue, ex);
				}
				
				return;
			}
			
			Element subelem = (Element) fieldValue;
			
			try
			{
				if(name != null && !name.equals(subelem.getNodeName()))
				{
					document.renameNode(subelem, null, name);
				}
				
				element.appendChild(subelem);
			}catch(Exception ex)
			{
				throw new TransformException(state.getLocation(), "Failed to append chile node: [Name: {}, New Name: {}]", subelem.getTagName(), name, ex);
			}
			return;
		}
		
		try
		{
			Text textVal = document.createTextNode(fieldValue.toString());
			element.appendChild(textVal);
		}catch(Exception ex)
		{
			throw new TransformException(state.getLocation(), "Failed to append text value: {}", fieldValue, ex);
		}
	}
	
	@Override
	public void injectReplaceEntry(TransformState state, TransformObjectField field, Object object, Object injectedValue)
	{
		//if result value is not map
		if(!(injectedValue instanceof Element))
		{
			throw new TransformException(state.getLocation(), "Value of @replace key must be a element but found: {}", injectedValue.getClass().getName());
		}
		
		Element element = (Element) object;
		Element injectedElem = (Element) injectedValue;
		
		if(injectedElem.getOwnerDocument() != document)
		{
			Element clonedElem = (Element) document.importNode(injectedElem, true);
			injectedElem = clonedElem;
		}
		
		NamedNodeMap attrMap = injectedElem.getAttributes();
		
		if(attrMap != null)
		{
			int count = attrMap.getLength();
			
			for(int i = 0; i < count; i++)
			{
				element.setAttribute(attrMap.item(i).getNodeName(), 
						attrMap.item(i).getNodeValue());
			}			
		}
		
		NodeList nodeList = injectedElem.getChildNodes();
		int nodeCount = nodeList.getLength();
		
		for(int i = 0; i < nodeCount; i++)
		{
			// As and when element from nodeList is added to element
			//    it is removed from nodeList. So always zero index element index is accessed

			if(nodeList.item(0) == null)
			{
				continue;
			}
			
			element.appendChild(nodeList.item(0));
		}
	}
	
	@Override
	public Object convertIncluded(TransformState state, Object value)
	{
		if(value == null)
		{
			return null;
		}
		
		Element element = (Element) value;
		
		if(element.getOwnerDocument() != document)
		{
			Element clonedElem = (Element) document.importNode(element, true);
			return clonedElem;
		}
		
		return element;
	}
	
	@SuppressWarnings("unchecked")
	private Object toSimpleObjectInternal(Element element)
	{
		if(element == null)
		{
			return null;
		}

		NamedNodeMap attrMap = element.getAttributes();
		int attrCount = 0;
		
		Map<String, Object> res = new LinkedHashMap<String, Object>();
		
		if(attrMap != null)
		{
			attrCount = attrMap.getLength();
			
			for(int i = 0; i < attrCount; i++)
			{
				res.put(attrMap.item(i).getNodeName(), 
						attrMap.item(i).getNodeValue());
			}			
		}
		
		NodeList nodeList = element.getChildNodes();
		int nodeCount = nodeList.getLength();
		
		for(int i = 0; i < nodeCount; i++)
		{
			Node node = nodeList.item(i);
			
			// if text is found, assume this is text node and return text as value
			if(node instanceof Text)
			{
				String content = ((Text) node).getTextContent();
				
				if(content.trim().length() > 0)
				{
					return content;
				}
			}
			
			Element childElem = (Element) node;
			Object convertedVal = toSimpleObjectInternal(childElem);
			Object existingVal = res.get(childElem.getTagName());
			
			if(existingVal != null)
			{
				if(existingVal instanceof List)
				{
					List<Object> existingList = (List<Object>) existingVal;
					existingList.add(convertedVal);
				}
				else
				{
					List<Object> newList = new ArrayList<>();
					newList.add(existingVal);
					newList.add(convertedVal);
					res.put(childElem.getTagName(), newList);
				}
			}
			else
			{
				res.put(childElem.getTagName(), convertedVal);
			}
		}
		
		return res;
	}
	
	@Override
	public Object toSimpleObject(Object value)
	{
		return toSimpleObjectInternal((Element) value);
	}

    public String formatObject(Object object)
    {
		Element element = (Element) object;
		return TransformXmlUtils.toXmlString(element);
	}
    
    @Override
    public boolean isIgnorable(TransformObject transformObject, Object object)
    {
    	if(!(object instanceof Element))
    	{
    		return false;
    	}
    	
    	Element element = (Element) object;
    	
    	// if element has at least one attribute return false
    	if(element.getAttributes() != null && element.getAttributes().getLength() > 0)
    	{
    		return false;
    	}
    	
    	// if element has at least one child element return false
    	if(element.getChildNodes() != null && element.getChildNodes().getLength() > 0)
    	{
    		return false;
    	}
    	
    	// if element is empty, check as per def if its suppose to be text node, 
    	//      then return true, so that it is not part of response
    	if(CollectionUtils.isNotEmpty(transformObject.getFields()) 
    			&& ITransformConstants.FIELD_TEXT_CONTENT.equals(transformObject.getFields().get(0).getName()))
    	{
    		return true;
    	}
    	
    	return false;
    }
}
