package com.yukthitech.autox.ide.model.proj;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yukthitech.autox.ide.FileParseCollector;

public class ProjectElementTree extends CodeElementContainer
{
	private Map<String, FunctionDefElement> functions = new HashMap<>();
	
	private Map<String, TestSuiteElement> testSuites = new HashMap<>();
	
	private Map<File, List<CodeElement>> fileToElements = new HashMap<>();
	
	private Map<String, Integer> appProp;

	public ProjectElementTree(Map<String, Integer> appProp)
	{
		super(null, -1);
		this.appProp = appProp;
	}
	
	@Override
	public boolean isValidAppProperty(String name)
	{
		return appProp.containsKey(name);
	}
	
	@Override
	protected void addFileElement(CodeElement element)
	{
		List<CodeElement> elements = fileToElements.get(element.getFile());
		
		if(elements == null)
		{
			elements = new ArrayList<>();
			fileToElements.put(element.getFile(), elements);
		}
		
		elements.add(element);
	}

	public void addFunction(FunctionDefElement element)
	{
		this.functions.put(element.getName(), element);
		addFileElement(element);
		element.setParent(this);
	}
	
	public void addTestSuite(TestSuiteElement testSuite)
	{
		if(testSuites.containsKey(testSuite.getName()))
		{
			return;
		}
		
		testSuites.put(testSuite.getName(), testSuite);
		//Note: As test suite spans multiple files, this will not be added as file-element
	}
	
	@Override
	public void compile(FileParseCollector collector)
	{
		super.compile(collector);
		
		testSuites.values().forEach(ts -> ts.compile(collector));
		functions.values().forEach(ts -> ts.compile(collector));
	}
}
