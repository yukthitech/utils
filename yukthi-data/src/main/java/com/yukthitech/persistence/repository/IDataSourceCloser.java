package com.yukthitech.persistence.repository;

import javax.sql.DataSource;

/**
 * Used to close the datasource in customized way.
 * @author akranthikiran
 */
public interface IDataSourceCloser
{
	public void close(DataSource source);
}
