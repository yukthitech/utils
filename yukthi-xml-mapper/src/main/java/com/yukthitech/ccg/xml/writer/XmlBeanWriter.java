/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.ccg.xml.writer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.XMLConstants;
import com.yukthitech.utils.beans.BeanProperty;

/**
 * Helps in converting java beans into xml.
 * @author akiran
 */
public class XmlBeanWriter
{
	private static Map<Class<?>, List<BeanProperty>> typeToProperties = new HashMap<Class<?>, List<BeanProperty>>();
	
	public static final String DEF_DATE_FORMAT_STR = "dd/MM/yyyy hh:mm:ss:SSS aa";
	
	public static final SimpleDateFormat DEF_DATE_FORMAT = new SimpleDateFormat(DEF_DATE_FORMAT_STR);
	
	public static final String CCG_PREFIX = "ccg:";
	
	/**
	 * Default xml writer configuration.
	 */
	private static XmlWriterConfig DEF_WRITER_CONFIG = new XmlWriterConfig();
	
	/**
	 * Fetches properties for specified type.
	 * @param type type for which properties needs to be fetched
	 * @return properties
	 */
	synchronized static List<BeanProperty> getReadProperties(Class<?> type, XmlWriterConfig writerConfig)
	{
		List<BeanProperty> properties = typeToProperties.get(type);
		
		if(properties != null)
		{
			return properties;
		}
		
		boolean readCompatible = writerConfig.isReadCompatible();
		properties = BeanProperty.loadProperties(type, true, readCompatible, true);
		typeToProperties.put(type, properties);
		
		return properties;
	}
	
	public static synchronized void clearCache()
	{
		typeToProperties.clear();
	}
	
	/**
	 * Creates the xml out of the bean specified.
	 * @param rootName root element of the output xml document.
	 * @param bean bean to be converted
	 * @return coverted xml.
	 */
	public static String writeToString(String rootName, Object bean)
	{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writeTo(rootName, bean, bos, null);
        return new String(bos.toByteArray());
	}
	
	/**
	 * Creates the xml out of the bean specified.
	 * @param rootName root element of the output xml document.
	 * @param bean bean to be converted
	 * @param config Configuration for writer.
	 * @return converted xml.
	 */
	public static String writeToString(String rootName, Object bean, XmlWriterConfig config)
	{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writeTo(rootName, bean, bos, config);
        return new String(bos.toByteArray());
	}

	/**
	 * Creates the xml out of the bean specified.
	 * @param rootName root element of the output xml document.
	 * @param bean bean to be converted
	 */
	public static void writeTo(String rootName, Object bean, File file)
	{
		try
		{
	        FileOutputStream fos = new FileOutputStream(file);
	        writeTo(rootName, bean, fos, null);
	
	        fos.close();
		}catch(IOException ex)
		{
			throw new XmlWriteException("An error occurred while wrting xml content to file: {}", file.getPath(), ex);
		}
	}

	/**
	 * Creates the xml out of the bean specified.
	 * @param rootName root element of the output xml document.
	 * @param bean bean to be converted
	 * @param config Configuration for writer.
	 */
	public static void writeTo(String rootName, Object bean, File file, XmlWriterConfig config)
	{
		try
		{
	        FileOutputStream fos = new FileOutputStream(file);
	        writeTo(rootName, bean, fos, config);
	
	        fos.close();
		}catch(IOException ex)
		{
			throw new XmlWriteException("An error occurred while wrting xml content to file: {}", file.getPath(), ex);
		}
	}

	/**
	 * Converts specified bean to xml and writes it to specified output stream.
	 * @param rootName root element of the output xml document.
	 * @param bean bean to be converted
	 * @param os output stream to which xml should be written
	 */
	public static void writeTo(String rootName, Object bean, OutputStream os)
	{
		writeTo(rootName, bean, os, null);
	}
	
	/**
	 * Converts specified bean to xml and writes it to specified output stream.
	 * @param rootName root element of the output xml document.
	 * @param bean bean to be converted
	 * @param os output stream to which xml should be written
	 * @param writerConfig Configuration for writer.
	 */
	public static void writeTo(String rootName, Object bean, OutputStream os, XmlWriterConfig writerConfig)
	{
		try
		{
			writerConfig = (writerConfig == null) ? DEF_WRITER_CONFIG : writerConfig;
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			
			//create root element with ccg namespaces
			Element rootElement = doc.createElement(rootName);
			doc.appendChild(rootElement);

			if(!writerConfig.isExcludeNameSpace())
			{
				rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ccg", XMLConstants.NEW_CCG_URI);
				rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:wrap", XMLConstants.NEW_CCG_WRAP_URI);
				
				rootElement.setAttribute( CCG_PREFIX + DefaultParserHandler.ATTR_DATE_FORMAT, DEF_DATE_FORMAT_STR);
				rootElement.setAttribute( CCG_PREFIX + DefaultParserHandler.ATTR_BEAN_TYPE, bean.getClass().getName());
			}
			
			if(writerConfig.isEscapeExpressions())
			{
				Element exprElement = doc.createElement(CCG_PREFIX + DefaultParserHandler.RNODE_EXPR_PATTERN);
				exprElement.setAttribute(DefaultParserHandler.ATTR_ENABLED, "false");
				
				rootElement.appendChild(exprElement);
			}
			
			BeanToDocPopulator.populateElement(doc, rootElement, bean, null, writerConfig);
			
			//generate the xml from document
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        
	        if(writerConfig.isIndentXml())
	        {
	        	transformerFactory.setAttribute("indent-number", 4);
	        	
	        	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        	transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	        }
	        
	        if(writerConfig.isExcludeXmlDeclaration())
	        {
	        	transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	        }
	        
	        DOMSource source = new DOMSource(doc);
	
	        //write to console or file
	        StreamResult file = new StreamResult(os);
	
	        //write data
	        transformer.transform(source, file);
			
		}catch(Exception ex)
		{
			throw new XmlWriteException("An error occurred while converting specified bean to xml", ex);
		}
	}
}
