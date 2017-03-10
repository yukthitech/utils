package com.yukthitech.automation.test.sql.steps;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.automation.AbstractStep;
import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.Param;
import com.yukthitech.automation.config.DbPlugin;
import com.yukthitech.automation.test.TestCaseFailedException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Step to execute multiple dml queries in single transaction.
 * @author akiran
 */
@Executable(name = "multiDmlQuery", requiredPluginTypes = DbPlugin.class, message = "Executes specified multiple DML queries in single transaction")
public class MultiDmlQueryStep extends AbstractStep
{
	/**
	 * List of queries executed in single transaction.
	 */
	@Param(description = "Queries to execute.")
	private List<String> queries;
	
	/**
	 * Name of the data source to use.
	 */
	@Param(description = "Data source to be used for sql execution.")
	private String dataSourceName;
	
	/**
	 * Flag to indicate, if test case has to failed if there are no updates.
	 */
	@Param(description = "If true, error will be thrown if no rows are affected by specified DML.\nDefault Value: false", required = false)
	private boolean failOnNoUpdate = false;

	/**
	 * Sets the name of the data source to use.
	 *
	 * @param dataSourceName the new name of the data source to use
	 */
	public void setDataSourceName(String dataSourceName) 
	{
		this.dataSourceName = dataSourceName;
	}
	
	/**
	 * Adds the query to execute.
	 * 
	 * @param queries the new queries.
	 */
	public void addQuery(String query) 
	{
		if(StringUtils.isBlank(query))
		{
			throw new NullPointerException("Null or blank query is specified");
		}
		
		if(this.queries == null)
		{
			this.queries = new ArrayList<>();
		}
		
		this.queries.add(query);
	}
	
	/**
	 * Sets the flag to indicate, if test case has to failed if there are no updates.
	 *
	 * @param failOnNoUpdate the new flag to indicate, if test case has to failed if there are no updates
	 */
	public void setFailOnNoUpdate(boolean failOnNoUpdate)
	{
		this.failOnNoUpdate = failOnNoUpdate;
	}

	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		DbPlugin dbConfiguration = context.getPlugin(DbPlugin.class);
		DataSource dataSource = dbConfiguration.getDataSource(dataSourceName);

		if(dataSource == null)
		{
			throw new InvalidStateException("No data source found with specified name - {}", dataSourceName);
		}

		Connection connection = null;

		String query = null;
		Map<String, Object> paramMap = new HashMap<>();
		List<Object> values = new ArrayList<>();
		String processedQuery = null;
		
		try
		{
			connection = dataSource.getConnection();
			
			for (int i = 0; i < queries.size(); i++) 
			{
				query = queries.get(i);
				
				paramMap.clear();
				values.clear();
				
				processedQuery = QueryUtils.extractQueryParams(query, context, paramMap, values);
				
				exeLogger.debug("On data-source '{}' executing query: \n<code class='SQL'>{}</code> \nParams: {}", dataSourceName, query, paramMap);
				
				exeLogger.trace("On data-source '{}' executing processed query: \n<code class='SQL'>{}</code> \nParams: {}", dataSourceName, processedQuery, values);
				
				int count = QueryUtils.executeDml(connection, processedQuery, values);
				
				exeLogger.debug("Number of rows affected: {}", count);
				
				if(count <= 0 && failOnNoUpdate)
				{
					exeLogger.error("No records got updated by query failing test case.");
					throw new InvalidStateException("No records got updated by query: {}", query);
				}
			}
			
			connection.commit();

		} catch(SQLException ex)
		{
			exeLogger.error(ex, "An error occurred while executing query: {}", query);
			DbUtils.rollbackAndCloseQuietly(connection);
			
			throw new TestCaseFailedException("An erorr occurred while executing sql query: {}", query, ex);
		} finally
		{
			DbUtils.closeQuietly(connection);
		}
	}
}
