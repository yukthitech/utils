package com.yukthitech.ccg.xml.util;

import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

public interface ValueProvider
{
	public class MapValueProvider implements ValueProvider
	{
		private Map<String, ?> nameToValue;

		public MapValueProvider(Map<String, ?> nameToValue)
		{
			if(nameToValue == null)
				throw new NullPointerException("Map nameToValue can not be null.");

			this.nameToValue = nameToValue;
		}

		public Object getValue(String name)
		{
			return nameToValue.get(name);
		}
	}

	public class PropertyValueProvider implements ValueProvider
	{
		private Object rootBean;

		public PropertyValueProvider(Object bean)
		{
			this.rootBean = bean;
		}

		@Override
		public Object getValue(String name)
		{
			try
			{
				return PropertyUtils.getProperty(rootBean, name);
			} catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while invoking property " + name + " on bean " + rootBean.getClass().getName(), ex);
			}
		}

	}

	public Object getValue(String name);
}
