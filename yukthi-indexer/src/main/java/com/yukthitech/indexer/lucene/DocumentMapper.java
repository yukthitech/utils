/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

			Collection<Object> valueCollection = (value instanceof Collection) ? (Collection<Object>) value : Arrays.asList(value);
			
			for(Object objvalue : valueCollection)
			{
				String strValue = objvalue.toString().toLowerCase();
				
				if(field.getIndexType() == IndexType.ANALYZED)
				{
					indexedFields.add(new TextField(field.getName(), strValue, Store.YES));
				}
				else
				{
					indexedFields.add(new StringField(field.getName(), strValue, Store.YES));
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
