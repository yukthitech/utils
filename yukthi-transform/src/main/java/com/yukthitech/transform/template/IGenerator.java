package com.yukthitech.transform.template;

import com.yukthitech.transform.TransformState;
import com.yukthitech.transform.template.TransformTemplate.TransformObject;
import com.yukthitech.transform.template.TransformTemplate.TransformObjectField;

public interface IGenerator
{
	public String getRootPath();
	
	public String getSubPath(TransformObjectField field);
	
	public Object generateObject(TransformState state, TransformObject rootTransform);
	
	public void setField(TransformState state, TransformObjectField field, Object object, String name, Object fieldValue);
	
	public void injectReplaceEntry(TransformState state, TransformObjectField field, Object object, Object injectedValue);
	
	public String formatObject(Object object);
}
