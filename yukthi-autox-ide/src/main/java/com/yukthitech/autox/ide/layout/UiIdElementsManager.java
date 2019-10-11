package com.yukthitech.autox.ide.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Used to collect different ui elements across with a given "id". And access these
 * ui elements based on this "id".
 * @author akiran
 */
public class UiIdElementsManager
{
	private static Map<String, List<Object>> idElements = new HashMap<>();
	
	public static void registerElement(String id, Object element)
	{
		List<Object> elements = idElements.get(id);
		
		if(elements == null)
		{
			elements = new ArrayList<>();
			idElements.put(id, elements);
		}
		
		elements.add(element);
	}
	
	public static Set<String> getUiIds()
	{
		return idElements.keySet();
	}
	
	public static List<Object> getElements(String id)
	{
		List<Object> elements = idElements.get(id);
		
		if(elements == null)
		{
			return null;
		}
		
		return new ArrayList<>(elements);
	}
}
