/**
 * 
 */
package com.yukthitech.utils.rest;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
	private static final String JSON_CONTENT_TYPE = "application/json";
	
	private static final String TEXT_CONTENT_TYPE = "application/text";
	
	private static final class RequestPart
	{
		private String name;
		private Object value;
		private String contentType;
		
		public RequestPart(String name, Object value, String contentType)
		{
			this.name = name;
			this.value = value;
			
			this.contentType = contentType;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder(super.toString());
			builder.append("[");

			builder.append("Name: ").append(name);
			builder.append(",").append("Value: ").append(value);

			builder.append("]");
			return builder.toString();
		}

	}
	
	/**
	 * Request body
	 */
	private String requestBody;

	/**
	 * Map to hold file fields.
	 */
	private List<RequestPart> multiparts = new ArrayList<RequestPart>();

	/**
	 * Object mapper that will be used to convert objects to json
	 */
	private ObjectMapper objectMapper = RestClient.OBJECT_MAPPER;

	/**
	 * HTTP method represented by this request
	 */
	private String method;

	/**
	 * indicates if this is multipart request
	 */
	private boolean multipartRequest;

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
	 * Checks if is indicates if this is multipart request.
	 *
	 * @return the indicates if this is multipart request
	 */
	public boolean isMultipartRequest()
	{
		return multipartRequest;
	}

	/**
	 * Sets the indicates if this is multipart request.
	 *
	 * @param multipartRequest the new indicates if this is multipart request
	 */
	public void setMultipartRequest(boolean multipartRequest)
	{
		if(requestBody != null || !super.getParams().isEmpty())
		{
			throw new IllegalStateException("Body/params is already set on this request");
		}
		
		this.multipartRequest = multipartRequest;
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
		if(!super.params.isEmpty())
		{
			throw new IllegalStateException("Both params and body can not be set on a single request. Param(s) were already set");
		}
		
		if(multipartRequest)
		{
			throw new IllegalStateException("Body can not be set on multi part request");
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
	@SuppressWarnings("unchecked")
	public T setJsonBody(Object object)
	{
		try
		{
			setBody(objectMapper.writeValueAsString(object));
			super.setContentType(JSON_CONTENT_TYPE);
			
			return (T)this;
		} catch(JsonProcessingException ex)
		{
			throw new IllegalArgumentException("Failed to format specified object as json - " + object, ex);
		}
	}
	
	/**
	 * Gets the request body.
	 *
	 * @return the request body
	 */
	public String getRequestBody()
	{
		return requestBody;
	}
	
	/**
	 * Gets the map to hold file fields.
	 *
	 * @return the map to hold file fields
	 */
	public List<RequestPart> getMultiparts()
	{
		return multiparts;
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
		
		if(multipartRequest)
		{
			throw new IllegalStateException("Params can not be added for multi part request");
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
		if(!multipartRequest)
		{
			throw new IllegalStateException("Attachments can be added only to multi part request");
		}

		this.multiparts.add(new RequestPart(field, new FileInfo(file, contentType), null));
	}
	
	/**
	 * Adds the specified object as json part to this multipart request
	 * @param partName Name of the request part
	 * @param object Object to be added
	 * @return current request instance
	 */
	@SuppressWarnings("unchecked")
	public T addJsonPart(String partName, Object object)
	{
		if(!multipartRequest)
		{
			throw new IllegalStateException("Parts can be added only to multi part request");
		}
		
		try
		{
			String jsonObject = objectMapper.writeValueAsString(object);
			this.multiparts.add(new RequestPart(partName, jsonObject, JSON_CONTENT_TYPE));
		} catch(JsonProcessingException ex)
		{
			throw new IllegalArgumentException("Failed to format specified object as json - " + object, ex);
		}
		
		return (T)this;
	}
	
	/**
	 * Adds the specified object as string part to this multipart request
	 * @param partName Name of the request part
	 * @param object string to be added
	 * @return current request instance
	 */
	public T addTextPart(String partName, String object)
	{
		return addTextPart(partName, object, TEXT_CONTENT_TYPE);
	}

	/**
	 * Adds the specified object as string part to this multipart request
	 * @param partName Name of the request part
	 * @param object string to be added
	 * @param contentType Content type of the part
	 * @return current request instance
	 */
	@SuppressWarnings("unchecked")
	public T addTextPart(String partName, String object, String contentType)
	{
		if(!multipartRequest)
		{
			throw new IllegalStateException("Parts can be added only to multi part request");
		}
		
		this.multiparts.add(new RequestPart(partName, object, contentType));
		
		return (T)this;
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
		if(!this.multiparts.isEmpty())
		{
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

			FileInfo fileInfo = null;

			// add files to request body
			for(RequestPart part : this.multiparts)
			{
				if(part.value instanceof FileInfo)
				{
					fileInfo = (FileInfo)part.value;
					
					if(fileInfo.getContentType() != null)
					{
						builder.addPart(part.name, new FileBody(fileInfo.getFile(), ContentType.create(fileInfo.getContentType())));
					}
					else
					{
						builder.addPart(part.name, new FileBody(fileInfo.getFile()));
					}
				}
				else
				{
					//stringBody = new StringBody( part.value.toString(), ContentType.create(part.contentType) );
					//builder.addTextBody(part.name, part.value.toString(), ContentType.create(part.contentType));
					//builder.addPart(part.name, stringBody);
					builder.addBinaryBody(part.name, part.value.toString().getBytes(), ContentType.create(part.contentType), part.name);
				}
			}
			
			postRequest.setEntity(builder.build());
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
		builder.append("\n\t").append("Multipart: ").append(multipartRequest);
		builder.append("\n\t").append("Params: ").append(super.params);

		if(headers != null && !headers.isEmpty())
		{
			builder.append("\n\t").append("Headers: ").append(super.headers);
		}

		if(requestBody != null)
		{
			builder.append("\n\t").append("Body: ").append(requestBody);
		}
		
		if(multiparts != null && !multiparts.isEmpty())
		{
			builder.append("\n\t").append("Multi parts: ").append(multiparts);
		}

		builder.append("\n]");
		return builder.toString();
	}
}
