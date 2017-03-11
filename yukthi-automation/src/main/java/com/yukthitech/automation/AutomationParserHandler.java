package com.yukthitech.automation;

import org.apache.commons.beanutils.BeanUtils;

import com.yukthitech.automation.config.ApplicationConfiguration;
import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class AutomationParserHandler extends DefaultParserHandler
{
	private static final String ATTR_BEAN_REF = "beanRef";
	
	private static final String ATTR_BEAN_COPY = "beanCopy";
	
	private ApplicationConfiguration appConfig;

	public AutomationParserHandler(AutomationContext context, ApplicationConfiguration appConfig)
	{
		this.appConfig = appConfig;
		
		AutomationReserveNodeHandler reserveNodeHandler = new AutomationReserveNodeHandler(context, appConfig);
		super.registerReserveNodeHandler(reserveNodeHandler);
	}

	@Override
	public Object createBean(BeanNode node, XMLAttributeMap att, ClassLoader loader)
	{
		String beanRefName = att.getReserved(ATTR_BEAN_REF, null);
		
		if(beanRefName != null)
		{
			Object bean = appConfig.getDataBean(beanRefName);
			
			if(bean == null)
			{
				throw new InvalidStateException("Invalid data-bean name specified in bean-ref. Name: ", beanRefName);
			}
			
			return bean;
		}
		
		String beanCopyName = att.getReserved(ATTR_BEAN_COPY, null);
		
		if(beanCopyName != null)
		{
			Object bean = appConfig.getDataBean(beanCopyName);
			
			if(bean == null)
			{
				throw new InvalidStateException("Invalid data-bean name specified in bean-copy. Name: {}", beanCopyName);
			}

			try
			{
				Object copy = bean.getClass().newInstance();
				BeanUtils.copyProperties(copy, bean);
				return copy;
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while making copy of bean - {}", beanCopyName);
			}
		}
		
		return super.createBean(node, att, loader);
	}
	
	@Override
	public String processText(Object rootBean, String text)
	{
		return text;
	}
}
