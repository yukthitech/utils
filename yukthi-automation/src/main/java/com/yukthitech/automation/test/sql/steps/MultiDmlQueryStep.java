package com.yukthitech.automation.test.sql.steps;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
 * Step to execute multiple dml queries in single transaction.
 * @author akiran
 */
@Executable(value = "dmlQueriesSingleTransaction", requiredConfigurationTypes = DbConfiguration.class, message = "Executes specified multiple DML queries in single transaction")
public class MultiDmlQueryStep implements IStep
{
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(MultiDmlQueryStep.class);
	
	/**
	 * List of queries executed in single transaction.
	 */
	private List<String> queries = new ArrayList<String>();
	
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
	 * Adds the query to execute.
	 * 
	 * @param queries the new queries.
	 */
	public void addQuery(String query) 
	{
		this.queries.add(query);
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

		String query = null;
		
		try
		{
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			
			statement = connection.createStatement();

			for (int i = 0; i < queries.size(); i++) 
			{
				query = queries.get(i);
				statement.execute(query);
			}
			
			connection.commit();

		} catch(SQLException ex)
		{
			try 
			{
				connection.rollback();
			} 
			catch (SQLException e) 
			{
				throw new InvalidStateException(ex, "An erorr occurred while rolling back");
			}
			
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
