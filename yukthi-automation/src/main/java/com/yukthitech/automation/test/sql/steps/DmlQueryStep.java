package com.yukthitech.automation.test.sql.steps;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.Param;
import com.yukthitech.automation.config.DbPlugin;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Step to execute DML Query.
 * @author akiran
 */
@Executable(name = "dmlQuery", requiredPluginTypes = DbPlugin.class, message = "Executes specified DML Query on specified data source.")
public class DmlQueryStep implements IStep 
{
	/**
	 * Query to execute.
	 */
	@Param(description = "Query to execute.")
	private String query;
	
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
	 * If specified, number of rows affected will be set on the context.
	 */
	@Param(description = "If specified, number of rows affected will be set on the context", required = false)
	private String countAttribute;
	
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
	 * Sets the query.
	 * 
	 * @param query the new query.
	 */
	public void setQuery(String query) 
	{
		this.query = query;
	}
	
	/**
	 * Sets the if specified, number of rows affected will be set on the context.
	 *
	 * @param countAttribute the new if specified, number of rows affected will be set on the context
	 */
	public void setCountAttribute(String countAttribute)
	{
		this.countAttribute = countAttribute;
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
	public void execute(AutomationContext context, IExecutionLogger exeLogger) 
	{
		DbPlugin dbConfiguration = context.getPlugin(DbPlugin.class);
		DataSource dataSource = dbConfiguration.getDataSource(dataSourceName);

		if(dataSource == null)
		{
			throw new InvalidStateException("No data source found with specified name - {}", dataSourceName);
		}

		Connection connection = null;

		try
		{
			connection = dataSource.getConnection();
			
			Map<String, Object> paramMap = new HashMap<>();
			List<Object> values = new ArrayList<>();
			
			String processedQuery = QueryUtils.extractQueryParams(query, context, paramMap, values);
			
			exeLogger.debug("On data-source '{}' executing query: \n\n{} \n\nParams: {}", dataSource, query, paramMap);
			
			exeLogger.trace("On data-source '{}' executing processed query: \n\n{} \n\nParams: {}", dataSource, processedQuery, values);
			
			int count = QueryUtils.executeDml(connection, processedQuery, values);
			
			connection.commit();
			
			exeLogger.debug("Number of rows affected - {}", count);
			
			if(count <= 0 && failOnNoUpdate)
			{
				exeLogger.error("No records got updated by query");
				throw new InvalidStateException("No records got updated by query - {}", query);
			}
			
			if(countAttribute != null)
			{
				exeLogger.debug("Setting result count {} on context attribute - {}", count, countAttribute);
				context.setAttribute(countAttribute, count);
			}

		} catch(SQLException ex)
		{
			exeLogger.error(ex, "An error occurred while executing query");
			throw new InvalidStateException(ex, "An erorr occurred while executing sql query - {}", query);
		} finally
		{
			DbUtils.closeQuietly(connection);
		}
	}
}
