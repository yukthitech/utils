package com.yukthitech.transform.template;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;

import org.w3c.dom.Element;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class TransformUtils
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

}
