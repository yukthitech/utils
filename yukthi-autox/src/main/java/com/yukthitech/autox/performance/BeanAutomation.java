package com.yukthitech.autox.performance;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.yukthitech.ccg.xml.DynamicBean;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * The Class BeanAutomation.
 */
public class BeanAutomation
{

	/**
	 * Generate persist.
	 *
	 * @param details
	 *            the details
	 */
	public void generatePersist(BeanDetails details)
	{
		BeanAutomationContext context = new BeanAutomationContext();
		Object createBean = null;

		for(int i = 0; i <= details.getCount(); i++)
		{
			context.setIndex(i);

			createBean = createBean(context, details.getData(), details.getBeanType());

			details.getBeanPersister().persist(createBean);
		}
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(String args[]) throws Exception
	{
		BeanAutomation beanAutomation = new BeanAutomation();
		System.out.println(args[0]);
		File beanAutoData = new File(args[0]);
		BeanAutomationData beanData = new BeanAutomationData();
		FileInputStream fileInputStream = new FileInputStream(beanAutoData);
		XMLBeanParser.parse(fileInputStream, beanData);
		fileInputStream.close();
		List<BeanDetails> beanDetails = beanData.getBeanDetails();
		// loop through list
		for(BeanDetails b : beanDetails)
		{
			beanAutomation.generatePersist(b);
		}
	}

	/**
	 * Creates the bean.
	 *
	 * @param context
	 *            the context
	 * @param data
	 *            the data
	 * @param beanType
	 *            the bean type
	 * @return the object
	 */
	public Object createBean(BeanAutomationContext context, DynamicBean data, String beanType)
	{
		Object object = null;
		Class<?> cls = null;

		try
		{
			cls = Class.forName(beanType);
			object = cls.newInstance();
		} catch(Exception ex)
		{
			ex.printStackTrace();
		}

		BeanAutomationData beanautoData = new BeanAutomationData();

		try
		{
			Object propValue = null;

			for(String name : data.getProperties().keySet())
			{
				propValue = beanautoData.replaceExpressions(context, "" + data.get(name));
				BeanUtils.setProperty(object, name, propValue);
			}
		} catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while creating bean - {}", beanType);
		}

		return (object);
	}
}
