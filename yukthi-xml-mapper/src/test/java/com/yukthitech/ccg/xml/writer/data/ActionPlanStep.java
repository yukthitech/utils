package com.yukthitech.ccg.xml.writer.data;

import java.util.Objects;

public class ActionPlanStep
{
	private String name;
	
	private Object data;
	
	public ActionPlanStep()
	{}

	public ActionPlanStep(String name, Object data)
	{
		this.name = name;
		this.data = data;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Object getData()
	{
		return data;
	}

	public void setData(Object data)
	{
		this.data = data;
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

		if(!(obj instanceof ActionPlanStep))
		{
			return false;
		}

		ActionPlanStep other = (ActionPlanStep) obj;
		return Objects.equals( name, other.name )
				&& Objects.equals(data, other.data)
				;
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
