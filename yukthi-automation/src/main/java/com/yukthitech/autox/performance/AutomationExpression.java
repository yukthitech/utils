package com.yukthitech.autox.performance;

import java.util.Date;
import java.util.Random;

import com.yukthitech.utils.expr.FunctionInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class AutomationExpression.
 */
public class AutomationExpression
{

	/**
	 * Today.
	 *
	 * @return the date
	 */
	@FunctionInfo(name = "TODAY", syntax = "", description = "Displays todays date")

	public static Date today()
	{
		return new Date();
	}

	/**
	 * Random int.
	 *
	 * @return the integer
	 */
	@FunctionInfo(name = "RANDOM_INT", description = "Displays todays randomInt", syntax = "")
	public static Integer randomInt()
	{
		Random random = new Random();

		return random.nextInt();
	}

	/**
	 * Adds the totoday.
	 *
	 * @param n the n
	 * @return the date
	 */
	@FunctionInfo(name = "ADD_TO_TODAY", syntax = "ADD_TO_TODAY(num1, num2...)", description = "Displays date by increamenting the value from today ")
	public static Date addTotoday(int n)
	{
		Date date = new Date();
		return date;
	}

	/**
	 * Sum.
	 *
	 * @param firstVal the first val
	 * @param values the values
	 * @return the number
	 */
	@FunctionInfo(name = "SUM", syntax = "SUM(num1, num2, num3...)", description = "Returns sum of the provided values.")
	public static Number sum(Number firstVal, Number... values)
	{
		double sum = firstVal.doubleValue();

		return sum;
	}

	/**
	 * Adds the to date.
	 *
	 * @return the date
	 */
	@FunctionInfo(name = "ADD_TO_DATE", description = "add date ", syntax = "")
	public static Date addToDate()
	{
		return null;
	}

	/**
	 * Random date.
	 *
	 * @return the date
	 */
	@FunctionInfo(name = "RANDOM_DATE", description = "randomDate", syntax = "")
	public static Date randomDate()
	{
		return null;
	}
}
