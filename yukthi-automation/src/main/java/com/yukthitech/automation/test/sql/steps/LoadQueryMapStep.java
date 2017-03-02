package com.yukthitech.automation.test.sql.steps;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

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
 * Executes specified query and creates map out of results. And sets this map on
 * the context.
 */
@Executable(name = "loadQueryMap", requiredConfigurationTypes = DbConfiguration.class, message = "Executes specified query and loads the results as map on context")
public class LoadQueryMapStep implements IStep
{
	private static Logger logger = LogManager.getLogger(LoadQueryMapStep.class);

	/**
	 * Query to execute.
	 */
	private String query;

	/**
	 * Key column to be used to load the map.
	 */
	private String keyColumn;

	/**
	 * Value column to be used to load the map.
	 */
	private String valueColumn;

	/**
	 * Context attribute to be used to load the map.
	 */
	private String contextAttribute;

	/**
	 * Name of the data source to use.
	 */
	private String dataSourceName;

	/**
	 * Sets the query to execute.
	 *
	 * @param query
	 *            the new query to execute
	 */
	public void setQuery(String query)
	{
		this.query = query;
	}

	/**
	 * Sets the key column to be used to load the map.
	 *
	 * @param keyColumn the new key column to be used to load the map
	 */
	public void setKeyColumn(String keyColumn)
	{
		this.keyColumn = keyColumn;
	}

	/**
	 * Sets the value column to be used to load the map.
	 *
	 * @param valueColumn the new value column to be used to load the map
	 */
	public void setValueColumn(String valueColumn)
	{
		this.valueColumn = valueColumn;
	}

	/**
	 * Sets the context attribute to be used to load the map.
	 *
	 * @param contextAttribute the new context attribute to be used to load the map
	 */
	public void setContextAttribute(String contextAttribute)
	{
		this.contextAttribute = contextAttribute;
	}

	/**
	 * Sets the name of the data source to use.
	 *
	 * @param dataSourceName the new name of the data source to use
	 */
	public void setDataSourceName(String dataSourceName)
	{
		this.dataSourceName = dataSourceName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ui.automation.IValidation#execute(com.yukthitech.ui.automation.
	 * AutomationContext, com.yukthitech.ui.automation.IExecutionLogger)
	 */
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

			rs = statement.executeQuery(query);

			Map<Object, Object> resMap = new HashMap<>();

			while(rs.next())
			{
				resMap.put(rs.getObject(keyColumn), rs.getObject(valueColumn));
			}
			
			context.setAttribute(contextAttribute, resMap);
		} catch(SQLException ex)
		{
			throw new InvalidStateException(ex, "An erorr occurred while executing sql validation with query - {}", query);
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
