package com.yukthitech.indexer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.indexer.common.DataType;
import com.yukthitech.indexer.common.FieldIndexDetails;
import com.yukthitech.indexer.common.IndexUtils;
import com.yukthitech.indexer.common.TypeIndexDetails;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidStateException;

public abstract class AbstractDataIndex implements IDataIndex
{
	private static Logger logger = LogManager.getLogger(AbstractDataIndex.class);
	
	public static final String OBJECT_FIELD = "__object";
	public static final String OBJECT_TYPE_FIELD = "__objectType";

	/**
	 * Object mapper that can be used for json conversion.
	 */
	protected static ObjectMapper objectMapper = new ObjectMapper();
	
	protected Map<Class<?>, TypeIndexDetails> indexedTypes = new HashMap<>();
	
	protected synchronized TypeIndexDetails getIndexType(Class<?> type) throws Exception
	{
		TypeIndexDetails typeIndexDetails = indexedTypes.get(type);
		
		if(typeIndexDetails != null)
		{
			return typeIndexDetails;
		}
		
		typeIndexDetails = new TypeIndexDetails(type);
		indexedTypes.put(type, typeIndexDetails);
		
		return typeIndexDetails;
	}
	
	/**
	 * Creates index object map from specified data and specified fields.
	 * @param fields
	 * @param data
	 * @param idWrapper Object wrapper to hold id field value.
	 * @return
	 */
	private Map<String, Object> toIndexObjectMap(Collection<FieldIndexDetails> fields, Object data, ObjectWrapper<Object> idWrapper)
	{
		Object value = null;
		Map<String, Object> indexObj = new HashMap<>();
		
		for(FieldIndexDetails field : fields)
		{
			try
			{
				value = PropertyUtils.getProperty(data, field.getName());
			}catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while fetching property - {}", field.getName());
			}
			
			if(value == null)
			{
				continue;
			}
			
			if(field.isIgnoreCase() && field.getDataType() == DataType.STRING)
			{
				value = IndexUtils.toLowerCase(value);
			}
			
			//for sub objects create index objects recursively
			if(field.getDataType() == DataType.OBJECT)
			{
				value = toIndexObjectMap(field.getSubfields(), value, null);
			}
			
			indexObj.put(field.getName(), value);
			
			if(field.isIdField() && idWrapper != null && idWrapper.getValue() == null)
			{
				idWrapper.setValue(value);
			}
		}
		
		return indexObj;
	}
	
	protected Map<String, Object> toDataMap(Object indexData, ObjectWrapper<Object> idWrapper)
	{
		try
		{
			logger.debug("Indexing object of type - {}", indexData.getClass().getName());
			
			Class<?> type = indexData.getClass();
			TypeIndexDetails typeIndexDetails = getIndexType(type);
			
			Map<String, Object> indexObj = toIndexObjectMap(typeIndexDetails.getFields(), indexData, idWrapper);
			
			indexObj.put(OBJECT_FIELD, objectMapper.writeValueAsString(indexData));
			indexObj.put(OBJECT_TYPE_FIELD, indexData.getClass().getName());

			return indexObj;
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while indexing data - {}", indexData);
		}
	}

}
