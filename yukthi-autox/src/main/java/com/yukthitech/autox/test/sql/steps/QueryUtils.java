package com.yukthitech.autox.test.sql.steps;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.dbutils.QueryRunner;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.autox.expr.ExpressionFactory;

/**
 * Common query utility methods.
 * @author akiran
 */
public class QueryUtils
{
	/**
	 * Query param pattern.
	 */
	public static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("\\?\\{(.+)\\}");
	
	/**
	 * Query runner for query execution.
	 */
	private static QueryRunner queryRunner = new QueryRunner();
	
	/**
	 * Extract query params which are of pattern defined by {@link #QUERY_PARAM_PATTERN}.
	 *
	 * @param query query to parse
	 * @param context Context to be used to obtain values for expressions
	 * @param paramMap Map that can be useful for logging purpose
	 * @param values Order of values to be passed for query execution.
	 * @return Query after expression replacement
	 */
	public static String extractQueryParams(String query, AutomationContext context, Map<String, Object> paramMap, List<Object> values)
	{
		Matcher matcher = QUERY_PARAM_PATTERN.matcher(query);
		Object value = null;
		
		StringBuffer buffer = new StringBuffer();
		String expression = null;
		
		while(matcher.find())
		{
			expression = matcher.group(1);
			
			if(IAutomationConstants.EXPRESSION_PATTERN.matcher(expression).find() ||
					IAutomationConstants.EXPRESSION_WITH_PARAMS_PATTERN.matcher(expression).find()
					)
			{
				value = ExpressionFactory.getExpressionFactory().parseExpression(context, expression);
			}
			else
			{
				try
				{
					value = PropertyUtils.getProperty(context, expression);
				}catch(Exception ex)
				{
					value = null;
				}
			}
			
			paramMap.put(matcher.group(1), value);
			values.add(value);
			
			matcher.appendReplacement(buffer, "?");
		}
		
		matcher.appendTail(buffer);
		return buffer.toString();
	}
	
	/**
	 * Executes specified dml query on specified connection.
	 * @param connection Connection to be used
	 * @param query Query to execute.
	 * @param params Params to be used
	 * @return number of rows affected
	 */
	public static int executeDml(Connection connection, String query, List<Object> params) throws SQLException
	{
		int count = queryRunner.update(connection, query, params.toArray());
		
		return count;
	}

	/**
	 * Gets the query runner for query execution.
	 *
	 * @return the query runner for query execution
	 */
	public static QueryRunner getQueryRunner()
	{
		return queryRunner;
	}
}
