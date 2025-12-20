package com.yukthitech.transform.template;

public interface ITemplateFactory
{
	public TransformTemplate pareseTemplate(String jsonContent);
	
	public String formatObject(Object object);
}
