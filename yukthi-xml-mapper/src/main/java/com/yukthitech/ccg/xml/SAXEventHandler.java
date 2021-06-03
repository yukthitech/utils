package com.yukthitech.ccg.xml;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.yukthitech.utils.ReflectionUtils;

/**
 * <P>
 * This class is SAX based event handler used by CCG XMLBeanParser.
 * </P>
 * 
 * @author Kranthi
 */
class SAXEventHandler extends DefaultHandler
{
	/**
	 * This class is used to hold methods of a class setters and adders
	 * separately <BR>
	 * 
	 * @author Kranthi
	 */
	private static class MethodList
	{
		// The keys are going to be the property name in upper case
		// For Eg.,
		// setTable() method will be on setters with key TABLE
		// addTable() method will be on adders with key TABLE
		HashMap<String, Method> setters = new HashMap<String, Method>();
		HashMap<String, Method> adders = new HashMap<String, Method>();
		HashMap<String, Method> idBased = new HashMap<String, Method>();
	}

	private BeanNode activeNode = null;
	// private Stack stack=new Stack();
	private HashMap<Class<?>, MethodList> clsToMethods = new HashMap<Class<?>, MethodList>();
	private boolean root = true;

	// factory on which methods will be called
	private IParserHandler parserHandler;

	// used for reference by XMLBeanParser
	private Object rootBean;

	private Stack<String> skipModeElements = new Stack<String>();
	private boolean stopProcessing = false;
	
	/**
	 * Locator set by sax parser used to find current location in document.
	 */
	private Locator saxLocator;

	/**
	 * @param factory
	 */
	public SAXEventHandler(IParserHandler factory, Object rootBean)
	{
		this.parserHandler = factory;
		this.rootBean = rootBean;
	}
	
	/**
	 * @return Root bean
	 */
	public Object getRootBean()
	{
		return rootBean;
	}

	private String processText(String text)
	{
		if(rootBean == null)
		{
			return text;
		}

		return parserHandler.processText(rootBean, text);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char chars[], int start, int len)
	{
		if(skipModeElements.size() > 0)
			return;

		String text = new String(chars, start, len);
		boolean whiteSpaces = (text.trim().length() == 0);

		// check if this method is getting called due to white spaces
		// generally white spaces are used for indentation in XML
		if(text.length() <= 0)
			return;

		if(activeNode.getActualBean() instanceof IHybridTextBean)
		{
			activeNode.appendText(text);
			return;
		}

		if(activeNode.isReserved() && !activeNode.isTextNode())
		{
			if(!activeNode.isReservedNullNode())
			{
				if(!whiteSpaces)
					throw new XMLLoadException("Text found in non-null reserved node: " + activeNode, activeNode, saxLocator);
				return;
			}

			if(whiteSpaces)
				return;

			activeNode.setTextNodeFlag(true);
		}
		
		if(Object.class.equals(activeNode.getActualType()))
		{
			activeNode.setTextNodeFlag(true);
		}

		// check if this method is called due to meta-text node
		if(!activeNode.isTextNode())
		{
			if(!whiteSpaces)
				throw new XMLLoadException("Meta-text node or text in non-text based node encountered.\n" + "Bean Type: " + activeNode.getActualBean().getClass().getName(), activeNode, saxLocator);
			return;
		}

		activeNode.appendText(text);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String name, String qname)
	{
		name = XMLUtil.replaceHyphens(name);
		
		if(skipModeElements.size() > 0)
		{
			skipModeElements.pop();
			return;
		}

		if(parserHandler.isWrapUri(uri))
		{
			return;
		}

		BeanNode activeNode = this.activeNode;
		this.activeNode = activeNode.getParentNode();

		Object curBean = activeNode.getActualBean();
		BeanNode parentNode = activeNode.getParentNode();
		String beanDesc = null;

		if(curBean instanceof IHybridTextBean)
		{
			if(activeNode.containsText())
			{
				((IHybridTextBean) curBean).addText(processText(activeNode.getActualText()));
				activeNode.clearText();
			}
		}

		if(!activeNode.isTextNode() && !activeNode.isReservedNullNode())
		{
			beanDesc = parserHandler.getBeanDescription(curBean);

			if(beanDesc == null)
				beanDesc = curBean.getClass().getName();
		}

		if(activeNode.isReserved())
		{
			if(activeNode.isReservedNullNode())
			{
				curBean = null;
			}
			//process the test in active reserve text node
			else if(activeNode.isTextNode())
			{
				String text = processText(activeNode.getText());
				activeNode.setText(text);
			}
			else
			{
				try
				{
					parserHandler.validateBean(activeNode);
				} catch(Exception ex)
				{
					throw new XMLLoadException("Error in validating reserved node bean: " + beanDesc, ex, activeNode, saxLocator);
				}
			}

			try
			{
				parserHandler.processReserveNodeEnd(activeNode, activeNode.getAttributeMap());
			} catch(Exception ex)
			{
				throw new XMLLoadException("Error in processing reserve node end: " + name, ex, activeNode, saxLocator);
			}
			return;
		}

		// validate the bean
		try
		{
			if(!activeNode.isTextNode())
				parserHandler.validateBean(activeNode);
		} catch(Exception ex)
		{
			throw new XMLLoadException("Error in validating bean: " + beanDesc, ex, activeNode, saxLocator);
		}

		if(parentNode == null)// if the root element is reached
			return;

		Object parentBean = parentNode.getActualBean();
		Method met = getSubnodeMethod(name, parentBean);
		Object parameters[] = null;
		String nodeType = null;
		beanDesc = parserHandler.getBeanDescription(parentBean);

		if(met == null && (parentBean instanceof DynamicDataAcceptor))
		{
			DynamicDataAcceptor acceptor = (DynamicDataAcceptor) parentBean;
			try
			{
				Object nodeValue = activeNode.isTextNode() ? processText(activeNode.getText()) : activeNode.getActualBean();

				if(activeNode.isIDBased())
					acceptor.add(activeNode.getName(), activeNode.getID(), nodeValue);
				else
					acceptor.add(activeNode.getName(), nodeValue);
			} catch(Exception ex)
			{
				throw new XMLLoadException("Failed to set dynamic property for bean \"" + beanDesc + "\" for node: " + name, ex, activeNode, saxLocator);
			}
			return;
		}

		if(activeNode.isIDBased())
		{
			Class<?> idType = met.getParameterTypes()[0];
			try
			{

				Object idValue = XMLUtil.parseAttributeObject( processText(activeNode.getID()), idType, parserHandler.getDateFormat());
				Object argValue = null;

				if(activeNode.isTextNode())
				{
					argValue = parserHandler.parseTextNodeValue(activeNode, activeNode.getAttributeMap());
					
					if(argValue instanceof String)
					{
						argValue = processText((String) argValue);
					}
					
					nodeType = "ID-Based Text Node";
				}
				else
				{
					argValue = curBean;
					nodeType = "ID-Based Bean Node";
				}

				parameters = new Object[] { idValue, argValue };

			} catch(Exception ex)
			{
				throw new XMLLoadException("Failed to invoke method \"" + ReflectionUtils.toString(met) + "\" in bean \"" + beanDesc + "\" for ID-based node: " + name, ex, activeNode, saxLocator);
			}
		}
		else if(activeNode.isTextNode())
		{
			try
			{
				String text = processText(activeNode.getText());
				activeNode.setText(text);

				parameters = new Object[] { parserHandler.parseTextNodeValue(activeNode, activeNode.getAttributeMap()) };
				nodeType = "Text Node";
			} catch(Exception ex)
			{
				throw new XMLLoadException("Failed to invoke method \"" + ReflectionUtils.toString(met) + "\" in bean \"" + beanDesc + "\" for text-based node: " + name, ex, activeNode, saxLocator);
			}
		}
		else // if normal node
		{
			parameters = new Object[] { curBean };
			nodeType = "Node";
		}

		try
		{
			met.invoke(parentNode.getActualBean(), parameters);
		} catch(Exception ex)
		{
			Throwable th = ex;
			
			if( (ex instanceof InvocationTargetException) && ex.getCause() != null)
			{
				th = ex.getCause();
			}

			String mssg = String.format("Failed to invoke method '%s' on bean '%s' with args %s. [Node: %s:%s]", ReflectionUtils.toString(met), beanDesc, Arrays.toString(parameters), nodeType, name); 
			
			throw new XMLLoadException(
					mssg, 
					th, activeNode, saxLocator);
		}

	}

	private BeanNode buildNewNode(String uri, String name, Attributes att)
	{
		BeanNode newNode = new BeanNode(uri, name, parserHandler);
		XMLAttributeMap attrMap = new XMLAttributeMap(att, parserHandler);
		newNode.setAttributeMap(attrMap);

		Set<String> attrs = attrMap.getKeySet(false);

		for(String key : attrs)
		{
			attrMap.put(key, processText((String) attrMap.get(key)), false);
		}

		attrs = attrMap.getKeySet(true);

		for(String key : attrs)
		{
			attrMap.put(key, processText((String) attrMap.getReserved(key)), true);
		}

		String desc = parserHandler.getNodeDescription(newNode, newNode.getAttributeMap());
		desc = (desc == null) ? name : desc;
		newNode.setDescription(desc);

		newNode.setParentNode(activeNode);
		activeNode = newNode;

		return newNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String name, String qname, Attributes att)
	{
		name = XMLUtil.replaceHyphens(name);
		
		if(skipModeElements.size() > 0)
		{
			skipModeElements.push(name);
			return;
		}

		if(parserHandler.isWrapUri(uri))
		{
			return;
		}

		Object curBean = (this.activeNode != null) ? this.activeNode.getActualBean() : null;

		if(curBean instanceof IHybridTextBean)
		{
			if(this.activeNode.containsText())
			{
				((IHybridTextBean) curBean).addText(this.activeNode.getActualText());
				this.activeNode.clearText();
			}
		}

		BeanNode newNode = buildNewNode(uri, name, att);

		if(root)
		{
			if(newNode.isReserved())
				throw new XMLLoadException("Root node can not be of reserved type.", newNode, saxLocator);

			if(newNode.isSkipNode())
				throw new XMLLoadException("Root node can not be skipped.", newNode, saxLocator);

			if(rootBean != null)
			{
				newNode.setBean(rootBean);
				parserHandler.setRootBean(newNode, newNode.getAttributeMap());
				setAttributeData(newNode);
				root = false;
				return;
			}

			rootBean = parserHandler.createRootBean(newNode, newNode.getAttributeMap());
			newNode.setBean(rootBean);
			setAttributeData(newNode);
			root = false;
			return;
		}

		if(newNode.isSkipNode())
		{
			activeNode = newNode.getParentNode();
			skipModeElements.push(name);
			return;
		}

		BeanNode parentNode = newNode.getParentNode();
		if(parentNode.isReservedNullNode())
			throw new XMLLoadException("Subnode encountered in non-bean reserved node.", newNode, saxLocator);

		/*
		 * If the previous node is assumed to be TextBasedNode, and since
		 * sub-node occured under it, it can not be text based node.
		 * 
		 * So, it should be converted into normal bean.
		 * 
		 * This situation may occur for supported mutable attribute type.
		 * Currently only java.util.Date falls in this category.
		 */
		if(parentNode.isTextNode())
		{
			if(parentNode.containsText())
			{
				if(parentNode.isIDBased())
					throw new XMLLoadException("Encountered hybrid-text nodes (while id-based node was expected) with name: " + name, newNode, saxLocator);

				throw new XMLLoadException("Encountered hybrid-text nodes with name: " + name, newNode, saxLocator);
			}

			Object parentBean = null;

			try
			{
				parentBean = parserHandler.createBean(parentNode, parentNode.getAttributeMap());// create
																							// the
																							// replacing
																							// bean
																							// which
																							// is
																							// suppose
				parentNode.setTextNodeFlag(false);
				parentNode.setBean(parentBean);
			} catch(Exception ex)
			{
				throw new XMLLoadException("Error in creating beanof type: " + parentNode.getType().getName(), ex, parentNode, saxLocator);
			}
			setAttributeData(parentNode);
		}

		Object nextBean = null;

		if(newNode.isReserved())// reserved node
		{
			try
			{
				nextBean = parserHandler.processReservedNode(newNode, newNode.getAttributeMap());
			} catch(Exception ex)
			{
				throw new XMLLoadException("Error in processing reserve node: " + name, ex, newNode, saxLocator);
			}
			
			if(newNode.isTextNode())
			{
				return;
			}

			if(nextBean == null)
			{
				nextBean = BeanNode.NULL_RESERVED_NODE;
			}
			
			newNode.setBean(nextBean);
			setAttributeData(newNode);
			return;
		}

		XMLAttributeMap curAttMap = newNode.getAttributeMap();
		boolean normalAtt = (!curAttMap.hasReserveAttributes() && curAttMap.size() > 0);

		// Determine the current node's bean type and create the bean
		Object parentBean = parentNode.getActualBean();
		Class<?> typ = getNodeSubelementType(name, parentBean, newNode);
		Class<?> dynType = null;

		try
		{
			dynType = parserHandler.getDynamicBeanType(newNode, curAttMap);
		} catch(Exception ex)
		{
			throw new XMLLoadException("Error in fetching dynamic type for node: " + name, ex, newNode, saxLocator);
		}

		if(typ != null && dynType != null)
		{
			if(!typ.isAssignableFrom(dynType))
				throw new XMLLoadException("There was a mismatch between expected and dynamic types for node: " + name + "\n" + "Expected Type: " + typ.getName() + "\n" + "Dynamic Type: " + dynType.getName(), newNode, saxLocator);
		}

		if(dynType == null)
			dynType = typ;

		if(typ == null)
		{
			Class<?> valType = getIDBasedType(name, parentBean);
			if(curAttMap.getNormalAttributeCount() == 1 && valType != null)
			{
				if(dynType == null)
					dynType = valType;

				loadIDBasedNode(newNode, curAttMap, valType, dynType);
				return;
			}

			if(parentBean instanceof DynamicDataAcceptor)
			{
				if(dynType == null)
				{
					dynType = String.class;
				}

				DynamicDataAcceptor acceptor = (DynamicDataAcceptor) parentBean;

				if(curAttMap.getNormalAttributeCount() == 1 && acceptor.isIdBased(newNode.getName()))
				{
					loadIDBasedNode(newNode, curAttMap, dynType, dynType);
					return;
				}

				typ = dynType;
			}
			else
			{
				String beanDesc = parserHandler.getBeanDescription(parentBean);
				if(beanDesc == null)
					beanDesc = parentBean.getClass().getName();

				throw new XMLLoadException("Can not find appropriate adder/setter method for sub-node \"" + name + "\" in bean type: " + beanDesc, newNode, saxLocator);
			}
		}

		newNode.setActualType(typ);
		newNode.setType(dynType);
		
		// if no non-reserve attributes are found then
		// assume the current bean to be text-node.
		// if any sub-node is encountered under this type of node then exception
		// will be thrown
		if(!normalAtt && XMLUtil.isSupportedAttributeClass(dynType))
		{
			// if true create text based object and keep it in stack
			// till it gets its text into it.
			newNode.setTextNodeFlag(true);
			return;
		}

		try
		{
			nextBean = parserHandler.createBean(newNode, curAttMap);
		} catch(Exception ex)
		{
			throw new XMLLoadException("Error in creating bean of type: " + typ.getName(), ex, newNode, saxLocator);
		}

		if(nextBean == null)
			throw new XMLLoadException("A null value is returned by the factory for attributed node: " + name, newNode, saxLocator);
		
		//if final bean type is found to be Object, then treat is as text node
		// so that enclosed text can be set as value
		if(Object.class.equals(nextBean.getClass()))
		{
			newNode.setTextNodeFlag(true);
			return;
		}
		
		// this will take care of throwing exception when factory returns null
		// for non-text object
		newNode.setBean(nextBean);
		
		if(nextBean instanceof IParentAware)
		{
			((IParentAware) nextBean).setParent(parentBean);
		}
		
		setAttributeData(newNode);
	}
	
	private void loadIDBasedNode(BeanNode newNode, XMLAttributeMap curAttMap, Class<?> valType, Class<?> dynType)
	{
		String attName = (String) curAttMap.getKeySet(false).toArray()[0];
		newNode.setID(curAttMap.get(attName, null));
		newNode.setActualType(valType);
		newNode.setType(dynType);

		if(XMLUtil.isSupportedAttributeClass(dynType) || Object.class.equals(dynType))
		{
			newNode.setTextNodeFlag(true);
		}
		else
		{
			Object nextBean = null;
			try
			{
				nextBean = parserHandler.createBean(newNode, curAttMap.getReservedMap());
			} catch(Exception ex)
			{
				throw new XMLLoadException("Error in creating bean of type: " + valType.getName(), ex, newNode, saxLocator);
			}

			if(nextBean == null)
				throw new XMLLoadException("A null value is returned by the factory for ID based bean node: " + newNode.getName(), newNode, saxLocator);
			// this will take care of throwing exception when factory returns
			// null for non-text object
			newNode.setBean(nextBean);
		}
	}

	/**
	 * This method tries to find approp seeter/adder method for the specified
	 * sub-node (might be text-node) in the parentBean class. <BR>
	 * First adder method will be searched if not found then a search will be
	 * made for setter method.
	 * 
	 * @param nodeName
	 *            Name of the node for which method should be searched
	 * @param parentBean
	 *            Bean under whose class method needs to be searched.
	 * @return Approp method thats needs to be used to set/add the bean/text to
	 *         parentBean.
	 */
	private Method getSubnodeMethod(String nodeName, Object parentBean)
	{
		nodeName = nodeName.toUpperCase();

		Class<?> cls = parentBean.getClass();
		MethodList metList = clsToMethods.get(cls);
		if(metList == null)
			metList = loadClassMethods(cls, clsToMethods);

		Method met = metList.adders.get(nodeName);// find adder method
		if(met == null)
			met = metList.setters.get(nodeName);// find setter method

		if(met == null)
			met = metList.idBased.get(nodeName);

		return met;
	}

	/**
	 * For each attribute in att, a matching setter will be searched in toBean
	 * and the value of the attribute(String) will be converted into respective
	 * type and will be sent as argument to the found setter method. The
	 * attribute values can be converted into String, primitive types, primitive
	 * wrapper classes or into java.util.Date class objects. For date objects
	 * factory.getDateFormat() will be used.
	 * 
	 * @param toBean
	 *            Bean to which attribute data should be loaded.
	 * @param att
	 *            Attributes which needs to be set to toBean
	 */
	private void setAttributeData(BeanNode newNode)
	{
		Object toBean = newNode.getActualBean();
		XMLAttributeMap att = newNode.getAttributeMap();

		if(toBean == BeanNode.NULL_RESERVED_NODE)
			return;

		if(att == null || att.size() <= 0)
			return;
		Class<?> type = toBean.getClass();
		MethodList metList = clsToMethods.get(type);

		if(metList == null)
			metList = loadClassMethods(type, clsToMethods);

		Method met = null;
		Class<?> argType = null;
		String attName = null;
		String attValue = null;
		Iterator<String> nameIterator = att.keySet().iterator();
		String beanDesc = parserHandler.getBeanDescription(toBean);

		if(beanDesc == null)
			beanDesc = toBean.getClass().getName();

		while(nameIterator.hasNext())
		{
			attName = nameIterator.next();

			if(att.containsReservedKey(attName))
				continue;

			attValue = (String) att.get(attName);
			met = metList.setters.get(attName.toUpperCase());
			
			if(met == null)
			{
				met = metList.adders.get(attName.toUpperCase());
			}

			if(met == null)
			{
				if(toBean instanceof IDynamicAttributeAcceptor)
				{
					((IDynamicAttributeAcceptor) toBean).set(attName, attValue);
					continue;
				}

				throw new XMLLoadException("Can not find appropriate setter method for attribute \"" + attName + "\" in bean type: " + beanDesc, newNode, saxLocator);
			}

			argType = met.getParameterTypes()[0];
			try
			{
				Object value = parserHandler.createAttributeBean(newNode, attName, argType);

				if(value == IParserHandler.NOT_SUPPORTED)
					value = XMLUtil.parseAttributeObject(attValue, argType, parserHandler.getDateFormat());

				met.invoke(toBean, new Object[] { value });
			} catch(Exception e)
			{
				throw new XMLLoadException("Failed to set attribute \"" + attName + "\" in bean of type: " + beanDesc, e, newNode, saxLocator);
			}
		}
	}

	/**
	 * For the subnode name under a node (which in turn represented by bean
	 * parent) appropriate setter/adder will be fetched and the type of the
	 * parameter will be returned. <BR>
	 * Thus the return type represents the bean type, which should be used to
	 * represent subnode.
	 * 
	 * @param name
	 *            Name of the subnode for which bean type needs to be
	 *            determined.
	 * @param parent
	 *            Bean representing the parent node of the node.
	 * @param newNode Node for which type is being determined. Used to set generic type for this node. 
	 * @return The type of bean that should be used to represent the node.
	 */
	private Class<?> getNodeSubelementType(String name, Object parent, BeanNode newNode)
	{
		name = name.toUpperCase();

		Class<?> cls = parent.getClass();
		MethodList metList = clsToMethods.get(cls);
		
		if(metList == null)
			metList = loadClassMethods(cls, clsToMethods);

		Method met = metList.adders.get(name);
		
		if(met == null)
		{
			met = metList.setters.get(name);
		
			if(met != null)
			{
				newNode.setGenericType(met.getGenericParameterTypes()[0]);
				return met.getParameterTypes()[0];
			}
			
			return null;
		}
		
		newNode.setGenericType(met.getGenericParameterTypes()[0]);
		return met.getParameterTypes()[0];
	}

	private Class<?> getIDBasedType(String name, Object parent)
	{
		name = name.toUpperCase();

		Class<?> cls = parent.getClass();
		MethodList metList = clsToMethods.get(cls);
		if(metList == null)
			metList = loadClassMethods(cls, clsToMethods);

		Method met = metList.idBased.get(name);

		if(met == null)
			return null;

		Class<?> valType = met.getParameterTypes()[1];

		return valType;
	}

	/**
	 * Fetches all the setters and adders of cls and creates an instance of
	 * MethodList with these fetched data and adds this MethodList object to the
	 * map with cls as the key. <BR>
	 * Thus from the second time, the map will be used as the reference to get
	 * MethodList for a particular class instead of using this method.
	 * 
	 * @param cls
	 *            Type from which the setter/adders needs to be loaded.
	 * @param map
	 *            Acts as reference to loaded classes.
	 * @return MethodList object representing Setters/adders of cls.
	 */
	private static MethodList loadClassMethods(Class<?> cls, HashMap<Class<?>, MethodList> map)
	{
		MethodList metList = new MethodList();
		Method mets[] = cls.getMethods();
		String name;
		Class<?> params[];
		for(int i = 0; i < mets.length; i++)
		{
			if(!Modifier.isPublic(mets[i].getModifiers()))
				continue;
			name = mets[i].getName();
			params = mets[i].getParameterTypes();
			// if plugin method
			if((params.length != 1 && params.length != 2) || name.length() <= 3)
				continue;

			if(params.length == 1)
			{
				if(name.startsWith("set"))
				{
					name = name.substring(3, name.length()).toUpperCase();
					metList.setters.put(name, mets[i]);
				}

				if(name.startsWith("add"))
				{
					name = name.substring(3, name.length()).toUpperCase();
					metList.adders.put(name, mets[i]);
				}
			}
			else
			{
				if(!name.startsWith("set") && !name.startsWith("add"))
					continue;

				if(XMLUtil.isSupportedAttributeClass(params[0]))
				{
					name = name.substring(3, name.length()).toUpperCase();
					metList.idBased.put(name, mets[i]);
					continue;
				}
			}
		}
		map.put(cls, metList);
		return metList;
	}

	public void stopProcessing()
	{
		stopProcessing = false;
	}

	@Override
	public void setDocumentLocator(Locator locator)
	{
		if(stopProcessing)
		{
			throw new InternalException();
		}
		
		this.saxLocator = locator;
		parserHandler.setLocator(saxLocator);
	}
	
	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException
	{
		return new InputSource(new StringReader(""));
	}

}
