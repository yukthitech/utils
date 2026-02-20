package com.yukthitech.transform.template;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.jxpath.JXPathContext;
import org.w3c.dom.Element;

import com.yukthitech.transform.ExpressionUtil;
import com.yukthitech.transform.ITransformConstants;
import com.yukthitech.transform.ITransformContext;
import com.yukthitech.transform.TransformException;
import com.yukthitech.transform.TransformState;
import com.yukthitech.transform.template.TransformTemplate.Expression;
import com.yukthitech.transform.template.TransformTemplate.ExpressionType;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

public class TransformUtils
{
	/**
	 * Expression used by value string which has to be replaced with resultant value.
	 */
	public static final Pattern EXPR_PATTERN = Pattern.compile("^\\@([\\w\\-]+)\\s*\\:\\s*(.*)$");

    public static String toXmlString(Element element)
    {
		try
		{
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			// Prepare the source and result
			DOMSource source = new DOMSource(element);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);

			// Perform the transformation
			transformer.transform(source, result);

			return writer.toString();
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting to xml string", ex);
		}
	}

    public static Expression parseExpression(String expression, String path, boolean nullByDefault)
    {
        Matcher matcher = EXPR_PATTERN.matcher(expression);
        
        if(!matcher.matches())
        {
        	if(expression.contains("${") || expression.contains("<#"))
        	{
        		return new Expression(ExpressionType.TEMPLATE, expression);
        	}
        	
            return nullByDefault ? null : new Expression(ExpressionType.STRING, expression);
        }

        String exprType = matcher.group(1);
        String expr = matcher.group(2);

        if(ITransformConstants.EXPR_TYPE_FMARKER.equals(exprType))
        {
            return new Expression(ExpressionType.FMARKER, expr);
        }
        else if(ITransformConstants.EXPR_TYPE_XPATH.equals(exprType))
        {
            return new Expression(ExpressionType.XPATH, expr);
        }
        else if(ITransformConstants.EXPR_TYPE_XPATH_MULTI.equals(exprType))
        {
            return new Expression(ExpressionType.XPATH_MULTI, expr);
        }

        throw new TransformException(path, "Invalid expression type specified '{}' in expression: {}", exprType, expression);
    }

	/**
	 * If input string contains expression syntax the same will be processed and result will be returned. If not string will
	 * be evaluated as simple free marker template.
	 * @param str string to evaluate
	 * @param context context to be used
	 * @param path path where this string is found
	 * @return processed value
	 */
	public static Object processExpression(FreeMarkerEngine freeMarkerEngine, Expression expression, ITransformContext context, TransformState transformState)
	{
		ExpressionType exprType = expression.getType();
		String expr = expression.getExpression();
		
		try
		{
			if(exprType == ExpressionType.FMARKER)
			{
				return ExpressionUtil.processValueExpression(freeMarkerEngine, transformState.getPath(), "transform-expr", expr, context);
			}
			else if(exprType == ExpressionType.XPATH)
			{
				return JXPathContext.newContext(context).getValue(expr);
			}
			else if(exprType == ExpressionType.XPATH_MULTI)
			{
				return JXPathContext.newContext(context).selectNodes(expr);
			}
			else if(exprType == ExpressionType.TEMPLATE)
			{
				return ExpressionUtil.processTemplate(freeMarkerEngine, transformState.getPath(), "transform-template", expr, context);
			}
			else if(exprType == ExpressionType.STRING)
			{
				return expr;
			}
		} catch(TransformException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new TransformException(transformState.getPath(), "An error occurred while processing expression: {} [Type: {}]", expr, exprType, ex);
		}
		
		throw new TransformException(transformState.getPath(), "Invalid expression type specified '{}' in expression: {}", exprType, expr);
	}
}
