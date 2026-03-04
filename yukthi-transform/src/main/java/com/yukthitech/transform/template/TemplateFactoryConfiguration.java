package com.yukthitech.transform.template;

public class TemplateFactoryConfiguration
{
	private static ThreadLocal<TemplateFactoryConfiguration> instance = new ThreadLocal<>();

	private boolean xpathDisabled;

	public void setXpathDisabled(boolean xpathDisabled)
	{
		this.xpathDisabled = xpathDisabled;
	}

	public boolean isXpathDisabled()
	{
		return xpathDisabled;
	}

	void pushCurrentInstance()
	{
		instance.set(this);
	}

	void popCurrentInstance()
	{
		instance.remove();
	}

	static TemplateFactoryConfiguration getCurrentInstance()
	{
		return instance.get();
	}
}
