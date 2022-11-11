package com.yukthitech.autox.exec.report;

import java.util.function.Function;

/**
 * Base interface used to extract information from different executables.
 * @author akranthikiran
 */
public class ReportInfoProvider<T>
{
	private Function<T, String> codeFunc;
	
	private Function<T, String> nameFunc;
	
	private Function<T, String> descFunc;
	
	public ReportInfoProvider(Function<T, String> codeFunc, Function<T, String> nameFunc, Function<T, String> descFunc)
	{
		this.codeFunc = codeFunc;
		this.nameFunc = nameFunc;
		this.descFunc = descFunc;
	}

	public String getCode(T executor)
	{
		return codeFunc.apply(executor);
	}
	
	public String getName(T executor)
	{
		return nameFunc.apply(executor);
	}
	
	public String getDescription(T executor)
	{
		return descFunc.apply(executor);
	}
}
