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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.ccg.xml.DynamicBean;
import com.yukthitech.ccg.xml.DynamicBeanParserHandler;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.test.beans.DynamicTestBean;
import com.yukthitech.test.beans.ListBean;
import com.yukthitech.test.beans.MapBean;
import com.yukthitech.test.beans.TestBeanForRef;
import com.yukthitech.test.beans.TestDataBean;
import com.yukthitech.utils.CommonUtils;

/**
 * @author akiran
 *
 */
public class TXmlBeanParser
{
	@Test
	public void testDynamicBean()
	{
		DynamicTestBean bean = new DynamicTestBean();
		XMLBeanParser.parse(TXmlBeanParser.class.getResourceAsStream("/xml-parser-dynamic-bean.xml"), bean);
		
		Assert.assertEquals(bean.getData().get("attr1"), "val1");
		Assert.assertEquals(bean.getData().get("attr2"), "val2");
		Assert.assertEquals(bean.getData().get("node1"), "node-val1");
		Assert.assertEquals(bean.getData().get("node2"), "node-val2");
		
		TestDataBean testDataBean = (TestDataBean)bean.getData().get("node3");
		Assert.assertEquals(testDataBean.getIntVal(), 100);
	}
	
	/**
	 * Ensure xml to dynamic bean converstion is happening properly. And also ensures dynamic bean to map-of-map 
	 * coversion is also happening properly.
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testWithDynamicHandler()
	{
		DynamicBean bean = (DynamicBean) XMLBeanParser.parse(TXmlBeanParser.class.getResourceAsStream("/xml-parser-dynamic-bean-1.xml"), null, new DynamicBeanParserHandler());

		System.out.println(bean.toSimpleMap());
		
		Assert.assertEquals(bean.get("attr1"), "val1");
		Assert.assertEquals(bean.get("attr2"), "val2");
		
		Assert.assertEquals(bean.get("node1"), "node-val1");
		Assert.assertEquals(bean.get("node2"), Arrays.asList("node-val2", "node-val3"));
		
		Assert.assertTrue(bean.get("node3") instanceof DynamicBean);
		Assert.assertEquals( ((DynamicBean) bean.get("node3")).get("intVal"), "100");
		
		Assert.assertTrue(bean.get("node4") instanceof List);
		Assert.assertTrue( ((List) bean.get("node4")).get(0) instanceof DynamicBean);
		
		//Ensure conversion of dynamic bean to map is working fine
		Map<String, Object> map = bean.toSimpleMap();
		
		Assert.assertEquals(map.get("attr1"), "val1");
		Assert.assertEquals(map.get("attr2"), "val2");
		
		Assert.assertEquals(map.get("node1"), "node-val1");
		Assert.assertEquals(map.get("node2"), Arrays.asList("node-val2", "node-val3"));
		
		Assert.assertTrue(map.get("node3") instanceof Map);
		Assert.assertEquals( ((Map) map.get("node3")).get("intVal"), "100");
		
		Assert.assertTrue(bean.get("node4") instanceof List);
		Assert.assertTrue( ((List) map.get("node4")).get(0) instanceof Map);
	}

	/**
	 * Tests collection loading from xml.
	 */
	@Test
	public void testCollectionLoading()
	{
		ListBean bean = new ListBean();
		XMLBeanParser.parse(TXmlBeanParser.class.getResourceAsStream("/list-data.xml"), bean);
		
		Assert.assertEquals(bean.getStrList(), Arrays.asList("Value1", "Value2"));
		Assert.assertEquals(bean.getIntSet(), CommonUtils.toSet(10, 20));
		Assert.assertEquals(bean.getBeanList(), Arrays.asList(new TestDataBean(5), new TestDataBean(6)));
		Assert.assertEquals(bean.getObjectList(), Arrays.asList(new TestDataBean(5)));
	}
	
	@Test
	public void testMapLoading()
	{
		MapBean bean = new MapBean();
		XMLBeanParser.parse(TXmlBeanParser.class.getResourceAsStream("/map-data.xml"), bean);
		
		Assert.assertEquals(bean.getStrMap(), CommonUtils.toMap("key1", "Value1", "key2", "Value2"));
		Assert.assertEquals(bean.getIntMap(), CommonUtils.toMap(1, 10, 2, 20));
		Assert.assertEquals(bean.getBeanMap(), CommonUtils.toMap("key1", new TestDataBean(5), "key2", new TestDataBean(6)));
		Assert.assertEquals(bean.getObjectMap(), CommonUtils.toMap("key1", new TestDataBean(5)));
	}

	@Test
	public void testBeanReferences()
	{
		ListBean bean = new ListBean();
		XMLBeanParser.parse(TXmlBeanParser.class.getResourceAsStream("/bean-id-ref.xml"), bean);
		
		Assert.assertEquals(bean.getObjectList(), Arrays.asList(
			new TestBeanForRef("Some string value", Arrays.asList(1, 2, 3, 4)),
			new TestBeanForRef("Other value", null)
		));
	}
}
