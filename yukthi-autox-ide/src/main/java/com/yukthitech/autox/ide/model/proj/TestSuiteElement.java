package com.yukthitech.autox.ide.model.proj;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.yukthitech.autox.ide.FileParseCollector;
import com.yukthitech.autox.ide.xmlfile.Element;
import com.yukthitech.utils.CommonUtils;

/**
 * Test suite element.
 * @author akiran
 */
public class TestSuiteElement extends CodeElementContainer
{
	/**
	 * Name of the test suite.
	 */
	private String name;

	private Map<String, TestCaseElement> testCases = new HashMap<>();

	private Map<String, FunctionDefElement> functions = new HashMap<>();
	
	public TestSuiteElement(File file, int position, Element element)
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
	
	public void addTestCaseElement(TestCaseElement testCase)
	{
		testCases.put(testCase.getName(), testCase);
		super.addFileElement(testCase);
		testCase.setParent(this);
	}

	public void addFunction(FunctionDefElement element)
	{
		this.functions.put(element.getName(), element);
		addFileElement(element);
		element.setParent(this);
	}
	
	@Override
	public void compile(FileParseCollector collector)
	{
		super.compile(collector);
		
		testCases.values().forEach(tc -> tc.compile(collector));
		functions.values().forEach(tc -> tc.compile(collector));
	}
}
