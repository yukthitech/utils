package com.yukthitech.autox.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.filter.ExpressionConfig;
import com.yukthitech.autox.filter.ExpressionFactory;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Data provider that can be configured to provide simple static data list.
 * @author akiran
 */
public class DefaultDataProvider extends AbstractDataProvider implements Validateable
{
	/**
	 * List of objects to be passed for test case.
	 */
	private List<Object> dataLst = new ArrayList<>();
	
	/**
	 * Sets the step data list to this data provider.
	 * @param data data to be set
	 */
	public void addStepDataList(Object data)
	{
		this.dataLst.add(data);
	}
	
	@SuppressWarnings("rawtypes")
	private void processDataList(Object data, List<TestCaseData> testCaseDataLst)
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
				testCaseDataLst.add( (TestCaseData) obj);
			}
			else
			{
				testCaseDataLst.add(new TestCaseData("" + obj, obj));
			}
		}
	}
	
	@Override
	public List<TestCaseData> getStepData()
	{
		List<TestCaseData> testCaseData = new LinkedList<TestCaseData>();
		
		for(Object data : this.dataLst)
		{
			processDataList(data, testCaseData);
		}
		
		return testCaseData;
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
