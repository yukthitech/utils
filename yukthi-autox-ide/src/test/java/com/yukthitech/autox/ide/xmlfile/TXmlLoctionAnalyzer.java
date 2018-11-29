package com.yukthitech.autox.ide.xmlfile;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TXmlLoctionAnalyzer
{
	private Map<String, String> templates = new HashMap<String, String>();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public TXmlLoctionAnalyzer() throws Exception
	{
		Properties prop = new Properties();
		prop.loadFromXML(TXmlLoctionAnalyzer.class.getResourceAsStream("/xml-loc-test-templates.xml"));
		
		templates.putAll( (Map) prop );
	}
	
	private XmlFileLocation getLocation(String templateName) throws Exception
	{
		String template = templates.get(templateName);
		int pos = template.indexOf("${pos}");
		
		template = template.replace("${pos}", "");
		
		System.out.println("Position: " + pos);
		System.out.println("Testing template: \n" + template);
		
		XmlFileLocation res = XmlLoctionAnalyzer.getLocation(template, pos);
		System.out.println("Got result as: " + res);
		
		return res;
	}
	
	@Test
	public void testNewNodeLocation() throws Exception
	{
		XmlFileLocation loc = getLocation("testNewNodeLocation");
		Assert.assertEquals(loc.getType(), XmlLocationType.CHILD_ELEMENT);
		Assert.assertEquals(loc.getParentElement().getName(), "child");
	}
	
	@Test
	public void testNewNodeLocation1() throws Exception
	{
		XmlFileLocation loc = getLocation("testNewNodeLocation1");
		Assert.assertEquals(loc.getType(), XmlLocationType.CHILD_ELEMENT);
		Assert.assertEquals(loc.getParentElement().getName(), "child");
	}
	
	@Test
	public void testNewNodeLocation2() throws Exception
	{
		XmlFileLocation loc = getLocation("testNewNodeLocation2");
		Assert.assertEquals(loc.getType(), XmlLocationType.CHILD_ELEMENT);
		Assert.assertEquals(loc.getParentElement().getName(), "parent");
	}

	@Test
	public void testNewNodeLocation_WithToken() throws Exception
	{
		XmlFileLocation loc = getLocation("testNewNodeLocationWithToken");
		Assert.assertEquals(loc.getType(), XmlLocationType.CHILD_ELEMENT);
		Assert.assertEquals(loc.getParentElement().getName(), "child");
		Assert.assertEquals(loc.getCurrentToken(), "ccg:rest-");
	}
	
	@Test
	public void testNewNodeLocation_WithToken1() throws Exception
	{
		XmlFileLocation loc = getLocation("testNewNodeLocationWithToken1");
		Assert.assertEquals(loc.getType(), XmlLocationType.CHILD_ELEMENT);
		Assert.assertEquals(loc.getParentElement().getName(), "child");
		Assert.assertEquals(loc.getCurrentToken(), "ccg:rest-");
	}
	
	@Test
	public void testNewTextNodeLocation() throws Exception
	{
		XmlFileLocation loc = getLocation("testNewTextNodeLocation");
		Assert.assertEquals(loc.getType(), XmlLocationType.TEXT_ELEMENT);
		Assert.assertEquals(loc.getParentElement().getName(), "child");
		
		Assert.assertEquals(loc.getText().trim(), "Some text prefix_");
		Assert.assertEquals(loc.getCurrentToken(), "prefix_");
	}

	@Test
	public void testAttrLocation() throws Exception
	{
		XmlFileLocation loc = getLocation("testNewAttrLocation");
		Assert.assertEquals(loc.getType(), XmlLocationType.ATTRIBUTE);
		Assert.assertEquals(loc.getParentElement().getName(), "rest-get");
		Assert.assertEquals(loc.getParentElement().getNamespace(), "ccg");
	}

	@Test
	public void testAttrLocation1() throws Exception
	{
		XmlFileLocation loc = getLocation("testNewAttrLocation1");
		Assert.assertEquals(loc.getType(), XmlLocationType.ATTRIBUTE);
		Assert.assertEquals(loc.getParentElement().getName(), "rest-get");
		Assert.assertEquals(loc.getParentElement().getNamespace(), "ccg");
		Assert.assertEquals(loc.getParentElement().getAttribute("attr1").getValue(), "val1");
		Assert.assertEquals(loc.getParentElement().getAttribute("attr2").getValue(), "val2");
	}

	@Test
	public void testAttrValLocation() throws Exception
	{
		XmlFileLocation loc = getLocation("testAttrValLoc");
		Assert.assertEquals(loc.getType(), XmlLocationType.ATTRIBUTE_VALUE);
		Assert.assertEquals(loc.getParentElement().getName(), "rest-get");
		Assert.assertEquals(loc.getParentElement().getNamespace(), "ccg");
		Assert.assertEquals(loc.getParentElement().getAttribute("attr1").getValue(), "val1");
		Assert.assertEquals(loc.getParentElement().getAttribute("attr2").getValue(), "val2");
		
		Assert.assertEquals(loc.getName(), "new-attr");
		Assert.assertEquals(loc.getNameSpace(), "/fw/ccg/XMLBeanParser");
		Assert.assertEquals(loc.getPrefix(), "ccg");
	}

	@Test
	public void testAttrValLocation1() throws Exception
	{
		XmlFileLocation loc = getLocation("testAttrValLoc1");
		Assert.assertEquals(loc.getType(), XmlLocationType.ATTRIBUTE_VALUE);
		Assert.assertEquals(loc.getParentElement().getName(), "rest-get");
		Assert.assertEquals(loc.getParentElement().getNamespace(), "ccg");
		Assert.assertEquals(loc.getParentElement().getAttribute("attr1").getValue(), "val1");
		Assert.assertEquals(loc.getParentElement().getAttribute("attr2").getValue(), "val2");
		
		Assert.assertEquals(loc.getName(), "new-attr");
		Assert.assertEquals(loc.getNameSpace(), "/fw/ccg/XMLBeanParser");
		Assert.assertEquals(loc.getPrefix(), "ccg");
		Assert.assertEquals(loc.getCurrentToken(), "${val_");
		Assert.assertEquals(loc.getText(), "some val ${val_");
	}
}
