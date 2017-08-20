package com.yukthitech.autox.performance;

import com.yukthitech.ccg.xml.DynamicBean;

/**
 * The Class BeanDetails.
 */
public class BeanDetails
{

	/**
	 * The data.
	 */
	private DynamicBean data;

	/**
	 * The bean persister.
	 */
	private IBeanPersister beanPersister;

	/**
	 * The count.
	 */
	private int count;

	/**
	 * The bean type.
	 */
	private String beanType;

	/**
	 * The controller type.
	 */
	private String controllerType;

	/**
	 * Gets the controller type.
	 *
	 * @return the controller type
	 */
	public String getControllerType()
	{
		return controllerType;
	}

	/**
	 * Sets the controller type.
	 *
	 * @param controllerType
	 *            the new controller type
	 */
	public void setControllerType(String controllerType)
	{
		this.controllerType = controllerType;
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public DynamicBean getData()
	{
		return data;
	}

	/**
	 * Sets the data.
	 *
	 * @param data
	 *            the new data
	 */
	public void setData(DynamicBean data)
	{
		this.data = data;
	}

	/**
	 * Gets the bean persister.
	 *
	 * @return the bean persister
	 */
	public IBeanPersister getBeanPersister()
	{
		return beanPersister;
	}

	/**
	 * Sets the bean persister.
	 *
	 * @param beanPersister
	 *            the new bean persister
	 */
	public void setBeanPersister(IBeanPersister beanPersister)
	{
		this.beanPersister = beanPersister;
	}

	/**
	 * Gets the count.
	 *
	 * @return the count
	 */
	public int getCount()
	{
		return count;
	}

	/**
	 * Sets the count.
	 *
	 * @param count
	 *            the new count
	 */
	public void setCount(int count)
	{
		this.count = count;
	}

	/**
	 * Gets the bean type.
	 *
	 * @return the bean type
	 */
	public String getBeanType()
	{
		return beanType;
	}

	/**
	 * Sets the bean type.
	 *
	 * @param beanType
	 *            the new bean type
	 */
	public void setBeanType(String beanType)
	{
		this.beanType = beanType;
	}
}
