package com.yukthitech.indexer.lucene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.indexer.IndexType;
import com.yukthitech.indexer.common.DataType;
import com.yukthitech.indexer.common.FieldIndexDetails;
import com.yukthitech.indexer.common.IndexUtils;
import com.yukthitech.indexer.common.TypeIndexDetails;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class DocumentMapper
{
	/**
	 * Field name to hold json content.
	 */
	private static final String FLD_JSON_CONTENT = "__jsonValue";
	
	private static ObjectMapper JSON_WITH_TYPE_CONVERTER = new ObjectMapper() 
	{
		private static final long serialVersionUID = 1L;

		{
			activateDefaultTyping(super.getPolymorphicTypeValidator(), DefaultTyping.NON_FINAL, As.PROPERTY);
			setSerializationInclusion(Include.NON_NULL);
		}
	};
	
	private Map<Class<?>, TypeIndexDetails> indexedTypes = new HashMap<>();

	private synchronized TypeIndexDetails getIndexType(Class<?> type)
	{
		TypeIndexDetails det = indexedTypes.get(type);
		
		if(det != null)
		{
			return det;
		}
		
		det = new TypeIndexDetails(type);
		indexedTypes.put(type, det);
		
		return det;
	}

	/**
	 * Creates index object map from specified data and specified fields.
	 * @param fields
	 * @param data
	 * @param idWrapper Object wrapper to hold id field value.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void fetchIndexFields(Collection<FieldIndexDetails> fields, Object data, List<IndexableField> indexedFields)
	{
		Object value = null;
		
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
			
			//for sub objects create index objects recursively
			if(field.getDataType() == DataType.OBJECT)
			{
				fetchIndexFields(field.getSubfields(), value, indexedFields);
				continue;
			}

			if(field.isIgnoreCase())
			{
				value = IndexUtils.toLowerCase(value);
			}
			
			Collection<Object> valueCollection = (value instanceof Collection) ? (Collection<Object>) value : Arrays.asList(value);
			
			for(Object objvalue : valueCollection)
			{
				if(field.getIndexType() == IndexType.ANALYZED)
				{
					indexedFields.add(new TextField(field.getName(), (String) objvalue, Store.NO));
				}
				else
				{
					indexedFields.add(new StringField(field.getName(), (String) objvalue, Store.NO));
				}
			}
		}
	}
	
	public List<IndexableField> toDocument(Object object)
	{
		TypeIndexDetails typeIndexDetails = getIndexType(object.getClass());
		
		List<IndexableField> indexes = new ArrayList<>();
		fetchIndexFields(typeIndexDetails.getFields(), object, indexes);

		try
		{
			indexes.add(new StringField(FLD_JSON_CONTENT, JSON_WITH_TYPE_CONVERTER.writeValueAsString(object), Store.YES));
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting indexing object to json", ex);
		}
		
		return indexes;
	}

	public Object mapDocument(Document doc)
	{
		try
		{
			String json = doc.get(FLD_JSON_CONTENT);
			return JSON_WITH_TYPE_CONVERTER.readValue(json, Object.class);
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while converting json to indexed object", ex);
		}
	}
}
