package com.yukthitech.transform.template;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.jxpath.JXPathContext;

import com.yukthitech.transform.ExpressionUtil;
import com.yukthitech.transform.ITransformConstants;
import com.yukthitech.transform.ITransformContext;
import com.yukthitech.transform.TransformException;
import com.yukthitech.transform.TransformState;
import com.yukthitech.transform.event.ITransformListener;
import com.yukthitech.transform.event.TransformEvent;
import com.yukthitech.transform.event.TransformEventType;
import com.yukthitech.transform.template.TransformTemplate.Expression;
import com.yukthitech.transform.template.TransformTemplate.ExpressionType;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

public class TransformUtils
{
	/**
	 * Expression used by value string which has to be replaced with resultant value.
	 */
	public static final Pattern EXPR_PATTERN = Pattern.compile("^\\@([\\w\\-]+)\\s*\\:\\s*(.*)$");

    public static Expression parseExpression(String expression, Location location, boolean nullByDefault)
    {
        Matcher matcher = EXPR_PATTERN.matcher(expression);
        
        if(!matcher.matches())
        {
        	if(expression.contains("${") || expression.contains("<#"))
        	{
        		return new Expression(location, ExpressionType.TEMPLATE, expression);
        	}
        	
            return nullByDefault ? null : new Expression(location, ExpressionType.STRING, expression);
        }

        String exprType = matcher.group(1);
        String expr = matcher.group(2);

        if(ITransformConstants.EXPR_TYPE_FMARKER.equals(exprType))
        {
            return new Expression(location, ExpressionType.FMARKER, expr);
        }
        else if(ITransformConstants.EXPR_TYPE_XPATH.equals(exprType))
        {
            return new Expression(location, ExpressionType.XPATH, expr);
        }
        else if(ITransformConstants.EXPR_TYPE_XPATH_MULTI.equals(exprType))
        {
            return new Expression(location, ExpressionType.XPATH_MULTI, expr);
        }

        throw new TemplateParseException(location, "Invalid expression type specified '{}' in expression: {}", exprType, expression);
    }

	/**
	 * If input string contains expression syntax the same will be processed and result will be returned. If not string will
	 * be evaluated as simple free marker template.
	 * @param str string to evaluate
	 * @param context context to be used
	 * @param path path where this string is found
	 * @return processed value
	 */
	public static Object processExpression(FreeMarkerEngine freeMarkerEngine, Expression expression, 
		ITransformContext context, TransformState transformState, ITransformListener listener)
	{
		ExpressionType exprType = expression.getType();
		String expr = expression.getExpression();
		
		try
		{
			if(exprType == ExpressionType.FMARKER)
			{
				Object res = ExpressionUtil.processValueExpression(freeMarkerEngine, transformState.getLocation(), 
					"transform-expr", expr, context);
				listener.onTransform(new TransformEvent(transformState.getLocation(), 
					TransformEventType.FMARKER_EXPRESSION_EVALUATED, res));
				return res;
			}
			else if(exprType == ExpressionType.XPATH)
			{
				Object res = JXPathContext.newContext(context).getValue(expr);
				listener.onTransform(new TransformEvent(transformState.getLocation(), 
					TransformEventType.XPATH_EXPRESSION_EVALUATED, res));
				return res;
			}
			else if(exprType == ExpressionType.XPATH_MULTI)
			{
				Object res = JXPathContext.newContext(context).selectNodes(expr);
				listener.onTransform(new TransformEvent(transformState.getLocation(), 
					TransformEventType.XPATH_MULTI_EXPRESSION_EVALUATED, res));
				return res;
			}
			else if(exprType == ExpressionType.TEMPLATE)
			{
				Object res = ExpressionUtil.processTemplate(freeMarkerEngine, transformState.getLocation(), 
					"transform-template", expr, context);
				listener.onTransform(new TransformEvent(transformState.getLocation(), 
					TransformEventType.TEMPLATE_EXPRESSION_EVALUATED, res));
				return res;
			}
			else if(exprType == ExpressionType.STRING)
			{
				listener.onTransform(new TransformEvent(transformState.getLocation(), 
					TransformEventType.STRING_EXPRESSION_EVALUATED, expr));
				return expr;
			}
		} catch(TransformException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new TransformException(transformState.getLocation(), "An error occurred while processing expression: {} [Type: {}]", expr, exprType, ex);
		}
		
		throw new TransformException(transformState.getLocation(), "Invalid expression type specified '{}' in expression: {}", exprType, expr);
	}
}
