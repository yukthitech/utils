package com.yukthitech.autox;

import org.apache.commons.beanutils.BeanUtils;

import com.yukthitech.autox.config.AppConfigParserHandler;
import com.yukthitech.autox.config.AppConfigValueProvider;
import com.yukthitech.autox.config.ApplicationConfiguration;
import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.ccg.xml.util.StringUtil;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Parser handler for loading test suite files.
 * @author akiran
 */
public class TestSuiteParserHandler extends DefaultParserHandler
{
	private static final String ATTR_BEAN_REF = "beanRef";
	
	private static final String ATTR_BEAN_COPY = "beanCopy";
	
	/**
	 * Application configuration.
	 */
	private ApplicationConfiguration appConfig;

	/**
	 * Value provider for providing application properties and system/env properties.
	 */
	private AppConfigValueProvider appConfigValueProvider;

	public TestSuiteParserHandler(AutomationContext context, ApplicationConfiguration appConfig)
	{
		this.appConfig = appConfig;
		appConfigValueProvider = new AppConfigValueProvider(appConfig.getApplicationProperties());
		
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
		return StringUtil.getPatternString(text, appConfigValueProvider, AppConfigParserHandler.EXPR_PATTERN, AppConfigParserHandler.EXPR_ESCAPE_PREFIX, AppConfigParserHandler.EXPR_ESCAPE_REPLACE);
	}
}
