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
package com.yukthitech.persistence.query;

public class ColumnParam
{
	private String name;
	private Object value;
	private int index;
	private String sequence;
	
	public ColumnParam(String name, Object value, int index, String sequence)
	{
		this.name = name;
		this.value = value;
		this.index = index;
		this.sequence = sequence;
	}

	public ColumnParam(String name, Object value, int index)
	{
		this(name, value, index, null);
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public boolean isSequenceGenerated()
	{
		return (sequence != null);
	}
	
	public String getSequence()
	{
		return sequence;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(getClass().getName());
		builder.append("[");
		
		builder.append(name).append(" = ").append(value);

		builder.append("]");
		return builder.toString();
	}
}
