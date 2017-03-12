package com.yukthitech.automation.test.sql.steps;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.yukthitech.automation.AbstractStep;
import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Executable;
import com.yukthitech.automation.ExecutionLogger;
import com.yukthitech.automation.Param;
import com.yukthitech.automation.config.DbPlugin;
import com.yukthitech.automation.test.TestCaseFailedException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Executes specified query and creates map out of results. And sets this map on
 * the context.
 */
@Executable(name = "loadQueryMap", requiredPluginTypes = DbPlugin.class, message = "Executes specified query and loads the results as bean(s) on context. "
		+ "\nIn case of zero results empty map will be kept on context. \nPer row new bean will be created.")
public class LoadQueryRowBeanStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Query to execute.
	 */
	@Param(description = "Query to execute, the results will be used to create bean.")
	private String query;
	
	/**
	 * Flag to indicate if all rows should be processed into bean. If true, list of beans will be loaded on context otherwise first row map will be loaded
	 * on context.
	 */
	@Param(description = "If false, only first row will be processed into bean. If true, per row new map will be created and loads of this beans into context.\nDefault: true", required = false)
	private boolean processAllRows = true;

	/**
	 * Context attribute to be used to load the map.
	 */
	@Param(description = "Name of the attribute which should be used to keep the result bean on the context.")
	private String contextAttribute;

	/**
	 * Name of the data source to use.
	 */
	@Param(description = "Name of the data source to be used for query execution.")
	private String dataSourceName;
	
	/**
	 * Type of beans to which rows should be converted.
	 */
	@Param(description = "Type of bean to which rows should be converted.")
	private Class<?> beanType;

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
	 * Sets the flag to indicate if all rows should be processed into map. If true, list of maps will be loaded on context otherwise first row map will be loaded on context.
	 *
	 * @param processAllRows the new flag to indicate if all rows should be processed into map
	 */
	public void setProcessAllRows(boolean processAllRows)
	{
		this.processAllRows = processAllRows;
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
	
	/**
	 * Sets the type of beans to which rows should be converted.
	 *
	 * @param beanType the new type of beans to which rows should be converted
	 */
	public void setBeanType(Class<?> beanType)
	{
		this.beanType = beanType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ui.automation.IValidation#execute(com.yukthitech.ui.automation.
	 * AutomationContext, com.yukthitech.ui.automation.IExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		DbPlugin dbConfiguration = context.getPlugin(DbPlugin.class);
		DataSource dataSource = dbConfiguration.getDataSource(dataSourceName);

		if(dataSource == null)
		{
			throw new InvalidStateException("No data source found with specified name: {}", dataSourceName);
		}

		Connection connection = null;

		try
		{
			connection = dataSource.getConnection();

			Map<String, Object> paramMap = new HashMap<>();
			List<Object> values = new ArrayList<>();

			String processedQuery = QueryUtils.extractQueryParams(query, context, paramMap, values);
			
			exeLogger.debug("On data-source '{}' executing query: \n<code class='SQL'>{}</code> \n\nParams: {}", dataSourceName, query, paramMap);
			
			exeLogger.trace("On data-source '{}' executing processed query: \n<code class='SQL'>{}</code> \nParams: {}", dataSourceName, processedQuery, values);

			Object result = null;
			
			if(processAllRows)
			{
				exeLogger.debug("Loading muliple row beans on context attribute: {}", contextAttribute);
				result = QueryUtils.getQueryRunner().query(processedQuery, new BeanHandler<>(beanType), values);
				
				if(result == null)
				{
					result = new ArrayList<>();
				}
			}
			else
			{
				exeLogger.debug("Loading first-row as bean on context attribute: {}", contextAttribute);
				result = QueryUtils.getQueryRunner().query(processedQuery, new BeanListHandler<>(beanType), values);
				
				if(result == null)
				{
					result = new HashMap<>();
				}
			}
			
			context.setAttribute(contextAttribute, result);
		} catch(SQLException ex)
		{
			exeLogger.error(ex, "An error occurred while executing query: {}", query);
			
			throw new TestCaseFailedException("An erorr occurred while executing query: {}", query, ex);
		} finally
		{
			DbUtils.closeQuietly(connection);
		}
	}
}
