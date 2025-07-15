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
package com.yukthitech.ccg.xml.writer;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.TreeMap;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.ccg.xml.writer.data.ActionPlan;
import com.yukthitech.ccg.xml.writer.data.ActionPlanStep;
import com.yukthitech.ccg.xml.writer.data.TestEnum;
import com.yukthitech.test.beans.ListBean;
import com.yukthitech.test.beans.MapBean;
import com.yukthitech.test.beans.TestDataBean;
import com.yukthitech.utils.CommonUtils;

public class TXmlWriter
{
	@Test
	public void testBasicSerialization()
	{
		ActionPlan actionPlan = new ActionPlan();
		actionPlan.setId(20);
		actionPlan.setName("TestPlan");
		actionPlan.setDescription("Some test desc");
		actionPlan.setDummy("Dummy str");
		
		 String xml = XmlBeanWriter.writeToString("action-plan", actionPlan);
		 System.out.println(xml);
		 
		 ActionPlan readActionPlan = new ActionPlan();
		 XMLBeanParser.parse(new ByteArrayInputStream(xml.getBytes()), readActionPlan);
		 
		 Assert.assertEquals(readActionPlan, actionPlan);
	}
	
	@Test
	public void testListSerialization()
	{
		ListBean mainValue = new ListBean();
		
		LinkedList<TestDataBean> beans = new LinkedList<TestDataBean>();
		beans.add(new TestDataBean(5));
		beans.add(new TestDataBean(3));
		mainValue.setBeanList(beans);
		
		mainValue.setIntSet(CommonUtils.toSet(3, 4, 6, 7));
		mainValue.setStrList(Arrays.asList("one", "two", "three"));
		mainValue.setObjectList(new LinkedList<Object>(Arrays.asList(5, 6, 7)));
		
		 String xml = XmlBeanWriter.writeToString("test", mainValue);
		 System.out.println(xml);
		 
		 ListBean readValue = new ListBean();
		 XMLBeanParser.parse(new ByteArrayInputStream(xml.getBytes()), readValue);
		 
		 Assert.assertEquals(readValue, mainValue);
	}
	
	@Test
	public void testMapSerialization()
	{
		MapBean mainValue = new MapBean();

		mainValue.setStrMap(CommonUtils.<String, String>toMap("one", "one", "two", "2"));
		mainValue.setIntMap(CommonUtils.<Integer, Integer>toMap(1, 100, 2, 200));
		mainValue.setObjectMap( new TreeMap<String, Object>(CommonUtils.<String, Object>toMap("intVal", 100, "bean", new TestDataBean(23))) );
		
		 String xml = XmlBeanWriter.writeToString("test", mainValue);
		 System.out.println(xml);
		 
		 MapBean readValue = new MapBean();
		 XMLBeanParser.parse(new ByteArrayInputStream(xml.getBytes()), readValue);
		 
		 Assert.assertEquals(readValue, mainValue);
	}
	
	@Test
	public void testComplexObject()
	{
		ActionPlan mainValue = new ActionPlan();
		mainValue.setId(20);
		mainValue.setName("TestPlan");
		mainValue.setDescription("Some test desc");
		mainValue.setDummy("Dummy str");

		ActionPlanStep step1 = new ActionPlanStep("Step1", new TestDataBean(20));
		ActionPlanStep step2 = new ActionPlanStep("Step2", TestEnum.ONE);

		mainValue.setSteps(Arrays.asList(step1, step2));
		
		XmlWriterConfig config = new XmlWriterConfig()
				.setExcludeXmlDeclaration(true)
				.setIndentXml(true)
				.setReadCompatible(true);

		String xml = XmlBeanWriter.writeToString("test", mainValue, config);
		System.out.println(xml);

		ActionPlan readValue = new ActionPlan();
		XMLBeanParser.parse(new ByteArrayInputStream(xml.getBytes()), readValue);

		Assert.assertEquals(readValue, mainValue);
		
	}
}
