package com.yukthitech.autox.test;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

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
	 * Adds the map bean.
	 *
	 * @param mapData the map bean
	 */
	public void addMapData(MapData mapData)
	{
		this.dataLst.add(new TestCaseData(mapData.getName(), mapData));
	}
	
	/**
	 * Adds new data object for this data provider.
	 * @param data data to add
	 */
	public void addTestCaseData(TestCaseData data)
	{
		this.dataLst.add(data);
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
