package com.yukthitech.utils.fmarker;

import freemarker.template.Template;

public class FreeMarkerTemplate
{
	public static enum TemplateType
	{
		TEMPLATE,
		CONDITION,
		VALUE_EXPRESSION
	}
	
	private String name;
	private TemplateType type;
	private Template template;
	private String sourceTemplate;
	
	public FreeMarkerTemplate(String name, TemplateType type, Template template, String sourceTemplate)
	{
		this.name = name;
		this.type = type;
		this.template = template;
		this.sourceTemplate = sourceTemplate;
	}
	
	public String getName()
	{
		return name;
	}

	public TemplateType getType()
	{
		return type;
	}

	public Template getTemplate()
	{
		return template;
	}
	
	public String getSourceTemplate()
	{
		return sourceTemplate;
	}
}
