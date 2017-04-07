package com.yukthitech.autox.test;

import java.util.LinkedList;
import java.util.List;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Data provider which can be configured to provide range of integers in given range.
 * @author akiran
 */
public class RangeDataProvider extends AbstractDataProvider implements Validateable
{
	/**
	 * Range starting value.
	 */
	private int start;
	
	/**
	 * Range ending value.
	 */
	private int end;

	/**
	 * Sets the range starting value.
	 *
	 * @param start the new range starting value
	 */
	public void setStart(int start)
	{
		this.start = start;
	}

	/**
	 * Sets the range ending value.
	 *
	 * @param end the new range ending value
	 */
	public void setEnd(int end)
	{
		this.end = end;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.automation.test.IDataProvider#getStepData()
	 */
	@Override
	public List<TestCaseData> getStepData()
	{
		List<TestCaseData> rangeValues = new LinkedList<>();
		
		for(int i = start; i <= end; i++)
		{
			rangeValues.add(new TestCaseData("" + i, i));
		}
		
		return rangeValues;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.util.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(start > end)
		{
			throw new ValidateException("Start value is greater than end value. [Start: " + start + ", End: " + end + "]");
		}
	}
}
