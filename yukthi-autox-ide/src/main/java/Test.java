import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.IOUtils;

import com.yukthitech.autox.ide.xmlfile.XmlFile;

public class Test
{
	public static void main(String[] args) throws Exception
	{
		String xmlContent = IOUtils.toString( Test.class.getResourceAsStream("/templates/test-file-template.xml") );
		
		XmlFile xmlFile = XmlFile.parse(xmlContent);
		System.out.println(xmlFile);
	}
}
