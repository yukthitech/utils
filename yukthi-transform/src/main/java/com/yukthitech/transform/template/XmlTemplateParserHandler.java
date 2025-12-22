package com.yukthitech.transform.template;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.DefaultParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class XmlTemplateParserHandler extends DefaultParserHandler
{
	public static final String TRANSFORM_RESERVE_URI = "/transform";
	
	@Override
	public boolean isReserveUri(String uri)
	{
		return TRANSFORM_RESERVE_URI.equals(uri);
	}
	
	private XmlDynamicBean mapToBean(BeanNode node, XMLAttributeMap att)
	{
		XmlDynamicBean rootBean = new XmlDynamicBean(node.getName());
		
		for(Map.Entry<String, XMLAttributeMap.Attribute> entry : att.entrySet())
		{
			if(!entry.getValue().isReserved())
			{
				continue;
			}
			
			rootBean.setReserveAttr(entry.getKey(), entry.getValue().getValue());
		}
		
		return rootBean;
	}
	
	@Override
	public Object createRootBean(BeanNode node, XMLAttributeMap att)
	{
		return mapToBean(node, att);
	}

	@Override
	public Object createBean(BeanNode node, XMLAttributeMap att, ClassLoader loader)
	{
		return mapToBean(node, att);
	}
	
	@Override
	public Object processReservedNode(BeanNode node, XMLAttributeMap att)
	{
		if(XmlTemplateFactory.SET.equals(node.getName()))
		{
			if(StringUtils.isBlank(att.get("name", null)))
			{
				throw new InvalidStateException("Manadatory parameter 'name' is missing");
			}
		}
		
		if(XmlTemplateFactory.INCLUDE_FILE.equals(node.getName())
				|| XmlTemplateFactory.INCLUDE_RES.equals(node.getName())
				 || XmlTemplateFactory.RES.equals(node.getName())
				)
		{
			if(StringUtils.isBlank(att.get("path", null)))
			{
				throw new InvalidStateException("Manadatory parameter 'path' is missing");
			}
		}

		return mapToBean(node, att);
	}
	
	@Override
	public void processReserveNodeEnd(BeanNode node, XMLAttributeMap att)
	{
		XmlDynamicBean parent = (XmlDynamicBean) node.getParent();
		parent.addReserve(node.getName(), node.getActualBean());
	}
}
