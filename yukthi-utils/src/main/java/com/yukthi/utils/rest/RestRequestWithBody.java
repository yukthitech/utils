/**
 * 
 */
package com.yukthi.utils.rest;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents Rest request method with body
 * 
 * @author akiran
 */
public abstract class RestRequestWithBody<T extends RestRequestWithBody<T>> extends RestRequest<T>
{
	/**
	 * Request body
	 */
	private String requestBody;

	/**
	 * Map to hold file fields.
	 */
	private Map<String, FileInfo> fileAttachments = new HashMap<>();

	/**
	 * Object mapper that will be used to convert objects to json
	 */
	private ObjectMapper objectMapper = RestClient.OBJECT_MAPPER;

	/**
	 * HTTP method represented by this request
	 */
	private String method;

	/**
	 * Name of the request body field used, when multipart request is built
	 * (when request has file attachments)
	 */
	private String requestFieldName = "request";

	/**
	 * @param uri
	 */
	public RestRequestWithBody(String uri, String method)
	{
		super(uri);
		this.method = method;
	}
	
	/**
	 * Gets the object mapper that will be used to convert objects to json. By default, the returned object mapper
	 * is a common used by all rest clients and requests (so pay attention when returned mapper is modified).
	 *
	 * @return the object mapper that will be used to convert objects to json
	 */
	public ObjectMapper getObjectMapper()
	{
		return objectMapper;
	}

	/**
	 * Sets the object mapper that will be used to convert objects to json.
	 *
	 * @param objectMapper the new object mapper that will be used to convert objects to json
	 */
	public void setObjectMapper(ObjectMapper objectMapper)
	{
		if(objectMapper == null)
		{
			throw new NullPointerException("Object mapper can not be null.");
		}
		
		this.objectMapper = objectMapper;
	}

	/**
	 * Gets the name of the request body field used, when multipart request is built (when request has file attachments).
	 *
	 * @return the name of the request body field used, when multipart request is built (when request has file attachments)
	 */
	public String getRequestFieldName()
	{
		return requestFieldName;
	}

	/**
	 * Sets the name of the request body field used, when multipart request is built (when request has file attachments).
	 *
	 * @param requestFieldName the new name of the request body field used, when multipart request is built (when request has file attachments)
	 */
	public void setRequestFieldName(String requestFieldName)
	{
		this.requestFieldName = requestFieldName;
	}

	/**
	 * Sets the request body. Note: params and request body can not be used on
	 * same request
	 * 
	 * @param body
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T setBody(String body)
	{
		if(!super.params.isEmpty() || !this.fileAttachments.isEmpty())
		{
			throw new IllegalStateException("Both params and body can not be set on a single request. Param(s) were already set");
		}

		this.requestBody = body;
		return (T) this;
	}

	/**
	 * Converts specified object into json and sets it as request body. Note:
	 * Params and request body can not be used on same request
	 * 
	 * @param object
	 * @return
	 */
	public T setJsonBody(Object object)
	{
		try
		{
			return setBody(objectMapper.writeValueAsString(object));
		} catch(JsonProcessingException ex)
		{
			throw new IllegalArgumentException("Failed to format specified object as json - " + object, ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yodlee.common.rest.RestRequest#addParam(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public T addParam(String name, String value)
	{
		if(requestBody != null)
		{
			throw new IllegalStateException("Both params and body can not be set on a single request. Request body was already set");
		}

		return super.addParam(name, value);
	}

	/**
	 * Adds file param to the request
	 * 
	 * @param field
	 *            Name of the field
	 * @param file
	 *            File to be attached
	 * @param contentType File content mime type
	 */
	public void addAttachment(String field, File file, String contentType)
	{
		if(requestBody != null)
		{
			throw new IllegalStateException("Both params and body can not be set on a single request. Request body was already set");
		}

		this.fileAttachments.put(field, new FileInfo(file, contentType));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yodlee.common.rest.RestRequest#toHttpRequestBase(java.lang.String)
	 */
	@Override
	public HttpRequestBase toHttpRequestBase(String baseUrl) throws UnsupportedEncodingException
	{
		HttpEntityEnclosingRequestBase postRequest = newRequest(baseUrl);

		super.populateHeaders(postRequest);

		// if file params are attached
		if(!this.fileAttachments.isEmpty())
		{
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

			FileInfo fileInfo = null;

			// add files to request body
			for(String name : this.fileAttachments.keySet())
			{
				fileInfo = fileAttachments.get(name);

				if(fileInfo.getContentType() != null)
				{
					builder.addPart(name, new FileBody(fileInfo.getFile(), ContentType.create(fileInfo.getContentType())));
				}
				else
				{
					builder.addPart(name, new FileBody(fileInfo.getFile()));
				}
			}

			if(requestBody != null)
			{
				builder.addPart(requestFieldName, new StringBody( requestBody, ContentType.create(super.getContentType()) ) );
			}
		}
		// if params are present set the request body in the http FORM format
		else if(!super.params.isEmpty())
		{
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>(super.params.size());

			for(String paramName : super.params.keySet())
			{
				urlParameters.add(new BasicNameValuePair(paramName, super.params.get(paramName)));
			}

			postRequest.setEntity(new UrlEncodedFormEntity(urlParameters));
		}
		// if no params are present but request body is present, set body as
		// simple string
		else if(requestBody != null)
		{
			// set the content type if specified
			if(super.getContentType() != null)
			{
				postRequest.setHeader(HttpHeaders.CONTENT_TYPE, super.getContentType());
			}

			postRequest.setEntity(new StringEntity(requestBody));
		}

		return postRequest;
	}

	/**
	 * Factory method. Child classes are expected to override this method and
	 * provide new request object.
	 * 
	 * @param baseUrl
	 * @return
	 */
	protected abstract HttpEntityEnclosingRequestBase newRequest(String baseUrl);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("\n\tRequest Type: ").append(method);
		builder.append("\n\t").append("Resolved URI: ").append(super.getResolvedUri());
		builder.append("\n\t").append("Params: ").append(super.params);

		if(!headers.isEmpty())
		{
			builder.append("\n\t").append("Headers: ").append(super.headers);
		}

		builder.append("\n\t").append("Body: ").append(requestBody);

		builder.append("\n]");
		return builder.toString();
	}
}
