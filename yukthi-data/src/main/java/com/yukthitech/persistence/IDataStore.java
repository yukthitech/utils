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

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import com.yukthitech.persistence.conversion.ConversionService;
import com.yukthitech.persistence.query.ChildrenExistenceQuery;
import com.yukthitech.persistence.query.AggregateQuery;
import com.yukthitech.persistence.query.CreateExtendedTableQuery;
import com.yukthitech.persistence.query.CreateIndexQuery;
import com.yukthitech.persistence.query.CreateTableQuery;
import com.yukthitech.persistence.query.DeleteQuery;
import com.yukthitech.persistence.query.DropTableQuery;
import com.yukthitech.persistence.query.FetchChildrenIdsQuery;
import com.yukthitech.persistence.query.FinderQuery;
import com.yukthitech.persistence.query.SaveQuery;
import com.yukthitech.persistence.query.UpdateQuery;
import com.yukthitech.persistence.repository.IDataSourceCloser;
import com.yukthitech.utils.ObjectWrapper;

public interface IDataStore
{
	/**
	 * This method is called during init of repository factory. This entity details factory is expected
	 * to be used by datastore to fetch entity details and constraint details on need basis.
	 * @param entityDetailsFactory
	 */
	public void setEntityDetailsFactory(EntityDetailsFactory entityDetailsFactory);
	
	/**
	 * Fetches entity details for specified type from underlying entity details factory. Returns null 
	 * if no matching entity is not found or if the entity is not loaded yet.
	 * @param type type for which entity details to be fetched
	 * @return matching entity details
	 */
	public EntityDetails getEntityDetails(Class<?> type);
	
	/**
	 * Sets the specified native query factory for this data store
	 * @param factory
	 */
	public void setNativeQueryFactory(NativeQueryFactory factory);
	
	public ConversionService getConversionService();
	
	public ITransactionManager<? extends ITransaction> getTransactionManager();
	
	public boolean tableExists(String tableName);
	
	public Set<String> getColumnNames(String tableName);
	
	public void createTable(CreateTableQuery query);
	
	public void createExtendedTable(CreateExtendedTableQuery query);
	
	public void createIndex(CreateIndexQuery query);
	
	public Double fetchAggregateValue(AggregateQuery existenceQuery, EntityDetails entityDetails);
	
	/**
	 * Executes the specified save-query using structure details from specified entity-details. And stores
	 * generated id if any, into idGenerated.
	 * 
	 * @param saveQuery
	 * @param entityDetails
	 * @param idGenerated
	 * @return Number of rows effected (1 or zero in general)
	 */
	public int save(SaveQuery saveQuery, EntityDetails entityDetails, ObjectWrapper<Object> idGenerated);

	public int update(UpdateQuery updateQuery, EntityDetails entityDetails);
	
	public int delete(DeleteQuery deleteQuery, EntityDetails entityDetails);
	
	public int checkChildrenExistence(ChildrenExistenceQuery childrenExistenceQuery);
	
	public List<Object> fetchChildrenIds(FetchChildrenIdsQuery fetchChildrenIdsQuery);

	public List<Record> executeFinder(FinderQuery findQuery, EntityDetails entityDetails, IFinderRecordProcessor recordProcessor);
	
	/**
	 * Used to execute native search query indicated by "queryName"
	 * @param queryName Name of the query to execute
	 * @param context Context to be used to inject query params
	 * @param recordProcessor record processor to control result records.
	 * @return List of fetched records
	 */
	public List<Record> executeNativeFinder(String queryName, Object context, IFinderRecordProcessor recordProcessor);
	
	/**
	 * Used to execute native DML queries
	 * @param queryName DML query name to execute
	 * @param context Context to be used to inject query params
	 * @return Number of records affected
	 */
	public int executeNativeDml(String queryName, Object context);
	
	/**
	 * Drops the underlying entity table
	 * @param query
	 */
	public void dropTable(DropTableQuery query);
	
	/**
	 * Indicates whether check for foreign key relation should be done explicitly. Needed by NOSQL DB, if integrity needs
	 * to be maintained.
	 * This is used 
	 * 		During delete, to check/delete child entities with parent entity
	 * 		During insert/update, to check if parent entity exists or not
	 * 
	 * DataStore which needs this explicit support, should have setter to accept whether this explicit check is required by
	 * the application. By this developer can choose whether it should be enabled or not.
	 * @return
	 */
	public boolean isExplicitForeignCheckRequired();
	
	/**
	 * Indicates whether explicit check for unique constraint is required or not
	 * @return
	 */
	public boolean isExplicitUniqueCheckRequired();
	
	/**
	 * Returns true, if paging is supported by this data store.
	 * @return true if paging is supported
	 */
	public boolean isPagingSupported();
	
	/**
	 * Specified wether unique id column has to be added for fetching actual id.
	 * @return
	 */
	public boolean isUniqueIdColumnRequired();
	
	public void close(IDataSourceCloser closer) throws SQLException;
}


