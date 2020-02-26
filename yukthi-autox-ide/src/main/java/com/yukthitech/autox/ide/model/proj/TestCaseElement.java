package com.yukthitech.autox.ide.model.proj;

import java.io.File;
import java.util.Map;

import com.yukthitech.autox.ide.xmlfile.Element;
import com.yukthitech.utils.CommonUtils;

/**
 * Test suite element.
 * @author akiran
 */
public class TestCaseElement extends CodeElementContainer
{
	/**
	 * Name of the test suite.
	 */
	private String name;

	public TestCaseElement(File file, int position, Element element)
	{
		super(file, position);

		Map<String, String> valMap = element.getChildValues(CommonUtils.toSet("name"));
		this.name = valMap.get("name");
	}

	/**
	 * Gets the name of the test suite.
	 *
	 * @return the name of the test suite
	 */
	public String getName()
	{
		return name;
	}
}
