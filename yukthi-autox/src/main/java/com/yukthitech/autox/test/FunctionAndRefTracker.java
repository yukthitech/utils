package com.yukthitech.autox.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Used to track functions and their references, which later is used to validate
 * the function-ref for params.
 * @author akiran
 */
public class FunctionAndRefTracker
{
	private Map<String, Function> globalFunctions = new HashMap<>();
	
	private Map<String, Map<String, Function>> testSuiteFunctions = new HashMap<>();
	
	private List<FunctionRef> functionReferences = new LinkedList<>();
	
	public void addGlobalFunction(Function function)
	{
		this.globalFunctions.put(function.getName(), function);
	}
	
	public void addTestSuiteFunction(String testSuite, Function function)
	{
		
	}
	
	public void addFunctionRef(FunctionRef funcRef)
	{
		
	}
}
