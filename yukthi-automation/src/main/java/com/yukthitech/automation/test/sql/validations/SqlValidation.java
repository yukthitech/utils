package com.yukthitech.automation.test.sql.validations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.automation.AbstractValidation;
import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.IExecutionLogger;
import com.yukthitech.automation.config.DbConfiguration;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * SQL based validation.
 */
@Executable(value = "validateWithSql", requiredConfigurationTypes = DbConfiguration.class, message = "Executes specified query and validates expected data is returned")
public class SqlValidation extends AbstractValidation
{
	private static Logger logger = LogManager.getLogger(SqlValidation.class);

	/**
	 * Result row expectation.
	 * 
	 * @author akiran
	 */
	public static class ExpectedRow
	{
		/**
		 * Expected column to value mapping.
		 */
		private Map<String, String> columnToValue = new HashMap<>();

		/**
		 * Adds the column and value expectation.
		 * 
		 * @param name
		 *            Name of column.
		 * @param value
		 *            Expected value.
		 */
		public void addColumn(String name, String value)
		{
			if(name == null || name.trim().length() == 0)
			{
				throw new NullPointerException("Name can not be null or empty");
			}

			if(value == null || value.trim().length() == 0)
			{
				throw new NullPointerException("Value can not be null or empty");
			}

			columnToValue.put(name, value);
		}
	}
	
	/**
	 * Name of the data source to use.
	 */
	private String dataSourceName;

	/**
	 * Query to execute.
	 */
	private String query;

	/**
	 * Rows and expected values.
	 */
	private List<ExpectedRow> expectedRows = new ArrayList<>();
	
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
	 * Adds row expectation.
	 * 
	 * @param row
	 *            row expectation
	 */
	public void addExpectedRow(ExpectedRow row)
	{
		expectedRows.add(row);
	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ui.automation.IValidation#execute(com.yukthitech.ui.automation.
	 * AutomationContext, com.yukthitech.ui.automation.IExecutionLogger)
	 */
	@Override
	public boolean validate(AutomationContext context, IExecutionLogger exeLogger)
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

			int rowIdx = 0;
			ExpectedRow row = null;
			String actualVal = null, expectedVal = null;

			while(rs.next())
			{
				if(expectedRows.size() <= rowIdx)
				{
					exeLogger.error("Actual rows are more than expected row count- {}", expectedRows.size());
					return false;
				}

				row = expectedRows.get(rowIdx);

				for(String column : row.columnToValue.keySet())
				{
					actualVal = rs.getString(column);
					expectedVal = row.columnToValue.get(column);

					if(!expectedVal.equals(actualVal))
					{
						exeLogger.error("At row {} for column {} expected value '{}' is not matching with actual value - {}", rowIdx, column, expectedVal, actualVal);
						return false;
					}
				}
			}

			return true;
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
