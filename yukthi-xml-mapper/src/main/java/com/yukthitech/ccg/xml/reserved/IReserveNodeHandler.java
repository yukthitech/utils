/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.ccg.xml.reserved;

import org.xml.sax.Locator;

import com.yukthitech.ccg.xml.BeanNode;
import com.yukthitech.ccg.xml.IParserHandler;
import com.yukthitech.ccg.xml.XMLAttributeMap;

/**
 * This is plugin contract for handling reserve nodes (non-standard reserve nodes). The handler should
 * get registered with target parser handler using {@link IParserHandler#registerReserveNodeHandler(IReserveNodeHandler)} 
 * 
 * @author akiran
 */
public interface IReserveNodeHandler
{
	/**
	 * Called when a new custom node is encountered (with non default name).
	 * This method is expected to return bean, if the node can be handled.
	 * 
	 * @param parserHandler Parent handler
	 * @param node
	 *            Custom node started
	 * @param att
	 *            Attributes of custom node.
	 * @param locator Sax locator 
	 * @return Bean representing custom node.
	 */
	public Object createCustomNodeBean(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator);

	/**
	 * Called when custom node is encountered.
	 * 
	 * @param parserHandler Parent handler
	 * @param node
	 *            Node ended.
	 * @param att
	 *            Attributes of the node.
	 * @param locator Sax locator 
	 */
	public void handleCustomNodeEnd(IParserHandler parserHandler, BeanNode node, XMLAttributeMap att, Locator locator);
}
