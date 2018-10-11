package com.yukthitech.autox.ide.help;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.doc.PluginInfo;

public class HelpTreeModel extends DefaultTreeModel
{
	private static final long serialVersionUID = 1L;

	DefaultMutableTreeNode rootNode;

	public HelpTreeModel(DocInformation docInformation)
	{
		super(new DefaultMutableTreeNode("Plugins"));
		rootNode = (DefaultMutableTreeNode) super.getRoot();
		
		rootNode.add(new PluginInfoTreeNode("Default Plugin", docInformation));

		for(PluginInfo pluginInfo : docInformation.getPlugins())
		{
			rootNode.add(new PluginInfoTreeNode(pluginInfo, docInformation));
		}
	}

}
