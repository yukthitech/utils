package com.yukthitech.autox.ide.help;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.doc.PluginInfo;
import com.yukthitech.autox.doc.StepInfo;
import com.yukthitech.utils.fmarker.FreeMarkerMethodDoc;

public class HelpNodeData
{
	private Object nodeValue;
	
	private String label;
	
	private List<HelpNodeData> childNodes;
	
	private boolean filtered = true;

	public HelpNodeData(Object nodeValue, DocInformation docInformation)
	{
		this.nodeValue = nodeValue;
		
		if(docInformation == null)
		{
			this.label = "" + nodeValue;
			return;
		}
		
		if(nodeValue instanceof PluginInfo)
		{
			PluginInfo pluginInfo = (PluginInfo) nodeValue;
			this.label = pluginInfo.getName();
			
			childNodes = new ArrayList<>();
			
			for(StepInfo step : docInformation.getSteps())
			{
				if(!step.getRequiredPlugins().contains(label))
				{
					continue;
				}
				
				childNodes.add( new HelpNodeData(step, docInformation) );
			}
		}
		else if(nodeValue instanceof String)
		{
			this.label = (String) nodeValue;
			childNodes = new ArrayList<>();
			
			for(StepInfo step : docInformation.getSteps())
			{
				if(CollectionUtils.isNotEmpty(step.getRequiredPlugins()))
				{
					continue;
				}
				
				childNodes.add( new HelpNodeData(step, docInformation) );
			}
		}
		else if(nodeValue instanceof StepInfo)
		{
			StepInfo info = (StepInfo) nodeValue;
			this.label = info.getName();
		}
		else if(nodeValue instanceof Set)
		{
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Set<FreeMarkerMethodDoc> freeMarkerMethods = (Set) nodeValue;
			this.label = "Methods";
			childNodes = new ArrayList<>();
			
			for(FreeMarkerMethodDoc met : freeMarkerMethods)
			{
				childNodes.add( new HelpNodeData(met, docInformation) );
			}
		}
		else if(nodeValue instanceof FreeMarkerMethodDoc)
		{
			FreeMarkerMethodDoc metDoc = (FreeMarkerMethodDoc) nodeValue;
			this.label = metDoc.getName();
		}
	}

	public Object getNodeValue()
	{
		return nodeValue;
	}

	public String getLabel()
	{
		return label;
	}

	public List<HelpNodeData> getChildNodes()
	{
		return childNodes;
	}

	public boolean isFiltered()
	{
		return filtered;
	}
	
	public void addHelpNode(HelpNodeData child)
	{
		if(this.childNodes == null)
		{
			this.childNodes = new ArrayList<>();
		}
		
		this.childNodes.add(child);
	}
	
	public boolean filter(String text)
	{
		boolean res = false;
		
		if(childNodes != null)
		{
			for(HelpNodeData child: childNodes)
			{
				if(child.filter(text))
				{
					res = true;
				}
			}
		}
		
		if(res || StringUtils.isEmpty(text))
		{
			filtered = true;
			return true;
		}
		
		filtered = label.toLowerCase().contains(text);
		return filtered;
	}
	
}
