/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.ccg.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.xml.sax.Locator;

import com.yukthitech.ccg.xml.reserved.BeanNodeHandler;
import com.yukthitech.ccg.xml.reserved.ElementNodeHandler;
import com.yukthitech.ccg.xml.reserved.EntryNodeHandler;
import com.yukthitech.ccg.xml.reserved.IReserveNodeHandler;
import com.yukthitech.ccg.xml.reserved.IncludeXmlNodeHandler;
import com.yukthitech.ccg.xml.reserved.JsonNodeHandler;
import com.yukthitech.ccg.xml.reserved.NodeName;
import com.yukthitech.ccg.xml.reserved.RegisterNodeHandler;
import com.yukthitech.ccg.xml.util.StringUtil;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;
import com.yukthitech.ccg.xml.util.ValueProvider;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Default Parser handler to convert xml into beans.
 */
public class DefaultParserHandler implements IParserHandler
{
	/**
	 * Pattern used by values to refer to other beans.
	 */
	private static final Pattern REF_VALUE_PATTERN = Pattern.compile("\\s*ref\\s*\\:\\s*(\\w+)\\s*");
	
	/** The Constant ATTR_PATH. */
	public static final String ATTR_PATH = "path";

	/** The Constant ATTR_BEAN_TYPE. */
	public static final String ATTR_BEAN_TYPE = "beanType";

	/** The Constant ATTR_DATE_FORMAT. */
	public static final String ATTR_DATE_FORMAT = "dateFormat";

	/** The Constant ATTR_PARAMTER_TYPES. */
	public static final String ATTR_PARAMTER_TYPES = "paramTypes";

	/** The Constant ATTR_PARAMTERS. */
	public static final String ATTR_PARAMTERS = "params";

	/**
	 * Can be used to refer to a constant by using expressions like
	 * 		fully qualified class name.field name
	 * 
	 * In same way (using dot as delimiter) it can refer to nested public methods or fields.
	 */
	public static final String ATTR_BEAN_EXPRESSION = "beanExpr";

	/** The Constant ATTR_TRIM_LINES. */
	public static final String ATTR_TRIM_LINES = "trimLines";

	/** The Constant RNODE_BEAN_FACTORY. */
	public static final String RNODE_BEAN_FACTORY = "factory";

	/** The Constant ATTR_RNODE_TYPE. */
	public static final String ATTR_RNODE_TYPE = "type";

	/** The Constant ATTR_RNODE_NAME. */
	public static final String ATTR_RNODE_NAME = "name";

	/** The Constant ATTR_RNODE_VALUE. */
	public static final String ATTR_RNODE_VALUE = "value";

	/** The Constant ATTR_BEAN_ID. */
	public static final String ATTR_BEAN_ID = "beanId";

	/** The Constant ATTR_REF_BEAN. */
	public static final String ATTR_REF_BEAN = "beanRef";

	/** The Constant RNODE_INIT. */
	public static final String RNODE_INIT = "init";

	/** The Constant RNODE_CONSTANT. */
	public static final String RNODE_CONSTANT = "constant";

	/** The Constant STD_DATE_FORMAT. */
	public static final String STD_DATE_FORMAT = "MM/dd/yyyy";

	/** The Constant RNODE_EXPR_PATTERN. */
	public static final String RNODE_EXPR_PATTERN = "exprPattern";

	/** The Constant ATTR_PATTERN. */
	public static final String ATTR_PATTERN = "pattern";

	/** The Constant ATTR_ESCAPE_PREFIX. */
	public static final String ATTR_ESCAPE_PREFIX = "escapePrefix";

	/** The Constant ATTR_ESCAPE_REPLACE. */
	public static final String ATTR_ESCAPE_REPLACE = "escapeReplace";

	/** The Constant ATTR_ENABLED. */
	public static final String ATTR_ENABLED = "enabled";
	
	/**
	 * Encapsulation of pattern and handler details.
	 * @author akiran
	 */
	private static class ReserveNodeHandlerDetails
	{
		/**
		 * Node name pattern for which associated handler.
		 */
		private Pattern pattern;
		
		/**
		 * Handler to be used.
		 */
		private IReserveNodeHandler reserveNodeHandler;

		/**
		 * Instantiates a new reserve node handler details.
		 *
		 * @param pattern the pattern
		 * @param reserveNodeHandler the reserve node handler
		 */
		public ReserveNodeHandlerDetails(Pattern pattern, IReserveNodeHandler reserveNodeHandler)
		{
			this.pattern = pattern;
			this.reserveNodeHandler = reserveNodeHandler;
		}
	}
	

	/** The root. */
	private Object root;

	/** The date format. */
	private String dateFormat = STD_DATE_FORMAT;

	/** The type to fact cls. */
	private HashMap<Class<?>, Class<?>> typeToFactCls = new HashMap<Class<?>, Class<?>>();

	/** The cls to fact inst. */
	private HashMap<Class<?>, BeanFactory> clsToFactInst = new HashMap<Class<?>, BeanFactory>();

	/** The obj id to obj. */
	private HashMap<String, Object> objIdToObj = new HashMap<String, Object>();

	/** The class loader. */
	private ClassLoader classLoader;

	/** The constant map. */
	private Map<String, String> constantMap = new HashMap<String, String>();

	/** The parser. */
	private XMLBeanParser parser;

	/** The expression pattern. */
	private Pattern expressionPattern = Pattern.compile("\\$+\\{([.[^\\}]]+)\\}");

	/** The escape prefix. */
	private String escapePrefix = "$$";

	/** The escape replace. */
	private String escapeReplace = "$";

	/** The expression enabled. */
	private boolean expressionEnabled = true;

	/**
	 * List of reserve node handlers.
	 */
	private List<ReserveNodeHandlerDetails> reserveNodeHandlers = new ArrayList<ReserveNodeHandlerDetails>();
	
	/**
	 * Sax locator of the parser.
	 */
	protected Locator saxLocator;
	
	/**
	 * Flag to control if white-spaces should be retained while getting text content of node.
	 */
	private boolean retainWhiteSpacesEnabled = false;

	/**
	 * If the root is null, then reserve attribute beanType is mandatory in root
	 * node.
	 */
	public DefaultParserHandler()
	{
		registerReserveNodeHandler(new ElementNodeHandler());
		registerReserveNodeHandler(new EntryNodeHandler());
		registerReserveNodeHandler(new IncludeXmlNodeHandler());
		registerReserveNodeHandler(new RegisterNodeHandler());
		registerReserveNodeHandler(new BeanNodeHandler());
		registerReserveNodeHandler(new JsonNodeHandler());
	}

	/**
	 * Instantiates a new default parser handler.
	 *
	 * @param classLoader
	 *            the class loader
	 */
	public DefaultParserHandler(ClassLoader classLoader)
	{
		this();
		this.classLoader = classLoader;
	}
	
	public void setRetainWhiteSpacesEnabled(boolean retainWhiteSpacesEnabled)
	{
		this.retainWhiteSpacesEnabled = retainWhiteSpacesEnabled;
	}
	
	public boolean isRetainWhiteSpacesEnabled()
	{
		return retainWhiteSpacesEnabled;
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.ccg.xml.IParserHandler#setLocator(org.xml.sax.Locator)
	 */
	@Override
	public void setLocator(Locator locator)
	{
		this.saxLocator = locator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ccg.xml.ParserHandler#setParser(com.yukthitech.ccg.xml.
	 * XMLBeanParser)
	 */
	public void setParser(XMLBeanParser parser)
	{
		this.parser = parser;
	}
	
	/**
	 * Enables/disables expression parsing as specified.
	 * @param expressionEnabled
	 */
	public void setExpressionEnabled(boolean expressionEnabled)
	{
		this.expressionEnabled = expressionEnabled;
	}

	/**
	 * Loads the date format. This method is spearated so that is can be used in
	 * setRootBean() and createRootBean().
	 * 
	 * @param att
	 */
	private void loadHandlerConfigurationData(XMLAttributeMap att)
	{
		if(att.containsReservedKey(ATTR_DATE_FORMAT))
		{
			try
			{
				String dateFormat = att.getReserved(ATTR_DATE_FORMAT, "");
				if(dateFormat.trim().length() > 0)
				{
					new SimpleDateFormat(dateFormat);// just to make sure format
														// specified is proper
					this.dateFormat = dateFormat;
				}
			} catch(Exception ex)
			{
				throw new IllegalArgumentException("Invalid date format specified in " + ATTR_DATE_FORMAT + " attribute: " + dateFormat);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ccg.xml.ParserHandler#setRootBean(com.yukthitech.ccg.xml.BeanNode,
	 * com.yukthitech.ccg.xml.XMLAttributeMap)
	 */
	@Override
	public void setRootBean(BeanNode node, XMLAttributeMap att)
	{
		this.root = node.getActualBean();
		loadHandlerConfigurationData(att);
		// BeanParserSession.createSession(root);
	}
	
	@Override
	public Object getRootBean()
	{
		return root;
	}

	/*
	 * (non-Javadoc) A bean is created using beanType. If root bean is not
	 * specified in XMLBeanParser parse() method then beanType is mandatory
	 * argument. <BR> If paramTypes, params are specified, they will be used to
	 * make constructor call. <BR> If dateFormat is specified, then the same
	 * will be used as date format for parsing date properties. <BR>
	 * 
	 * @see com.yukthitech.ccg.xml.ParserHandler#createRootBean(com.yukthitech.ccg.xml.
	 * BeanNode, com.yukthitech.ccg.xml.XMLAttributeMap)
	 */
	@Override
	public Object createRootBean(BeanNode node, XMLAttributeMap att)
	{
		if(root == null)
		{
			String beanType = (String) att.getReserved(ATTR_BEAN_TYPE);
			
			if(beanType == null)
			{
				return new DynamicBean();
			}
			
			String params = (String) att.getReserved(ATTR_PARAMTERS);
			String paramTypes = (String) att.getReserved(ATTR_PARAMTER_TYPES);
			root = createBean(null, beanType, paramTypes, params, dateFormat, classLoader, objIdToObj);
		}

		loadHandlerConfigurationData(att);
		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ccg.xml.ParserHandler#createBean(com.yukthitech.ccg.xml.BeanNode,
	 * com.yukthitech.ccg.xml.XMLAttributeMap)
	 */
	@Override
	public Object createBean(BeanNode node, XMLAttributeMap att)
	{
		return createBean(node, att, classLoader);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ccg.xml.ParserHandler#getDynamicBeanType(com.yukthitech.ccg.xml.
	 * BeanNode, com.yukthitech.ccg.xml.XMLAttributeMap)
	 */
	@Override
	public Class<?> getDynamicBeanType(BeanNode node, XMLAttributeMap att)
	{
		String beanType = att.getReserved(ATTR_BEAN_TYPE, null);

		if(beanType == null)
			return null;

		Class<?> btype = null;
		ClassLoader loader = classLoader;

		if(loader == null)
			loader = this.getClass().getClassLoader();

		try
		{
			btype = Class.forName(beanType, true, loader);
		} catch(Exception ex)
		{
			throw new IllegalStateException("Invalid bean type specified: " + beanType);
		}

		return btype;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ccg.xml.ParserHandler#parseTextNodeValue(com.yukthitech.ccg.xml.
	 * BeanNode, com.yukthitech.ccg.xml.XMLAttributeMap)
	 */
	public Object parseTextNodeValue(BeanNode node, XMLAttributeMap att)
	{
		String expr = att.getReserved(ATTR_BEAN_EXPRESSION, null);

		// if expression is specified ignore text value
		if(expr != null)
		{
			return getExpressionObject(expr);
		}
		
		Object refVal = getReferenceValue(node, att);
		
		if(refVal != null)
		{
			return refVal;
		}

		Object value = XMLUtil.parseAttributeObject(node.getText(), node.getType(), dateFormat);

		if(value instanceof String)
		{
			boolean trimLines = "true".equalsIgnoreCase(att.getReserved(ATTR_TRIM_LINES, null));

			if(!trimLines)
				return value;

			String str = (String) value;
			StringTokenizer st = new StringTokenizer(str, "\n");
			StringBuilder finalVal = new StringBuilder();

			while(st.hasMoreTokens())
			{
				finalVal.append(st.nextToken().trim());

				if(st.hasMoreTokens())
					finalVal.append("\n");
			}

			return finalVal.toString();
		}

		return value;
	}
	
	private Object getReferenceValue(BeanNode node, XMLAttributeMap att)
	{
		String objId = att.getReserved(ATTR_REF_BEAN, null);

		if(objId == null)
		{
			return null;
		}
		
		Object obj = objIdToObj.get(objId);

		if(obj == null)
			throw new IllegalStateException("No ID Bean found in cache with id: " + objId);

		if(obj instanceof FutureValue)
		{
			obj = ((FutureValue) obj).getValue();
		}

		if(!node.getType().isAssignableFrom(obj.getClass()))
		{
			throw new IllegalStateException("Node required type \"" + node.getType() + "\" " + "is not compatible with the specified object-id object type: " + obj.getClass().getName());
		}

		return obj;
	}

	/**
	 * Creates the bean.
	 *
	 * @param node
	 *            the node
	 * @param att
	 *            the att
	 * @param loader
	 *            the loader
	 * @return the object
	 */
	public Object createBean(BeanNode node, XMLAttributeMap att, ClassLoader loader)
	{
		Object refVal = getReferenceValue(node, att);
		
		if(refVal != null)
		{
			return refVal;
		}
		
		String beanType = att.getReserved(ATTR_BEAN_TYPE, null);
		Class<?> btype = null;
		String beanId = att.getReserved(ATTR_BEAN_ID, null);

		if(beanId != null && objIdToObj.containsKey(beanId))
		{
			throw new IllegalStateException("Duplicate bean-id encouneted: " + beanId);
		}

		if(loader == null)
			loader = this.getClass().getClassLoader();

		if(beanType != null)
		{
			try
			{
				btype = Class.forName(beanType, true, loader);
			} catch(Exception ex)
			{
			}
		}

		if(typeToFactCls.containsKey(node.getType()) || typeToFactCls.containsKey(btype))
		{
			Class<?> beanTypeCls = typeToFactCls.containsKey(btype) ? btype : node.getType();
			BeanFactory factory = getBeanFactory(beanTypeCls);

			btype = (btype == null) ? node.getType() : btype;
			Object bean = factory.buildBean(btype, node);

			if(bean != BeanFactory.SKIP_TO_NORMAL)
			{
				if(beanId != null)
				{
					objIdToObj.put(beanId, bean);
				}

				return bean;
			}
		}

		String params = att.getReserved(ATTR_PARAMTERS, null);
		String paramTypes = att.getReserved(ATTR_PARAMTER_TYPES, null);

		if(paramTypes != null && params == null)
			throw new IllegalStateException("\"" + ATTR_PARAMTERS + "\" is mandatory attribute when \"" + ATTR_PARAMTER_TYPES + "\" is used.");

		String expr = att.getReserved(ATTR_BEAN_EXPRESSION, null);
		Object bean = null;

		// if expression is specified ignore bean type
		if(expr != null)
			bean = getExpressionObject(expr);
		else
			bean = createBean(node.getType(), beanType, paramTypes, params, dateFormat, loader, objIdToObj);

		if(beanId != null)
		{
			objIdToObj.put(beanId, bean);
		}

		return bean;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ccg.xml.ParserHandler#createAttributeBean(com.yukthitech.ccg.xml.
	 * BeanNode, java.lang.String, java.lang.Class)
	 */
	@Override
	public Object createAttributeBean(BeanNode node, String attName, Class<?> type)
	{
		if(typeToFactCls.containsKey(type))
		{
			BeanFactory factory = getBeanFactory(type);
			Object bean = factory.buildAttributeBean(type, node, attName);

			if(bean != BeanFactory.SKIP_TO_NORMAL)
				return bean;
		}

		return NOT_SUPPORTED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ccg.xml.ParserHandler#validateBean(com.yukthitech.ccg.xml.
	 * BeanNode)
	 */
	@Override
	public void validateBean(BeanNode node) throws ValidateException
	{
		Object bean = node.getActualBean();

		if(bean instanceof Validateable)
			((Validateable) bean).validate();

		if(bean == root)// end of processing
		{
			BeanFactory fact = null;
			Iterator<BeanFactory> it = clsToFactInst.values().iterator();
			while(it.hasNext())
			{
				fact = it.next();
				fact.finalize();
			}
			// BeanParserSession.destroyCurrentSession();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ccg.xml.ParserHandler#processReservedNode(com.yukthitech.ccg.xml.
	 * BeanNode, com.yukthitech.ccg.xml.XMLAttributeMap)
	 */
	@Override
	public Object processReservedNode(BeanNode node, XMLAttributeMap att)
	{
		if(RNODE_BEAN_FACTORY.equals(node.getName()))
		{
			String factName = att.get(ATTR_RNODE_NAME, null);
			String typeName = att.get(ATTR_RNODE_TYPE, null);
			if(factName == null)
				throw new IllegalArgumentException("Name is manadatory attribute in <factory> node.");

			if(typeName == null)
				throw new IllegalArgumentException("Type is manadatory attribute in <factory> node.");

			try
			{
				Class<?> factCls = Class.forName(factName);
				Class<?> typeCls = Class.forName(typeName);
				addBeanFactory(typeCls, factCls);
			} catch(Exception ex)
			{
				throw new IllegalStateException("Invalid types mentioned in <factory>: " + factName + "," + typeName, ex);
			}

			return null;
		}

		if(RNODE_INIT.equals(node.getName()))
		{
			String typeName = att.get(ATTR_RNODE_TYPE, null);

			if(typeName == null)
				throw new IllegalArgumentException("Type is manadatory attribute in <init> node.");

			try
			{
				Class.forName(typeName);
			} catch(Exception ex)
			{
				throw new IllegalStateException("Invalid type mentioned in <init>: " + typeName, ex);
			}

			return null;
		}

		if(RNODE_CONSTANT.equals(node.getName()))
		{
			String name = att.get(ATTR_RNODE_NAME, null);
			String value = att.get(ATTR_RNODE_VALUE, null);

			if(name == null)
			{
				throw new IllegalStateException("Mandatory attribute \"name\" is missing in node: " + RNODE_CONSTANT);
			}

			if(value == null)
			{
				throw new IllegalStateException("Mandatory attribute \"value\" is missing in node: " + RNODE_CONSTANT);
			}

			constantMap.put(name, value);
			return null;
		}

		if(RNODE_EXPR_PATTERN.equals(node.getName()))
		{
			this.expressionEnabled = "true".equalsIgnoreCase(att.get(ATTR_ENABLED, "true"));

			if(!expressionEnabled)
			{
				return null;
			}

			setExpressionPattern(att.get(ATTR_PATTERN, "\\$+\\{([.[^\\}]]+)\\}"), att.get(ATTR_ESCAPE_PREFIX, "$$"), att.get(ATTR_ESCAPE_REPLACE, "$"));
			return null;
		}

		String nodeName = node.getName();
		
		for(ReserveNodeHandlerDetails reserveNodeHandler : this.reserveNodeHandlers)
		{
			if(!reserveNodeHandler.pattern.matcher(nodeName).matches())
			{
				continue;
			}
			
			return reserveNodeHandler.reserveNodeHandler.createCustomNodeBean(this, node, att, saxLocator);
		}

		return null;
	}
	
	public boolean isValidReserverNode(String nodeName)
	{
		for(ReserveNodeHandlerDetails reserveNodeHandler : this.reserveNodeHandlers)
		{
			if(reserveNodeHandler.pattern.matcher(nodeName).matches())
			{
				return true;
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ccg.xml.ParserHandler#getBeanDescription(java.lang.Object)
	 */
	@Override
	public String getBeanDescription(Object bean)
	{
		return bean.getClass().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ccg.xml.ParserHandler#getNodeDescription(com.yukthitech.ccg.xml.
	 * BeanNode, com.yukthitech.ccg.xml.XMLAttributeMap)
	 */
	@Override
	public String getNodeDescription(BeanNode node, XMLAttributeMap att)
	{
		/*
		 * There are default reserve nodes. So we should not just skip it
		 * 
		 * And the mechanism to skip should not be based on description
		 */
		// if(node.isReserved())
		// return BeanNode.SKIP_NODE_ELEMENT;

		String firstKey = att.getFirstKey(false);

		if(firstKey == null)
			return node.getName();

		StringBuilder builder = new StringBuilder(node.getName());
		builder.append("(").append(firstKey).append("=").append(att.get(firstKey)).append(")");
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ccg.xml.ParserHandler#getDateFormat()
	 */
	@Override
	public String getDateFormat()
	{
		return dateFormat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ccg.xml.ParserHandler#processReserveNodeEnd(com.yukthitech.ccg.xml
	 * .BeanNode, com.yukthitech.ccg.xml.XMLAttributeMap)
	 */
	@Override
	public void processReserveNodeEnd(BeanNode node, XMLAttributeMap att)
	{
		String nodeName = node.getName();
		
		for(ReserveNodeHandlerDetails reserveNodeHandler : this.reserveNodeHandlers)
		{
			if(!reserveNodeHandler.pattern.matcher(nodeName).matches())
			{
				continue;
			}
			
			reserveNodeHandler.reserveNodeHandler.handleCustomNodeEnd(this, node, att, saxLocator);
		}
	}

	/**
	 * This method is expected to be used by child classes to change date format
	 * as per the needs. <BR>
	 * 
	 * @param dateFormat
	 *            New date format.
	 */
	public void setDateFormat(String dateFormat)
	{
		if(dateFormat == null || dateFormat.length() <= 0)
			throw new IllegalArgumentException("Date format cannot be null or empty.");
		new SimpleDateFormat(dateFormat);// just to make sure format specified
											// is proper
		this.dateFormat = dateFormat;
	}

	/**
	 * Adds the bean factory.
	 *
	 * @param type
	 *            the type
	 * @param factCls
	 *            the fact cls
	 */
	public void addBeanFactory(Class<?> type, Class<?> factCls)
	{
		if(type == null)
			throw new NullPointerException("Type can not be null.");

		if(factCls == null)
		{
			typeToFactCls.remove(type);
			return;
		}

		typeToFactCls.put(type, factCls);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ccg.xml.ParserHandler#getBeanFactory(java.lang.Class)
	 */
	@Override
	public BeanFactory getBeanFactory(Class<?> type)
	{
		Class<?> factCls = typeToFactCls.get(type);

		if(factCls == null)
			return null;

		BeanFactory fact = clsToFactInst.get(factCls);

		if(fact == null)
		{
			try
			{
				fact = (BeanFactory) factCls.newInstance();
				clsToFactInst.put(factCls, fact);
			} catch(Exception ex)
			{
				throw new InvalidStateException("Error in initiating bean factory: " + factCls.getName(), ex);
			}
		}

		return fact;
	}

	/**
	 * Creates the bean.
	 *
	 * @param node
	 *            the node
	 * @param loader
	 *            the loader
	 * @return the object
	 */
	public static Object createBean(BeanNode node, ClassLoader loader, Map<String, Object> refBeans)
	{
		XMLAttributeMap att = node.getAttributeMap();
		String beanType = att.getReserved(ATTR_BEAN_TYPE, null);

		String params = att.getReserved(ATTR_PARAMTERS, null);
		String paramTypes = att.getReserved(ATTR_PARAMTER_TYPES, null);

		if(paramTypes != null && params == null)
			throw new IllegalStateException("\"" + ATTR_PARAMTERS + "\" is mandatory attribute when \"" + ATTR_PARAMTER_TYPES + "\" is used.");

		String expr = att.getReserved(ATTR_BEAN_EXPRESSION, null);
		// if expression is specified ignore bean type
		if(expr != null)
			return getExpressionObject(expr);
		return createBean(node.getType(), beanType, paramTypes, params, null, loader, refBeans);
	}

	/**
	 * Creates the bean.
	 *
	 * @param type
	 *            the type
	 * @param preferredType
	 *            the preferred type
	 * @param paramTypes
	 *            the param types
	 * @param paramValues
	 *            the param values
	 * @param dateFormat
	 *            the date format
	 * @param loader
	 *            the loader
	 * @return the object
	 */
	public static Object createBean(Class<?> type, String preferredType, String paramTypes, String paramValues, String dateFormat, ClassLoader loader, Map<String, Object> refBeans)
	{
		if(preferredType != null)
		{
			try
			{
				Class<?> cls = null;
				if(loader != null)
					cls = Class.forName(preferredType, true, loader);
				else
					cls = Class.forName(preferredType);

				if(type != null && !type.isAssignableFrom(cls))
					throw new UnsupportedDataTypeException("Specified class \"" + cls.getName() + "\" is not assignable to \"" + type.getName() + "\"");
				type = cls;
			} catch(ClassNotFoundException ex)
			{
				throw new UnsupportedDataTypeException("Invalid class name specified: " + preferredType);
			}
		}
		else
		{
			if(type == null)
				throw new IllegalArgumentException("Either of type or preferredType is mandatory.");
			
			try
			{
				if(loader != null)
					type = Class.forName(type.getName(), true, loader);
			} catch(ClassNotFoundException ex)
			{
				throw new UnsupportedDataTypeException("Failed to load class: " + type.getName() + " using class loader: " + loader);
			}
		}

		if(paramTypes != null && paramTypes.length() > 0)
		{
			Constructor<?> cons = null;
			String types[] = StringUtil.tokenize(paramTypes, ",", false, true);
			Object values[] = StringUtil.tokenize(paramValues, ",", false, true);
			Object actValues[] = new Object[values.length];

			if(types.length != values.length)
				throw new IllegalStateException("Number of types and values are not matching.");

			Class<?> paramTypeCls[] = new Class[types.length];
			int i = 0;
			try
			{
				for(i = 0; i < paramTypeCls.length; i++)
				{
					paramTypeCls[i] = CommonUtils.getClass(types[i]);
				}
			} catch(UnsupportedOperationException ex)
			{
				throw ex;
			} catch(Exception ex)
			{
				throw new InvalidStateException("Invalid parameter type specified: " + types[i], ex);
			}

			try
			{
				cons = type.getConstructor(paramTypeCls);
			} catch(Exception ex)
			{
			}

			if(cons == null)
				throw new IllegalStateException("No constructor found in class \"" + type.getName() + "\" with arguments: " + paramTypes);

			Matcher matcher = null;
			
			for(i = 0; i < values.length; i++)
			{
				matcher = REF_VALUE_PATTERN.matcher((String) values[i]);
				
				if(matcher.matches())
				{
					actValues[i] = refBeans.get(matcher.group(1));
					continue;
				}
				
				actValues[i] = XMLUtil.parseAttributeObject((String) values[i], paramTypeCls[i], dateFormat);
			}

			try
			{
				return cons.newInstance(actValues);
			} catch(Exception ex)
			{
				throw new InvalidStateException("Error in creating bean of type: " + type.getName() + "\nRootCause: " + ex, ex);
			}
		}
		
		//Handle common collection types
		if(Collection.class.isAssignableFrom(type))
		{
			if(type.isAssignableFrom(ArrayList.class))
			{
				type = ArrayList.class;
			}
			else if(type.isAssignableFrom(HashSet.class))
			{
				type = HashSet.class;
			}
		}

		if(Map.class.isAssignableFrom(type))
		{
			if(type.isAssignableFrom(HashMap.class))
			{
				type = HashMap.class;
			}
			else if(type.isAssignableFrom(TreeMap.class))
			{
				type = TreeMap.class;
			}
		}

		try
		{
			return type.newInstance();
		} catch(Exception ex)
		{
			throw new InvalidStateException("Error in creating bean of type: " + type.getName() + "\nRootCause: " + ex, ex);
		}
	}

	/**
	 * Gets the object represnted by the specified static field expression.
	 * 
	 * @param expression
	 *            static field expression.
	 * @return Object represented by static field expression.
	 */
	public static Object getExpressionObject(String expression)
	{
		String tokens[] = StringUtil.tokenize(expression, ".", false, true);
		Class<?> cls = null;
		Class<?> tmpCls = null;
		String tmpExpr = null;
		int idx = 0;

		MAIN: for(; idx < tokens.length; idx++)
		{
			try
			{
				if(tmpExpr != null)
					tmpExpr += "." + tokens[idx];
				else
					tmpExpr = tokens[idx];
				/*
				 * Even if class is found after cetain tokens, we need to
				 * stillcontinue iterations in order to consider inner classes.
				 */
				tmpCls = Class.forName(tmpExpr);
				cls = tmpCls;
			} catch(Exception ex)
			{
				if(cls != null)// if previous iteration resulted in class
				{
					Class<?> innerCls[] = cls.getClasses();

					if(innerCls == null || innerCls.length == 0)
						break;

					for(Class<?> icls : innerCls)
					{
						if(tokens[idx].equals(icls.getSimpleName()))
						{
							cls = icls;
							continue MAIN;
						}
					}

					break;
				}
			}
		}

		if(cls == null)
			throw new InvalidStateException("Failed to evaluation static object expression: " + expression);

		if(idx >= tokens.length)
			throw new InvalidStateException("Specified expression indicates a class not a static object: " + expression);

		Field fld = null;
		Object res = null;
		Method method = null;

		for(; idx < tokens.length; idx++)
		{
			try
			{
				try
				{
					fld = cls.getField(tokens[idx]);
					res = fld.get(res);
				} catch(NoSuchFieldException ex)
				{
					method = cls.getMethod(tokens[idx]);
					res = method.invoke(res);
				}

				cls = res.getClass();
			} catch(Exception ex)
			{
				if(res == null)
					throw new InvalidStateException("Failed to fetch field/method \"" + tokens[idx] + "\" value from class " + cls.getName() + " for expression: " + expression, ex);

				throw new InvalidStateException("Failed to fetch instance field/method \"" + tokens[idx] + "\" value from class " + cls.getName() + " for expression: : " + expression, ex);
			}
		}
		return res;
	}

	/**
	 * Gets the parser.
	 *
	 * @return the parser
	 */
	public XMLBeanParser getParser()
	{
		return parser;
	}

	/**
	 * Stop processing.
	 */
	public void stopProcessing()
	{
		parser.stopProcessing();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ccg.xml.ParserHandler#getConstantValue(java.lang.String)
	 */
	@Override
	public String getConstantValue(String name)
	{
		String value = constantMap.get(name);
		int index = 0;

		if((index = name.indexOf(".")) > 0)
		{
			if(name.startsWith("environment.") && name.length() > index)
			{
				name = name.substring(index + 1);
				return System.getenv(name);
			}

			if(name.startsWith("systemProperty.") && name.length() > index)
			{
				name = name.substring(index + 1);
				return System.getProperty(name);
			}

			Object valBean = getExpressionObject(name);

			if(valBean != null)
			{
				value = valBean.toString();
			}
		}

		return value;
	}

	/**
	 * This method will be called by passing the text of attribute and node-text. This method
	 * should replace all the expressions and return final output.
	 * 
	 * Default parser handler by default, replaces ${} expressions by using root-bean as context. 
	 * The default pattern and its behaviour can be changed by using {@link #setExpressionPattern(String, String, String)}
	 */ 
	@Override
	public String processText(final Object rootBean, String text)
	{
		if(!expressionEnabled)
		{
			return text;
		}

		ValueProvider valueProvider = new ValueProvider()
		{
			@Override
			public Object getValue(String name)
			{
				if(name.startsWith("#"))
				{
					return getConstantValue(name.substring(1));
				}

				try
				{
					// return CCGUtility.invokeGetProperty(rootBean, name, null,
					// false);
					return "" + PropertyUtils.getProperty(rootBean, name);
				} catch(Exception ex)
				{
					throw new IllegalStateException("An error occurred while invoking property " + name + " on bean " + rootBean.getClass().getName(), ex);
				}
			}
		};

		return StringUtil.getPatternString(text, valueProvider, expressionPattern, escapePrefix, escapeReplace);
	}
	
	/**
	 * Register reserve node handler.
	 *
	 * @param handler the handler
	 */
	@Override
	public void registerReserveNodeHandler(IReserveNodeHandler handler)
	{
		NodeName nodeName = handler.getClass().getAnnotation(NodeName.class);
		
		if(nodeName == null)
		{
			throw new InvalidArgumentException("Specified handler type '{}' does not have @NodeName annotation", handler.getClass().getName());
		}
		
		Pattern pattern = Pattern.compile(nodeName.namePattern());
		this.reserveNodeHandlers.add(new ReserveNodeHandlerDetails(pattern, handler));
	}

	/**
	 * Sets the expression pattern to be used for processing expressions within target xml.
	 *
	 * @param expressionPattern Pattern to be used to identify expressions. By default this is ${}. This pattern should include escape prefix also.
	 * 	By default $+{} is used as expression, in other terms multiple $ can be used in expressions. If more than one $ is found it is considered to be escaped
	 *  and will not be processed.  
	 * @param escapePrefix In the match, when expression should be considered as escaped. By default, this value is $$. That means if expression starts with 
	 * 	more than one $ it is considered to be escaped.
	 * @param escapeReplace In escaped match, what string should be used as replacement for escaped string. By default this is $. That means for escaped
	 *  string first $$ of escaped expression will be replaced by $.
	 */
	public void setExpressionPattern(String expressionPattern, String escapePrefix, String escapeReplace)
	{
		this.expressionPattern = Pattern.compile(expressionPattern);
		this.escapePrefix = escapePrefix;
		this.escapeReplace = escapeReplace;
	}
	
	@Override
	public void registerBean(String id, Object bean)
	{
		objIdToObj.put(id, bean);
	}
	
	@Override
	public Object getBean(String id)
	{
		return objIdToObj.get(id);
	}
}
