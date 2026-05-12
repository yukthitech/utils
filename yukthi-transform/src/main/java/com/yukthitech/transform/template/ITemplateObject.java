package com.yukthitech.transform.template;

import java.io.Serializable;

/**
 * Node in a transform template tree. Implementations compile non-serializable artifacts
 * (FreeMarker templates, XPath/JsonPath compiled forms) from serializable source data.
 */
public interface ITemplateObject extends Serializable
{
	/**
	 * Builds parsed artifacts for this node and recursively compiles child nodes.
	 *
	 * @param context
	 *            compilation context (FreeMarker engine and cycle guards)
	 */
	void compile(TemplateCompileContext context);
}
