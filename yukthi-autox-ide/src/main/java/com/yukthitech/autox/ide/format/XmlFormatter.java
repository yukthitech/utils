package com.yukthitech.autox.ide.format;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Comment;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XmlFormatter
{
	private static List<FormattingRule> rules = new ArrayList<>();
	
	static
	{
		rules.add(new FormattingRule("\\{$", IndentAction.POST_INCR_INDENT));
		rules.add(new FormattingRule("\\}$", IndentAction.PRE_DECR_INDENT));
		rules.add(new FormattingRule("\\<\\#if", IndentAction.POST_INCR_INDENT, "\\<\\/\\#if\\>"));
		rules.add(new FormattingRule("\\<\\/\\#if\\>", IndentAction.PRE_DECR_INDENT));
	}
	
	public static String formatXml(String content)
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			Document oldDocument = builder.parse(new ByteArrayInputStream(content.getBytes()));
			Document newDoc = builder.newDocument();
			
			Element oldElement = oldDocument.getDocumentElement();
	
			Element newElement = formatElement(oldElement, newDoc, "");
			newDoc.appendChild(newElement);
			
			//convert to xml text
			DOMSource domSource = new DOMSource(newDoc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(domSource, result);		
			
			return writer.toString();
		}catch(Exception ex)
		{
			if(ex instanceof RuntimeException)
			{
				throw (RuntimeException) ex;
			}
			
			throw new IllegalStateException("An error occurred while formatting xml", ex);
		}
	}
	
	private static Element formatElement(Element oldElement, Document newDoc, String indentation)
	{
		Element element = newDoc.createElement(oldElement.getNodeName());
		
		if(StringUtils.isNotEmpty(oldElement.getPrefix()))
		{
			element.setPrefix(oldElement.getPrefix());
		}
		
		//set attributes on the new node
		NamedNodeMap attrMap = oldElement.getAttributes();
		int size = attrMap.getLength();
		
		for(int i = 0; i < size; i++)
		{
			Attr oldAttr = (Attr) attrMap.item(i);
			Attr attr = newDoc.createAttribute(oldAttr.getName());
			attr.setValue(oldAttr.getValue());
			
			if(StringUtils.isNotEmpty(oldAttr.getPrefix()))
			{
				attr.setPrefix(oldAttr.getPrefix());
			}
			
			element.setAttributeNode(attr);
		}
		
		//set the child nodes recursively
		NodeList nodeList = oldElement.getChildNodes();
		size = nodeList.getLength();
		Node node = null;
		String childIndent = indentation + "\t";
		String indentText = "\n" + childIndent;
		
		for(int i = 0 ; i < size ; i++)
		{
			node = nodeList.item(i);
			
			if(node instanceof Attr)
			{
				continue;
			}
			
			if(node instanceof Text)
			{
				String text = ((Text) node).getNodeValue();
				text = formatText(text, childIndent, indentation);
				
				if(StringUtils.isBlank(text))
				{
					continue;
				}
				
				element.appendChild(newDoc.createTextNode(text));
				continue;
			}
			
			if(node instanceof CDATASection)
			{
				String text = ((CDATASection) node).getNodeValue();
				text = formatText(text, childIndent, indentation);
				
				element.appendChild(newDoc.createCDATASection(text));
				continue;
			}
			
			if(node instanceof Comment)
			{
				String text = ((Comment) node).getText();
				text = formatText(text, childIndent, indentation);
				
				element.appendChild(newDoc.createTextNode(indentText));
				element.appendChild(newDoc.createComment(text));
				continue;
			}
			
			if(node instanceof Element)
			{
				element.appendChild(newDoc.createTextNode(indentText));
				
				Element newElem = formatElement((Element) node, newDoc, indentation + "\t");
				element.appendChild(newElem);
				
				element.appendChild(newDoc.createTextNode("\n" + indentation));
				continue;
			}
			
			throw new IllegalStateException("Unknow node type encountered in xml conent: " + node.getNodeType());
		}
		
		return element;
	}

	private static String formatText(String text, String indentation, String parentIndent)
	{
		String trimText = text.trim();
		
		//if text is not multi line text
		if(!trimText.contains("\n"))
		{
			if(trimText.length() > 30)
			{
				return "\n" + indentation + trimText + "\n" + parentIndent;
			}
			
			return trimText;
		}
		
		try
		{
			BufferedReader reader = new BufferedReader(new StringReader(trimText));
			StringBuilder builder = new StringBuilder();
			String line = null;
			String extraIndent = "";
			
			FormattingRule matchedRule = null;
			
			while((line = reader.readLine()) != null)
			{
				line = line.trim();
				matchedRule = null;
				
				for(FormattingRule rule : rules)
				{
					if(!rule.isMatching(line))
					{
						continue;
					}
					
					matchedRule = rule;
					break;
				}
				
				if(matchedRule != null)
				{
					extraIndent = matchedRule.getIndentAction().alterIndet(true, extraIndent);
				}
				
				builder.append("\n").append(indentation).append(extraIndent).append(line);
				
				if(matchedRule != null)
				{
					extraIndent = matchedRule.getIndentAction().alterIndet(false, extraIndent);
				}
			}
			
			text = StringUtils.stripEnd(builder.toString(), "\n\t");
			return text + "\n" + parentIndent;
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while formating text content", ex);
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		String xmlContent = FileUtils.readFileToString(new File("./dml-test-suite.xml"));
		System.out.println(formatXml(xmlContent));
	}
}
