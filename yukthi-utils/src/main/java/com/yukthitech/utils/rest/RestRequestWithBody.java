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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Represents Rest request method with body
 * 
 * @author akiran
 */
public abstract class RestRequestWithBody<T extends RestRequestWithBody<T>> extends RestRequest<T>
{
	private static final String JSON_CONTENT_TYPE = "application/json";
	
	private static final String TEXT_CONTENT_TYPE = "application/text";
	
	private static enum PartType
	{
		TEXT,
		
		BINARY,
		
		FILE
	}
	
	private static final class RequestPart
	{
		private String name;
		private Object value;
		private String contentType;
		private PartType partType;
		private String charset;
		
		public RequestPart(String name, Object value, String contentType, PartType partType, String charset)
		{
			this.name = name;
			this.value = value;
			this.contentType = contentType;
			this.partType = partType;
			this.charset = charset;
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
			builder.append(",").append("Content Type: ").append(contentType);
			builder.append(",").append("Charset: ").append(charset);
			builder.append(",").append("Type: ").append(partType);
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
	private Boolean multipartRequest;
	
	/**
	 * Form fields.
	 */
	protected Map<String, String> formFields = new HashMap<String, String>();
	
	/**
	 * Charset to be used for body content.
	 */
	protected String contentCharset;
	
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
	 * Adds a form field with specified name and value.
	 * @param name
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T addFormField(String name, String value)
	{
		if(requestBody != null)
		{
			throw new IllegalStateException("Both form-fields and body can not be set on a single request. Request body was already set");
		}
		
		if(Boolean.TRUE.equals(multipartRequest))
		{
			throw new IllegalStateException("Form-fields can not be added for multi part request");
		}

		multipartRequest = false;
		this.formFields.put(name, value);
		return (T) this;
	}

	/**
	 * Adds a form field with specified name and value. Value will be converted to json string before adding.
	 * @param name
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T addJsonFormField(String name, Object value)
	{
		try
		{
			addFormField(name, RestClient.OBJECT_MAPPER.writeValueAsString(value));
			return (T) this;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while coverting specified object to json", ex);
		}
	}

	/**
	 * Gets value of form-fields
	 * 
	 * @return the form fields
	 */
	public Map<String, String> getFormFields()
	{
		return formFields;
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
		if(!formFields.isEmpty())
		{
			throw new IllegalStateException("Both formFields and body can not be set on a single request. Param(s) were already set");
		}
		
		if(Boolean.TRUE.equals(multipartRequest))
		{
			throw new IllegalStateException("Body can not be set on multi part request");
		}

		multipartRequest = false;
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
			this.contentCharset = "UTF-8";
			
			return (T)this;
		} catch(JsonProcessingException ex)
		{
			throw new IllegalArgumentException("Failed to format specified object as json - " + object, ex);
		}
	}
	
	/**
	 * Sets the charset to be used for body content.
	 *
	 * @param contentCharset
	 *            the new charset to be used for body content
	 */
	public void setContentCharset(String contentCharset)
	{
		this.contentCharset = contentCharset;
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
		addAttachment(field, null, file, contentType);
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
	public void addAttachment(String field, String name, File file, String contentType)
	{
		if(Boolean.FALSE.equals(multipartRequest))
		{
			throw new IllegalStateException("Attachments can be added only to multi part request");
		}
		
		name = StringUtils.isBlank(name) ? file.getName() : name;

		multipartRequest = true;
		this.multiparts.add(new RequestPart(field, new FileInfo(name, file, contentType), null, PartType.FILE, null));
	}

	public void addBinaryPart(String field, String name, File file, String contentType)
	{
		if(Boolean.FALSE.equals(multipartRequest))
		{
			throw new IllegalStateException("Attachments can be added only to multi part request");
		}
		
		name = StringUtils.isBlank(name) ? file.getName() : name;

		multipartRequest = true;
		this.multiparts.add(new RequestPart(field, new FileInfo(name, file, contentType), contentType, PartType.BINARY, null));
	}

	public void addBinaryPart(String field, String name, byte[] binaryContent, String contentType)
	{
		if(Boolean.FALSE.equals(multipartRequest))
		{
			throw new IllegalStateException("Attachments can be added only to multi part request");
		}
		
		multipartRequest = true;
		this.multiparts.add(new RequestPart(field, binaryContent, contentType, PartType.BINARY, name));
	}

	/**
	 * Adds the specified object as json part to this multipart request
	 * @param partName Name of the request part
	 * @param object Object to be added
	 * @return current request instance
	 */
	public T addJsonPart(String partName, Object object)
	{
		return addJsonPart(partName, object, null);
	}
	
	/**
	 * Adds the specified object as json part to this multipart request
	 * @param partName Name of the request part
	 * @param object Object to be added
	 * @param charset Charset to be used.
	 * @return current request instance
	 */
	@SuppressWarnings("unchecked")
	public T addJsonPart(String partName, Object object, String charset)
	{
		if(Boolean.FALSE.equals(multipartRequest))
		{
			throw new IllegalStateException("Parts can be added only to multi part request");
		}
		
		try
		{
			multipartRequest = true;
			String jsonObject = objectMapper.writeValueAsString(object);
			this.multiparts.add(new RequestPart(partName, jsonObject, JSON_CONTENT_TYPE, PartType.TEXT, charset));
		} catch(JsonProcessingException ex)
		{
			throw new IllegalArgumentException("Failed to format specified object as json - " + object, ex);
		}
		
		return (T)this;
	}
	
	/**
	 * Adds the specified object as string part to this multipart request
	 * @param partName Name of the request part
	 * @param text string to be added
	 * @return current request instance
	 */
	public T addTextPart(String partName, String text)
	{
		return addTextPart(partName, text, TEXT_CONTENT_TYPE, null);
	}

	/**
	 * Adds the specified object as string part to this multipart request
	 * @param partName Name of the request part
	 * @param text string to be added
	 * @param contentType Content type of the part
	 * @return current request instance
	 */
	public T addTextPart(String partName, String text, String contentType)
	{
		return addTextPart(partName, text, contentType, null);
	}
	
	/**
	 * Adds the specified object as string part to this multipart request
	 * @param partName Name of the request part
	 * @param text string to be added
	 * @param contentType Content type of the part
	 * @param charset charset to be used
	 * @return current request instance
	 */
	@SuppressWarnings("unchecked")
	public T addTextPart(String partName, String text, String contentType, String charset)
	{
		if(Boolean.FALSE.equals(multipartRequest))
		{
			throw new IllegalStateException("Parts can be added only to multi part request");
		}
		
		multipartRequest = true;
		this.multiparts.add(new RequestPart(partName, text, contentType, PartType.TEXT, charset));
		
		return (T)this;
	}

	@Override
	public HttpUriRequestBase toHttpRequestBase(String baseUrl) throws UnsupportedEncodingException, URISyntaxException
	{
		String uri = getResolvedUri();
		String fullUrl = FULL_URL_PATTERN.matcher(uri).matches() ? uri : (baseUrl + uri);
		
		URIBuilder uriBuilder = new URIBuilder(fullUrl);
		
		for(String paramName : super.params.keySet())
		{
			uriBuilder.addParameter(paramName, super.params.get(paramName));
		}

		HttpUriRequestBase request = newRequest(uriBuilder.build());
		super.populateHeaders(request);

		// if file params are attached
		if(!this.multiparts.isEmpty())
		{
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.STRICT);

			FileInfo fileInfo = null;

			// add files to request body
			for(RequestPart part : this.multiparts)
			{
				if(part.partType == PartType.FILE)
				{
					fileInfo = (FileInfo)part.value;
					
					if(fileInfo.getContentType() != null)
					{
						builder.addPart(part.name, new FileBody(fileInfo.getFile(), ContentType.create(fileInfo.getContentType()), fileInfo.getName()));
					}
					else
					{
						builder.addPart(part.name, new FileBody(fileInfo.getFile(), ContentType.DEFAULT_BINARY, fileInfo.getName()));
					}
				}
				else if(part.partType == PartType.TEXT)
				{
					builder.addTextBody(part.name, (String) part.value, ContentType.create(part.contentType, part.charset));
				}
				else
				{
					if(part.value instanceof FileInfo)
					{
						fileInfo = (FileInfo) part.value;
						builder.addBinaryBody(part.name, fileInfo.getFile(), ContentType.create(part.contentType), fileInfo.getName());
					}
					else if(part.value instanceof byte[])
					{
						builder.addBinaryBody(part.name, (byte[]) part.value, ContentType.create(part.contentType), part.name);
					}
					else
					{
						builder.addBinaryBody(part.name, part.value.toString().getBytes(), ContentType.create(part.contentType), part.name);
					}
				}
			}
			
			request.setEntity(builder.build());
		}
		// if form-fields are present set the request body in the http FORM format
		else if(!formFields.isEmpty())
		{
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>(formFields.size());

			for(String paramName : formFields.keySet())
			{
				urlParameters.add(new BasicNameValuePair(paramName, formFields.get(paramName)));
			}

			request.setEntity(new UrlEncodedFormEntity(urlParameters));
		}
		// if no params are present but request body is present, set body as
		// simple string
		else if(requestBody != null)
		{
			// set the content type if specified
			if(super.getContentType() != null)
			{
				request.setHeader(HttpHeaders.CONTENT_TYPE, super.getContentType());
			}

			if(this.contentCharset == null)
			{
				request.setEntity(new StringEntity(requestBody));
			}
			else
			{
				request.setEntity(new StringEntity(requestBody, Charset.forName(contentCharset)));
			}
		}

		return request;
	}

	/**
	 * Factory method. Child classes are expected to override this method and
	 * provide new request object.
	 * 
	 * @param baseUrl
	 * @return
	 */
	protected abstract HttpUriRequestBase newRequest(URI resolvedUri);

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
