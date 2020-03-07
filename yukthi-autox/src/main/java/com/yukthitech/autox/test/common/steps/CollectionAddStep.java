package com.yukthitech.autox.test.common.steps;

import java.util.Collection;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.utils.exceptions.InvalidArgumentException;

/**
 * Adds the specified value to specified collection.
 * 
 * @author akiran
 */
@Executable(name = "collectionAdd", group = Group.Common, message = "Adds the specified value to specified collection")
public class CollectionAddStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Expression to be used to set the value.
	 */
	@Param(description = "Collection expression to which specified value needs to be added.", required = true, sourceType = SourceType.EXPRESSION)
	private Object collection;

	/**
	 * Value of the attribute to set.
	 */
	@Param(description = "Value expression which needs to be added to specified collection. Default: null (null will be added to specified collection)", required = false, sourceType = SourceType.EXPRESSION)
	private Object value = null;

	public void setCollection(String collection)
	{
		this.collection = collection;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("To collection of type {} adding value of type: {}", 
				(collection != null) ? collection.getClass().getName() : "null", 
				((value != null) ? value.getClass().getName() : value)
				);
		
		if(!(collection instanceof Collection))
		{
			throw new InvalidArgumentException("Non-collection object specified as collection: {}", collection);
		}
		
		((Collection) collection).add(value);
		return true;
	}
}
