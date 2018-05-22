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
	 * Fetches properties for specified type.
	 * @param type type for which properties needs to be fetched
	 * @return properties
	 */
	synchronized static List<BeanProperty> getReadProperties(Class<?> type)
	{
		List<BeanProperty> properties = typeToProperties.get(type);
		
		if(properties != null)
		{
			return properties;
		}
		
		properties = BeanProperty.loadProperties(type, true, false);
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
        writeTo(rootName, bean, bos);
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
	        writeTo(rootName, bean, fos);
	
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
		try
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			
			//create root element with ccg namespaces
			Element rootElement = doc.createElement(rootName);
			doc.appendChild(rootElement);

			rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ccg", XMLConstants.CCG_URI);
			rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:wrap", XMLConstants.CCG_WRAP_URI);
			
			rootElement.setAttribute( CCG_PREFIX + DefaultParserHandler.ATTR_DATE_FORMAT, DEF_DATE_FORMAT_STR);
			
			BeanToDocPopulator.populateElement(doc, rootElement, bean, null);
			
			//generate the xml from document
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
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
