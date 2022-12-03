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
package com.yukthitech.ccg.xml.writer.data;

import java.util.List;
import java.util.Objects;

import com.yukthitech.ccg.xml.annotations.XmlAttribute;
import com.yukthitech.ccg.xml.annotations.XmlElement;
import com.yukthitech.ccg.xml.annotations.XmlIgnore;
import com.yukthitech.utils.CommonUtils;

public class ActionPlan
{
	@XmlAttribute
	private int id;
	
	private String name;
	
	@XmlElement(cdata = true)
	private String description;
	
	private List<ActionPlanStep> steps;
	
	private String dummy;
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public List<ActionPlanStep> getSteps()
	{
		return steps;
	}

	public void setSteps(List<ActionPlanStep> steps)
	{
		this.steps = steps;
	}

	@XmlIgnore
	public String getDummy()
	{
		return dummy;
	}

	public void setDummy(String dummy)
	{
		this.dummy = dummy;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}

		if(!(obj instanceof ActionPlan))
		{
			return false;
		}

		ActionPlan other = (ActionPlan) obj;
		
		boolean res = Objects.equals(id, other.id) && 
				Objects.equals(name, other.name) && 
				Objects.equals(description, other.description) &&
				CommonUtils.isEqual(steps, other.steps);
				; 
		return res;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
}
