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
			String expMssg = bean.getExpectedError();
			
			Assert.assertEquals(ex.getMessage().replaceAll("\\s+", " "), expMssg);
		}
	}
}
