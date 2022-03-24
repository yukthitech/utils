package com.yukthitech.autox.test.common.steps;

import java.util.Map;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Adds the specified key-value entry to specified map.
 * 
 * @author akiran
 */
@Executable(name = "mapPut", group = Group.Common, message = "Adds the specified key-value entry to specified map.")
public class MapPutStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Expression to be used to set the value.
	 */
	@Param(description = "Map expression to which specified entry needs to be added.", required = true, sourceType = SourceType.EXPRESSION)
	private Object map;

	/**
	 * Value expression which needs to be added to specified collection.
	 * Default: null (null will be added)
	 */
	@Param(description = "Value expression which needs to be added to specified collection. Default: null (null will be added)", required = false, sourceType = SourceType.EXPRESSION)
	private Object value = null;

	/**
	 * Key expression which needs to be added to specified collection. Default:
	 * null (null will be added)
	 */
	@Param(description = "Key expression which needs to be added to specified collection. Default: null (null will be added)", required = false, sourceType = SourceType.EXPRESSION)
	private Object key = null;

	public void setMap(Object map)
	{
		this.map = map;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public void setKey(Object key)
	{
		this.key = key;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("To map of type {} adding entry of [Key Type: {}, Value Type: {}]", 
				(map != null) ? map.getClass().getName() : "null", 
				((key != null) ? key.getClass().getName() : key),
				((value != null) ? value.getClass().getName() : value)
				);

		if(!(map instanceof Map))
		{
			throw new InvalidArgumentException("Non-map object specified as map: {}", map);
		}

		((Map) map).put(key, value);
	}
}
