package com.yukthitech.autox.ide.help;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.doc.PluginInfo;
import com.yukthitech.autox.doc.StepInfo;

public class PluginInfoTreeNode extends DefaultMutableTreeNode
{
	private static final long serialVersionUID = 1L;
	private PluginInfo pluginInfo;

	public PluginInfoTreeNode(PluginInfo pluginInfo, DocInformation docInformation)
	{
		super(pluginInfo.getName());
		this.pluginInfo = pluginInfo;
		
		for(StepInfo step : docInformation.getSteps())
		{
			if(step.getRequiredPlugins().contains(pluginInfo.getName()))
			{
				super.add(new StepInforTreeNode(step, docInformation));
			}
		}
	}

	public PluginInfoTreeNode(String name, DocInformation docInformation)
	{
		super(name);
		
		for(StepInfo step : docInformation.getSteps())
		{
			if(CollectionUtils.isEmpty(step.getRequiredPlugins()))
			{
				super.add(new StepInforTreeNode(step, docInformation));
			}
		}
	}

	public PluginInfo getPluginInfo()
	{
		
		return pluginInfo;
	}

}
