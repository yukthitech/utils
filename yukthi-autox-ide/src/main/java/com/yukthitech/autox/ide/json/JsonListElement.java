package com.yukthitech.autox.ide.json;

import java.util.ArrayList;
import java.util.List;

public class JsonListElement extends AbstractJsonElement
{
	private List<IJsonElement> elements = new ArrayList<>();

	public List<IJsonElement> getElements()
	{
		return elements;
	}

	public void setElements(List<IJsonElement> elements)
	{
		this.elements = elements;
	}
}
