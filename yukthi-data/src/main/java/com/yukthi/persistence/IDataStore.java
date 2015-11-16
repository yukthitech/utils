package com.yukthi.persistence;

import java.util.List;
import java.util.Set;

import com.yukthi.persistence.conversion.ConversionService;
import com.yukthi.persistence.query.ChildrenExistenceQuery;
import com.yukthi.persistence.query.CountQuery;
import com.yukthi.persistence.query.CreateIndexQuery;
import com.yukthi.persistence.query.CreateTableQuery;
import com.yukthi.persistence.query.DeleteQuery;
import com.yukthi.persistence.query.DropTableQuery;
import com.yukthi.persistence.query.FetchChildrenIdsQuery;
import com.yukthi.persistence.query.FinderQuery;
import com.yukthi.persistence.query.SaveQuery;
import com.yukthi.persistence.query.UpdateQuery;
import com.yukthi.utils.ObjectWrapper;

public interface IDataStore
{
	public ConversionService getConversionService();
	
	public ITransactionManager<? extends ITransaction> getTransactionManager();
	
	public Set<String> getColumnNames(String tableName);
	
	public void checkAndCreateSequence(String name);
	
	public void createTable(CreateTableQuery query);
	
	public void createIndex(CreateIndexQuery query);
	
	public long getCount(CountQuery existenceQuery, EntityDetails entityDetails);
	
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
}


