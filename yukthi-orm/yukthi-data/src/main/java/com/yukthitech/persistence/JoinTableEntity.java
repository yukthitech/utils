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
package com.yukthitech.persistence;

/**
 * Dynamic entity representing join table entries
 * @author akiran
 */
public class JoinTableEntity
{
	public static final String FIELD_JOIN_COLUMN = "joinColumn";
	public static final String FIELD_INV_JOIN_COLUMN = "inverseJoinColumn";
	
	private Object joinColumn;
	private Object inverseJoinColumn;

	public JoinTableEntity(Object joinColumn, Object inverseJoinColumn)
	{
		this.joinColumn = joinColumn;
		this.inverseJoinColumn = inverseJoinColumn;
	}

	/**
	 * @return the {@link #joinColumn joinColumn}
	 */
	public Object getJoinColumn()
	{
		return joinColumn;
	}

	/**
	 * @param joinColumn
	 *            the {@link #joinColumn joinColumn} to set
	 */
	public void setJoinColumn(Object joinColumn)
	{
		this.joinColumn = joinColumn;
	}

	/**
	 * @return the {@link #inverseJoinColumn inverseJoinColumn}
	 */
	public Object getInverseJoinColumn()
	{
		return inverseJoinColumn;
	}

	/**
	 * @param inverseJoinColumn
	 *            the {@link #inverseJoinColumn inverseJoinColumn} to set
	 */
	public void setInverseJoinColumn(Object inverseJoinColumn)
	{
		this.inverseJoinColumn = inverseJoinColumn;
	}

}
