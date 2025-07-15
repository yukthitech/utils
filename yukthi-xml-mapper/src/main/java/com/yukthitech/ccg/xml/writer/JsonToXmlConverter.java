package com.yukthitech.ccg.xml.writer;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.ccg.xml.DynamicBean;
import com.yukthitech.utils.CommonUtils;

public class JsonToXmlConverter
{
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> jsonMap = (Map<String, Object>) mapper.readValue(
				FileUtils.readFileToString(
						new File("cobrand_template.json"), 
						Charset.defaultCharset()), 
				Object.class);
		
		DynamicBean dynBean = new DynamicBean(true);
		dynBean.loadSimpleMap(jsonMap);
		
		File tempFile = new File("cobrand_template.xml");
		
		Set<String> ATTR = CommonUtils.toSet(
				"name", "product", "feature", "virtualKey"
			);
			
		XmlWriterConfig writerConfig = new XmlWriterConfig()
		{
			@Override
			public boolean isAttributable(String nodeName, String propName)
			{
				return ATTR.contains(propName);
			}
		};
		
		writerConfig.setIndentXml(true);
		writerConfig.setExcludeNameSpace(true);
		writerConfig.setEscapeExpressions(false);
		writerConfig.setExcludeXmlDeclaration(true);
		
		XmlBeanWriter.writeTo("dyn-content", dynBean, tempFile, writerConfig);
		
		System.out.println("Xml is generated successfully...");
		
	}
}
