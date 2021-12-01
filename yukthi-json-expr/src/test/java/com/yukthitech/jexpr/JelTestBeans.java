package com.yukthitech.jexpr;

import java.util.ArrayList;
import java.util.List;

/**
 * List of test beans.
 * @author akiran
 */
public class JelTestBeans
{
	private List<JelTestBean> testBeans;
	
	public void addJelTestBean(JelTestBean bean)
	{
		if(testBeans == null)
		{
			this.testBeans = new ArrayList<JelTestBean>();
		}
		
		this.testBeans.add(bean);
	}
	
	public void setTestBeans(List<JelTestBean> testBeans)
	{
		this.testBeans = testBeans;
	}
	
	public List<JelTestBean> getTestBeans()
	{
		return testBeans;
	}
}
