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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.transform.event.ITransformListener;
import com.yukthitech.transform.event.TransformEvent;
import com.yukthitech.transform.event.TransformEventType;
import com.yukthitech.transform.template.JsonTemplateFactory;
import com.yukthitech.transform.template.TransformTemplate;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

/**
 * Test cases for transform listener functionality.
 * @author akiran
 */
public class TestTransformListener
{
	private JsonTemplateFactory templateFactory = new JsonTemplateFactory();
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private TransformEngine transformEngine = new TransformEngine();
	
	@BeforeClass
	public void setup() throws Exception
	{
		FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
		freeMarkerEngine.loadClass(TestMethods.class);
		
		transformEngine.setFreeMarkerEngine(freeMarkerEngine);
	}
	
	/**
	 * Listener implementation that captures all events for verification.
	 */
	private static class EventCaptureListener implements ITransformListener
	{
		private List<TransformEvent> events = new ArrayList<>();
		
		@Override
		public void onTransform(TransformEvent event)
		{
			System.out.println(event);
			events.add(event);
		}
		
		public List<TransformEvent> getEvents()
		{
			return events;
		}
		
		public int getEventCount(TransformEventType eventType)
		{
			int count = 0;
			for(TransformEvent event : events)
			{
				if(event.getEventType() == eventType)
				{
					count++;
				}
			}
			return count;
		}
		
		public List<TransformEvent> getEvents(TransformEventType eventType)
		{
			List<TransformEvent> filtered = new ArrayList<>();
			for(TransformEvent event : events)
			{
				if(event.getEventType() == eventType)
				{
					filtered.add(event);
				}
			}
			return filtered;
		}
	}
	
	private String processJson(String json, Object context, EventCaptureListener listener)
	{
		transformEngine.setListener(listener);
		TransformTemplate template = templateFactory.parseTemplate("test", json);
		return transformEngine.processAsString(template, context);
	}

	@Test
	public void testSimpleValueEvaluation() throws Exception
	{
		EventCaptureListener listener = new EventCaptureListener();
		Map<String, Object> context = CommonUtils.toMap("name", "John", "age", 30);
		
		String template = "{\n"
				+ "\"name\": \"@fmarker: name\",\n"
				+ "\"age\": \"@fmarker: age\"\n"
				+ "}";
		String result = processJson(template, context, listener);
		
		// Verify result
		Object actualResult = objectMapper.readValue(result, Object.class);
		Map<String, Object> expected = CommonUtils.toMap("name", "John", "age", 30);
		Assert.assertEquals(objectMapper.writeValueAsString(actualResult), objectMapper.writeValueAsString(expected));
		
		// Verify events
		List<TransformEvent> valueEvents = listener.getEvents(TransformEventType.VALUE_EVALUATED);
		Assert.assertTrue(valueEvents.size() >= 2, "Expected at least 2 VALUE_EVALUATED events");
		
		// Check that value events have correct results
		boolean nameFound = false, ageFound = false;
		for(TransformEvent event : valueEvents)
		{
			if("name".equals(event.getKeyName()) && "John".equals(event.getResult()))
			{
				nameFound = true;
			}
			if("age".equals(event.getKeyName()) && Integer.valueOf(30).equals(event.getResult()))
			{
				ageFound = true;
			}
		}
		Assert.assertTrue(nameFound, "Name value event not found");
		Assert.assertTrue(ageFound, "Age value event not found");
	}

	@Test
	public void testConditionEvaluation() throws Exception
	{
		EventCaptureListener listener = new EventCaptureListener();
		Map<String, Object> context = CommonUtils.toMap("flag", true, "value", "test");
		
		String template = "{\n"
				+ "\"@condition\": \"flag\", \n"
				+ "\"result\": \"@fmarker: value\"\n"
				+ "}";
		String result = processJson(template, context, listener);
		
		// Verify result
		Object actualResult = objectMapper.readValue(result, Object.class);
		Map<String, Object> expected = CommonUtils.toMap("result", "test");
		Assert.assertEquals(objectMapper.writeValueAsString(actualResult), objectMapper.writeValueAsString(expected));
		
		// Verify condition event
		List<TransformEvent> conditionEvents = listener.getEvents(TransformEventType.CONDITION_EVALUATED);
		Assert.assertTrue(conditionEvents.size() >= 1, "Expected at least 1 CONDITION_EVALUATED event");
		Assert.assertEquals(conditionEvents.get(0).getResult(), Boolean.TRUE, "Condition should evaluate to true");
	}

	@Test
	public void testForEachLoop() throws Exception
	{
		EventCaptureListener listener = new EventCaptureListener();
		List<String> items = new ArrayList<>();
		items.add("item1");
		items.add("item2");
		items.add("item3");
		Map<String, Object> context = CommonUtils.toMap("items", items);
		
		String template = "{\n"
				+ "\"result\": [{\n"
				+ "\"@for-each(item)\": \"items\",\n"
				+ "\"name\": \"@fmarker: item\"\n"
				+ "}]\n"
				+ "}";
		String result = processJson(template, context, listener);
		
		// Verify result
		Object actualResult = objectMapper.readValue(result, Object.class);
		Assert.assertNotNull(actualResult);
		
		// Verify for-each events
		List<TransformEvent> listEvents = listener.getEvents(TransformEventType.LIST_EXPRESSION_EVALUATED);
		Assert.assertTrue(listEvents.size() >= 1, "Expected at least 1 LIST_EXPRESSION_EVALUATED event");
		
		List<TransformEvent> loopEvents = listener.getEvents(TransformEventType.LOOP_EVALUATED);
		Assert.assertTrue(loopEvents.size() >= 1, "Expected at least 1 LOOP_EVALUATED event");
		
		// Verify loop evaluated event has list result
		@SuppressWarnings("unchecked")
		List<Object> loopResult = (List<Object>) loopEvents.get(0).getResult();
		Assert.assertNotNull(loopResult, "Loop result should not be null");
		Assert.assertEquals(loopResult.size(), 3, "Loop should have 3 items");
	}

	@Test
	public void testForEachWithCondition() throws Exception
	{
		EventCaptureListener listener = new EventCaptureListener();
		List<Integer> numbers = new ArrayList<>();
		numbers.add(1);
		numbers.add(2);
		numbers.add(3);
		numbers.add(4);
		Map<String, Object> context = CommonUtils.toMap("numbers", numbers);
		
		String template = "{\n"
				+ "\"result\": [{\n"
				+ "\"@for-each(num)\": \"numbers\",\n"
				+ "\"@for-each-condition\": \"num gt 2\",\n"
				+ "\"value\": \"@fmarker: num\"\n"
				+ "}]\n"
				+ "}";
		String result = processJson(template, context, listener);
		
		// Verify result
		Object actualResult = objectMapper.readValue(result, Object.class);
		Assert.assertNotNull(actualResult);
		
		// Verify for-each condition events
		List<TransformEvent> forEachConditionEvents = listener.getEvents(TransformEventType.FOR_EACH_CONDITION_EVALUATED);
		Assert.assertTrue(forEachConditionEvents.size() >= 4, "Expected at least 4 FOR_EACH_CONDITION_EVALUATED events (one per iteration)");
		
		// Count true conditions (should be 2: for 3 and 4)
		int trueCount = 0;
		for(TransformEvent event : forEachConditionEvents)
		{
			if(Boolean.TRUE.equals(event.getResult()))
			{
				trueCount++;
			}
		}
		Assert.assertEquals(trueCount, 2, "Should have 2 true conditions");
	}

	@Test
	public void testSwitchStatement() throws Exception
	{
		EventCaptureListener listener = new EventCaptureListener();
		Map<String, Object> context = CommonUtils.toMap("type", "A");
		
		String template = "{\n"
				+ "\"result\": {\n"
				+ "\"@switch\": [\n"
				+ "{\"@case\": \"type == 'A'\", \"@value\": \"TypeA\"},\n"
				+ "{\"@case\": \"type == 'B'\", \"@value\": \"TypeB\"},\n"
				+ "{\"@value\": \"Default\"}\n"
				+ "]\n"
				+ "}\n"
				+ "}";
		String result = processJson(template, context, listener);
		
		// Verify result
		Object actualResult = objectMapper.readValue(result, Object.class);
		@SuppressWarnings("unchecked")
		Map<String, Object> resultMap = (Map<String, Object>) actualResult;
		Assert.assertEquals(resultMap.get("result"), "TypeA", "Switch should return TypeA");
		
		// Verify switch events
		List<TransformEvent> switchConditionEvents = listener.getEvents(TransformEventType.SWITCH_CONDITION_EVALUATED);
		Assert.assertTrue(switchConditionEvents.size() >= 1, "Expected at least 1 SWITCH_CONDITION_EVALUATED event");
		
		List<TransformEvent> switchValueEvents = listener.getEvents(TransformEventType.SWITCH_VALUE_EVALUATED);
		Assert.assertTrue(switchValueEvents.size() >= 1, "Expected at least 1 SWITCH_VALUE_EVALUATED event");
		Assert.assertEquals(switchValueEvents.get(switchValueEvents.size() - 1).getResult(), "TypeA", "Switch value should be TypeA");
	}

	@Test
	public void testKeyExpression() throws Exception
	{
		EventCaptureListener listener = new EventCaptureListener();
		Map<String, Object> context = CommonUtils.toMap("keyName", "dynamicKey", "value", "testValue");
		
		String template = "{\n"
				+ "\"@fmarker: keyName\": \"@fmarker: value\"\n"
				+ "}";
		String result = processJson(template, context, listener);
		
		// Verify result
		Object actualResult = objectMapper.readValue(result, Object.class);
		@SuppressWarnings("unchecked")
		Map<String, Object> resultMap = (Map<String, Object>) actualResult;
		Assert.assertEquals(resultMap.get("dynamicKey"), "testValue", "Dynamic key should be set correctly");
		
		// Verify key expression events
		// Note: KEY_EXPRESSION_EVALUATED is only fired for for-each loops, not for regular key expressions
		List<TransformEvent> keyReplacedEvents = listener.getEvents(TransformEventType.KEY_REPLACED);
		Assert.assertTrue(keyReplacedEvents.size() >= 1, "Expected at least 1 KEY_REPLACED event");
		Assert.assertEquals(keyReplacedEvents.get(0).getResult(), "dynamicKey", "Key should be replaced with dynamicKey");
		
		List<TransformEvent> keyValueSetEvents = listener.getEvents(TransformEventType.KEY_VALUE_SET);
		Assert.assertTrue(keyValueSetEvents.size() >= 1, "Expected at least 1 KEY_VALUE_SET event");
	}

	@Test
	public void testSetVariable() throws Exception
	{
		EventCaptureListener listener = new EventCaptureListener();
		Map<String, Object> context = CommonUtils.toMap("initialValue", "initial");
		
		String template = "{\n"
				+ "\"@set(myVar)\": \"@fmarker: initialValue\",\n"
				+ "\"result\": \"@fmarker: myVar\"\n"
				+ "}";
		String result = processJson(template, context, listener);
		
		// Verify result
		Object actualResult = objectMapper.readValue(result, Object.class);
		@SuppressWarnings("unchecked")
		Map<String, Object> resultMap = (Map<String, Object>) actualResult;
		Assert.assertEquals(resultMap.get("result"), "initial", "Set variable should be accessible");
		
		// Verify set variable events
		List<TransformEvent> setVariableEvents = listener.getEvents(TransformEventType.SET_VARIABLE_EVALUATED);
		Assert.assertTrue(setVariableEvents.size() >= 1, "Expected at least 1 SET_VARIABLE_EVALUATED event");
		Assert.assertEquals(setVariableEvents.get(0).getKeyName(), "myVar", "Set variable name should be myVar");
		Assert.assertEquals(setVariableEvents.get(0).getResult(), "initial", "Set variable value should be initial");
	}

	@Test
	public void testConditionFalseValue() throws Exception
	{
		EventCaptureListener listener = new EventCaptureListener();
		Map<String, Object> context = CommonUtils.toMap("flag", false);
		
		String template = "{\n"
				+ "\"@condition\": \"flag\",\n"
				+ "\"@value\": \"trueValue\",\n"
				+ "\"@falseValue\": \"falseValue\"\n"
				+ "}";
		String result = processJson(template, context, listener);
		
		// Verify result
		Object actualResult = objectMapper.readValue(result, Object.class);
		Assert.assertEquals(actualResult, "falseValue", "Should return falseValue when condition is false");
		
		// Verify condition event
		List<TransformEvent> conditionEvents = listener.getEvents(TransformEventType.CONDITION_EVALUATED);
		Assert.assertTrue(conditionEvents.size() >= 1, "Expected at least 1 CONDITION_EVALUATED event");
		Assert.assertEquals(conditionEvents.get(0).getResult(), Boolean.FALSE, "Condition should evaluate to false");
		
		// Verify value event (for falseValue)
		List<TransformEvent> valueEvents = listener.getEvents(TransformEventType.VALUE_EVALUATED);
		boolean falseValueFound = false;
		for(TransformEvent event : valueEvents)
		{
			if("@falseValue".equals(event.getKeyName()) && "falseValue".equals(event.getResult()))
			{
				falseValueFound = true;
				break;
			}
		}
		Assert.assertTrue(falseValueFound, "False value event should be fired");
	}

	@Test
	public void testNestedTransformations() throws Exception
	{
		EventCaptureListener listener = new EventCaptureListener();
		Map<String, Object> context = CommonUtils.toMap("name", "John", "items", Arrays.asList("a", "b"));
		
		String template = "{\n"
				+ "\"user\": {\n"
				+ "\"name\": \"@fmarker: name\",\n"
				+ "\"items\": [{\n"
				+ "\"@for-each(item)\": \"items\",\n"
				+ "\"item\": \"@fmarker: item\"\n"
				+ "}]\n"
				+ "}\n"
				+ "}";
		String result = processJson(template, context, listener);
		
		// Verify result
		Object actualResult = objectMapper.readValue(result, Object.class);
		Assert.assertNotNull(actualResult);
		
		// Verify multiple event types are fired
		Assert.assertTrue(listener.getEventCount(TransformEventType.VALUE_EVALUATED) >= 1, "Should have value evaluation events");
		Assert.assertTrue(listener.getEventCount(TransformEventType.LIST_EXPRESSION_EVALUATED) >= 1, "Should have list expression events");
		Assert.assertTrue(listener.getEventCount(TransformEventType.LOOP_EVALUATED) >= 1, "Should have loop evaluation events");
		Assert.assertTrue(listener.getEventCount(TransformEventType.KEY_VALUE_SET) >= 1, "Should have key-value set events");
	}

	@Test
	public void testNullListener() throws Exception
	{
		// Test that setting null listener doesn't cause issues
		transformEngine.setListener(null);
		
		Map<String, Object> context = CommonUtils.toMap("name", "John");
		String template = "{\n"
				+ "\"name\": \"@fmarker: name\"\n"
				+ "}";
		TransformTemplate templateObj = templateFactory.parseTemplate("test", template);
		String result = transformEngine.processAsString(templateObj, context);
		
		// Verify result is still correct
		Object actualResult = objectMapper.readValue(result, Object.class);
		Map<String, Object> expected = CommonUtils.toMap("name", "John");
		Assert.assertEquals(objectMapper.writeValueAsString(actualResult), objectMapper.writeValueAsString(expected));
	}

	@Test
	public void testEventLocation() throws Exception
	{
		EventCaptureListener listener = new EventCaptureListener();
		Map<String, Object> context = CommonUtils.toMap("value", "test");
		
		String template = "{\n"
				+ "\"result\": \"@fmarker: value\"\n"
				+ "}";
		String result = processJson(template, context, listener);
		
		// Verify result
		Object actualResult = objectMapper.readValue(result, Object.class);
		Assert.assertNotNull(actualResult);
		
		// Verify events have location information
		List<TransformEvent> events = listener.getEvents();
		Assert.assertTrue(events.size() > 0, "Should have at least one event");
		
		for(TransformEvent event : events)
		{
			Assert.assertNotNull(event.getLocation(), "Event location should not be null");
			Assert.assertNotNull(event.getEventType(), "Event type should not be null");
		}
	}
}
