package com.yukthitech.autox.expr;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import com.yukthitech.autox.AbstractLocationBased;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.common.FreeMarkerMethodManager;
import com.yukthitech.autox.common.IAutomationConstants;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Factory for parsing expressions.
 * @author akiran
 */
public class ExpressionFactory extends AbstractLocationBased
{
	private static Logger logger = LogManager.getLogger(ExpressionFactory.class);
	
	/**
	 * Singleton instance.
	 */
	private static ExpressionFactory expressionFactory;
	
	/**
	 * Available expression parsers.
	 */
	private Map<String, ExpressionParserDetails> parsers = new HashMap<>();
	
	private ExpressionFactory()
	{}
	
	/**
	 * Gets the expression factory.
	 *
	 * @return the expression factory
	 */
	public static void init(ClassLoader classLoader, Set<String> basePackagesAct)
	{
		if(classLoader == null)
		{
			classLoader = FreeMarkerMethodManager.class.getClassLoader();
		}
		
		Set<String> basePackages = new HashSet<>();
		
		if(basePackagesAct != null)
		{
			basePackages.addAll(basePackagesAct);
		}
		
		basePackages.add("com.yukthitech");
		
		ExpressionFactory factory = new ExpressionFactory();

		Reflections reflections = null;
		Set<Method> parserMethods = null;
		
		Map<Class<?>, Object> parserClasses = new HashMap<>();

		for(String pack : basePackages)
		{
			logger.debug("Scanning for expressions parser methods in package - {}", pack);
			reflections = new Reflections(
					ConfigurationBuilder.build(pack, new MethodAnnotationsScanner(), classLoader)
				);

			parserMethods = reflections.getMethodsAnnotatedWith(ExpressionParser.class);
			
			if(parserMethods != null)
			{
				Object parserObj = null;
				ExpressionParser parserAnnot = null;
				ExpressionParserDetails parserDet = null;
				Class<?> paramTypes[] = null;
				
				for(Method method : parserMethods)
				{
					paramTypes = method.getParameterTypes();
					
					if(paramTypes.length == 2)
					{
						if(!ExpressionParserContext.class.equals(paramTypes[0]) || !String.class.equals(paramTypes[1]))
						{
							throw new InvalidStateException("Invalid arguments specified for expression parser method: {}.{}", method.getDeclaringClass().getName(), method.getName());
						}
					}
					else if(paramTypes.length == 3)
					{
						if(!ExpressionParserContext.class.equals(paramTypes[0]) || !String.class.equals(paramTypes[1]) || !String[].class.equals(paramTypes[2]))
						{
							throw new InvalidStateException("Invalid arguments specified for expression parser method: {}.{}", method.getDeclaringClass().getName(), method.getName());
						}
					} 
					else
					{
						throw new InvalidStateException("Invalid arguments specified for expression parser method: {}.{}", method.getDeclaringClass().getName(), method.getName());
					}
					
					if(!IPropertyPath.class.equals(method.getReturnType()))
					{
						throw new InvalidStateException("Expression parser method is not returning property path: {}.{}", method.getDeclaringClass().getName(), method.getName());
					}
					
					parserAnnot = method.getAnnotation(ExpressionParser.class);
					parserObj = parserClasses.get(method.getDeclaringClass());
					
					if(parserObj == null)
					{
						try
						{
							parserObj = method.getDeclaringClass().newInstance();
						}catch(Exception ex)
						{
							throw new InvalidStateException("An error occurred while creating instance of expression parser enclosing object of type: {}", method.getDeclaringClass().getName(), ex);
						}
						
						parserClasses.put(method.getDeclaringClass(), parserObj);
					}
					
					parserDet = new ExpressionParserDetails(parserAnnot.type(), parserAnnot.description(), parserAnnot.example(), parserObj, method);
					factory.parsers.put(parserDet.getType(), parserDet);
				}
			}
		}
		
		logger.debug("Found expression parsers to be: {}", factory.parsers);
		ExpressionFactory.expressionFactory = factory;
	}
	
	public static ExpressionFactory getExpressionFactory()
	{
		return expressionFactory;
	}
	
	public Object parseExpression(AutomationContext context, Object expressionObj)
	{
		if(!(expressionObj instanceof String))
		{
			return expressionObj;
		}
		
		String expression = (String) expressionObj;
		
		if(expression.trim().length() == 0)
		{
			return expression;
		}
			
		//Parse the expression into tokens delimited by '|'
		List<String> lst = new ArrayList<String>();
		char ch[] = expression.toCharArray();
		StringBuilder token = new StringBuilder();
		
		for(int i = 0; i < ch.length; i++)
		{
			if(ch[i] == '\\')
			{
				if(i < ch.length - 1 && ch[i + 1] == '|')
				{
					token.append('|');
					i++;
					continue;
				}
			}
			
			if(ch[i] == '|')
			{
				if(token.length() > 0)
				{
					lst.add(token.toString());
				}
				
				token.setLength(0);
				continue;
			}
			
			token.append(ch[i]);
		}
		
		if(token.length() > 0)
		{
			lst.add(token.toString());
		}
		
		Object result = null;
		ExpressionParserContext expressionParserContext = new ExpressionParserContext(context);
		ObjectWrapper<Boolean> expressionParsed = new ObjectWrapper<Boolean>(true);
		
		//convert tokens into objects
		for(String tokenStr : lst)
		{
			result = parseSingleExpression(expressionParserContext, tokenStr, expressionParsed);
			
			if(!expressionParsed.getValue())
			{
				return expressionObj;
			}
			
			expressionParserContext.setCurrentValue(result);
		}
		
		return result;
	}
	
	private IPropertyPath getPropertyPath(ExpressionParserContext context, String expression)
	{
		ExecutionLogger exeLogger = context.getAutomationContext().getExecutionLogger();
		
		exeLogger.debug(this, "Fetching expression parser for value: {}", expression);
		
		context.clearParameters();
		
		//check if string is a reference
		String exprType = null, mainExpr = null;
		String exprTypeParams[] = null;
		
		Matcher matcher = IAutomationConstants.EXPRESSION_PATTERN.matcher(expression);
		Matcher matcherWithType = IAutomationConstants.EXPRESSION_WITH_TYPE_PATTERN.matcher(expression);
		
		if(matcher.find())
		{
			exprType = matcher.group("exprType");
			mainExpr = expression.substring(matcher.end()).trim();
		}
		else if(matcherWithType.find())
		{
			exprType = matcherWithType.group("exprType");
			mainExpr = expression.substring(matcherWithType.end()).trim();
			
			String type = matcherWithType.group("type");
			exprTypeParams = type.trim().split("\\s*\\,\\s*");
			
			//parse parameter types
			List<String> typeParams = new ArrayList<>();
			Matcher keyValMatcher = null;
			
			for(String param : exprTypeParams)
			{
				keyValMatcher = IAutomationConstants.KEY_VALUE_PATTERN.matcher(param);
				
				if(keyValMatcher.matches())
				{
					context.addParameter(keyValMatcher.group("key"), keyValMatcher.group("value"));
				}
				else
				{
					typeParams.add(param);
				}
			}
			
			exprTypeParams = typeParams.isEmpty() ? null : typeParams.toArray(new String[0]);
		}
		else
		{
			return null;
		}
		
		ExpressionParserDetails parser = parsers.get(exprType);
		
		if(parser == null)
		{
			throw new InvalidArgumentException("Invalid expression type '{}' specified in expression: {}", exprType, expression);
		}
		
		context.setCurrentParser(parser);
		
		exeLogger.trace(this, "Executing expression: {}", expression);
		return parser.invoke(context, mainExpr, exprTypeParams);
	}
	
	private Object parseSingleExpression(ExpressionParserContext context, String expression, ObjectWrapper<Boolean> expressionParsed)
	{
		IPropertyPath propPath = getPropertyPath(context, expression);
		
		if(propPath == null)
		{
			expressionParsed.setValue(false);
			return expression;
		}
		
		ExecutionLogger exeLogger = context.getAutomationContext().getExecutionLogger();
		exeLogger.trace(this, "Executing expression: {}", expression);
		
		try
		{
			Object result = propPath.getValue();
			ExpressionParserDetails parser = context.getCurrentParser();
			String exprTypeParams[] = context.getExpressionTypeParameters();
			
			exeLogger.trace(this, "Execution of property expression {} resulted in: {}", expression, result);
	
			if(!parser.isConversionHandled() && exprTypeParams != null)
			{
				if(exprTypeParams.length > 0)
				{
					throw new InvalidArgumentException("Multiple result type parameters specified in expression: {}", expression);
				}
				
				Class<?> resultType = null;
				
				try
				{
					resultType = CommonUtils.getClass(exprTypeParams[0]);
				}catch(Exception ex)
				{
					throw new InvalidArgumentException("Invalid result type '{}' specified in expression: {}", exprTypeParams[0], expression, ex);
				}
	
				if(resultType != null)
				{
					result = ConvertUtils.convert(result, resultType);
				}
			}
			
			return result;
		}catch(Exception ex)
		{
			exeLogger.error(this, "Evaluation of expression {} resulted in error", expression, ex);
			throw new InvalidStateException("An error occurred while evaluating expression '{}'", expression, ex);
		}
	}
	
	public void setExpressionValue(AutomationContext context, String expression, Object value)
	{
		ExpressionParserContext expressionParserContext = new ExpressionParserContext(context);
		IPropertyPath propertyPath = getPropertyPath(expressionParserContext, expression);
		ExecutionLogger exeLogger = context.getExecutionLogger();
		
		if(propertyPath == null)
		{
			exeLogger.debug(this, "Setting attribute '{}' as value: {}", expression, value);
			context.setAttribute(expression, value);
			return;
		}
		
		exeLogger.debug(this, "Setting expression '{}' as value: {}", expression, value);
		
		ExpressionParserDetails parser = expressionParserContext.getCurrentParser();
		String exprTypeParams[] = expressionParserContext.getExpressionTypeParameters();

		if(!parser.isConversionHandled() && exprTypeParams != null)
		{
			if(exprTypeParams.length > 0)
			{
				throw new InvalidArgumentException("Multiple result type parameters specified in expression: {}", expression);
			}
			
			Class<?> valueType = null;
			
			try
			{
				valueType = CommonUtils.getClass(exprTypeParams[0]);
			}catch(Exception ex)
			{
				throw new InvalidArgumentException("Invalid result type '{}' specified in expression: {}", exprTypeParams[0], expression, ex);
			}

			if(valueType != null)
			{
				value = ConvertUtils.convert(value, valueType);
			}
		}

		try
		{
			propertyPath.setValue(value);
		}catch(Exception ex)
		{
			exeLogger.error(this, "Failed to set specified value {} on path {}", value, expression, ex);
			throw new InvalidStateException("Failed to set specified value {} on path {}", value, expression, ex);
		}
	}
}
