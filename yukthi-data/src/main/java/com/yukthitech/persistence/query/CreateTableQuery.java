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

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.query.data.TableStructure;
import com.yukthitech.persistence.repository.annotations.Charset;

/**
 * The Class CreateTableQuery.
 */
public class CreateTableQuery extends Query
{
	
	/** The table structure. */
	private TableStructure tableStructure;

	/**
	 * Indicates if this is join table or not
	 */
	private boolean isUniqueKeyDisabled;

	/**
	 * Character set to be used.
	 */
	private Charset charset;

	/**
	 * Instantiates a new creates the table query.
	 *
	 * @param entityDetails the entity details
	 */
	public CreateTableQuery(EntityDetails entityDetails)
	{
		super(entityDetails);

		this.tableStructure = new TableStructure(entityDetails);
	}

	/**
	 * Instantiates a new creates the table query.
	 *
	 * @param entityDetails the entity details
	 * @param isUniqueKeyDisable the is unique key disable
	 */
	public CreateTableQuery(EntityDetails entityDetails, boolean isUniqueKeyDisable)
	{
		this(entityDetails);
		this.isUniqueKeyDisabled = isUniqueKeyDisable;
	}

	/**
	 * Gets the table structure.
	 *
	 * @return the table structure
	 */
	public TableStructure getTableStructure()
	{
		return tableStructure;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.persistence.query.Query#getTableName()
	 */
	public String getTableName()
	{
		return tableStructure.getTableName();
	}

	/**
	 * @return the {@link #isUniqueKeyDisabled isJoinTable}
	 */
	public boolean isUniqueKeyDisabled()
	{
		return isUniqueKeyDisabled;
	}

	/**
	 * @param isUniqueKeyDisabled
	 *            the {@link #isUniqueKeyDisabled isJoinTable} to set
	 */
	public void setUniqueKeyDisabled(boolean isUniqueKeyDisabled)
	{
		this.isUniqueKeyDisabled = isUniqueKeyDisabled;
	}
	
	/**
	 * Gets the character set to be used.
	 *
	 * @return the character set to be used
	 */
	public Charset getCharset()
	{
		return charset;
	}

	/**
	 * Sets the character set to be used.
	 *
	 * @param charset the new character set to be used
	 */
	public void setCharset(Charset charset)
	{
		this.charset = charset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(getClass().getName());
		return builder.toString();
	}
}
