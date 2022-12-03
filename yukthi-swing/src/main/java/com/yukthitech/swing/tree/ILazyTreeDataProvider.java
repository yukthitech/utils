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
package com.yukthitech.swing.tree;

import java.util.List;

/**
 * Interface for providing data for checkbox tree.
 * @author akranthikiran
 */
public interface ILazyTreeDataProvider<N>
{
	/**
	 * Fetches the root node from which rest of tree expands on demand.
	 * @return root node data
	 */
	public N getRootNode();
	
	/**
	 * Fetches child nodes of specified parent. If no child nodes
	 * are available empty list should be returned.
	 * @param parent parent for which child nodes to be fetched
	 * @return child nodes data
	 */
	public List<N> getChildNodes(N parent);
	
	/**
	 * Fetches label for specified node. Defaults to string representation of node.
	 * @param node node
	 * @return label for node
	 */
	public default String getLabel(N node)
	{
		return node.toString();
	}
}
