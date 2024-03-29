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
package com.yukthitech.test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.ccg.xml.DynamicBean;
import com.yukthitech.ccg.xml.DynamicBeanParserHandler;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.ccg.xml.writer.XmlBeanWriter;
import com.yukthitech.ccg.xml.writer.XmlWriterConfig;

public class TXmlToJson
{
	/**
	 * Tests json to xml conversion.
	 */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void testJsonToXml() throws Exception
	{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> jsonMap = (Map<String, Object>) mapper.readValue(
				IOUtils.toString(
						TXmlToJson.class.getResourceAsStream("/xmlToJson/content.json"), 
						Charset.defaultCharset()), 
				Object.class);
		
		DynamicBean dynBean = new DynamicBean(true);
		dynBean.loadSimpleMap(jsonMap);
		
		File tempFile = File.createTempFile("test", ".xml");
		
		XmlWriterConfig writerConfig = new XmlWriterConfig();
		writerConfig.setIndentXml(true);
		writerConfig.setExcludeNameSpace(true);
		writerConfig.setEscapeExpressions(false);
		writerConfig.setExcludeXmlDeclaration(true);
		
		XmlBeanWriter.writeTo("dyn-content", dynBean, tempFile, writerConfig);
		
		String actualXmlContent = FileUtils.readFileToString(tempFile, Charset.defaultCharset());
		String expectedXmlContent = IOUtils.toString(
						TXmlToJson.class.getResourceAsStream("/xmlToJson/content.xml"), 
						Charset.defaultCharset());
		
		System.out.println(actualXmlContent);
		Assert.assertEquals(actualXmlContent.trim(), expectedXmlContent.trim());
	}
	
	@Test
	public void testXmlToJson() throws Exception
	{
		DynamicBeanParserHandler handler = new DynamicBeanParserHandler();
		handler.setTypeConversationEnabled(true);
		
		DynamicBean dynBean = (DynamicBean) XMLBeanParser.parse(TXmlToJson.class.getResourceAsStream("/xmlToJson/content.xml"), handler);
		
		ObjectMapper objectMapper = new ObjectMapper();
		String actualJson = objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(dynBean.toSimpleMap());
		String expectedJson = IOUtils.toString(
				TXmlToJson.class.getResourceAsStream("/xmlToJson/content.json"), 
				Charset.defaultCharset());
		
		System.out.println(actualJson);
		
		Assert.assertEquals(actualJson.trim(), expectedJson.trim());
	}
}
