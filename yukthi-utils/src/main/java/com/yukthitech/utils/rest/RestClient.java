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
package com.yukthitech.utils.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * A utility over the Apache http client which eases the invocation of Rest APIs
 * @author akiran
 */
public class RestClient
{
	private static Logger logger = Logger.getLogger(RestClient.class.getName());
	
	private static final int MAX_STR_LENGTH = 1000;
	
	static class RestResultHandler implements HttpClientResponseHandler<RestResult<String>>
	{
		public RestResult<String> handleResponse(ClassicHttpResponse response) throws ClientProtocolException,IOException
		{
			int status = response.getCode();
			String value = null;
			
			logger.log(Level.FINER, "Got response-status as " + status);
			
			HttpEntity entity = response.getEntity();
			
			try
			{
				value = entity != null? EntityUtils.toString(entity): null;
			}catch(Exception ex)
			{
				logger.log(Level.SEVERE, "An error occurred while fetching response content", ex);
				value = null;
			}
			
			logger.log(Level.FINE, String.format("Got response status as %s and body as: %s", status, truncate(value)));
			
			if(StringUtils.isBlank(value))
			{
				value = null;
			}
			
			RestResult<String> result = new RestResult<String>(value, status, new HttpResponse(response));
			
			Header headers[] =  response.getHeaders();
			
			if(headers != null)
			{
				for(Header header : headers)
				{
					result.addHeader(header.getName(), header.getValue());
				}
			}
			
			return result;
		}

	}
	
	/**
	 * On need basis truncates the long value for logging.
	 * @param content
	 * @return
	 */
	private static String truncate(String content)
	{
		if(content == null)
		{
			return null;
		}
		
		if(content.length() <= MAX_STR_LENGTH)
		{
			return content;
		}
		
		return content.substring(0, MAX_STR_LENGTH) + "...";
	}

	static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	/**
	 * Base url of the API server
	 */
	private String baseUrl;

	/**
	 * Apache http client to invoke rest requests
	 */
	CloseableHttpClient httpclient;

	/**
	 * Object mapper that will be used to parse json responses
	 */
	private ObjectMapper objectMapper = OBJECT_MAPPER;
	
	/**
	 * Listener that can listen to rest client events
	 */
	private IRestClientListener restClientListener;

	/**
	 * A base url of the API server (eg: http://localhost:8080/test), which will get prepended to each request being invoked
	 * @param baseUrl
	 */
	public RestClient(String baseUrl, String proxyHostPort)
	{
		int len = baseUrl.length();

		//remove ending "/" if any
		if(baseUrl.endsWith("/"))
		{
			baseUrl = baseUrl.substring(0, len - 1);
		}

		this.baseUrl = baseUrl;
		httpclient = HttpClientFactory.getInstance().newHttpClient(proxyHostPort);
	}
	
	public RestClient(String baseUrl)
	{
		this(baseUrl, null);
	}

	/**
	 * @return the {@link #restClientListener restClientListener}
	 */
	public IRestClientListener getRestClientListener()
	{
		return restClientListener;
	}

	/**
	 * @param restClientListener the {@link #restClientListener restClientListener} to set
	 */
	public void setRestClientListener(IRestClientListener restClientListener)
	{
		this.restClientListener = restClientListener;
	}

	/**
	 * Gets value of objectMapper. By default, the returned object mapper
	 * is a common used by all rest clients and requests (so pay attention when returned mapper is modified).
	 * @return the objectMapper
	 */
	public ObjectMapper getObjectMapper()
	{
		return objectMapper;
	}

	/**
	 * Sets value for objectMapper
	 * @param objectMapper the objectMapper to set
	 */
	public void setObjectMapper(ObjectMapper objectMapper)
	{
		if(objectMapper == null)
		{
			throw new NullPointerException("Object mapper can not be null");
		}
		
		this.objectMapper = objectMapper;
	}

	/**
	 * Gets value of baseUrl 
	 * @return the baseUrl
	 */
	public String getBaseUrl()
	{
		return baseUrl;
	}

	/**
	 * Invokes the provided request. The response obtained should be in json format which
	 * will be converted to "expectedResponseType" instance and will be returned.
	 * @param request Request to be invoked
	 * @param expectedResponseType Expected response type, which will be used to convert json response
	 * @return Response (json) converted object as part of {@link RestResult}
	 */
	public <T> RestResult<T> invokeJsonRequest(RestRequest<?> request, Class<T> expectedResponseType)
	{
		return invokeJsonRequest(request, TypeFactory.defaultInstance().constructType(expectedResponseType));
	}

	/**
	 * To be invoked when expected result is collection
	 * @param request Request to be invoked
	 * @param collectionType Expected return collection type
	 * @param expectedResponseType Expected return collection element type
	 * @return Rest response
	 */
	public <T, C extends Collection<T>> RestResult<C> invokeJsonRequestForList(RestRequest<?> request, Class<C> collectionType, Class<T> expectedResponseType)
	{
		JavaType collectionJavaType = TypeFactory.defaultInstance().constructCollectionType(collectionType, expectedResponseType);
		return invokeJsonRequest(request, collectionJavaType);
	}

	/**
	 * To be invoked when expected result is having wrapper with single type argument
	 * @param request Request to be invoked
	 * @param wrapperType Expected return wrapper type
	 * @param expectedResponseType Expected return collection element type
	 * @return Rest response
	 */
	public <T, W> RestResult<W> invokeJsonRequest(RestRequest<?> request, Class<W> wrapperType, Class<T> expectedResponseType)
	{
		JavaType wrapperJavaType = TypeFactory.defaultInstance().constructParametricType(wrapperType, expectedResponseType);
		return invokeJsonRequest(request, wrapperJavaType);
	}

	/**
	 * Invokes the provided request. The response obtained should be in json format which
	 * will be converted to "expectedResponseType" instance and will be returned.
	 * @param request Request to be invoked
	 * @param expectedResponseType Expected response type, which will be used to convert json response
	 * @return Response (json) converted object as part of {@link RestResult}
	 */
	public <T> RestResult<T> invokeJsonRequest(RestRequest<?> request, final JavaType expectedResponseType)
	{
		//invoked the request
		RestResult<String> stringResult = makeRequest(request, new RestResultHandler());
		T resultValue = null;
		String parseError = null;

		//if response has body
		if(stringResult.getValue() != null)
		{
			try
			{
				if(logger.isLoggable(Level.INFO))
				{
					logger.log(Level.INFO, String.format("Got response as %s", truncate(stringResult.getValue())));
				}
				
				//convert the response json into required object
				resultValue = objectMapper.readValue(stringResult.getValue(), expectedResponseType);
			}catch(Exception ex)
			{
				logger.log(Level.SEVERE, "An error occurred while parsing json response", ex);
				//throw new RestInvocationException("An error occurred while parsing json response", ex);
				resultValue = null;
				parseError = "" + ex;
			}
		}
		
		//return final result
		RestResult<T> result = new RestResult<T>(resultValue, stringResult.getStatusCode(), stringResult.getHttpResponse());
		result.setParseError(parseError);
		result.setHeaders(stringResult.getHeaders());
		
		if(restClientListener != null)
		{
			logger.log(Level.FINER, "Calling rest client listener before sending JSON result to caller");
			restClientListener.postrequest(request, result);
		}
			
		return result;
	}

	/**
	 * Invokes the specified request and returns the response code and body as result.
	 * @param request Request to be invoked
	 * @return Response body and status as {@link RestResult}
	 */
	public RestResult<String> invokeRequest(RestRequest<?> request)
	{
		RestResult<String> result = makeRequest(request, new RestResultHandler());
		
		if(restClientListener != null)
		{
			logger.log(Level.FINER, "Calling rest client listener before sending result to caller");
			restClientListener.postrequest(request, result);
		}
		
		return result;
	}
	
	/**
	 * Invokes the request with custom handler
	 * @param request request to processed
	 * @param handler Handler to handle response
	 * @return result of processing
	 */
	public <T> RestResult<T> invokeRequest(RestRequest<?> request, IRestResponseHandler<RestResult<T>> handler)
	{
		RestResult<T> result = makeRequest(request, new ResponseHandlerAdapter<>(handler));
		
		if(restClientListener != null)
		{
			logger.log(Level.FINER, "Calling rest client listener before sending result to caller");
			restClientListener.postrequest(request, result);
		}
		
		return result;
	}
	
	private <T> RestResult<T> makeRequest(RestRequest<?> request, HttpClientResponseHandler<RestResult<T>> handler)
	{
		try
		{
			if(restClientListener != null)
			{
				logger.log(Level.FINER, "Calling rest client listener before sending request to server");
				restClientListener.prerequest(request);
			}
			
			//dont print request details of secured request
			if(request.isSecured())
			{
				logger.log(Level.FINE, "Invoking request [Base Url - {}]", baseUrl);
			}
			else
			{
				logger.log(Level.FINE, String.format("Invoking request [Base Url - %s]: %s", baseUrl, request));
			}
			
			//build http client request
			HttpUriRequestBase convertedRequest = request.toHttpRequestBase(baseUrl);
			
			//invoke the request and capture the response
			RestResult<T> result = httpclient.execute(convertedRequest, handler);

			return result;
		}catch(Exception ex)
		{
			throw new RestInvocationException("An error occurred while invoking request: " + request.getUri(), ex);
		}
	}

	/**
	 * Closes underlying http client
	 */
	public void close()
	{
		try
		{
			httpclient.close();
		}catch(IOException ex)
		{
			throw new IllegalStateException("An error occurred while closing http-client", ex);
		}
	}
}
