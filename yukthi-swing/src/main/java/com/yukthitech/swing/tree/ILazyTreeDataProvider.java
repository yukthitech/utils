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
