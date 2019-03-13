package com.yukthitech.autox.test.lang.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yukthitech.autox.common.CommonFreeMarkerMethods;

/**
 * Test cases for common free marker methods.
 * @author akiran
 */
public class TCommonFreeMarkerMethods
{
	private ParentBean parentBean = new ParentBean();
	
	private Set<String> names = new HashSet<>( Arrays.asList("name1", "name2") );
	
	@BeforeClass
	public void setup()
	{
		parentBean.setParentBean(new ParentBean());
		parentBean.setSubbean(new Subbean("name1"));
		parentBean.getParentBean().setSubbean(new Subbean("name2"));
	}
	
	@Test
	public void testGetValueByXpath()
	{
		Assert.assertTrue( names.contains(CommonFreeMarkerMethods.getValueByXpath(parentBean, "/subbean/name")) );
	}

	@Test
	public void testGetValuesByXpath()
	{
		Set<Object> resNames = new HashSet<>( CommonFreeMarkerMethods.getValuesByXpath(parentBean, "//name") );
		Assert.assertTrue( names.equals(resNames) );
	}


	@Test
	public void testCountByXpath()
	{
		Assert.assertEquals(CommonFreeMarkerMethods.countOfXpath(parentBean, "//name"), 2);
		Assert.assertEquals(CommonFreeMarkerMethods.countOfXpath(parentBean, "//name1"), 0);	
	}
}
