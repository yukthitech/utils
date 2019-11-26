package com.yukthitech.autox.test;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.expr.ExpressionConfig;
import com.yukthitech.autox.expr.ExpressionFactory;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Data provider that can be configured to provide simple static data list.
 * @author akiran
 */
public class ListDataProvider extends AbstractDataProvider implements Validateable
{
	/**
	 * List of objects to be passed for test case.
	 */
	private List<TestCaseData> dataLst = new ArrayList<>();
	
	/**
	 * Adds new data object for this data provider.
	 * @param data data to add
	 */
	public void addData(Object data)
	{
		this.dataLst.add(new TestCaseData("" + data, data));
	}
	
	/**
	 * Adds new data object for this data provider.
	 * @param data data to add
	 */
	public void addTestCaseData(TestCaseData data)
	{
		this.dataLst.add(data);
	}
	
	/**
	 * Sets the step data list to this data provider.
	 * @param data data to be set
	 */
	@SuppressWarnings("rawtypes")
	public void setStepDataList(Object data)
	{
		ExpressionFactory expressionFactory = ExpressionFactory.getExpressionFactory();
		AutomationContext automationContext = AutomationContext.getInstance();
		boolean loggerSet = false;
		
		if(automationContext.getExecutionLogger() == null)
		{
			automationContext.setExecutionLogger(new ExecutionLogger(automationContext, "<data-provider-load>", "Data provider load"));
			loggerSet = true;
		}
		
		Object result = expressionFactory.parseExpression(AutomationContext.getInstance(), data, new ExpressionConfig(null, TestCaseDataList.class));
		
		if(loggerSet)
		{
			automationContext.setExecutionLogger(null);
		}
		
		if(!(result instanceof List))
		{
			throw new InvalidArgumentException("Non list object was specified as data list - {}", result.getClass().getName());
		}
		
		for(Object obj : ((List) result) )
		{
			if(obj instanceof TestCaseData)
			{
				addTestCaseData( (TestCaseData) obj);
			}
			else
			{
				addData(obj);
			}
		}
	}
	
	@Override
	public List<TestCaseData> getStepData()
	{
		return dataLst;
	}

	@Override
	public void validate() throws ValidateException
	{
		if(dataLst.isEmpty())
		{
			throw new ValidateException("No data specified for list data provider.");
		}
	}
}
