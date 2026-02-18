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
package com.yukthitech.transform;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.ccg.xml.DynamicBean;
import com.yukthitech.ccg.xml.DynamicBeanParserHandler;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.transform.template.TransformTemplate;
import com.yukthitech.transform.template.XmlTemplateFactory;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

/**
 * Test cases for json expression engine.
 * @author akiran
 */
public class TestXmlTransformation
{
	private XmlTemplateFactory templateFactory = new XmlTemplateFactory();
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private TransformEngine transformEngine = new TransformEngine();
	
	private DynamicBeanParserHandler parserHandler = new DynamicBeanParserHandler();
	
	@BeforeClass
	public void setup() throws Exception
	{
		FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
		freeMarkerEngine.loadClass(TestMethods.class);
		
		transformEngine.setFreeMarkerEngine(freeMarkerEngine);
	}
	
	private Object[][] loadXmlFile(String file)
	{
		TransformTestBeans beans = new TransformTestBeans();
		XMLBeanParser.parse(TestXmlTransformation.class.getResourceAsStream(file), beans);
		
		int size = beans.getTestBeans().size();
		List<Object[]> rows = new ArrayList<>();
		
		for(int i = 0; i < size; i++)
		{
			TransformTestBean testBean = beans.getTestBeans().get(i);
			
			if(beans.isDisableByDefault() && !testBean.isEnabled())
			{
				continue;
			}
			
			rows.add(new Object[] {testBean});
		}
		
		return rows.toArray(new Object[1][]);
	}
	
	@DataProvider(name = "xmlTransDataProvider")
	public Object[][] getTestData() throws Exception
	{
		return loadXmlFile("/xml-trans-test-data.xml");
	}
	
	/*
	@DataProvider(name = "jsonElNeagtiveDataProvider")
	public Object[][] getNegativeTestData() throws Exception
	{
		return loadXmlFile("/json-trans-negative-test-data.xml");
	}

	@DataProvider(name = "pojoJsonElDataProvider")
	public Object[][] getPojoTestData() throws Exception
	{
		return loadXmlFile("/pojo-json-trans-test-data.xml");
	}
	*/
	
	private String processXml(String xml, Object context)
	{
		TransformTemplate tempalte = templateFactory.parseTemplate(xml);
		
		//System.out.println("Got Template as: \n" + tempalte);
		return transformEngine.processAsString(tempalte, context);
	}
	
	private Map<String, Object> parseXml(String xml)
	{
		DynamicBean bean = (DynamicBean) XMLBeanParser.parse(new ByteArrayInputStream(xml.getBytes()), parserHandler);
		return bean.toSimpleMapWithRoot();
	}

	@Test(dataProvider =  "xmlTransDataProvider")
	public void testXmlTransformation(TransformTestBean bean) throws Exception
	{
		//parse json contents from test bean
		Map<String, Object> context = parseXml(bean.getContext());
		Object expectedResult = parseXml(bean.getExpectedResult());
		
		//execute the jel
		String res = processXml(bean.getTemplate(), context);
		Object actualResult = parseXml(res);
		
		System.out.println("Xml Output:\n============= \n" + res);
		
		//as deep comparison is done, re-covert data to json (removing identations) and compare
		Assert.assertEquals(objectMapper.writeValueAsString(actualResult), objectMapper.writeValueAsString(expectedResult));
	}

	/*
	@Test(dataProvider =  "pojoJsonElDataProvider")
	public void testPojoJel(TransformTestBean bean) throws Exception
	{
		//parse json contents from test bean
		Object expectedResult = objectMapper.readValue(bean.getExpectedResult(), Object.class);
		
		//execute the jel
		String res = processXml(bean.getTemplate(), bean.getPojoContext());
		Object actualResult = objectMapper.readValue(res, Object.class);
		
		//as deep comparison is done, re-covert data to json (removing identations) and compare
		Assert.assertEquals(objectMapper.writeValueAsString(actualResult), objectMapper.writeValueAsString(expectedResult));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(dataProvider =  "jsonElNeagtiveDataProvider")
	public void testJelNegative(TransformTestBean bean) throws Exception
	{
		//parse json contents from test bean
		Map<String, Object> context = (Map) objectMapper.readValue(bean.getContext(), Object.class);
		
		//execute the jel
		try
		{
			processXml(bean.getTemplate(), context);
			Assert.fail("No error is thrown");
		}catch(Exception ex)
		{
			ex.printStackTrace();
			
			String expMssg = bean.getExpectedError();
			String actMssg = ex.getMessage().replaceAll("\\s+", " ");
			
			System.out.println(String.format("Error evluation for %s:"
					+ "\n\tActual Message: %s"
					+ "\n\tExpected Message: %s",
					bean.getName(), actMssg, expMssg));
			
			Assert.assertTrue(actMssg.startsWith(expMssg),
					String.format("\nActual Message: %s\nDoes not start with: %s", actMssg, expMssg));
		}
	}
	*/
}
