package com.yukthitech.autox.expr;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.ccg.xml.DynamicBean;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Default expression parser methods.
 * @author akiran
 */
public class DefaultExpressionParsers
{
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	@ExpressionParser(type = "prop", description = "Parses specified expression as bean property on effective-context (context or current object in case of piping).", example = "prop: attr.bean.value1")
	public IPropertyPath propertyParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public void setValue(Object value) throws Exception
			{
				PropertyUtils.setProperty(parserContext.getEffectiveContext(), expression, value);
			}
			
			@Override
			public Object getValue() throws Exception
			{
				return PropertyUtils.getProperty(parserContext.getEffectiveContext(), expression);
			}
		};
	}
	
	@ExpressionParser(type = "store", description = "Parses specified expression as value on/from store.", example = "store: key1")
	public IPropertyPath storeParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public void setValue(Object value) throws Exception
			{
				parserContext.getAutomationContext().getPersistenceStorage().set(expression, value);
			}
			
			@Override
			public Object getValue() throws Exception
			{
				return parserContext.getAutomationContext().getPersistenceStorage().get(expression);
			}
			
			@Override
			public void removeValue() throws Exception
			{
				parserContext.getAutomationContext().getPersistenceStorage().remove(expression);
			}
		};
	}

	@ExpressionParser(type = "attr", description = "Parses specified expression as context attribute.", example = "attr: attrName", contentType = ParserContentType.ATTRIBUTE)
	public IPropertyPath attrParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public void setValue(Object value) throws Exception
			{
				parserContext.getAutomationContext().setAttribute(expression, value);
			}
			
			@Override
			public Object getValue() throws Exception
			{
				return parserContext.getAutomationContext().getAttribute(expression);
			}
			
			@Override
			public void removeValue() throws Exception
			{
				parserContext.getAutomationContext().removeAttribute(expression);
			}
		};
	}

	@ExpressionParser(type = "param", description = "Parses specified expression as parameter.", example = "param: paramName")
	public IPropertyPath paramParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return parserContext.getAutomationContext().getParameter(expression);
			}
		};
	}

	@ExpressionParser(type = "xpath", description = "Parses specified expression as xpath on effective-context (context or current object in case of piping).", example = "xpath: /attr/bean/value1",
			params = {
					@ParserParam(name = "multi", type = "boolean", defaultValue = "false", description = "If true, list of matches will be returned"),
				})
	public IPropertyPath xpathParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public void setValue(Object value) throws Exception
			{
				JXPathContext.newContext(parserContext.getEffectiveContext()).setValue(expression, value);
			}
			
			@Override
			public Object getValue() throws Exception
			{
				try
				{
					if("true".equalsIgnoreCase(parserContext.getParameter("multi")))
					{
						return JXPathContext.newContext(parserContext.getEffectiveContext()).selectNodes(expression);
					}
					else
					{
						return JXPathContext.newContext(parserContext.getEffectiveContext()).getValue(expression);
					}
				}catch(JXPathNotFoundException ex)
				{
					return null;
				}
			}
			
			@Override
			public void removeValue() throws Exception
			{
				try
				{
					//get the value of expression. Which would throw JXPathNotFoundException if path is not found
					// Note: removePath() or removeAll() will not throw JXPathNotFoundException when path is not found
					
					JXPathContext.newContext(parserContext.getEffectiveContext()).getValue(expression);
					
					if("true".equalsIgnoreCase(parserContext.getParameter("multi")))
					{
						JXPathContext.newContext(parserContext.getEffectiveContext()).removeAll(expression);
					}
					else
					{
						JXPathContext.newContext(parserContext.getEffectiveContext()).removePath(expression);
					}
				}catch(JXPathNotFoundException ex)
				{
				}
			}
		};
	}
	
	private String getStringValue(ExpressionParserContext parserContext, String expression) throws Exception
	{
		if("$".equals(expression.trim()))
		{
			Object curVal = parserContext.getCurrentValue();
			
			if(curVal == null)
			{
				return null;
			}
			
			if(curVal instanceof Clob)
			{
				Clob clob = (Clob) curVal;
				return IOUtils.toString(clob.getAsciiStream());
			}
			
			if(curVal instanceof Blob)
			{
				Blob clob = (Blob) curVal;
				return IOUtils.toString(clob.getBinaryStream());
			}

			return curVal.toString().trim();
		}
		
		return expression.trim();
	}

	@ExpressionParser(type = "string", description = "Returns specified expression as stirng value after triming. In case of '$', current value will be converted to string. "
			+ "In case input object Blob/Clob, string value will be extracted from it.", 
			example = "string: str")
	public IPropertyPath strParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return getStringValue(parserContext, expression);
			}
		};
	}

	@ExpressionParser(type = "int", description = "Parses specified expression into int. In case of '$', current value's string value will be parsed.", example = "int: 10")
	public IPropertyPath intParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String expr = getStringValue(parserContext, expression);
				return Integer.parseInt(expr);
			}
		};
	}

	@ExpressionParser(type = "long", description = "Parses specified expression into long. In case of '$', current value's string value will be parsed.", example = "long: 10")
	public IPropertyPath longParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return Long.parseLong(getStringValue(parserContext, expression));
			}
		};
	}

	@ExpressionParser(type = "float", description = "Parses specified expression into float. In case of '$', current value's string value will be parsed.", example = "float: 10.2")
	public IPropertyPath floatParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return Float.parseFloat(getStringValue(parserContext, expression));
			}
		};
	}

	@ExpressionParser(type = "double", description = "Parses specified expression into double. In case of '$', current value's string value will be parsed.", example = "double: 10.2")
	public IPropertyPath doubleParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return Double.parseDouble(getStringValue(parserContext, expression));
			}
		};
	}

	@ExpressionParser(type = "boolean", description = "Parses specified expression into boolean. If expression value is true (case insensitive), then result will be true.  "
			+ "In case of '$', current value's string value will be parsed.", 
			example = "boolean: True")
	public IPropertyPath booleanParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return "true".equalsIgnoreCase(getStringValue(parserContext, expression));
			}
		};
	}

	@ExpressionParser(type = "date", description = "Parses specified expression into date. "
			+ "In case of '$', current value's string value will be parsed.", 
			example = "date: 21/3/2018, date(format=MM/dd/yyy): 3/21/2018")
	public IPropertyPath dateParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String format = parserContext.getParameter("template");
				
				if(format == null)
				{
					format = "dd/MM/yyyy";
				}
				
				SimpleDateFormat simpleDateFormat = null;
				
				try
				{
					simpleDateFormat = new SimpleDateFormat(format);
				}catch(Exception ex)
				{
					throw new InvalidArgumentException("Invalid date format specified: {}", format, ex);
				}
				
				try
				{
					return simpleDateFormat.parse(getStringValue(parserContext, expression));
				}catch(Exception ex)
				{
					throw new InvalidArgumentException("Specified date {} is not in specified format: {}", expression, format, ex);
				}
			}
		};
	}

	@ExpressionParser(type = "list", description = "Parses specified expression into list of strings (using comma as delimiter). If type specified, strings will be converted to specified type. "
			+ "In case of '$', current value's string value will be parsed.", 
			example = "list: val1, val2, val3")
	public IPropertyPath listParser(ExpressionParserContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String parts[] = getStringValue(parserContext, expression).split("\\s*\\,\\s*");
				
				if(exprType == null)
				{
					return Arrays.asList(parts);
				}
				
				if(exprType.length > 1)
				{
					throw new InvalidArgumentException("Multiple type parameters are specified for list conversion: {}", Arrays.toString(exprType));
				}
				
				Class<?> elemType = CommonUtils.getClass(exprType[0]);
				List<Object> resultLst = new ArrayList<>(parts.length);
				
				for(String part : parts)
				{
					resultLst.add( ConvertUtils.convert(part, elemType) );
				}
				
				return resultLst;
			}
		};
	}

	@ExpressionParser(type = "map", description = "Parses specified expression into map of strings (using comma as delimiter and = as delimiter for key and value). "
			+ "If types specified, strings will be converted to specified type. "
			+ "In case of '$', current value's string value will be parsed.", 
			example = "map: key1 = val1, key2=val2, key3=val3")
	public IPropertyPath mapParser(ExpressionParserContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String parts[] = getStringValue(parserContext, expression).split("\\s*\\,\\s*");
				Class<?> keyType = String.class;
				Class<?> valType = String.class;
				
				if(exprType != null)
				{
					if(exprType.length != 2)
					{
						throw new InvalidArgumentException("Insufficient/extra type parameters are specified for map conversion: {}", Arrays.toString(exprType));
					}
					
					keyType = Class.forName(exprType[0]);
					valType = Class.forName(exprType[1]);
				}
	
				Map<Object, Object> resMap = new HashMap<>();
				int eqIdx = 0;
				String key = null, value = null;
				
				for(String part : parts)
				{
					eqIdx = part.indexOf("=");
					
					if(eqIdx < 0)
					{
						throw new InvalidArgumentException("Invalid map entry specified '{}' in expresssion: {}", part, expression);
					}
					
					key = (eqIdx == 0) ? "" : part.substring(0, eqIdx);
					value = (eqIdx == part.length() - 1) ? "" : part.substring(eqIdx + 1, part.length());
					
					resMap.put(ConvertUtils.convert(key, keyType), ConvertUtils.convert(value, valType));
				}
				
				return resMap;
			}
		};
	}

	@ExpressionParser(type = "condition", description = "Evaluates specified expression as condition and resultant boolean value will be returned", 
			example = "condition: (attr.flag == true)", contentType = ParserContentType.FM_EXPRESSION)
	public IPropertyPath conditionParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return AutomationUtils.evaluateCondition(parserContext.getAutomationContext(), expression.trim());
			}
		};
	}
	
	@ExpressionParser(type = "expr", description = "Evaluates specified expression as freemarker expression and resultant value will be returned", 
			example = "expr: today()", contentType = ParserContentType.FM_EXPRESSION)
	public IPropertyPath expressionParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String randomVarName = "tmp_" + Long.toHexString(System.currentTimeMillis());
				String fmCode = "${setAttr('" + randomVarName + "', " + expression.trim() + ")}";
				
				ExecutionLogger logger = parserContext.getAutomationContext().getExecutionLogger();
				
				logger.debug("Evaluating expression {} using code snippet: {}", expression, fmCode);
				
				AutomationUtils.replaceExpressionsInString("expression", parserContext.getAutomationContext(), fmCode);
				
				Object result = parserContext.getAutomationContext().removeAttribute(randomVarName);

				logger.debug("From epxression {} got result as: {}", expression, result);
				return result;
			}
		};
	}

	/**
	 * Loads the specified input stream as bean.
	 * @param is input stream to load
	 * @param name name of the input
	 * @return loaded object
	 */
	private Object loadInputStream(String data, String name, String exprType[], ExpressionParserContext parserContext) throws Exception
	{
		ExecutionLogger logger = parserContext.getAutomationContext().getExecutionLogger();
		
		//if the input stream needs to be loaded as template, parse the expressions
		if("true".equalsIgnoreCase(parserContext.getParameter("template")))
		{
			logger.debug("Processing input data as template: {}", name);
			data = AutomationUtils.replaceExpressionsInString(name, parserContext.getAutomationContext(), data);
		}
		
		//if input stream has to be loaded as simple text, simply return the current data string
		if("true".equalsIgnoreCase(parserContext.getParameter("text")))
		{
			logger.debug("Returning input data as string: {}", name);
			return data;
		}
		
		InputStream is = new ByteArrayInputStream(data.getBytes());
		
		Class<?> type = null;
		name = name.trim();
		
		if(exprType != null)
		{
			if(exprType.length != 1)
			{
				throw new InvalidArgumentException("Insufficient/extra type parameters are specified for file loading: {}", Arrays.toString(exprType));
			}
			
			type = Class.forName(exprType[0]);
		}
		
		if(name.toLowerCase().endsWith(".properties"))
		{
			logger.debug("Processing input file as properties file: {}", name);
			Properties prop = new Properties();
			prop.load(is);
			
			return new HashMap<>(prop);
		}
		
		if(name.toLowerCase().endsWith(".json"))
		{
			logger.debug("Processing input file as json file: {} [Type: {}]", name, type);
			
			if(type == null)
			{
				type = Object.class;
			}
			
			Object res = AutomationUtils.convertToWriteable( objectMapper.readValue(is, type) );
			
			return res;
		}
		
		if(name.toLowerCase().endsWith(".xml"))
		{
			logger.debug("Processing input file as xml file: {} [Type: {}]", name, type);
			
			Object res = null;
			
			if(type != null)
			{
				res = type.newInstance();
			}
			
			res = XMLBeanParser.parse(is, res);
			
			if(res instanceof DynamicBean)
			{
				return ((DynamicBean) res).toSimpleMap();
			}
			
			return res;
		}
		
		throw new com.yukthitech.utils.exceptions.UnsupportedOperationException("Unsupported input specified for bean loading: '{}'", name);
	}
	
	private String loadFile(String filePath) throws IOException
	{
		File file = new File(filePath);
		
		if(!file.exists() || !file.isFile())
		{
			throw new InvalidArgumentException("Invalid/non-existing file specified for loading: {}", filePath);
		}
		
		return FileUtils.readFileToString(file);
	}

	@ExpressionParser(type = "file", description = "Parses specified expression as file path and loads it as object. Supported file types: xml, json, properties", 
			example = "file: /tmp/data.json",
			params = {
				@ParserParam(name = "template", type = "boolean", defaultValue = "false", description = "If true, the loaded content will be parsed as freemarker template"),
				@ParserParam(name = "text", type = "boolean", defaultValue = "false", description = "If true, the loaded content will be returned as text directly, without parsing into object.")
			})
	public IPropertyPath fileParser(ExpressionParserContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return loadInputStream(loadFile(expression), expression, exprType, parserContext);
			}
		};
	}

	@ExpressionParser(type = "bfile", description = "Parses specified expression as file path and loads it as binary data (byte array).", 
			example = "bfile: /tmp/data")
	public IPropertyPath bfileParser(ExpressionParserContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return FileUtils.readFileToByteArray(new File(expression));
			}
		};
	}

	@ExpressionParser(type = "res", description = "Parses specified expression as resource path and loads it as object. Supported file types: xml, json, properties",
			example = "res: /tmp/data.json",
			params = {
				@ParserParam(name = "template", type = "boolean", defaultValue = "false", description = "If true, the loaded content will be parsed as freemarker template"),
				@ParserParam(name = "text", type = "boolean", defaultValue = "false", description = "If true, the loaded content will be returned as text directly, without parsing into object.")
			})
	public IPropertyPath resParser(ExpressionParserContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String data = null;
				
				if("$".equals(expression.trim()))
				{
					Object curVal = parserContext.getCurrentValue();
					
					if(curVal == null || !(curVal instanceof String))
					{
						throw new InvalidStateException("No/incompatible data found on the pipe input. Piped Input: {}", curVal);
					}
					
					data = curVal.toString();
				}
				else
				{
					InputStream is = DefaultExpressionParsers.class.getResourceAsStream(expression); 

					if(is == null)
					{
						throw new InvalidArgumentException("Invalid/non-existing resource specified for loading: {}", expression);
					}
					
					data = IOUtils.toString(is);
					is.close();
				}
				
				Object object = loadInputStream(data, expression, exprType, parserContext);
				return object;
			}
		};
	}

	@ExpressionParser(type = "bres", description = "Parses specified expression as resource path and loads it as binary data (byte array).", 
			example = "res: /tmp/data.json")
	public IPropertyPath bresParser(ExpressionParserContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				byte data[] = null;
				
				if("$".equals(expression.trim()))
				{
					Object curVal = parserContext.getCurrentValue();
					
					if(curVal == null || (!(curVal instanceof String) && !(curVal instanceof byte[])) )
					{
						throw new InvalidStateException("No/incompatible data found on the pipe input. Piped Input: {}", curVal);
					}
					
					if(curVal instanceof String)
					{
						data = ((String) curVal).getBytes();
					}
					else
					{
						data = (byte[]) curVal;
					}
				}
				else
				{
					InputStream is = DefaultExpressionParsers.class.getResourceAsStream(expression); 

					if(is == null)
					{
						throw new InvalidArgumentException("Invalid/non-existing resource specified for loading: {}", expression);
					}
					
					data = IOUtils.toByteArray(is);
					is.close();
				}
				
				return data;
			}
		};
	}

	@ExpressionParser(type = "json", description = "Parses specified expression as json string and loads it as object. "
			+ "In case of '$', current value's string value will be parsed.", 
			example = "json: {\"a\": 2, \"b\": 3}")
	public IPropertyPath jsonParser(ExpressionParserContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String data = getStringValue(parserContext, expression);
				Object object = loadInputStream(data, ".json", exprType, parserContext);
				return object;
			}
		};
	}
}

