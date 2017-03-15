package com.yukthi.persistence.query;

import com.yukthi.persistence.EntityDetails;
import com.yukthi.persistence.query.data.TableStructure;
import com.yukthi.persistence.repository.annotations.Charset;

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
	 * @see com.yukthi.persistence.query.Query#getTableName()
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
