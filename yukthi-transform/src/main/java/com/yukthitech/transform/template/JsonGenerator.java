package com.yukthitech.transform.template;

import static com.yukthitech.transform.ITransformConstants.OBJECT_MAPPER;

import java.util.LinkedHashMap;
import java.util.Map;

import com.yukthitech.transform.TransformException;
import com.yukthitech.transform.TransformState;
import com.yukthitech.transform.template.TransformTemplate.TransformObject;
import com.yukthitech.transform.template.TransformTemplate.TransformObjectField;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class JsonGenerator implements IGenerator
{
	@Override
	public String getRootPath()
	{
		return "";
	}
	
	@Override
	public String getSubPath(TransformObjectField field)
	{
		return ">" + field.getName();
	}

	@Override
	public Object generateObject(TransformState state, TransformObject rootTransform)
	{
		return new LinkedHashMap<String, Object>();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setField(TransformState state, TransformObjectField field, Object object, String name, Object fieldValue)
	{
		LinkedHashMap<String, Object> objMap = (LinkedHashMap<String, Object>) object;
		objMap.put(name, fieldValue);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void injectReplaceEntry(TransformState state, TransformObjectField field, Object object, Object injectedValue)
	{
		//if result value is not map
		if(!(injectedValue instanceof Map))
		{
			throw new TransformException(state.getPath(), "Value of @replace key must be a map but found: {}", injectedValue.getClass().getName());
		}
		
		LinkedHashMap<String, Object> objMap = (LinkedHashMap<String, Object>) object;
		Map<String, Object> injectedValueMap = (Map<String, Object>) injectedValue;
		objMap.putAll(injectedValueMap);
	}

    public String formatObject(Object object)
    {
    	try
    	{
    		return OBJECT_MAPPER.writeValueAsString(object);
        } catch(Exception ex)
        {
            throw new InvalidStateException("An error occurred while writing json value.", ex);
        }
    }
}
