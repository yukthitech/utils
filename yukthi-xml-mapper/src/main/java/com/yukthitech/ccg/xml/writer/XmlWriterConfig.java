package com.yukthitech.ccg.xml.writer;

/**
 * Configurations to customize the xml serialization.
 * @author akiran
 */
public class XmlWriterConfig
{
	/**
	 * Indicates whether output xml should be indented or not.
	 */
	private boolean indentXml = false;
	
	/**
	 * Indicates whether expressions should be disabled.
	 */
	private boolean escapeExpressions = true;
	
	/**
	 * If set to true, the output xml will be read compatible. Which means
	 * the properties which are writeable only will be considered for generating the xml.
	 */
	private boolean readCompatible = true;
	
	/**
	 * If set to true, ccg namespace settings will be excluded in output xml.
	 */
	private boolean excludeNameSpace = false;
	
	/**
	 * Flag indicating if xml declaration has to be excluded.
	 */
	private boolean excludeXmlDeclaration = false;

	/**
	 * Gets the indicates whether output xml should be indented or not.
	 *
	 * @return the indicates whether output xml should be indented or not
	 */
	public boolean isIndentXml()
	{
		return indentXml;
	}

	/**
	 * Sets the indicates whether output xml should be indented or not.
	 *
	 * @param indentXml the new indicates whether output xml should be indented or not
	 */
	public void setIndentXml(boolean indentXml)
	{
		this.indentXml = indentXml;
	}

	/**
	 * Gets the indicates whether expressions should be disabled.
	 *
	 * @return the indicates whether expressions should be disabled
	 */
	public boolean isEscapeExpressions()
	{
		return escapeExpressions;
	}

	/**
	 * Sets the indicates whether expressions should be disabled.
	 *
	 * @param escapeExpressions the new indicates whether expressions should be disabled
	 */
	public void setEscapeExpressions(boolean escapeExpressions)
	{
		this.escapeExpressions = escapeExpressions;
	}

	/**
	 * Gets the if set to true, the output xml will be read compatible. Which means the properties which are writeable only will be considered for generating the xml.
	 *
	 * @return the if set to true, the output xml will be read compatible
	 */
	public boolean isReadCompatible()
	{
		return readCompatible;
	}

	/**
	 * Sets the if set to true, the output xml will be read compatible. Which means the properties which are writeable only will be considered for generating the xml.
	 *
	 * @param readCompatible the new if set to true, the output xml will be read compatible
	 */
	public void setReadCompatible(boolean readCompatible)
	{
		this.readCompatible = readCompatible;
	}

	/**
	 * Gets the if set to true, ccg namespace settings will be excluded in output xml.
	 *
	 * @return the if set to true, ccg namespace settings will be excluded in output xml
	 */
	public boolean isExcludeNameSpace()
	{
		return excludeNameSpace;
	}

	/**
	 * Sets the if set to true, ccg namespace settings will be excluded in output xml.
	 *
	 * @param excludeNameSpace the new if set to true, ccg namespace settings will be excluded in output xml
	 */
	public void setExcludeNameSpace(boolean excludeNameSpace)
	{
		this.excludeNameSpace = excludeNameSpace;
	}

	/**
	 * Checks if is flag indicating if xml declaration has to be excluded.
	 *
	 * @return the flag indicating if xml declaration has to be excluded
	 */
	public boolean isExcludeXmlDeclaration()
	{
		return excludeXmlDeclaration;
	}

	/**
	 * Sets the flag indicating if xml declaration has to be excluded.
	 *
	 * @param excludeXmlDeclaration
	 *            the new flag indicating if xml declaration has to be excluded
	 */
	public void setExcludeXmlDeclaration(boolean excludeXmlDeclaration)
	{
		this.excludeXmlDeclaration = excludeXmlDeclaration;
	}
	
	/**
	 * Method which tells whether a particular text value can be attribute or not. By default all text values
	 * can be attributes. This method can be overridden to provide custom implementation.
	 * @param nodeName parent node name
	 * @param propName Property name
	 * @return true if specified property can be attribute 
	 */
	public boolean isAttributable(String nodeName, String propName)
	{
		return true;
	}
}
