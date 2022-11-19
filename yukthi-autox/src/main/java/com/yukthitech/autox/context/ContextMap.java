package com.yukthitech.autox.context;

import java.util.Collections;
import java.util.HashMap;

/**
 * A map-wrapper over context which is generally used in JEL.
 * 
 * @author akranthikiran
 */
public class ContextMap extends HashMap<String, Object>
{
	private static final long serialVersionUID = 1L;

	public ContextMap(AutomationContext context)
	{
		super.put("attr", context.getAttr());
		
		if(context.isParamPresent())
		{
			super.put("param", Collections.unmodifiableMap(context.getParam()));
		}
		
		super.put("prop", Collections.unmodifiableMap(context.getProp()));
	}
}
