package com.yukthitech.utils.beans;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory and cache of bean property infos.
 * @author akiran
 */
public class BeanPropertyInfoFactory
{
	private Map<Class<?>, BeanPropertyInfo> typeToInfo = new HashMap<Class<?>, BeanPropertyInfo>();
	
	/**
	 * Fetches bean property info for specified type.
	 * @param type type for which property info needs to be fetched
	 * @return bean property info
	 */
	public synchronized BeanPropertyInfo getBeanPropertyInfo(Class<?> type)
	{
		BeanPropertyInfo beanPropertyInfo = typeToInfo.get(type);
		
		if(beanPropertyInfo != null)
		{
			return beanPropertyInfo;
		}
		
		List<BeanProperty> props = BeanProperty.loadProperties(type, false, false);
		props = (props == null) ? Collections.<BeanProperty>emptyList() : props;
		
		return new BeanPropertyInfo(type, props);
	}
}
