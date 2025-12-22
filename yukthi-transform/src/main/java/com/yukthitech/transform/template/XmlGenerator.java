package com.yukthitech.transform.template;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.yukthitech.transform.TransformException;
import com.yukthitech.transform.TransformState;
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
		
		String path = state.getPath();
		
		try
		{
			Element element = document.createElement(transformObj.getName());
			return element;
		}catch(Exception ex)
		{
			throw new TransformException(path, "Failed to create new DOM element with name: {}", transformObj.getName(), ex);
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
		String path = state.getPath();
		
		if(field.getType() == FieldType.ATTRIBUTE)
		{
			try
			{
				element.setAttribute(name, fieldValue.toString());
			}catch(Exception ex)
			{
				throw new TransformException(path, "Failed to add attribute: [Name: {}, Value: {}]", name, fieldValue, ex);
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
					throw new TransformException(path, "Failed to add text-node: [Name: {}, Value: {}]", name, fieldValue, ex);
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
				throw new TransformException(path, "Failed to append chile node: [Name: {}, New Name: {}]", subelem.getTagName(), name, ex);
			}
			return;
		}
		
		try
		{
			Text textVal = document.createTextNode(fieldValue.toString());
			element.appendChild(textVal);
		}catch(Exception ex)
		{
			throw new TransformException(path, "Failed to append text value: {}", fieldValue, ex);
		}
	}
	
	@Override
	public void injectReplaceEntry(TransformState state, TransformObjectField field, Object object, Object injectedValue)
	{
		String path = state.getPath();
		
		//if result value is not map
		if(!(injectedValue instanceof Element))
		{
			throw new TransformException(path, "Value of @replace key must be a element but found: {}", injectedValue.getClass().getName());
		}
		
		Element element = (Element) object;
		Element injectedElem = (Element) injectedValue;
		
		if(injectedElem.getOwnerDocument() != document)
		{
			Element clonedElem = (Element) document.importNode(injectedElem, true);
			injectedElem = clonedElem;
		}
		
		NodeList nodeList = injectedElem.getChildNodes();
		int nodeCount = nodeList.getLength();
		
		for(int i = 0; i < nodeCount; i++)
		{
			element.appendChild(nodeList.item(i));
		}
	}

    public String formatObject(Object object)
    {
		try
		{
			Element element = (Element) object;

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			// Prepare the source and result
			DOMSource source = new DOMSource(element);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);

			// Perform the transformation
			transformer.transform(source, result);

			return writer.toString();
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting to xml string", ex);
		}
	}
}
