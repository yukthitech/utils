package com.yukthitech.autox.ide.ui;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Base class for tree nodes.
 */
public class BaseTreeNode extends DefaultMutableTreeNode
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Id of the node.
	 */
	private String id;
	
	/**
	 * Icon for tree node.
	 */
	private Icon icon;
	
	/**
	 * Label for node.
	 */
	private String label;
	
	/**
	 * Child nodes added to this node.
	 */
	private Map<String, BaseTreeNode> childNodes = new LinkedHashMap<>();
	
	public BaseTreeNode()
	{}

	public BaseTreeNode(Icon icon, String label)
	{
		this.icon = icon;
		this.label = label;
	}
	
	/**
	 * Gets the id of the node.
	 *
	 * @return the id of the node
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Gets the icon for tree node.
	 *
	 * @return the icon for tree node
	 */
	public Icon getIcon()
	{
		return icon;
	}

	/**
	 * Sets the icon for tree node.
	 *
	 * @param icon the new icon for tree node
	 */
	public void setIcon(Icon icon)
	{
		this.icon = icon;
	}

	/**
	 * Gets the label for node.
	 *
	 * @return the label for node
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the label for node.
	 *
	 * @param label the new label for node
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public void addChild(String id, BaseTreeNode node)
	{
		if(childNodes.containsKey(id))
		{
			throw new InvalidStateException("A node with specified id already exist: {}", id);
		}
		
		node.id = id;
		childNodes.put(id, node);
		super.add(node);
	}
	
	public void insert(String id, BaseTreeNode node, int index)
	{
		if(childNodes.containsKey(id))
		{
			throw new InvalidStateException("A node with specified id already exist: {}", id);
		}
		
		node.id = id;
		childNodes.put(id, node);
		super.insert(node, index);
	}

	public BaseTreeNode getChild(String id)
	{
		return childNodes.get(id);
	}
	
	public Collection<BaseTreeNode> getChildNodes()
	{
		return childNodes.values();
	}
	
	public void removeChildNodes(Set<String> nodeIds)
	{
		BaseTreeNode node = null;
		
		for(String id : nodeIds)
		{
			node = childNodes.remove(id);
			
			if(node == null)
			{
				continue;
			}
			
			((BaseTreeNode) node.getParent()).remove(node);
		}
	}
	
	public void reload(boolean childReload)
	{}
}
