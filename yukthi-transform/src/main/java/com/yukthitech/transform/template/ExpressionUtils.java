package com.yukthitech.transform.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;

import com.jayway.jsonpath.JsonPath;
import com.yukthitech.transform.FreemarkerUtil;
import com.yukthitech.transform.ITransformConstants;
import com.yukthitech.transform.ITransformContext;
import com.yukthitech.transform.TransformException;
import com.yukthitech.transform.event.ITransformListener;
import com.yukthitech.transform.event.TransformEvent;
import com.yukthitech.transform.event.TransformEventType;
import com.yukthitech.transform.template.TransformTemplate.Expression;
import com.yukthitech.transform.template.TransformTemplate.ExpressionType;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;
import com.yukthitech.utils.fmarker.FreeMarkerTemplate;

public class ExpressionUtils
{
	/**
	 * Expression used by value string which has to be replaced with resultant value.
	 */
	public static final Pattern EXPR_PATTERN = Pattern.compile("^\\@([\\w\\-]+)\\s*\\:\\s*(.*)$", Pattern.DOTALL);

    private static Expression parseExpression(FreeMarkerEngine freeMarkerEngine, String mainExpression, 
    		String exprType, String expr, Location location, boolean compileParsed)
    {
    	try
    	{
	        if(ITransformConstants.EXPR_TYPE_FMARKER.equals(exprType))
	        {
	        	FreeMarkerTemplate freeMarkerTemplate = compileParsed
	        			? freeMarkerEngine.buildValueTemplate("transform-expr", expr)
	        			: null;
	            return new Expression(location, ExpressionType.FMARKER, expr, freeMarkerTemplate);
	        }
	        else if(ITransformConstants.EXPR_TYPE_XPATH.equals(exprType))
	        {
	        	CompiledExpression compiledExpr = compileParsed ? JXPathContext.compile(expr) : null;
	            return new Expression(location, ExpressionType.XPATH, expr, compiledExpr);
	        }
	        else if(ITransformConstants.EXPR_TYPE_XPATH_MULTI.equals(exprType))
	        {
	        	CompiledExpression compiledExpr = compileParsed ? JXPathContext.compile(expr) : null;
	            return new Expression(location, ExpressionType.XPATH_MULTI, expr, compiledExpr);
	        }
			else if(ITransformConstants.EXPR_TYPE_JSON_PATH.equals(exprType))
			{
				JsonPath compiledExpr = compileParsed ? JsonPath.compile(expr) : null;
				return new Expression(location, ExpressionType.JSON_PATH, expr, compiledExpr);
			}
			else if(ITransformConstants.EXPR_TYPE_JSON_PATH_MULTI.equals(exprType))
			{
				JsonPath compiledExpr = compileParsed ? JsonPath.compile(expr) : null;
				return new Expression(location, ExpressionType.JSON_PATH_MULTI, expr, compiledExpr);
			}
    	}catch(Exception ex)
    	{
    		throw new TemplateParseException(location, "Failed to parse expression: [Type: {}, Expression: {}]", exprType, mainExpression, ex);
    	}

        throw new TemplateParseException(location, "Invalid expression type specified '{}' in expression: {}", exprType, mainExpression);
    }

    public static Expression parseExpression(FreeMarkerEngine freeMarkerEngine, String expression, Location location, boolean nullByDefault)
    {
    	return parseExpression(freeMarkerEngine, expression, location, nullByDefault, true);
    }

    /**
     * Parses an expression into an {@link Expression} node. When {@code compileParsed} is false, the
     * serializable text and type are set but {@link Expression#getParsedExpression()} remains null until
     * {@link #compileParsedExpression(FreeMarkerEngine, Expression)} is run (for example after deserialization).
     */
    public static Expression parseExpression(FreeMarkerEngine freeMarkerEngine, String expression, Location location, boolean nullByDefault, boolean compileParsed)
    {
        Matcher matcher = EXPR_PATTERN.matcher(expression);
        
        if(!matcher.matches())
        {
        	if(expression.contains("${") || expression.contains("<#"))
        	{
        		FreeMarkerTemplate freeMarkerTemplate = compileParsed
        				? freeMarkerEngine.buildTemplate("transform-template", expression)
        				: null;
        		return new Expression(location, ExpressionType.TEMPLATE, expression, freeMarkerTemplate);
        	}
        	
            return nullByDefault ? null : new Expression(location, ExpressionType.STRING, expression, null);
        }

        String exprType = matcher.group(1);
        String expr = matcher.group(2);
        
        return parseExpression(freeMarkerEngine, expression, exprType, expr, location, compileParsed);
    }
    
    public static Expression parseValueExpression(FreeMarkerEngine freeMarkerEngine, String expression, Location location)
    {
    	return parseValueExpression(freeMarkerEngine, expression, location, true);
    }

    public static Expression parseValueExpression(FreeMarkerEngine freeMarkerEngine, String expression, Location location, boolean compileParsed)
    {
        Matcher matcher = EXPR_PATTERN.matcher(expression);
        String exprType = null;
        String expr = null;
        
        if(matcher.matches())
        {
            exprType = matcher.group(1);
            expr = matcher.group(2);
        }
        else
        {
        	// Defaults to free marker value expression
        	exprType = ITransformConstants.EXPR_TYPE_FMARKER;
        	expr = expression;
        }
        
        return parseExpression(freeMarkerEngine, expression, exprType, expr, location, compileParsed);
    }

    /**
     * Compiles the parsed expression artifact (FreeMarker template, XPath, JsonPath) from the stored
     * type and expression text. No-op if already compiled or if the type has no compiled form.
     */
    public static void compileParsedExpression(FreeMarkerEngine freeMarkerEngine, Expression expression)
    {
    	if(expression == null)
    	{
    		return;
    	}
    	if(expression.getParsedExpression() != null)
    	{
    		return;
    	}

    	ExpressionType exprType = expression.getType();
    	String expr = expression.getExpression();
    	Location location = expression.getLocation();

    	try
    	{
    		switch(exprType)
    		{
    			case STRING:
    				return;
    			case FMARKER:
    				expression.setParsedExpression(freeMarkerEngine.buildValueTemplate("transform-expr", expr));
    				return;
    			case TEMPLATE:
    				expression.setParsedExpression(freeMarkerEngine.buildTemplate("transform-template", expr));
    				return;
    			case XPATH:
    			case XPATH_MULTI:
    				expression.setParsedExpression(JXPathContext.compile(expr));
    				return;
    			case JSON_PATH:
    			case JSON_PATH_MULTI:
    				expression.setParsedExpression(JsonPath.compile(expr));
    				return;
    			default:
    				throw new TemplateParseException(location, "Unsupported expression type for compilation: {}", exprType);
    		}
    	} catch(Exception ex)
    	{
    		throw new TemplateParseException(location, "Failed to compile expression: [Type: {}, Expression: {}]", exprType, expr, ex);
    	}
    }

	/**
	 * If input string contains expression syntax the same will be processed and result will be returned. If not string will
	 * be evaluated as simple free marker template.
	 * @param str string to evaluate
	 * @param context context to be used
	 * @param path path where this string is found
	 * @return processed value
	 */
	@SuppressWarnings("unchecked")
	public static Object processExpression(FreeMarkerEngine freeMarkerEngine, Expression expression, 
		ITransformContext context, ITransformListener listener)
	{
		ExpressionType exprType = expression.getType();
		String expr = expression.getExpression();

		TemplateFactoryConfiguration templateFactoryConfiguration = TemplateFactoryConfiguration.getCurrentInstance();
		
		try
		{
			if(exprType == ExpressionType.FMARKER)
			{
				Object res = FreemarkerUtil.processValueExpression(freeMarkerEngine, expression.getLocation(), 
					(FreeMarkerTemplate) expression.getParsedExpression(), 
					context);
				listener.onTransform(new TransformEvent(expression.getLocation(), 
					TransformEventType.FMARKER_EXPRESSION_EVALUATED, res));
				return res;
			}
			else if(exprType == ExpressionType.XPATH)
			{
				if(templateFactoryConfiguration != null && templateFactoryConfiguration.isXpathDisabled())
				{
					throw new TransformException(expression.getLocation(), "XPath expression is not supported by the current template factory.");
				}

				CompiledExpression compiledExpression = (CompiledExpression) expression.getParsedExpression();
				Object res = compiledExpression.getValue(JXPathContext.newContext(context));
				
				listener.onTransform(new TransformEvent(expression.getLocation(), 
					TransformEventType.XPATH_EXPRESSION_EVALUATED, res));
				return res;
			}
			else if(exprType == ExpressionType.XPATH_MULTI)
			{
				if(templateFactoryConfiguration != null && templateFactoryConfiguration.isXpathDisabled())
				{
					throw new TransformException(expression.getLocation(), "XPath expression is not supported by the current template factory.");
				}
	
				CompiledExpression compiledExpression = (CompiledExpression) expression.getParsedExpression();
				
				Iterator<?> it = compiledExpression.iterate(JXPathContext.newContext(context));

				List<Object> res = new ArrayList<>();
				
				while (it.hasNext()) 
				{
					res.add(it.next());
				}
				
				listener.onTransform(new TransformEvent(expression.getLocation(), 
					TransformEventType.XPATH_MULTI_EXPRESSION_EVALUATED, res));
				return res;
			}
			else if(exprType == ExpressionType.JSON_PATH)
			{
				JsonPath compiledExpression = (JsonPath) expression.getParsedExpression();
				Object res = compiledExpression.read(context);
				
				if(res instanceof Collection)
				{
					Collection<Object> collection = (Collection<Object>) res;
					Iterator<Object> it = collection.iterator();
					res = it.hasNext() ? it.next() : null;
				}
				
				listener.onTransform(new TransformEvent(expression.getLocation(), 
					TransformEventType.JSON_PATH_EXPRESSION_EVALUATED, res));
				return res;
			}
			else if(exprType == ExpressionType.JSON_PATH_MULTI)
			{
				JsonPath compiledExpression = (JsonPath) expression.getParsedExpression();
				Object res = compiledExpression.read(context);
				
				if(res != null && !(res instanceof Collection))
				{
					res = Arrays.asList(res);
				}

				listener.onTransform(new TransformEvent(expression.getLocation(), 
					TransformEventType.JSON_PATH_MULTI_EXPRESSION_EVALUATED, res));
				return res;
			}
			else if(exprType == ExpressionType.TEMPLATE)
			{
				Object res = FreemarkerUtil.processTemplate(freeMarkerEngine, expression.getLocation(), 
					(FreeMarkerTemplate) expression.getParsedExpression(), 
					context);
				listener.onTransform(new TransformEvent(expression.getLocation(), 
					TransformEventType.TEMPLATE_EXPRESSION_EVALUATED, res));
				return res;
			}
			else if(exprType == ExpressionType.STRING)
			{
				listener.onTransform(new TransformEvent(expression.getLocation(), 
					TransformEventType.STRING_EXPRESSION_EVALUATED, expr));
				return expr;
			}
		} catch(TransformException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new TransformException(expression.getLocation(), "An error occurred while processing expression: {} [Type: {}]", expr, exprType, ex);
		}
		
		throw new TransformException(expression.getLocation(), "Invalid expression type specified '{}' in expression: {}", exprType, expr);
	}
}
