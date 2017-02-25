package com.yukthitech.indexer.es;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
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
import com.yukthitech.indexer.IDataIndex;
import com.yukthitech.indexer.IndexSearchResult;
import com.yukthitech.indexer.IndexType;
import com.yukthitech.indexer.es.TypeIndexDetails.FieldIndexDetails;
import com.yukthitech.indexer.search.SearchSettings;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.MessageFormatter;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.rest.DeleteRestRequest;
import com.yukthitech.utils.rest.GetRestRequest;
import com.yukthitech.utils.rest.PostRestRequest;
import com.yukthitech.utils.rest.PutRestRequest;
import com.yukthitech.utils.rest.RestClient;
import com.yukthitech.utils.rest.RestRequest;
import com.yukthitech.utils.rest.RestResult;

/**
 * Elastic search implementation of data index.
 * @author akiran
 */
public class EsDataIndex implements IDataIndex
{
	private static Logger logger = LogManager.getLogger(EsDataIndex.class);
	
	public static final String OBJECT_FIELD = "__object";
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
	
	private boolean dataModified = false;
	
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
		addProperties(typeIndexDetails.getFields(), properties, null);
		
		properties.put(OBJECT_FIELD, CommonUtils.<String, String>toMap("type", "string", 
				"index", "no",
				"store", "true"));

		properties.put(OBJECT_TYPE_FIELD, CommonUtils.<String, String>toMap("type", "string", 
				"index", "no",
				"store", "true"));
		
		Map<Object, Object> requestSource = CommonUtils.toMap("properties", properties);
		requestSource = CommonUtils.toMap(type.getName(), requestSource);
		
		if(logger.isDebugEnabled())
		{
			logger.debug("Creating index type '{}' using request source: {}", type.getName(), objectMapper.writeValueAsString(requestSource));
		}
		
		putMappingRequestBuilder.setSource(requestSource);

		PutMappingResponse response = putMappingRequestBuilder.execute().actionGet();
		indexedTypes.put(type, typeIndexDetails);
		
		logger.debug("Type '{}' is successfully added with response - {}", type.getName(), objectMapper.writeValueAsString(response));
	}
	
	private void addProperties(Collection<FieldIndexDetails> fields, Map<String, Map<String, String>> properties, String parentField)
	{
		if(fields == null)
		{
			return;
		}
		
		String name = null;
		String indexType = null;
		Map<String, String> params = null;
		
		for(TypeIndexDetails.FieldIndexDetails field : fields)
		{
			name = parentField != null ? parentField + "." + field.getName() : field.getName();
			
			if(field.getEsDataType() == EsDataType.OBJECT || field.getEsDataType() == EsDataType.MAP)
			{
				params = CommonUtils.<String, String>toMap("type", field.getEsDataType().getName()); 
			}
			else
			{
				indexType = field.getIndexType() == IndexType.ANALYZED ? "analyzed" : "not_analyzed";

				params = CommonUtils.<String, String>toMap("type", field.getEsDataType().getName(), 
						"index", indexType,
						"store", "false");
			}
			
			properties.put(name, params);
			
			addProperties(field.getSubfields(), properties, name);
		}
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
		
		for(TypeIndexDetails.FieldIndexDetails field : fields)
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
			
			if(field.isIgnoreCase() && field.getEsDataType() == EsDataType.STRING)
			{
				value = IndexUtils.toLowerCase(value);
			}
			
			//for sub objects create index objects recursively
			if(field.getEsDataType() == EsDataType.OBJECT)
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String storeObject(Object id, Object indexData, Object data)
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
			ObjectWrapper<Object> idWrapper = new ObjectWrapper<Object>(id);
			
			Map<String, Object> indexObj = toIndexObjectMap(typeIndexDetails.getFields(), data, idWrapper);
			id = idWrapper.getValue();
			
			indexObj.put(OBJECT_FIELD, objectMapper.writeValueAsString(data));
			indexObj.put(OBJECT_TYPE_FIELD, data.getClass().getName());
			
			/*
			client.prepareIndex(indexName, data.getClass().getName(), null)
		        .setSource(objectMapper.writeValueAsString(indexObj))
		        .execute()
		        .actionGet();
		    */
			
			RestRequest<?> request = null;
			
			if(id == null)
			{
				request = new PostRestRequest("/" + indexName + "/" + typeIndexDetails.getType().getName());
				((PostRestRequest)request).setBody(objectMapper.writeValueAsString(indexObj));
			}
			else
			{
				request = new PutRestRequest("/" + indexName + "/" + typeIndexDetails.getType().getName() + "/" + id);
				((PutRestRequest)request).setBody(objectMapper.writeValueAsString(indexObj));
			}
			
			
			RestResult<Object> result = restClient.invokeJsonRequest(request, Object.class);
			
			if(result.getValue() == null)
			{
				throw new InvalidStateException("Failed to index specified object. [Status: {}]", result.getStatusCode());
			}
			
			Map<String, Object> response = (Map)result.getValue();
			logger.debug("Specified object is successfully indexed with id: {}", response.get("_id"));
			
			dataModified = true;
			
			return "" + response.get("_id");
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while indexing data - {}", data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.indexer.IDataIndex#indexObject(java.lang.Object, java.lang.Object)
	 */
	public String indexObject(Object indexData, Object data)
	{
		return storeObject(null, indexData, data);
	}
	
	/*
	@Override
	public void updateObject(Class<?> indexType, Object updateData, Object id)
	{
		TypeIndexDetails typeIndexDetails = indexedTypes.get(indexType);
		
		if(typeIndexDetails == null)
		{
			throw new InvalidArgumentException("Invalid/non-existing index type specified - {}", indexType.getName());
		}
		
		Object updateQuery = UpdateQueryBuilder.buildQuery(typeIndexDetails, updateData);
		
		PostRestRequest request = new PostRestRequest("/" + indexName + "/" + typeIndexDetails.getType().getName() + "/" + id + "/_update");
		request.setJsonBody(updateQuery);
		
		RestResult<Object> result = restClient.invokeJsonRequest(request, Object.class);
		
		if(result.getValue() == null)
		{
			throw new InvalidStateException("No/invalid response obtained from elastic search. [Status Code: {}]", result.getStatusCode());
		}
	}
	*/
	
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> T getObject(Class<?> indexType, Object id)
	{
		TypeIndexDetails typeIndexDetails = indexedTypes.get(indexType);
		
		if(typeIndexDetails == null)
		{
			throw new InvalidArgumentException("Invalid/non-existing index type specified - {}", indexType.getName());
		}
		
		GetRestRequest request = new GetRestRequest("/" + indexName + "/" + typeIndexDetails.getType().getName() + "/" + id);
		RestResult<Object> result = restClient.invokeJsonRequest(request, Object.class);
		
		if(result.getValue() == null)
		{
			throw new InvalidStateException("No/invalid response obtained from elastic search. [Status Code: {}]", result.getStatusCode());
		}
		
		Map<String, Object> response = (Map)result.getValue();
		Object matchedObject = toSource((Map)response.get("_source"));
		
		return (T)matchedObject;
	}

	@Override
	public void updateObject(Object id, Object indexData, Object data)
	{
		if(id == null)
		{
			throw new InvalidArgumentException("No id specified for update");
		}
		
		storeObject(id, indexData, data);
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
	
	private Object toSource(Map<String, Object> source)
	{
		if(source == null)
		{
			return null;
		}
		
		try
		{
			String sourceTypeName = (String)source.get(OBJECT_TYPE_FIELD);
			String sourceJson = (String)source.get(OBJECT_FIELD);
			
			Class<?> sourceType = Class.forName(sourceTypeName);
			return objectMapper.readValue(sourceJson, sourceType);
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "Failed to convert hit into source object");
		}
	}
	
	/**
	 * Commits the changes which are not committed by calling refresh on es.
	 */
	private void commitChanges()
	{
		PostRestRequest request = new PostRestRequest("/" + indexName + "/_refresh");
		RestResult<String> result =  restClient.invokeRequest(request);

		dataModified = false;
		logger.debug("Got refresh/commit response as - {}", result.getValue());
	}

	@SuppressWarnings("unchecked")
	public <T> IndexSearchResult<T> search(Object queryObj, SearchSettings searchSettings)
	{
		try
		{
			if(dataModified)
			{
				logger.debug("As data is modified, performing refresh to ensure newly added data is committed and available for search");
				commitChanges();
			}
			
			TypeQueryDetails queryDetails = getQueryDetails(queryObj.getClass());
			TypeIndexDetails indexDetails = indexedTypes.get(queryDetails.getIndexType());
			
			Map<String, Object> query = queryDetails.buildQuery(queryObj, indexDetails);
			String queryJson = objectMapper.writeValueAsString(query);
			
			logger.debug("Executing search query - \n{}\n", queryJson);
			
			PostRestRequest searchRequest = new PostRestRequest("/" + indexName + "/" + indexDetails.getType().getName() + "/_search");
			searchRequest.setBody(queryJson);
			
			RestResult<EsSearchResult> restResult = restClient.invokeJsonRequest(searchRequest, EsSearchResult.class);
			
			if(restResult.getStatusCode() != 200)
			{
				throw new InvalidStateException("No/invalid response obtained from elastic search. [Status Code: {}]", restResult.getStatusCode());
			}
			
			EsSearchResult searchResult = restResult.getValue();
			
			IndexSearchResult<T> finalResult = new IndexSearchResult<>();
			
			if(searchResult.finalHits() == null)
			{
				return finalResult;
			}
			
			for(EsSearchResult.Hit hit : searchResult.finalHits())
			{
				finalResult.addResult((T)toSource(hit.getSource()), hit.getScore());
			}
			
			return finalResult;
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while executing search operation with query - {}", queryObj);
		}
	}
	
	@Override
	public void deleteObject(Class<?> indexType, Object id)
	{
		logger.debug("Deleting object of type '{}' with id - {}", indexType.getName(), id);
		DeleteRestRequest request = new DeleteRestRequest("/" + indexName + "/" + indexType.getName() + "/" + id);
		RestResult<String> result =  restClient.invokeRequest(request);

		dataModified = true;
		logger.debug("Got refresh/commit response as - {}", result.getValue());
	}

	@Override
	public void clean()
	{
		logger.debug("Deleting index - {}", indexName);
		
		DeleteRestRequest deleteRestRequest = new DeleteRestRequest("/" + indexName);
		RestResult<String> result = restClient.invokeRequest(deleteRestRequest);
		
		int status = result.getStatusCode();
		
		if(status >=200 && status <= 300)
		{
			logger.debug("Index {} deleted successfully", indexName);
		}
	}
}
