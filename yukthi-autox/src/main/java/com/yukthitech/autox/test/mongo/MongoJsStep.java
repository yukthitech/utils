package com.yukthitech.autox.test.mongo;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;

/**
 * Executes specified mongo script.
 * 
 * @author akiran
 */
@Executable(name = "mongoJs", group = Group.Mongodb, requiredPluginTypes = MongoPlugin.class, message = "Executes specified mongo script. "
		+ "Value of the last statement will be the result value of this script.")
public class MongoJsStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Script to be executed. If the execution ends with a return statement, the same will be considered as output of this script, if not null.
	 */
	@Param(description = "Script to be executed.")
	private String script;

	/**
	 * Mongo Resource to be used for query execution.
	 */
	@Param(description = "Mongo Resource to be used for query execution.")
	private String mongoResourceName;

	/**
	 * Name of the attribute to be used to set the result.
	 */
	@Param(description = "Name of the attribute to be used to set the result. Value of the last statement will be the result value of this script.", 
			required = false, attrName = true, defaultValue = "result")
	private String resultAttribute = "result";
	
	/**
	 * Sets the script to be executed. If the execution ends with a return
	 * statement, the same will be considered as output of this script, if not
	 * null.
	 *
	 * @param script the new script to be executed
	 */
	public void setScript(String script)
	{
		this.script = script;
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
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		Object result = MongoQuryUtils.executeJs(context, exeLogger, mongoResourceName, script);
		
		exeLogger.debug("Using attribute '{}' setting the obtained result. Result: {}", resultAttribute, result);
		
		if(result != null)
		{
			context.setAttribute(resultAttribute, result);
		}
	}
}
