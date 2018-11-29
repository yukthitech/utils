package com.yukthitech.autox.ide.json;

import java.util.HashMap;
import java.util.Map;

public class JsonMapElement extends AbstractJsonElement
{
	private Map<String, IJsonElement> map = new HashMap<>();

	public Map<String, IJsonElement> getMap()
	{
		return map;
	}

	public void setMap(Map<String, IJsonElement> map)
	{
		this.map = map;
	}
}
