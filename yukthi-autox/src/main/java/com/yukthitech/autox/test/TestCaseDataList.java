package com.yukthitech.autox.test;

import java.util.LinkedList;

/**
 * List of test case data.
 * @author akiran
 */
public class TestCaseDataList extends LinkedList<TestCaseData>
{
	private static final long serialVersionUID = 1L;

	/**
	 * Adds specified test case data to the list.
	 * @param data
	 */
	public void addTestCaseData(TestCaseData data)
	{
		super.add(data);
	}
}
