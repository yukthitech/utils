package com.yukthi.dao.qry;

public interface BulkQueryFilter extends QueryFilter
{
	public Class<?> getParamType(String paramName);
	public boolean next();
	public int getCommitCount();
}
