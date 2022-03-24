package com.yukthitech.autox.test.mongo;

import java.util.Map;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Executes specified mongo Query on specified mongo resource defined in https://docs.mongodb.com/manual/reference/command.
 * 
 * @author akiran
 */
@Executable(name = "mongoQuery", group = Group.Mongodb, requiredPluginTypes = MongoPlugin.class, message = "Executes specified mongo Query on specified mongo resource. "
		+ "Syntax of queries  can be found at https://docs.mongodb.com/manual/reference/command.")
public class MongoQueryStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Query to execute.
	 */
	@Param(description = "Query to execute.")
	private String query;

	/**
	 * Mongo Resource to be used for query execution.
	 */
	@Param(description = "Mongo Resource to be used for query execution.")
	private String mongoResourceName;

	/**
	 * Name of the attribute to be used to set the result.
	 */
	@Param(description = "Name of the attribute to be used to set the result.", required = false, attrName = true, defaultValue = "result")
	private String resultAttribute = "result";
	
	public void setQuery(String query)
	{
		this.query = query;
	}

	public void setMongoResourceName(String mongoResourceName)
	{
		this.mongoResourceName = mongoResourceName;
	}

	public void setResultAttribute(String resultAttribute)
	{
		this.resultAttribute = resultAttribute;
	}

	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		Map<String, Object> result = MongoQuryUtils.execute(context, exeLogger, mongoResourceName, query);
		
		exeLogger.debug("Using attribute '{}' setting the obtained result. Result: {}", resultAttribute, result);
		context.setAttribute(resultAttribute, result);
		
		if(!"1.0".equals("" + result.get("ok")))
		{
			throw new InvalidStateException("Query execution failed as the result does not contain entry: {ok: 1.0}");
		}
	}
}
