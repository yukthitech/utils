package com.yukthitech.ccg.xml;

import java.util.Collections;

public abstract class BeanFactory
{
	public static final Object SKIP_TO_NORMAL = new Object();

	public Object createBean(BeanNode node)
	{
		return DefaultParserHandler.createBean(node, (ClassLoader) null, Collections.<String, Object>emptyMap());
	}

	public Object createBean(BeanNode node, ClassLoader loader)
	{
		return DefaultParserHandler.createBean(node, loader, Collections.<String, Object>emptyMap());
	}

	public abstract Object buildBean(Class<?> preferredType, BeanNode node);

	public abstract Object buildAttributeBean(Class<?> preferredType, BeanNode node, String attName);

	public void finalize()
	{}
}
