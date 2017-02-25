package com.fw.test.persitence.queries;

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.search.DynamicResultField;
import com.yukthitech.persistence.repository.search.IDynamicSearchResult;

public class DynamicEmpSearchResult implements IDynamicSearchResult
{
	@Field("name")
	private String empName;
	
	@Field("age")
	private Integer age;
	
	private Map<String, Object> extraFields = new HashMap<String, Object>();

	/**
	 * @return the {@link #empName empName}
	 */
	public String getEmpName()
	{
		return empName;
	}

	/**
	 * @param empName the {@link #empName empName} to set
	 */
	public void setEmpName(String empName)
	{
		this.empName = empName;
	}

	/**
	 * @return the {@link #age age}
	 */
	public Integer getAge()
	{
		return age;
	}

	/**
	 * @param age the {@link #age age} to set
	 */
	public void setAge(Integer age)
	{
		this.age = age;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.persistence.repository.search.IDynamicSearchResult#addField(com.yukthitech.persistence.repository.search.DynamicResultField)
	 */
	@Override
	public void addField(DynamicResultField field)
	{
		extraFields.put(field.getField(), field.getValue());
	}
	
	/**
	 * @return the {@link #extraFields extraFields}
	 */
	public Map<String, Object> getExtraFields()
	{
		return extraFields;
	}
}
