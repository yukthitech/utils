package com.yukthitech.utils.cli;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TCommandLineOptions
{
	/**
	 * Tests command line args mapping to bean using short names.
	 * @throws MissingArgumentException
	 */
	@Test
	public void testWithSingleBean_shortNames() throws MissingArgumentException
	{
		CommandLineOptions options = OptionsFactory.buildCommandLineOptions(MappedBean1.class);
		MappedBean1 bean = (MappedBean1) options.parseBean("-n", "kranthi", "-a", "36");
		
		Assert.assertEquals(bean.getName(), "kranthi");
		Assert.assertEquals(bean.getAge(), 36);
	}

	/**
	 * Tests command line args mapping to bean using long names.
	 * @throws MissingArgumentException
	 */
	@Test
	public void testWithSingleBean_longNames() throws MissingArgumentException
	{
		CommandLineOptions options = OptionsFactory.buildCommandLineOptions(MappedBean1.class);
		MappedBean1 bean = (MappedBean1) options.parseBean("--name", "kranthi", "--age", "36");
		
		Assert.assertEquals(bean.getName(), "kranthi");
		Assert.assertEquals(bean.getAge(), 36);
	}

	/**
	 * Tests command line args mapping to bean when optional param is missing.
	 * @throws MissingArgumentException
	 */
	@Test
	public void testWithSingleBean_missingOptionalParam() throws MissingArgumentException
	{
		CommandLineOptions options = OptionsFactory.buildCommandLineOptions(MappedBean1.class);
		MappedBean1 bean = (MappedBean1) options.parseBean("--name", "kranthi");
		
		Assert.assertEquals(bean.getName(), "kranthi");
		Assert.assertEquals(bean.getAge(), 0);
	}

	/**
	 * Tests command line args mapping to bean when mandatory param is missing.
	 * @throws MissingArgumentException
	 */
	@Test(expectedExceptions = MissingArgumentException.class, 
		expectedExceptionsMessageRegExp = "Required command line argument \\'\\-n\\' \\(\\-\\-name\\) is not specified\\.")
	public void testWithSingleBean_missingRequiredParam() throws MissingArgumentException
	{
		CommandLineOptions options = OptionsFactory.buildCommandLineOptions(MappedBean1.class);
		options.parseBean("--age", "20");
	}
	
	/**
	 * Tests help information is working.
	 */
	@Test
	public void tesHelpInfo()
	{
		CommandLineOptions options = OptionsFactory.buildCommandLineOptions(MappedBean1.class);
		String help = options.fetchHelpInfo("java test");
		
		Assert.assertNotNull(help);
		System.out.println(help);
	}

	/**
	 * Tests command line args mapping to beans using short names.
	 * @throws MissingArgumentException
	 */
	@Test
	public void testWithMultipleBeans() throws MissingArgumentException
	{
		CommandLineOptions options = OptionsFactory.buildCommandLineOptions(MappedBean1.class, MappedBean2.class);
		System.out.println(options.fetchHelpInfo("test"));
		
		Map<Class<?>, Object> beans = options.parseBeans("-n", "kranthi", "-age", "36", "--flag", "-A", "Some address");
		
		MappedBean1 bean1 = (MappedBean1) beans.get(MappedBean1.class);
		MappedBean2 bean2 = (MappedBean2) beans.get(MappedBean2.class);
		
		Assert.assertEquals(bean1.getName(), "kranthi");
		Assert.assertEquals(bean1.getAge(), 36);
		
		Assert.assertEquals(bean2.getAddress(), "Some address");
		Assert.assertEquals(bean2.isFlag(), true);
	}
}
