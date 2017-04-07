package com.yukthitech.autox.performance;

/**
 * The Class BeanPersist.
 */
public class DummyBeanPersist implements IBeanPersister
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.automation.performance.IBeanPersister#beanPersiter(com.yukthitech.
	 * automation.performance.BeanDetails)
	 */
	@Override
	public void persist(Object bean)
	{
		System.out.println(bean + "from dummyPerisister");
	}
}
