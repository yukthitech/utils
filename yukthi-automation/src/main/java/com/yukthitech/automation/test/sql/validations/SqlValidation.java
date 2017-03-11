package com.yukthitech.automation.test.sql.validations;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;

import com.yukthitech.automation.AbstractValidation;
import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.IValidation;
import com.yukthitech.automation.Param;
import com.yukthitech.automation.common.AutomationUtils;
import com.yukthitech.automation.config.DbPlugin;
import com.yukthitech.automation.test.TestCaseFailedException;
import com.yukthitech.automation.test.sql.steps.QueryUtils;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * SQL based validation.
 */
@Executable(name = "validateWithSql", requiredPluginTypes = DbPlugin.class, message = "Executes specified query and validates expected data is returned")
public class SqlValidation extends AbstractValidation
{
	private static final long serialVersionUID = 1L;

	/**
	 * Result row expectation.
	 * 
	 * @author akiran
	 */
	public static class ExpectedRow implements Serializable
	{
		private static final long serialVersionUID = 1L;

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
	@Param(description = "Name of the data source to be used for query execution.")
	private String dataSourceName;

	/**
	 * Query to execute.
	 */
	@Param(description = "Query to execute whose results needs to be validated.")
	private String query;

	/**
	 * Rows and expected values.
	 */
	@Param(description = "Expected rows of values from query result. Each row will have list of column (name-value pairs)")
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
	public boolean validate(AutomationContext context, ExecutionLogger exeLogger)
	{
		if(!"true".equals(enabled))
		{
			exeLogger.debug("Current validation is disabled. Skipping validation execution.");
			return true;
		}
		
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
			
			exeLogger.debug("On data-source '{}' executing query: \n<code class='SQL'>{}</code>\nParams: {}", dataSourceName, query, paramMap);
			
			exeLogger.trace("On data-source '{}' executing processed query: \n<code class='SQL'>{}</code>\nParams: {}", dataSourceName, processedQuery, values);

			ResultSetHandler<Boolean> rsHandler = new ResultSetHandler<Boolean>()
			{
				@Override
				public Boolean handle(ResultSet rs) throws SQLException
				{
					int rowIdx = 0;
					ExpectedRow row = null;
					String actualVal = null, expectedVal = null;

					while(rs.next())
					{
						if(expectedRows.size() <= rowIdx)
						{
							exeLogger.error("Actual rows are more than expected row count: {}", expectedRows.size());
							return false;
						}

						row = expectedRows.get(rowIdx);

						for(String column : row.columnToValue.keySet())
						{
							actualVal = rs.getString(column);
							expectedVal = row.columnToValue.get(column);

							if(!expectedVal.equals(actualVal))
							{
								exeLogger.error("At row {} for column {} expected value '{}' is not matching with actual value: {}", rowIdx, column, expectedVal, actualVal);
								return false;
							}
						}
						
						rowIdx ++;
					}

					return true;
				}
			};
			
			Boolean res = QueryUtils.getQueryRunner().query(connection, processedQuery, rsHandler);
			return res;
		} catch(SQLException ex)
		{
			exeLogger.error(ex, "An error occurred while executing sql validation with query - {}", query);
			throw new TestCaseFailedException("An erorr occurred while executing sql validation with query - {}", query, ex);
		} finally
		{
			DbUtils.closeQuietly(connection);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[");

		builder.append("Query: ").append(query);

		builder.append("]");
		return builder.toString();
	}

	@Override
	public IValidation clone()
	{
		return AutomationUtils.deepClone(this);
	}
}
