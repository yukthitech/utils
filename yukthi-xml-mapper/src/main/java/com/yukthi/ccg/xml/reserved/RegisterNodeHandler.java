package com.yukthi.ccg.xml.reserved;

import com.yukthi.ccg.xml.BeanNode;
import com.yukthi.ccg.xml.IParserHandler;
import com.yukthi.ccg.xml.XMLAttributeMap;

/**
 * Handler to register custom reserver handler. This node should result in bean of type
 * {@link IReserveNodeHandler}.
 * 
 * @author akiran
 */
@NodeName(namePattern = "customNodeHandler")
public class RegisterNodeHandler implements IReserveNodeHandler
{
	@Override
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att)
	{
		Object nodeHandler = parserHandler.createBean(node, null);
		IReserveNodeHandler customNodeHandler = (IReserveNodeHandler) nodeHandler;
		parserHandler.registerReserveNodeHandler(customNodeHandler);
		
		return null;
	}

	@Override
	public void handleCustomNodeEnd(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att)
	{
	}
}
