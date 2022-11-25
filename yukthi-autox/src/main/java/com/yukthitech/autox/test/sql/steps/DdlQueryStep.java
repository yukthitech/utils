package com.yukthitech.autox.test.sql.steps;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.context.ExecutionContextManager;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.autox.test.TestCaseFailedException;
import com.yukthitech.autox.test.sql.DbPlugin;
import com.yukthitech.autox.test.sql.DbPluginSession;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Step to execute DDL query.
 */
@Executable(name = "sqlDdlQuery", group = Group.Rdbms, requiredPluginTypes = DbPlugin.class, message = "Executes specified DDL query on specified data source.")
public class DdlQueryStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Ddl query to execute.
	 */
	@Param(description = "DDL query to execute")
	private String query;
	
	/**
	 * Name of the data source to use.
	 */
	@Param(description = "Data source to be used for sql execution.")
	private String dataSourceName;
	
	@Param(description = "If set to true, exceptions during query execution will be ignored. Helpful to rest the db without assuming initial state.")
	private boolean ignoreErrors;
	
	/**
	 * Sets the ddl query to execute.
	 *
	 * @param query the new ddl query to execute
	 */
	public void setQuery(String query)
	{
		this.query = query;
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
	
	public void setIgnoreErrors(boolean ignoreErrors)
	{
		this.ignoreErrors = ignoreErrors;
	}

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
		Statement statement = null;

		try
		{
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			
			exeLogger.debug(false, "On data-source '{}' executing DDL query: \n <code class='SQL'>{}</code>", dataSourceName, query);
			
			statement.execute(query);
		} catch(SQLException ex)
		{
			//exeLogger.error(ex, "An error occurred while executing DDL query");
			
			if(ignoreErrors)
			{
				exeLogger.info("IGNORED Error: An error occurred while executing DDL query. Error: " + ex);
			}
			else
			{
				throw new TestCaseFailedException(this, "An erorr occurred while executing DDL query - {}", query, ex);
			}
		} finally
		{
			DbUtils.closeQuietly(connection, statement, null);
		}
	}
}
