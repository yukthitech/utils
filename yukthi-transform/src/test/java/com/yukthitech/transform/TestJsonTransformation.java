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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.transform.template.JsonTemplateFactory;
import com.yukthitech.transform.template.TransformTemplate;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

/**
 * Test cases for json expression engine.
 * @author akiran
 */
public class TestJsonTransformation
{
	private JsonTemplateFactory templateFactory = new JsonTemplateFactory();
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private TransformEngine jsonExprEngine = new TransformEngine();
	
	@BeforeClass
	public void setup() throws Exception
	{
		FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
		freeMarkerEngine.loadClass(TestMethods.class);
		
		jsonExprEngine.setFreeMarkerEngine(freeMarkerEngine);
	}
	
	private Object[][] loadXmlFile(String file)
	{
		TransformTestBeans beans = new TransformTestBeans();
		XMLBeanParser.parse(TestJsonTransformation.class.getResourceAsStream(file), beans);
		
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
	
	@DataProvider(name = "jsonElDataProvider")
	public Object[][] getTestData() throws Exception
	{
		return loadXmlFile("/json-trans-test-data.xml");
	}
	
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
	
	private String processJson(String json, Object context)
	{
		TransformTemplate tempalte = templateFactory.pareseTemplate(json);
		
		//System.out.println("Got Template as: \n" + tempalte);
		return jsonExprEngine.processAsString(tempalte, context);
	}

	@Test
	public void testException() throws Exception
	{
		//parse json contents from test bean
		Map<String, Object> context = CommonUtils.toMap("test", "Value");

		try
		{
			//execute the jel
			processJson("{\"key\": \"@fmarker: errorMethod(test)\"}", context);
			Assert.fail("No exception is thrown.");
		}catch(Exception ex)
		{
			Throwable th = ex;
			boolean reqErrorFound = false;
			
			while(th != null)
			{
				if("Test error".equals(th.getMessage()))
				{
					reqErrorFound = true;
					break;
				}
				
				th = th.getCause();
			}
			
			
			Assert.assertTrue(reqErrorFound);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(dataProvider =  "jsonElDataProvider")
	public void testJel(TransformTestBean bean) throws Exception
	{
		//parse json contents from test bean
		Map<String, Object> context = (Map) objectMapper.readValue(bean.getContext(), Object.class);
		Object expectedResult = objectMapper.readValue(bean.getExpectedResult(), Object.class);
		
		//execute the jel
		String res = processJson(bean.getTemplate(), context);
		Object actualResult = objectMapper.readValue(res, Object.class);
		
		//as deep comparison is done, re-covert data to json (removing identations) and compare
		Assert.assertEquals(objectMapper.writeValueAsString(actualResult), objectMapper.writeValueAsString(expectedResult));
	}

	@Test(dataProvider =  "pojoJsonElDataProvider")
	public void testPojoJel(TransformTestBean bean) throws Exception
	{
		//parse json contents from test bean
		Object expectedResult = objectMapper.readValue(bean.getExpectedResult(), Object.class);
		
		//execute the jel
		String res = processJson(bean.getTemplate(), bean.getPojoContext());
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
			processJson(bean.getTemplate(), context);
			Assert.fail("No error is thrown");
		}catch(Exception ex)
		{
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
}
