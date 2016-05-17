package com.yukthi.indexer.es;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthi.indexer.IDataIndex;
import com.yukthi.indexer.IndexType;
import com.yukthi.indexer.search.SearchSettings;
import com.yukthi.utils.CommonUtils;
import com.yukthi.utils.MessageFormatter;
import com.yukthi.utils.exceptions.InvalidArgumentException;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.utils.rest.GetRestRequest;
import com.yukthi.utils.rest.RestClient;
import com.yukthi.utils.rest.RestResult;

/**
 * Elastic search implementation of data index.
 * @author akiran
 */
public class EsDataIndex implements IDataIndex
{
	private static Logger logger = LogManager.getLogger(EsDataIndex.class);
	
	private static final String OBJECT_FIELD = "__object";
	private static final String OBJECT_TYPE_FIELD = "__objectType";
	
	/**
	 * Name of the current index.
	 */
	private String indexName;
	
	/**
	 * Elastic search client to be used.
	 */
	private Client client;
	
	/**
	 * Object mapper that can be used for json conversion.
	 */
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private Map<Class<?>, TypeIndexDetails> indexedTypes = new HashMap<>();
	
	private Map<Class<?>, TypeQueryDetails> queryTypes = new HashMap<>();
	
	private RestClient restClient;
	
	public EsDataIndex(String indexName, Client client, RestClient restClient)
	{
		this.indexName = indexName;
		this.client = client;
		
		this.restClient = restClient;
		
		checkAndCreateIndex();
	}
	
	private void checkAndCreateIndex()
	{
		IndicesExistsResponse res = client.admin().indices().prepareExists(indexName).execute().actionGet();

		if(res.isExists())
		{
			logger.debug("Found index '{}' already exists", indexName);
			return;
		}

		CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName);
		createIndexRequestBuilder.execute().actionGet();
		
		logger.debug("Specified index not found. Created new index with name - {}", indexName);
	}
	
	/**
	 * Checks if the specified type is already 
	 * @param type
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private synchronized boolean isIndexTypeExists(Class<?> type) throws Exception
	{
		if(indexedTypes.containsKey(type))
		{
			return true;
		}
		
		String url = MessageFormatter.format("/{}/_mapping/{}", indexName, type.getName());
		
		GetRestRequest getRequest = new GetRestRequest(url);
		RestResult<String> result = restClient.invokeRequest(getRequest);
		
		if(result.getValue() == null)
		{
			return false;
		}
		
		Map<String, Object> response = (Map)objectMapper.readValue(result.getValue(), Object.class);
		
		if(response == null || response.isEmpty())
		{
			return false;
		}

		logger.debug("Found type '{}' already defined in current index", type.getName());
		indexedTypes.put(type, new TypeIndexDetails(type));
		return true;
	}
	
	/**
	 * Adds specified type details to the index.
	 * @param type
	 * @throws JsonProcessingException 
	 */
	private synchronized void addIndexType(Class<?> type) throws JsonProcessingException
	{
		if(indexedTypes.containsKey(type))
		{
			return;
		}

		TypeIndexDetails typeIndexDetails = new TypeIndexDetails(type);
		
		if(typeIndexDetails.getFields().isEmpty())
		{
			throw new InvalidArgumentException("No index fields found in type - {}", type.getName());
		}
		
		logger.debug("Specified type is not defined. Creating type: {}", type.getName());
		
		PutMappingRequestBuilder putMappingRequestBuilder = client.admin().indices().preparePutMapping(indexName);
		putMappingRequestBuilder.setType(type.getName());
		
		Map<String, Map<String, String>> properties = new HashMap<String, Map<String,String>>();
		
		for(TypeIndexDetails.FieldIndexDetails field : typeIndexDetails.getFields())
		{
			properties.put(field.getName(), CommonUtils.<String, String>toMap("type", field.getEsDataType(), 
					"index", field.getIndexType() == IndexType.ANALYZED ? "analyzed" : "not_analyzed",
					"store", "false"));
		}
		
		properties.put(OBJECT_FIELD, CommonUtils.<String, String>toMap("type", "string", 
				"index", "no",
				"store", "true"));

		properties.put(OBJECT_TYPE_FIELD, CommonUtils.<String, String>toMap("type", "string", 
				"index", "no",
				"store", "true"));
		
		Map<Object, Object> requestSource = CommonUtils.toMap("properties", properties);
		requestSource = CommonUtils.toMap(type.getName(), requestSource);
		
		putMappingRequestBuilder.setSource(requestSource);

		PutMappingResponse response = putMappingRequestBuilder.execute().actionGet();
		indexedTypes.put(type, typeIndexDetails);
		
		logger.debug("Type '{}' is successfully added with response - {}", type.getName(), objectMapper.writeValueAsString(response));
	}

	/* (non-Javadoc)
	 * @see com.yukthi.indexer.IDataIndex#indexObject(java.lang.Object, java.lang.Object)
	 */
	public void indexObject(Object indexData, Object data)
	{
		try
		{
			logger.debug("Indexing object of type '{}' with index object type - {}", data.getClass().getName(), indexData.getClass().getName());
			
			Class<?> type = indexData.getClass();
			
			if(!isIndexTypeExists(type))
			{
				addIndexType(type);
			}
			
			TypeIndexDetails typeIndexDetails = indexedTypes.get(type);
			Map<String, Object> indexObj = new HashMap<>();
			Object value = null;
			
			for(TypeIndexDetails.FieldIndexDetails field : typeIndexDetails.getFields())
			{
				try
				{
					value = PropertyUtils.getProperty(indexData, field.getName());
				}catch(Exception ex)
				{
					throw new InvalidStateException(ex, "An error occurred while fetching property - {}", field.getName());
				}
				
				if(value == null)
				{
					continue;
				}
				
				indexObj.put(field.getName(), value);
			}
			
			indexObj.put(OBJECT_FIELD, objectMapper.writeValueAsString(data));
			indexObj.put(OBJECT_TYPE_FIELD, data.getClass().getName());
			
			client.prepareIndex(indexName, data.getClass().getName(), null)
		        .setSource(objectMapper.writeValueAsString(indexObj))
		        .execute()
		        .actionGet();
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while indexing data - {}", data);
		}
	}
	
	/**
	 * Gets and loads (if required) the query details for specified search query type.
	 * @param queryType
	 * @return
	 * @throws Exception
	 */
	private synchronized TypeQueryDetails getQueryDetails(Class<?> queryType) throws Exception
	{
		TypeQueryDetails typeQueryDetails = queryTypes.get(queryType);
		
		if(typeQueryDetails != null)
		{
			return typeQueryDetails;
		}
		
		typeQueryDetails = new TypeQueryDetails(queryType);
		
		Class<?> indexType = typeQueryDetails.getIndexType();
		
		if(!isIndexTypeExists(indexType))
		{
			addIndexType(indexType);
		}
		
		this.queryTypes.put(queryType, typeQueryDetails);
		return typeQueryDetails;
	}

	public <T> List<T> search(Object queryObj, SearchSettings searchSettings)
	{
		try
		{
			TypeQueryDetails queryDetails = getQueryDetails(queryObj.getClass());
			TypeIndexDetails indexDetails = indexedTypes.get(queryDetails.getIndexType());
			
			Map<String, Object> query = queryDetails.buildQuery(queryObj, indexDetails);
			
			logger.debug("Executing search query - \n{}\n", query);
			
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while executing search operation with query - {}", queryObj);
		}
		
		return null;
	}
}
