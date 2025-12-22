package com.yukthitech.transform;

import com.yukthitech.transform.template.IGenerator;
import com.yukthitech.transform.template.TransformTemplate;
import com.yukthitech.transform.template.TransformTemplate.TransformObject;
import com.yukthitech.transform.template.TransformTemplate.TransformObjectField;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class TransformState
{
	private IGenerator generator;
	
	private String path;
	
	public TransformState(TransformTemplate template)
	{
		try
		{
			generator = template.getGeneratorType().getConstructor().newInstance();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while creating generator instance of type: " + template.getGeneratorType(), ex);
		}
		
		this.path = generator.getRootPath();
	}
	
	private TransformState(IGenerator generator, String path)
	{
		this.generator = generator;
		this.path = path;
	}

	public TransformState forField(TransformObjectField field)
	{
		return new TransformState(generator, path + generator.getSubPath(field));
	}

	public TransformState forIndex(int index)
	{
		return new TransformState(generator, path + "[" + index + "]");
	}

	public TransformState forClone()
	{
		return new TransformState(generator, path + "{clone}");
	}

	public TransformState forDynField(String field)
	{
		return new TransformState(generator, path + "#" + field);
	}
	
	public TransformState forField(TransformObjectField field, String dynField)
	{
		String fieldPath = path + generator.getSubPath(field);
		String newPath =  fieldPath + "#" + dynField;
		
		return new TransformState(generator, newPath);
	}

	public Object newObject(TransformObject object)
	{
		return generator.generateObject(object);
	}
	
	public void setField(TransformObjectField field, Object object, String name, Object fieldValue)
	{
		generator.setField(field, object, name, fieldValue);
	}
	
	public void injectReplaceEntry(String path, TransformObjectField field, Object object, Object injectedValue)
	{
		generator.injectReplaceEntry(path, field, object, injectedValue);
	}
	
	public String formatObject(Object object)
	{
		return generator.formatObject(object);
	}
	
	public String getPath()
	{
		return path;
	}
}
