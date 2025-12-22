package com.yukthitech.transform.template;

import java.io.StringWriter;
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
		return "/";
	}
	
	@Override
	public String getSubPath(TransformObjectField field)
	{
		return "/" + field.getName();
	}

	@Override
	public Object generateObject(TransformObject transformObj)
	{
		Element element = document.createElement(transformObj.getName());
		return element;
	}
	
	@SuppressWarnings("unchecked")
	public void setField(TransformObjectField field, Object object, String name, Object fieldValue)
	{
		if(fieldValue instanceof List)
		{
			List<Object> lst = (List<Object>) fieldValue;
			
			for(Object val : lst)
			{
				this.setFieldSingleValue(field, object, name, val);
			}
			
			return;
		}

		setFieldSingleValue(field, object, name, fieldValue);
	}
	
	private void setFieldSingleValue(TransformObjectField field, Object object, String name, Object fieldValue)
	{
		Element element = (Element) object;
		
		if(field.getType() == FieldType.ATTRIBUTE)
		{
			element.setAttribute(name, fieldValue.toString());
			return;
		}
		
		if(field.getType() == FieldType.NODE)
		{
			if(!(fieldValue instanceof Element))
			{
				Element nameElem = document.createElement(name);
				Text textVal = document.createTextNode(fieldValue.toString());
				
				nameElem.appendChild(textVal);
				element.appendChild(nameElem);
				return;
			}
			
			Element subelem = (Element) fieldValue;
			
			if(name != null && !name.equals(subelem.getNodeName()))
			{
				document.renameNode(subelem, null, name);
			}
			
			element.appendChild(subelem);
			return;
		}
		
		Text textVal = document.createTextNode(fieldValue.toString());
		element.appendChild(textVal);
	}
	
	public void injectReplaceEntry(String path, TransformObjectField field, Object object, Object injectedValue)
	{
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
