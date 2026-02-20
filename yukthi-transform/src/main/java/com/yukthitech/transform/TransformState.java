package com.yukthitech.transform;

import java.util.function.Consumer;

import com.yukthitech.transform.template.IGenerator;
import com.yukthitech.transform.template.TransformTemplate;
import com.yukthitech.transform.template.TransformTemplate.TransformObject;
import com.yukthitech.transform.template.TransformTemplate.TransformObjectField;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class TransformState
{
	private IGenerator generator;
	
	private String path;
	
	/**
	 * Flag indicating that currently the transformation is being
	 * done for set-attribute sub content.
	 */
	private boolean attributeMode;
	
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
	
	private TransformState(TransformState parent, String path)
	{
		this.generator = parent.generator;
		this.attributeMode = parent.attributeMode;
		this.path = path;
	}

	public TransformState forField(TransformObjectField field)
	{
		return new TransformState(this, path + generator.getSubPath(field));
	}

	public TransformState forIndex(int index)
	{
		return new TransformState(this, path + "[" + index + "]");
	}

	public TransformState forClone()
	{
		return new TransformState(this, path + "{clone}");
	}

	public TransformState forDynField(String field)
	{
		return new TransformState(this, path + "#" + field);
	}
	
	public TransformState forField(TransformObjectField field, String dynField)
	{
		String fieldPath = path + generator.getSubPath(field);
		String newPath =  fieldPath + "#" + dynField;
		
		return new TransformState(this, newPath);
	}
	
	public void executeInAttributeMode(TransformObjectField field, Consumer<TransformState> attrStateConsumer)
	{
		TransformState newState = new TransformState(this, path + generator.getSubPath(field));
		newState.attributeMode = true;
		
		attrStateConsumer.accept(newState);
	}
	
	public boolean isAttributeMode()
	{
		return attributeMode;
	}

	public Object newObject(TransformObject object)
	{
		return generator.generateObject(this, object);
	}
	
	public void setField(TransformObjectField field, Object object, String name, Object fieldValue)
	{
		generator.setField(this, field, object, name, fieldValue);
	}
	
	public void injectReplaceEntry(String path, TransformObjectField field, Object object, Object injectedValue)
	{
		generator.injectReplaceEntry(this, field, object, injectedValue);
	}
	
	public Object convertIncluded(Object value)
	{
		return generator.convertIncluded(path, value);
	}
	
	public String formatObject(Object object)
	{
		return generator.formatObject(object);
	}
	
	public Object toSimpleObject(Object value)
	{
		return generator.toSimpleObject(value);
	}
	
	public String getPath()
	{
		return path;
	}
}
