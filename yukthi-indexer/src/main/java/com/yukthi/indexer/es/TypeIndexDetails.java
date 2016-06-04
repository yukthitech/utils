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
		
		/**
		 * Subfields for object field type.
		 */
		private Map<String, FieldIndexDetails> subfields;
		
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
		
		public Collection<FieldIndexDetails> getSubfields()
		{
			if(subfields == null)
			{
				return null;
			}
			
			return subfields.values();
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
		this.fetchFields(type, fields);
	}
	
	private void fetchFields(Class<?> type, Map<String, FieldIndexDetails> fieldMap)
	{
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
			
			indexField = field.getAnnotation(IndexField.class);
			
			//if field is not suppose to be indexed
			if(indexField == null)
			{
				continue;
			}
			
			//if the field type is not supported for indexing
			if(supportedTypes.containsKey(fieldType))
			{
				fieldIndexDetails = new FieldIndexDetails(field.getName(), supportedTypes.get(fieldType), indexField);
			}
			else if(Map.class.equals(fieldType))
			{
				fieldIndexDetails = new FieldIndexDetails(field.getName(), EsDataType.MAP, indexField);
			}
			else
			{
				fieldIndexDetails = new FieldIndexDetails(field.getName(), EsDataType.OBJECT, indexField);
				fieldIndexDetails.subfields = new HashMap<>();
				
				fetchFields(fieldType, fieldIndexDetails.subfields);
			}
			
			fieldMap.put(field.getName(), fieldIndexDetails);
			
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
		FieldIndexDetails fieldDetails = fields.get(name);
		
		//if it is simple field field would be found directly
		if(fieldDetails != null)
		{
			return fieldDetails;
		}
		
		//if name does not refer to nested field
		if(!name.contains("."))
		{
			return null;
		}
		
		//divide the field path into parts and try to find the field
		String fieldParts[] = name.split("\\.");
		FieldIndexDetails parentFieldDetails = null, curFieldDetails = null;
		
		for(int i = 0; i < fieldParts.length; i++)
		{
			//for the first property
			if(parentFieldDetails == null)
			{
				parentFieldDetails = fields.get(fieldParts[i]);
				
				//if unable to identify primary part itself
				if(parentFieldDetails == null)
				{
					return null;
				}
				
				continue;
			}
			
			//if current field does not have any sub fields
			if(parentFieldDetails.subfields == null)
			{
				//if map is encountered in the field path, return map field details
				if(EsDataType.MAP == parentFieldDetails.esDataType)
				{
					return parentFieldDetails;
				}
				
				return null;
			}
			
			curFieldDetails = parentFieldDetails.subfields.get(fieldParts[i]);
			
			//if next field is not found in parent field
			if(curFieldDetails == null)
			{
				return null;
			}
			
			parentFieldDetails = curFieldDetails;
		}
		
		return curFieldDetails;
	}
	
	public Class<?> getType()
	{
		return type;
	}
}
