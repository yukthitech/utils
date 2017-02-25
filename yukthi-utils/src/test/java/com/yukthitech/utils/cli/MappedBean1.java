package com.yukthitech.utils.cli;

/**
 * Test bean meant for testing command line arguments parsing.
 * @author akiran
 */
public class MappedBean1
{
	@CliArgument(name = "n", longName = "name", description = "Name for the bean")
	private String name;
	
	@CliArgument(name = "a", longName = "age", description = "Age for the bean", required = false)
	private int age;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getAge()
	{
		return age;
	}

	public void setAge(int age)
	{
		this.age = age;
	}
}
