package com.yukthi.utils.fmarker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.utils.CommonUtils;

public class TFreeMarkerEngine
{
	@Test
	public void testMethodLoading()
	{
		FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
		freeMarkerEngine.loadClass(TestMethods.class);
		
		String str = "Some string values ${var1} ${var2} and ${test()} ${sum(2,3)}";
		str = freeMarkerEngine.processTemplate("Test", str, CommonUtils.toMap("var1", "val1", "var2", "val2"));
		
		Assert.assertEquals(str, "Some string values val1 val2 and test 5");
	}
	
	/**
	 * Tests the ability to pass map to free marker method.
	 */
	@Test
	public void testMapMethodParam()
	{
		FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("one", 1);
		map.put("two", 2);
		map.put("three", 3);
		
		String template = "Map is: ${mapToString(map,'#key=#value','{',', ','}','null')}";
		String res = freeMarkerEngine.processTemplate("Test", template, CommonUtils.toMap("map", map));
		
		Assert.assertEquals(res, "Map is: {one=1, two=2, three=3}");
	}

	/**
	 * Tests the ability to pass list to free marker method.
	 */
	@Test
	public void testListMethodParam()
	{
		FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
		
		List<String> lst = new ArrayList<String>(Arrays.asList("one", "two", "three"));
				
		
		String template = "List is: ${collectionToString(lst,'[',' , ',']','null')}";
		String res = freeMarkerEngine.processTemplate("Test", template, CommonUtils.toMap("lst", lst));
		
		Assert.assertEquals(res, "List is: [one , two , three]");
	}

	/**
	 * Tests the ability to use nested functions.
	 */
	@Test
	public void testNestedParam()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
		
		String template = "Date is: ${dateToStr(today(), 'dd/MM/yyyy')}";
		String res = freeMarkerEngine.processTemplate("Test", template, Collections.emptyMap());
	
		Assert.assertEquals(res, "Date is: " + dateFormat.format(new Date()));
		
		//check passing direct data object
		Date yday = DateUtils.addDays(new Date(), -1);
		template = "Date is: ${dateToStr(addDays(today(), -1), 'dd/MM/yyyy')}";
		res = freeMarkerEngine.processTemplate("Test", template, Collections.emptyMap());
	
		Assert.assertEquals(res, "Date is: " + dateFormat.format(yday));
	}
	
	/**
	 * Tests passing custom params.
	 */
	@Test
	public void testCustomParam()
	{
		FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
		String template = "Obj is: ${toString(testBean)}";
		String res = freeMarkerEngine.processTemplate("Test", template, CommonUtils.toMap("testBean", new TestBean("test")));
	
		Assert.assertEquals(res, "Obj is: TestBean[test]");
	}
}
