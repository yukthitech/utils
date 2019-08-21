package com.yukthitech.ccg.xml;

import org.xml.sax.Locator;

import com.yukthitech.ccg.xml.reserved.IReserveNodeHandler;
import com.yukthitech.ccg.xml.util.ValidateException;

/**
 * <BR>
 * <P>
 * Handler interface (factory sum builder helper model) for the CCG Bean XML
 * parser. This interface is used to support customizable behaviour of CCG
 * parser and represents helper factory/builder model for XMLBeanParser.
 * </P>
 * <P>
 * This interface objects controls how the beans are created, how reserve
 * nodes/attributes to be treated, specifies descriptions for nodes and beans
 * which will be used for debugging purposes and also specified date format to
 * be used while parsing date objects.
 * </P>
 * <BR>
 * 
 * @author A. Kranthi Kiran
 */
public interface IParserHandler extends XMLConstants
{
	public static Object NOT_SUPPORTED = new Object();

	/**
	 * Sets the parser.
	 *
	 * @param parser
	 *            the new parser
	 */
	public void setParser(XMLBeanParser parser);
	
	/**
	 * Sets the sax locator of the parser.
	 * @param locator
	 */
	public void setLocator(Locator locator);

	/**
	 * This method will be called only the rootBean is specified in parse()
	 * method of XMLBeanParser.
	 * 
	 * @param node
	 *            Node to be processed
	 * @param att
	 *            node attributes
	 */
	public void setRootBean(BeanNode node, XMLAttributeMap att);
	
	/**
	 * Fetches root bean of the handler.
	 * @return Root bean
	 */
	public Object getRootBean();

	/**
	 * This is the first method that will be called and this method is expected
	 * to create the root bean and return the same. This method is called only
	 * if root bean is not specified in parse() of XMLBeanParser.
	 * 
	 * @param node
	 *            Node to be processed
	 * @param att
	 *            node attributes
	 * @return A bean representing the root node of the input XML data.
	 */
	public Object createRootBean(BeanNode node, XMLAttributeMap att);

	/**
	 * Whenever there is a node start, this method will be called and node name,
	 * all the attributes (as XMLAttributeMap) are passed as parameters to this
	 * method. Depending on the setter/adder that�s been chosen for this node,
	 * the type of bean being expected is also passed as parameter. This method
	 * is expected to return a bean representing the current node, whose type
	 * should be compatible/assignable to the type of bean specified as
	 * argument.
	 *
	 * @param node
	 *            Node to be processed
	 * @param att
	 *            node attributes
	 * @return A bean representing the current node and also which is compatible
	 *         with type.
	 */
	public Object createBean(BeanNode node, XMLAttributeMap att);

	/**
	 * This method will be used to determine the dynamic type for the current
	 * node.
	 * 
	 * The type returned by this method should match with the type of the
	 * corresponding parent bean setter/adder.
	 * 
	 * @param node
	 * @param att
	 * @return
	 */
	public Class<?> getDynamicBeanType(BeanNode node, XMLAttributeMap att);
	
	/**
	 * Parses the text node value.
	 *
	 * @param node
	 *            the node
	 * @param att
	 *            the att
	 * @return the object
	 */
	public Object parseTextNodeValue(BeanNode node, XMLAttributeMap att);

	/**
	 * Creates the attribute bean.
	 *
	 * @param node
	 *            the node
	 * @param attName
	 *            the att name
	 * @param type
	 *            the type
	 * @return the object
	 */
	public Object createAttributeBean(BeanNode node, String attName, Class<?> type);

	/**
	 * Whenever there is a node end, this method will be called. Since, this
	 * method is called at the end of the node there will be no setter and adder
	 * methods invocation after this point. This method is expected to validate
	 * the bean, for example, check if all the mandatory data is specified or
	 * not. If data provided is not valid or sufficient, this method is expected
	 * to throw ValidateException with a proper message. The exception will be
	 * wrapped with XMLLoadException and adds the node XML path at where
	 * exception is thrown and then this new XMLLoadException will be thrown.
	 *
	 * @param node
	 *            node to be validated
	 */
	public void validateBean(BeanNode node) throws ValidateException;

	/**
	 * <P>
	 * This method will be called at start of each node. Generally the return
	 * string value is used to represent the current node (when exception is
	 * thrown).
	 * </P>
	 * <P>
	 * Other than above mentioned usage, this method is capable of making
	 * XMLBeanParser to skip/ignore the current node and all its sub nodes. The
	 * node being skipped can be a standard node/reserve node. For skipping a
	 * node, this method should return constant SKIP_NODE_ELEMENT.
	 * </P>
	 * <BR>
	 * 
	 * @param node
	 *            Node to be processed
	 * @param att
	 *            node attributes
	 * @return String describing th current node.
	 */
	public String getNodeDescription(BeanNode node, XMLAttributeMap att);

	/**
	 * Fetch a description text for the specified bean. Note, this method may be
	 * called at any time. Before bean is completely loaded or when loaded
	 * partially. <BR>
	 * 
	 * @param bean
	 *            Bean for which description is needed.
	 * @return Description representiung the bean.
	 */
	public String getBeanDescription(Object bean);

	/**
	 * This method is invoked when a date property is encountered. The format
	 * returned by this method is used to parse the date string mentioned in XML
	 * to date object. The date format returned by this method should be
	 * according to the java.text.SimpleDateFormat class standard. <BR>
	 * 
	 * @return Date format to be used to parse dates.
	 */
	public String getDateFormat();

	/**
	 * This method is called when a start of reserved node is encountered. If
	 * the reserved node is expected to have sub nodes then this method should
	 * return a bean representing the reserve node. Otherwise the reserved node
	 * is a <B><I>null-based reserve node</I></B> and these nodes should not
	 * contain sub nodes. A non-bean reserve node body should be either empty or
	 * text. <BR>
	 * 
	 * @param node
	 *            Node to be processed
	 * @param att
	 *            node attributes
	 * @return An object representing reserved node. This value can be null for
	 *         non-bean reserved nodes.
	 */
	public Object processReservedNode(BeanNode node, XMLAttributeMap att);

	/**
	 * This method will be invoked when the end of reserved node is reached. If
	 * the reserve node is not a null-based reserved node, then the bean created
	 * during processReservedNode() will be passed as argument to this method.
	 * Subnodes of the bean based reserve nodes will be treated as normal
	 * sub-nodes but the considering reserve node bean as parent bean. If the
	 * current reserve node is a null-based text node, then the text specified
	 * in the node-body is passed as bean argument to this method.
	 *
	 * @param node
	 *            Node to be processed
	 * @param att
	 *            node attributes
	 */
	public void processReserveNodeEnd(BeanNode node, XMLAttributeMap att);
	
	public void registerReserveNodeHandler(IReserveNodeHandler handler);

	/**
	 * Gets the bean factory.
	 *
	 * @param type
	 *            the type
	 * @return the bean factory
	 */
	public BeanFactory getBeanFactory(Class<?> type);

	/**
	 * Gets the constant value.
	 *
	 * @param name
	 *            the name
	 * @return the constant value
	 */
	public String getConstantValue(String name);

	/**
	 * This method will be called by passing the text of attribute and node-text. This method
	 * should replace all the expressions and return final output. 
	 *
	 * @param rootBean
	 *            the root bean
	 * @param text
	 *            the text
	 * @return the string
	 */
	public String processText(Object rootBean, String text);
	
	public default boolean isReserveUri(String uri)
	{
		if(uri == null)
		{
			return false;
		}
		
		return XMLConstants.CCG_URI.equals(uri) || XMLConstants.NEW_CCG_URI.equals(uri) ;
	}

	public default boolean isWrapUri(String uri)
	{
		if(uri == null)
		{
			return false;
		}
		
		return XMLConstants.CCG_WRAP_URI.equals(uri) || XMLConstants.NEW_CCG_WRAP_URI.equals(uri) ;
	}
}
