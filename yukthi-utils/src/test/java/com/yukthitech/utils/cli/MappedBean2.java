package com.yukthitech.utils.cli;

/**
 * Test bean meant for testing command line arguments parsing.
 * @author akiran
 */
public class MappedBean2
{
	@CliArgument(name = "A", longName = "address", description = "Address for the bean")
	private String address;
	
	@CliArgument(name = "f", longName = "flag", description = "Flag for the bean", required = false)
	private boolean flag;

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public boolean isFlag()
	{
		return flag;
	}

	public void setFlag(boolean flag)
	{
		this.flag = flag;
	}
}
