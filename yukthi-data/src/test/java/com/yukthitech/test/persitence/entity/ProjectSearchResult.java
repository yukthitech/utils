/*
 * The MIT License (MIT)
 * Copyright (c) 2016 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.fw.test.persitence.entity;

import java.util.HashMap;
import java.util.Map;

import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.search.DynamicResultField;
import com.yukthitech.persistence.repository.search.IDynamicSearchResult;

/**
 * @author akiran
 *
 */
public class ProjectSearchResult implements IDynamicSearchResult
{
	@Field("name")
	private String name;
	
	private Map<String, String> extensionFields = new HashMap<String, String>();

	@Field("extensions.field3")
	private String extField3;

	@Field("extensions.field4")
	private String extField4;

	@Field("extensions.field5")
	private String extField5;

	/* (non-Javadoc)
	 * @see com.yukthitech.persistence.repository.search.IDynamicSearchResult#addField(com.yukthitech.persistence.repository.search.DynamicResultField)
	 */
	@Override
	public void addField(DynamicResultField field)
	{
		extensionFields.put(field.getField(), (String)field.getValue());
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Map<String, String> getExtensionFields()
	{
		return extensionFields;
	}

	public void setExtensionFields(Map<String, String> extensionFields)
	{
		this.extensionFields = extensionFields;
	}
	
	public String getExtensionField(String name)
	{
		return extensionFields.get(name);
	}

	public String getExtField3()
	{
		return extField3;
	}

	public void setExtField3(String extField3)
	{
		this.extField3 = extField3;
	}

	public String getExtField4()
	{
		return extField4;
	}

	public void setExtField4(String extField4)
	{
		this.extField4 = extField4;
	}

	public String getExtField5()
	{
		return extField5;
	}

	public void setExtField5(String extField5)
	{
		this.extField5 = extField5;
	}
}
