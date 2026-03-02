package com.yukthitech.transform;

import java.util.function.Consumer;

import com.yukthitech.transform.template.IGenerator;
import com.yukthitech.transform.template.Location;
import com.yukthitech.transform.template.TransformTemplate;
import com.yukthitech.transform.template.TransformTemplate.TransformObject;
import com.yukthitech.transform.template.TransformTemplate.TransformObjectField;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class TransformState
{
	private IGenerator generator;
	
	private Location location;
	
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
		
		this.location = template.getLocation();
	}
	
	private TransformState(TransformState parent, Location location)
	{
		this.generator = parent.generator;
		this.attributeMode = parent.attributeMode;
		this.location = location;
	}

	public TransformState forField(TransformObjectField field)
	{
		return new TransformState(this, field.getLocation());
	}

	public TransformState forIndex(int index)
	{
		Location newLoc = location.sublocation("[" + index + "]");
		return new TransformState(this, newLoc);
	}

	public TransformState forClone()
	{
		Location newLoc = location.sublocation("{clone}");
		return new TransformState(this, newLoc);
	}

	public TransformState forDynField(String field)
	{
		Location newLoc = location.sublocation("#" + field);
		return new TransformState(this, newLoc);
	}
	
	public TransformState forField(TransformObjectField field, String dynField)
	{
		String fieldPath = generator.getSubPath(field);
		Location newLoc = location.sublocation(fieldPath + "#" + dynField);
		return new TransformState(this, newLoc);
	}
	
	public void executeInAttributeMode(TransformObjectField field, Consumer<TransformState> attrStateConsumer)
	{
		Location newLoc = location.sublocation(generator.getSubPath(field));
		TransformState newState = new TransformState(this, newLoc);
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
	
	public void injectReplaceEntry(TransformObjectField field, Object object, Object injectedValue)
	{
		generator.injectReplaceEntry(this, field, object, injectedValue);
	}
	
	public Object convertIncluded(Object value)
	{
		return generator.convertIncluded(this, value);
	}
	
	public String formatObject(Object object)
	{
		return generator.formatObject(object);
	}
	
	public Object toSimpleObject(Object value)
	{
		return generator.toSimpleObject(value);
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public boolean isIgnorable(TransformObject transformObject, Object object)
	{
		return generator.isIgnorable(transformObject, object);
	}
}
