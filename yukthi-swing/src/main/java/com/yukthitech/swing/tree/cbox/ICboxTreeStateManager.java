package com.yukthitech.swing.tree.cbox;

/**
 * Manager to manage the state of current selection. In general this should be linked
 * with data provider. So that on refresh the new state takes affect.
 * @author akranthikiran
 */
public interface ICboxTreeStateManager
{
	
	/**
	 * Invoked when state of node is changed.
	 *
	 * @param nodeData the node data whose state is changed
	 * @param selected changed selection status
	 */
	public void stateChanged(Object nodeData, boolean selected);
}
