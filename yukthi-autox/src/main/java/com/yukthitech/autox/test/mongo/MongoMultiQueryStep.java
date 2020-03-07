package com.yukthitech.autox.test.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;

/**
 * Executes specified mongo Query on specified mongo resource defined in https://docs.mongodb.com/manual/reference/command.
 * 
 * @author akiran
 */
@Executable(name = "mongoMultiQuery", group = Group.Mongodb, requiredPluginTypes = MongoPlugin.class, message = "Executes specified multiple mongo Query on specified mongo resource. "
		+ "Syntax of queries  can be found at https://docs.mongodb.com/manual/reference/command.")
public class MongoMultiQueryStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Query to execute.
	 */
	@Param(description = "Query(ies) to execute.")
	private List<String> queries = new ArrayList<>();

	/**
	 * Mongo Resource to be used for query execution.
	 */
	@Param(description = "Mongo Resource to be used for query execution.")
	private String mongoResourceName;

	public void addQuery(String query)
	{
		this.queries.add(query);
	}

	public void setMongoResourceName(String mongoResourceName)
	{
		this.mongoResourceName = mongoResourceName;
	}

	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		for(String query : this.queries)
		{
			exeLogger.debug("Executing query: {}", query);
			Map<String, Object> result = MongoQuryUtils.execute(context, exeLogger, mongoResourceName, query);
		
			exeLogger.debug("Got result of query as: {}", result);
		}
		
		return true;
	}
}
