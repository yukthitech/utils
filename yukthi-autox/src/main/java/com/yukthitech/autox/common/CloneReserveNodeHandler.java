package com.yukthitech.autox.common;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.InvalidArgumentException;
import org.xml.sax.Locator;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.filter.ExpressionFactory;
import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.DynamicBean;
import com.yukthitech.ccg.xml.IParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.ccg.xml.reserved.IReserveNodeHandler;
import com.yukthitech.ccg.xml.reserved.NodeName;
import com.yukthitech.utils.exceptions.InvalidStateException;

@NodeName(namePattern = "clone")
public class CloneReserveNodeHandler implements IReserveNodeHandler
{
	private static final String ATTR_BEAN_ID = "beanId";
	
	private static final String ATTR_PROPERTY = "property";
	
	private static final Pattern PROP_PATTERN = Pattern.compile("^\\s*\\w+\\s*\\:.*");
	
	/**
	 * Property info to be altered.
	 * @author akiran
	 */
	public static class SetInfo
	{
		/**
		 * Property expression.
		 */
		private String property;
		
		/**
		 * Value expression.
		 */
		private String value;

		public void setProperty(String property)
		{
			this.property = property;
		}

		public void setValue(String value)
		{
			this.value = value;
		}
	}
	
	public static class RemoveInfo
	{
		/**
		 * Property expression.
		 */
		private String property;
		
		public void setProperty(String property)
		{
			this.property = property;
		}
		
	}
	
	/**
	 * Used to store clone info.
	 * @author akiran
	 */
	public static class BeanCloneInfo
	{
		/**
		 * Bean to be cloned and modified.
		 */
		private Object bean;
		
		public BeanCloneInfo(Object bean)
		{
			this.bean = bean;
		}
		
		public void setBeanId(String beanId)
		{
		}
		
		public void setProperty(String property)
		{
		}
		
		public void setSet(SetInfo info)
		{
			AutomationContext automationContext = AutomationContext.getInstance();
			ExpressionFactory exprFactory = ExpressionFactory.getExpressionFactory();
			
			String prop = info.property;
			
			if(!PROP_PATTERN.matcher(prop).matches())
			{
				prop = "prop: " + prop;
			}
			
			Object value = exprFactory.parseExpression(automationContext, info.value);
			exprFactory.setExpressionValue(automationContext, prop, value, bean);
		}
		
		public void setRemove(RemoveInfo removeInfo)
		{
			AutomationContext automationContext = AutomationContext.getInstance();
			ExpressionFactory exprFactory = ExpressionFactory.getExpressionFactory();

			exprFactory.removeByExpression(automationContext, removeInfo.property, bean);
		}
	}
	
	@Override
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		String id = att.get(ATTR_BEAN_ID, null);
		String property = att.get(ATTR_PROPERTY, null);
		
		Object actualBean = parserHandler.getBean(id);
		
		if(actualBean == null)
		{
			throw new InvalidArgumentException("Invalid/no id specified for bean cloning: " + id);
		}
		
		if(StringUtils.isBlank(property))
		{
			throw new InvalidArgumentException("No property name specified for cloned bean");
		}
		
		//create a clone
		try
		{
			Object clone = SerializationUtils.clone((Serializable) actualBean);
			return new BeanCloneInfo(clone);
		}catch(Exception ex)
		{
			throw new InvalidStateException("Failed to clone bean represented by id: {}", id, ex);
		}
	}

	@Override
	public void handleCustomNodeEnd(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator)
	{
		//replace the modified bean on the node. So that, that will take effect
		BeanCloneInfo cloneInfo = (BeanCloneInfo) node.getActualBean();
		node.replaceBean(cloneInfo.bean);
		
		String propName = att.get(ATTR_PROPERTY, null);
		Object parent = node.getParent();
		
		if(parent instanceof DynamicBean)
		{
			((DynamicBean) parent).add(propName, cloneInfo.bean);
			return;
		}
		
		try
		{
			PropertyUtils.setProperty(parent, propName, cloneInfo.bean);
		}catch(Exception ex)
		{
			String className = parent != null ? parent.getClass().getName() : "null";
			throw new InvalidStateException("An error occurred while setting property '{}' on bean of type: {}", propName, className, ex);
		}
	}
}
