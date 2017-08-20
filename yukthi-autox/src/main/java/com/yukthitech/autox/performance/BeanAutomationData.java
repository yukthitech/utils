package com.yukthitech.autox.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.utils.expr.Expression;
import com.yukthitech.utils.expr.ExpressionEvaluator;
import com.yukthitech.utils.expr.ExpressionRegistry;
import com.yukthitech.utils.expr.IVariableValueProvider;
import com.yukthitech.utils.expr.RegistryFactory;

/**
 * The Class BeanAutomation.
 */
public class BeanAutomationData
{

	/**
	 * The logger.
	 */
	private static Logger logger = LogManager.getLogger(BeanAutomationData.class);
	/**
	 * The Constant EXPRESSION_PATTERN.
	 */
	private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\#\\{([\\w\\.\\(\\)\\=]+)\\}");

	/**
	 * The bean details.
	 */
	private List<BeanDetails> beanDetails = new ArrayList<BeanDetails>();

	/**
	 * Adds the state.
	 *
	 * @param bean
	 *            the bean
	 */
	public void addBeanDetails(BeanDetails bean)
	{
		System.out.println("ADD bean details is invoked");
		beanDetails.add(bean);
	}

	/**
	 * Gets the bean details.
	 *
	 * @return the bean details
	 */
	public List<BeanDetails> getBeanDetails()
	{
		return beanDetails;
	}

	/**
	 * Sets the bean details.
	 *
	 * @param beanDetails
	 *            the new bean details
	 */
	public void setBeanDetails(List<BeanDetails> beanDetails)
	{
		System.out.println("setting iss called");
		this.beanDetails = beanDetails;
	}

	/**
	 * Replace expressions.
	 *
	 * @param bean
	 *            the bean
	 * @param expressionString
	 *            the expression string
	 * @return the object
	 */
	public Object replaceExpressions(Object bean, String expressionString)
	{
		Matcher matcher = EXPRESSION_PATTERN.matcher(expressionString);
		String key = null;
		ExpressionEvaluator ev = new ExpressionEvaluator();
		Expression expression = null;

		ExpressionRegistry registry = new ExpressionRegistry();
		RegistryFactory.registerDefaults(registry);

		IVariableValueProvider valueProvider = new IVariableValueProvider()
		{
			@Override
			public Object getVariableValue(String name)
			{
				return null;
			}
		};

		if(matcher.matches())
		{
			key = matcher.group(1);

			try
			{

				expression = ev.parse(expressionString);

				return expression.evaluate(valueProvider, registry);
			} catch(Exception ex)
			{
				// in case of error log a warning and ignore
				logger.warn("An error occurred while parsing full expression: " + key, ex);
				return null;
			}
		}

		StringBuffer result = new StringBuffer();
		Object value = null;

		// loop through the expressions
		while(matcher.find())
		{
			key = matcher.group(1);

			try
			{
				value = PropertyUtils.getProperty(bean, key);
			} catch(Exception ex)
			{
				// in case of error log a warning and ignore
				logger.warn("An error occurred while parsing expression: " + key, ex);
				value = null;
			}

			// if value is null, make it into empty string to avoid exceptions
			if(value == null)
			{
				value = "";
			}

			// replace expression with property value
			matcher.appendReplacement(result, Matcher.quoteReplacement(value.toString()));
		}

		matcher.appendTail(result);

		return result.toString();
	}
}
