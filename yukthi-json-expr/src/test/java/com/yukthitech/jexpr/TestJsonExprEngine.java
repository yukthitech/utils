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
package com.yukthitech.jexpr;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.ccg.xml.XMLBeanParser;

/**
 * Test cases for json expression engine.
 * @author akiran
 */
public class TestJsonExprEngine
{
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private JsonExprEngine jsonExprEngine = new JsonExprEngine();
	
	/*
	private FreeMarkerService freeMarkerService = new FreeMarkerService();
	
	@BeforeClass
	public void setup() throws Exception
	{
		ClassScannerService classScannerService = new ClassScannerService();
		freeMarkerService.setClassScannerService(classScannerService);
		
		ReflectionUtils.setFieldValue(jsonExprEngine, "freeMarkerService", freeMarkerService);
		ReflectionUtils.invokeMethod(freeMarkerService, FreeMarkerService.class.getDeclaredMethod("initValueCollector"));
	}
	*/
	
	private Object[][] loadXmlFile(String file)
	{
		JelTestBeans beans = new JelTestBeans();
		XMLBeanParser.parse(TestJsonExprEngine.class.getResourceAsStream(file), beans);
		
		int size = beans.getTestBeans().size();
		Object[][] rows = new Object[size][];
		
		for(int i = 0; i < size; i++)
		{
			rows[i] = new Object[] {beans.getTestBeans().get(i)};
		}
		
		return rows;
	}
	
	@DataProvider(name = "jsonElDataProvider")
	public Object[][] getTestData() throws Exception
	{
		return loadXmlFile("/json-expr-test-data.xml");
	}
	
	@DataProvider(name = "jsonElNeagtiveDataProvider")
	public Object[][] getNegativeTestData() throws Exception
	{
		return loadXmlFile("/json-expr-negative-test-data.xml");
	}

	@DataProvider(name = "pojoJsonElDataProvider")
	public Object[][] getPojoTestData() throws Exception
	{
		return loadXmlFile("/pojo-json-expr-test-data.xml");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(dataProvider =  "jsonElDataProvider")
	public void testJel(JelTestBean bean) throws Exception
	{
		//parse json contents from test bean
		Map<String, Object> context = (Map) objectMapper.readValue(bean.getContext(), Object.class);
		Object expectedResult = objectMapper.readValue(bean.getExpectedResult(), Object.class);
		
		//execute the jel
		String res = jsonExprEngine.processJson(bean.getTemplate(), context);
		Object actualResult = objectMapper.readValue(res, Object.class);
		
		//as deep comparison is done, re-covert data to json (removing identations) and compare
		Assert.assertEquals(objectMapper.writeValueAsString(actualResult), objectMapper.writeValueAsString(expectedResult));
	}

	@Test(dataProvider =  "pojoJsonElDataProvider")
	public void testPojoJel(JelTestBean bean) throws Exception
	{
		//parse json contents from test bean
		Object expectedResult = objectMapper.readValue(bean.getExpectedResult(), Object.class);
		
		//execute the jel
		String res = jsonExprEngine.processJson(bean.getTemplate(), bean.getPojoContext());
		Object actualResult = objectMapper.readValue(res, Object.class);
		
		//as deep comparison is done, re-covert data to json (removing identations) and compare
		Assert.assertEquals(objectMapper.writeValueAsString(actualResult), objectMapper.writeValueAsString(expectedResult));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(dataProvider =  "jsonElNeagtiveDataProvider")
	public void testJelNegative(JelTestBean bean) throws Exception
	{
		//parse json contents from test bean
		Map<String, Object> context = (Map) objectMapper.readValue(bean.getContext(), Object.class);
		
		//execute the jel
		try
		{
			jsonExprEngine.processJson(bean.getTemplate(), context);
			Assert.fail("No error is thrown");
		}catch(Exception ex)
		{
			ex.printStackTrace();
			
			String expMssg = bean.getExpectedError();
			String actMssg = ex.getMessage().replaceAll("\\s+", " ");
			
			Assert.assertTrue(actMssg.startsWith(expMssg),
					String.format("\nActual Message: %s\nDoes not start with: %s", actMssg, expMssg));
		}
	}
}
