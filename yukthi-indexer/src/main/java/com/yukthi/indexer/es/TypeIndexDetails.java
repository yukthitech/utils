package com.yukthi.indexer.es;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.yukthi.indexer.IndexField;
import com.yukthi.indexer.IndexType;
import com.yukthi.utils.CommonUtils;

/**
 * Index details of a type.
 * @author akiran
 */
public class TypeIndexDetails
{
	/**
	 * Index details of a field of indexable type.
	 * @author akiran
	 */
	public static class FieldIndexDetails
	{
		/**
		 * Name of the field.
		 */
		private String name;
		
		/**
		 * Indexing to be used.
		 */
		private IndexType indexType;
		
		/**
		 * ES data type name.
		 */
		private String esDataType;
		
		public FieldIndexDetails(String name, IndexType indexType, String esDataType)
		{
			this.name = name;
			this.indexType = indexType;
			this.esDataType = esDataType;
		}

		/**
		 * Gets the name.
		 *
		 * @return the name
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Gets the index type.
		 *
		 * @return the index type
		 */
		public IndexType getIndexType()
		{
			return indexType;
		}
		
		/**
		 * Gets the es data type.
		 *
		 * @return the es data type
		 */
		public String getEsDataType()
		{
			return esDataType;
		}
	}
	
	private static Map<Class<?>, String> supportedTypes;
	
	static
	{
		supportedTypes = CommonUtils.toMap(
			byte.class, "byte",
			Byte.class, "byte",
			
			short.class, "short",
			Short.class, "short",

			int.class, "integer",
			Integer.class, "integer",

			long.class, "long",
			Long.class, "long",

			float.class, "float",
			Float.class, "float",

			double.class, "double",
			Double.class, "double",

			boolean.class, "boolean",
			Boolean.class, "boolean",

			Date.class, "date",
			
			String.class, "string"
		);
	}
	
	/**
	 * Index details of fields of index type.
	 */
	private Map<String, FieldIndexDetails> fields = new HashMap<>();
	
	/**
	 * Index type.
	 */
	private Class<?> type;
	
	/**
	 * Loads index details of specified type.
	 * @param type
	 */
	public TypeIndexDetails(Class<?> type)
	{
		this.type = type;
		
		Field fields[] = type.getDeclaredFields();
		IndexField indexField = null;
		
		Class<?> fieldType = null;
		ParameterizedType parameterizedType = null;
		
		//loop through the fields
		for(Field field : fields)
		{
			fieldType = field.getType();
			
			//for collection types get the collection type
			if(Collection.class.isAssignableFrom(fieldType))
			{
				parameterizedType = (ParameterizedType)field.getGenericType();
				fieldType = (Class<?>)parameterizedType.getActualTypeArguments()[0];
			}
			
			//if the field type is not supported for indexing
			if(!supportedTypes.containsKey(fieldType))
			{
				continue;
			}
			
			indexField = field.getAnnotation(IndexField.class);
			
			//if field is not suppose to be indexed
			if(indexField == null)
			{
				continue;
			}
			
			this.fields.put(field.getName(), new FieldIndexDetails(field.getName(), indexField.value(), supportedTypes.get(fieldType)));
		}
	}
	
	public Collection<FieldIndexDetails> getFields()
	{
		return fields.values();
	}
	
	public FieldIndexDetails getField(String name)
	{
		return fields.get(name);
	}
	
	public Class<?> getType()
	{
		return type;
	}
}
