package com.yukthitech.persistence.repository.executors.builder;

import java.util.HashSet;
import java.util.Set;

/**
 * Bean context that can be used to parse/process expressions
 * 
 * @author akiran
 */
public class QueryBuilderContext
{
	/**
	 * Query builder for which fields or conditions are being added.
	 */
	ConditionQueryBuilder currentQueryBuilder;
	
	Object parameters[];
	Object repositoryExecutionContext;
	
	private Set<String> usedTableCodes = new HashSet<>();

	public QueryBuilderContext(ConditionQueryBuilder currentQueryBuilder, Object[] parameters, Object repositoryExecutionContext, String mainTableCode)
	{
		this.currentQueryBuilder = currentQueryBuilder;
		
		this.parameters = parameters;
		this.repositoryExecutionContext = repositoryExecutionContext;
		this.usedTableCodes.add(mainTableCode);
	}
	
	public Object[] getParameters()
	{
		return parameters;
	}
	
	public void addUsedTable(TableInfo tableInfo)
	{
		usedTableCodes.add(tableInfo.tableCode);
	}
	
	public boolean isTableInUse(String code)
	{
		return usedTableCodes.contains(code);
	}
}
