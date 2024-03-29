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
package com.yukthitech.autox.test.sql.steps;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.autox.test.sql.DbPlugin;
import com.yukthitech.autox.test.sql.DbPluginSession;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Executes specified query and creates map out of results. And sets this map on
 * the context.
 */
@Executable(name = "sqlLoadQueryRowMap", group = Group.Rdbms, requiredPluginTypes = DbPlugin.class, message = "Executes specified query and loads the results as map(s) on context. "
		+ "\nIn case of zero results empty map will be kept on context. \nPer row new map will be created.")
public class LoadQueryRowMapStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Query to execute.
	 */
	@Param(description = "Query to execute, the results will be used to create map.")
	private String query;
	
	/**
	 * Flag to indicate if all rows should be processed into map. If true, list of maps will be loaded on context otherwise first row map will be loaded
	 * on context.
	 */
	@Param(description = "If false, only first row will be processed into map. If true, per row new map will be created and loads of this maps into context.\nDefault: true", required = false)
	private boolean processAllRows = true;

	/**
	 * Context attribute to be used to load the map.
	 */
	@Param(description = "Name of the attribute which should be used to keep the result map on the context.", attrName = true)
	private String contextAttribute;

	/**
	 * Name of the data source to use.
	 */
	@Param(description = "Name of the data source to be used for query execution.")
	private String dataSourceName;
	
	/**
	 * Transformations to be applied on columns.
	 */
	@SkipParsing
	@Param(description = "Column transformation expressions. This can be used to transform column values using specified expressions before loading result on context.<br>"
			+ "In this expression current column/row details can be accessed using attribute specified by 'transformAttrName'.", required = false)
	private Map<String, String> columnTransformations = new HashMap<String, String>();
	
	/**
	 * Name of the attribute which should be used to set transform details on context while evaluating transform expression.
	 */
	@Param(description = "Name of the attribute which should be used to set trasnform details on context while evaluating transform expression. Defaults to: result", attrName = true)
	private String transformAttrName = "result";

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
	 * Adds the column transformation.
	 *
	 * @param column
	 *            the column
	 * @param expr
	 *            the expr
	 */
	public void addColumnTransformation(String column, String expr)
	{
		if(this.columnTransformations == null)
		{
			this.columnTransformations = new HashMap<String, String>();
		}
		
		this.columnTransformations.put(column, expr);
	}
	
	/**
	 * Sets the name of the attribute which should be used to set transform
	 * details on context while evaluating transform expression.
	 *
	 * @param transformAttrName
	 *            the new name of the attribute which should be used to set
	 *            transform details on context while evaluating transform
	 *            expression
	 */
	public void setTransformAttrName(String transformAttrName)
	{
		this.transformAttrName = transformAttrName;
	}
	
	private void doTransform(AutomationContext context, Map<String, Object> row)
	{
		for(Map.Entry<String, String> colTrans : columnTransformations.entrySet())
		{
			Object colVal = row.get(colTrans.getKey());
			TransformDetails transDet = new TransformDetails(colTrans.getKey(), colVal, row);
			
			colVal = QueryUtils.transform(context, colTrans.getValue(), transformAttrName, transDet);
			row.put(colTrans.getKey(), colVal);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ui.automation.IValidation#execute(com.yukthitech.ui.automation.
	 * AutomationContext, com.yukthitech.ui.automation.IExecutionLogger)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		DbPluginSession dbSession = ExecutionContextManager.getInstance().getPluginSession(DbPlugin.class);
		DataSource dataSource = dbSession.getDataSource(dataSourceName);

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
			
			exeLogger.debug(false, "On data-source '{}' executing query: \n<code class='SQL'>{}</code> \nParams: {}", dataSourceName, query, paramMap);
			
			exeLogger.trace(false, "On data-source '{}' executing processed query: \n<code class='SQL'>{}</code> \nParams: {}", dataSourceName, processedQuery, values);

			Object result = null;
			Object valueArr[] = values.isEmpty() ? null : values.toArray();
			
			if(processAllRows)
			{
				exeLogger.debug("Loading muliple row maps on context attribute: {}", contextAttribute);
				result = QueryUtils.getQueryRunner().query(connection, processedQuery, new MapListHandler(), valueArr);
				
				if(result == null)
				{
					result = new ArrayList<>();
				}
				else if(MapUtils.isNotEmpty(columnTransformations))
				{
					List<Map<String, Object>> rowLst = (List<Map<String,Object>>) result;
					
					for(Map<String, Object> row : rowLst)
					{
						doTransform(context, row);
					}
				}
			}
			else
			{
				exeLogger.debug("Loading first-row as map on context attribute: {}", contextAttribute);
				result = QueryUtils.getQueryRunner().query(connection, processedQuery, new MapHandler(), valueArr);
				
				if(result == null)
				{
					result = new HashMap<>();
				}
				else if(MapUtils.isNotEmpty(columnTransformations))
				{
					doTransform(context, (Map<String, Object>) result);
				}
			}
			
			context.setAttribute(contextAttribute, result);
			exeLogger.debug("Data loaded on context with name {}. Data: {}", contextAttribute, result);
		} catch(SQLException ex)
		{
			//exeLogger.error(ex, "An error occurred while executing query: {}", query);
			
			throw new TestCaseFailedException(this, "An erorr occurred while executing query: {}", query, ex);
		} finally
		{
			DbUtils.closeQuietly(connection);
		}
	}
}
