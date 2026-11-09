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
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.ccg.xml.DynamicBean;
import com.yukthitech.ccg.xml.DynamicBeanParserHandler;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.test.beans.DynamicNodeAcceptorBean;
import com.yukthitech.test.beans.DynamicNodeAcceptorRoot;
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
	public void testDynamicBean() throws Exception
	{
		DynamicTestBean bean = new DynamicTestBean();
		XMLBeanParser.parse(TXmlBeanParser.class.getResourceAsStream("/xml-parser-dynamic-bean.xml"), bean, new DynamicBeanParserHandler());
		
		ObjectMapper objectMapper = new ObjectMapper();
		String beanJson = objectMapper.writeValueAsString(bean.getData().toSimpleMap());
		
		String expectedJson = objectMapper.writeValueAsString(
				objectMapper.readValue(TXmlBeanParser.class.getResourceAsStream("/xml-parser-dynamic-bean.json"), Object.class)
			);
		
		Assert.assertEquals(beanJson, expectedJson);
		
		TestDataBean testDataBean = (TestDataBean)bean.getData().get("node3");
		Assert.assertEquals(testDataBean.getIntVal(), 100);
	}
	
	/**
	 * Ensure xml to dynamic bean converstion is happening properly. And also ensures dynamic bean to map-of-map 
	 * coversion is also happening properly.
	 */
	@Test
	public void testWithDynamicHandler() throws Exception
	{
		DynamicBean bean = (DynamicBean) XMLBeanParser.parse(TXmlBeanParser.class.getResourceAsStream("/xml-parser-dynamic-bean-1.xml"), null, new DynamicBeanParserHandler());

		ObjectMapper objectMapper = new ObjectMapper();
		String beanJson = objectMapper.writeValueAsString(bean.toSimpleMap());
		
		String expectedJson = objectMapper.writeValueAsString(
				objectMapper.readValue(TXmlBeanParser.class.getResourceAsStream("/xml-parser-dynamic-bean-1.json"), Object.class)
			);
		
		Assert.assertEquals(beanJson, expectedJson);
	}

	/**
	 * MANDATORY feature: DefaultParserHandler + IDynamicNodeAcceptor must accept text-valued
	 * child elements that replace dynamic attributes (FunctionRef-style params).
	 * <p>
	 * DefaultParserHandler.getDefaultDynamicNodeType() returns String so attribute-less
	 * dynamic children become text nodes and never hit createBean(null) /
	 * "Either of type or preferredType is mandatory". Do not remove without an equivalent.
	 * </p>
	 */
	@Test
	public void testDynamicNodeAcceptorTextChildren_mandatory()
	{
		DynamicNodeAcceptorRoot root = new DynamicNodeAcceptorRoot();
		XMLBeanParser.parse(TXmlBeanParser.class.getResourceAsStream("/xml-parser-dynamic-text-children.xml"), root);

		DynamicNodeAcceptorBean node = root.getNode();
		Assert.assertNotNull(node);
		Assert.assertEquals(node.getName(), "demo");
		Assert.assertEquals(node.getParams().get("paramA"), "value-a");
		Assert.assertEquals(node.getParams().get("paramB"), "value-b");
		Assert.assertTrue(node.getParams().get("paramA") instanceof String);
	}

	/**
	 * MANDATORY feature: DynamicBeanParserHandler must NOT default untyped dynamic children
	 * to String. Nested structure (and text leaves under DynamicBean) must be built via
	 * createBean → DynamicBean, which XML transform / DynamicBean flows rely on.
	 * <p>
	 * Forcing String.class in SAX for all handlers breaks nested trees. Keep
	 * DynamicBeanParserHandler.getDefaultDynamicNodeType() returning null.
	 * </p>
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testDynamicBeanNestedStructure_mandatory()
	{
		DynamicBean bean = (DynamicBean) XMLBeanParser.parse(
				TXmlBeanParser.class.getResourceAsStream("/xml-parser-dynamic-nested-structure.xml"),
				null,
				new DynamicBeanParserHandler());

		Assert.assertEquals(bean.get("attr"), "root-attr");

		// Text leaves are DynamicBeans with text (not SAX String text-nodes)
		Assert.assertTrue(bean.get("plainText") instanceof DynamicBean, "plainText must be DynamicBean, not String node");
		Assert.assertEquals(((DynamicBean) bean.get("plainText")).getTextContent(), "leaf-text");

		Object nestedObj = bean.get("nested");
		Assert.assertTrue(nestedObj instanceof DynamicBean, "nested must remain DynamicBean, not String");

		DynamicBean nested = (DynamicBean) nestedObj;
		Assert.assertTrue(nested.get("childAttr") instanceof DynamicBean);
		Assert.assertEquals(((DynamicBean) nested.get("childAttr")).get("attrOnly"), "a1");
		Assert.assertTrue(nested.get("childText") instanceof DynamicBean);
		Assert.assertEquals(((DynamicBean) nested.get("childText")).getTextContent(), "nested-text");

		DynamicBean deeper = (DynamicBean) nested.get("deeper");
		Assert.assertNotNull(deeper);
		Assert.assertTrue(deeper.get("item") instanceof DynamicBean);
		Assert.assertEquals(((DynamicBean) deeper.get("item")).getTextContent(), "x");

		// toSimpleMap collapses text-only DynamicBeans to String for JSON-friendly maps
		Map<String, Object> simple = (Map<String, Object>) bean.toSimpleMap();
		Assert.assertEquals(simple.get("plainText"), "leaf-text");
		Assert.assertTrue(simple.get("nested") instanceof Map);
		Map<String, Object> nestedMap = (Map<String, Object>) simple.get("nested");
		Assert.assertEquals(nestedMap.get("childText"), "nested-text");
		Assert.assertTrue(nestedMap.get("deeper") instanceof Map);
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
