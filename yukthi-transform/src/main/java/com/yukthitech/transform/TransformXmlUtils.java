package com.yukthitech.transform;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class TransformXmlUtils
{
    public static String toXmlString(Element element)
    {
		try
		{
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

	public static Map<String, Object> toMap(Element element)
	{
		Map<String, Object> result = new LinkedHashMap<>();
		NamedNodeMap attrMap = element.getAttributes();
		int attrCount = attrMap != null ? attrMap.getLength() : 0;

		for(int i = 0; i < attrCount; i++)
		{
			result.put(attrMap.item(i).getNodeName(), attrMap.item(i).getNodeValue());
		}

		NodeList nodeList = element.getChildNodes();
		int nodeCount = nodeList.getLength();
		
		for(int i = 0; i < nodeCount; i++)
		{
			Node node = nodeList.item(i);
			processNode(result, node, element);
		}

		return result;
	}

	private static void processNode(Map<String, Object> result, Node node, Element parentElement)
	{
		if(node instanceof Element)
		{
			Element element = (Element) node;

			if(isPureTextNode(element))
			{
				String content = ((Text) element.getFirstChild()).getTextContent().trim();
				addToMap(result, element.getTagName(), content);
				return;
			}

			Map<String, Object> elementMap = toMap(element);
			addToMap(result, element.getTagName(), elementMap);
		}
		// when text node is found, and parent element has attributes (or other nodes)
		//    add text content to parent element as a property "#text"
		else if(node instanceof Text)
		{
			String content = ((Text) node).getTextContent().trim();

			if(content.length() > 0)
			{
				addToMap(result, ITransformConstants.HYBRID_TEXT_PROP, content);
			}
		}
		else
		{
			throw new InvalidStateException("Unsupported node type: {}", node.getClass().getName());
		}
	}

	private static boolean isPureTextNode(Element element)
	{
		if(element.getAttributes() != null && element.getAttributes().getLength() > 0)
		{
			return false;
		}

		NodeList nodeList = element.getChildNodes();
		int nodeCount = nodeList.getLength();

		if(nodeCount != 1 || !(nodeList.item(0) instanceof Text))
		{
			return false;
		}

		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void addToMap(Map<String, Object> result, String name, Object value)
	{
		Object existingValue = result.get(name);

		if(existingValue == null)
		{
			result.put(name, value);
			return;
		}
		
		if(existingValue instanceof List)
		{
			((List) existingValue).add(value);
			return;
		}

		List<Object> newList = new ArrayList<>();
		newList.add(existingValue);
		newList.add(value);
		result.put(name, newList);
	}
}