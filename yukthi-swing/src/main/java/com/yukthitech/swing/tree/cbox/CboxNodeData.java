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
package com.yukthitech.swing.tree.cbox;

import java.util.ArrayList;
import java.util.List;

/**
 * Checkbox tree node.
 * @author akranthikiran
 */
public class CboxNodeData
{
	/**
	 * Label of node.
	 */
	protected String label;
	
	/**
	 * Flag indicating if node is selected or not.
	 */
	protected SelectStatus status;
	
	/**
	 * User data that can be used to represent this node.
	 */
	private Object userData;
	
	private List<Object> selectedChildren;
	
	/**
	 * Flag indicating this data is read-only or not.
	 */
	private boolean readOnly;
	
	/**
	 * Indicates status cannot be changed, which will disable
	 * the current folder expansion also.
	 */
	private boolean fixedStatus;

	public CboxNodeData(Object userData, String label)
	{
		this.userData = userData;
		this.label = label;
		this.status = SelectStatus.NOT_SELECTED;
	}
	
	public static CboxNodeData newReadOnlyData(Object userData, String label)
	{
		CboxNodeData data = new CboxNodeData(userData, label);
		data.readOnly = true;
		
		return data;
	}
	
	public void setFixedStatus(boolean fixedStatus)
	{
		this.fixedStatus = fixedStatus;
	}
	
	public boolean isFixedStatus()
	{
		return fixedStatus;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getLabel()
	{
		return label;
	}

	public SelectStatus getStatus()
	{
		return status;
	}

	public void setStatus(SelectStatus status, ICboxTreeStateManager stateManager)
	{
		this.status = status;
		
		if(this.userData != null && stateManager != null)
		{
			stateManager.stateChanged(this.userData, this.status == SelectStatus.SELECTED);
		}
	}
	
	public Object getUserData()
	{
		return userData;
	}
	
	public void childSelectionChanged(Object child, boolean selected, ICboxTreeStateManager stateManager)
	{
		if(selectedChildren == null)
		{
			selectedChildren = new ArrayList<Object>();
		}
		
		if(selected)
		{
			selectedChildren.add(child);
		}
		else
		{
			selectedChildren.remove(child);
		}
		
		if(selectedChildren.isEmpty())
		{
			setStatus(SelectStatus.NOT_SELECTED, stateManager);
		}
		else
		{
			setStatus(SelectStatus.PARTIALLY_SELECTED, stateManager);
		}
	}
	
	public boolean isEditable()
	{
		if(readOnly)
		{
			return false;
		}
		
		if(fixedStatus)
		{
			return false;
		}
		
		return (status != SelectStatus.PARTIALLY_SELECTED);
	}

	public String toString()
	{
		return label + " = " + status;
	}
}