package com.yukthitech.autox.expr;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
	
	@ExpressionParser(type = "prop", description = "Parses specified expression as bean property on context.", example = "prop: attr.bean.value1")
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

	@ExpressionParser(type = "attr", description = "Parses specified expression as context attribute.", example = "attr: attrName")
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
		};
	}

	@ExpressionParser(type = "xpath", description = "Parses specified expression as xpath on context.", example = "xpath: /attr/bean/value1")
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
				return JXPathContext.newContext(parserContext.getEffectiveContext()).getValue(expression);
			}
		};
	}

	@ExpressionParser(type = "string", description = "Returns specified expression as stirng value after trimming.", example = "sring: str")
	public IPropertyPath strParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return expression.trim();
			}
		};
	}

	@ExpressionParser(type = "int", description = "Parses specified expression into int.", example = "int: 10")
	public IPropertyPath intParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String expr = expression.trim();
				return Integer.parseInt(expr);
			}
		};
	}

	@ExpressionParser(type = "long", description = "Parses specified expression into long.", example = "long: 10")
	public IPropertyPath longParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return Long.parseLong(expression.trim());
			}
		};
	}

	@ExpressionParser(type = "float", description = "Parses specified expression into float.", example = "float: 10.2")
	public IPropertyPath floatParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return Float.parseFloat(expression.trim());
			}
		};
	}

	@ExpressionParser(type = "double", description = "Parses specified expression into double.", example = "double: 10.2")
	public IPropertyPath doubleParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return Double.parseDouble(expression.trim());
			}
		};
	}

	@ExpressionParser(type = "boolean", description = "Parses specified expression into boolean. If expression value is true (case insensitive), then result will be true.", example = "boolean: True")
	public IPropertyPath booleanParser(ExpressionParserContext parserContext, String expression)
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				return "true".equalsIgnoreCase(expression.trim());
			}
		};
	}

	@ExpressionParser(type = "list", description = "Parses specified expression into list of strings (using comma as delimiter). If type specified, strings will be converted to specified type.", 
			example = "list: val1, val2, val3")
	public IPropertyPath listParser(ExpressionParserContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String parts[] = expression.trim().split("\\s*\\,\\s*");
				
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
			+ "If types specified, strings will be converted to specified type.", 
			example = "map: key1 = val1, key2=val2, key3=val3")
	public IPropertyPath mapParser(ExpressionParserContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String parts[] = expression.trim().split("\\s*\\,\\s*");
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

	@ExpressionParser(type = "condition", description = "Evaluates specified expression as condition and resultant boolean value will be returned", example = "condition: (attr.flag == true)")
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
			logger.debug(null, "Processing input data as template: {}", name);
			data = AutomationUtils.replaceExpressionsInString(name, parserContext.getAutomationContext(), data);
		}
		
		//if input stream has to be loaded as simple text, simply return the current data string
		if("true".equalsIgnoreCase(parserContext.getParameter("text")))
		{
			logger.debug(null, "Returning input data as string: {}", name);
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
			logger.debug(null, "Processing input file as properties file: {}", name);
			Properties prop = new Properties();
			prop.load(is);
			
			return new HashMap<>(prop);
		}
		
		if(name.toLowerCase().endsWith(".json"))
		{
			logger.debug(null, "Processing input file as json file: {} [Type: {}]", name, type);
			
			if(type == null)
			{
				type = Object.class;
			}
			
			Object res = AutomationUtils.convertToWriteable( objectMapper.readValue(is, type) );
			
			return res;
		}
		
		if(name.toLowerCase().endsWith(".xml"))
		{
			logger.debug(null, "Processing input file as xml file: {} [Type: {}]", name, type);
			
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
			example = "file: /tmp/data.json")
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

	@ExpressionParser(type = "res", description = "Parses specified expression as resource path and loads it as object. Supported file types: xml, json, properties", 
			example = "res: /tmp/data.json")
	public IPropertyPath resParser(ExpressionParserContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String data = null;
				
				if("$".equals(expression))
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
						throw new InvalidArgumentException("Invalid/non-existing file specified for loading: {}", expression);
					}
					
					data = IOUtils.toString(is);
					is.close();
				}
				
				Object object = loadInputStream(data, expression, exprType, parserContext);
				return object;
			}
		};
	}

	@ExpressionParser(type = "json", description = "Parses specified expression as json string and loads it as object.", 
			example = "json: {\"a\": 2, \"b\": 3}")
	public IPropertyPath jsonParser(ExpressionParserContext parserContext, String expression, String exprType[])
	{
		return new IPropertyPath()
		{
			@Override
			public Object getValue() throws Exception
			{
				String data = null;
				
				if("$".equals(expression))
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
					data = expression;
				}
				
				Object object = loadInputStream(data, ".json", exprType, parserContext);
				return object;
			}
		};
	}
}

