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
import com.yukthi.utils.exceptions.InvalidConfigurationException;

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
		private EsDataType esDataType;
		
		/**
		 * Indicates if this id field or not.
		 */
		private boolean idField;
		
		/**
		 * Flag to indicate if case should be ignored for this field
		 */
		private boolean ignoreCase;
		
		public FieldIndexDetails(String name, EsDataType esDataType, IndexField indexField)
		{
			this.name = name;
			this.indexType = indexField.value();
			this.esDataType = esDataType;
			
			this.ignoreCase = indexField.ignoreCase();
			this.idField = indexField.idField();
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
		public EsDataType getEsDataType()
		{
			return esDataType;
		}
		
		public boolean isIgnoreCase()
		{
			return ignoreCase;
		}
		
		/**
		 * Checks if is indicates if this id field or not.
		 *
		 * @return the indicates if this id field or not
		 */
		public boolean isIdField()
		{
			return idField;
		}
	}
	
	private static Map<Class<?>, EsDataType> supportedTypes;
	
	static
	{
		supportedTypes = CommonUtils.toMap(
			byte.class, EsDataType.BYTE,
			Byte.class, EsDataType.BYTE,
			
			short.class, EsDataType.SHORT,
			Short.class, EsDataType.SHORT,

			int.class, EsDataType.INTEGER,
			Integer.class, EsDataType.INTEGER,

			long.class, EsDataType.LONG,
			Long.class, EsDataType.LONG,

			float.class, EsDataType.FLOAT,
			Float.class, EsDataType.FLOAT,

			double.class, EsDataType.DOUBLE,
			Double.class, EsDataType.DOUBLE,

			boolean.class, EsDataType.BOOLEAN,
			Boolean.class, EsDataType.BOOLEAN,

			Date.class, EsDataType.DATE,
			
			String.class, EsDataType.STRING
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
		FieldIndexDetails fieldIndexDetails = null, idFieldDetails = null;
		
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
			
			fieldIndexDetails = new FieldIndexDetails(field.getName(), supportedTypes.get(fieldType), indexField);
			this.fields.put(field.getName(), fieldIndexDetails);
			
			if(fieldIndexDetails.isIdField())
			{
				if(idFieldDetails != null)
				{
					throw new InvalidConfigurationException("In index type '{}' Multiple fields are marked as id fields - [{}, {}]", 
							type.getName(), idFieldDetails.getName(), fieldIndexDetails.getName());
				}
			}
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
