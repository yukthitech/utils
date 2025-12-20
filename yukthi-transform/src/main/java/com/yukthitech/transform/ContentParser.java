package com.yukthitech.transform;

import static com.yukthitech.transform.ITransformConstants.OBJECT_MAPPER;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class ContentParser
{
	public static Object parseJson(String json)
	{
		try
		{
			return OBJECT_MAPPER.readValue(json, Object.class);
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while parsing json template.", ex);
		}
		
	}
}
