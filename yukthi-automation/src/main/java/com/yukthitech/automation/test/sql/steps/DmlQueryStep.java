package com.yukthitech.automation.test.sql.steps;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.IStep;
import com.yukthitech.automation.config.DbConfiguration;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Step to execute DML Query.
 * @author akiran
 */
@Executable(value = "dmlQuery", requiredConfigurationTypes = DbConfiguration.class, message = "Executes specified DML Query")
public class DmlQueryStep implements IStep 
{
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(DmlQueryStep.class);
	
	/**
	 * Query to execute.
	 */
	private String query;
	
	/**
	 * Name of the data source to use.
	 */
	private String dataSourceName;
	
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

	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger) 
	{
		DbConfiguration dbConfiguration = context.getConfiguration(DbConfiguration.class);
		DataSource dataSource = dbConfiguration.getDataSource(dataSourceName);

		if(dataSource == null)
		{
			throw new InvalidStateException("No data source found with specified name - {}", dataSourceName);
		}

		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;

		try
		{
			connection = dataSource.getConnection();
			statement = connection.createStatement();

			statement.execute(query);

		} catch(SQLException ex)
		{
			throw new InvalidStateException(ex, "An erorr occurred while executing sql query - {}", query);
		} finally
		{
			try
			{
				if(rs != null)
				{
					rs.close();
				}

				if(statement != null)
				{
					statement.close();
				}

				if(connection != null)
				{
					connection.close();
				}
			} catch(Exception ex)
			{
				logger.warn("An error occurred while closing sql resources", ex);
			}
		}
	}
}
