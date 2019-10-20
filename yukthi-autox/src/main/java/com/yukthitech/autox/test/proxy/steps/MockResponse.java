package com.yukthitech.autox.test.proxy.steps;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Response being mocked with request-details to be matched.
 * @author akiran
 */
public class MockResponse implements Serializable 
{
	private static final long serialVersionUID = 1L;

	/**
	 * Uri for which this response should be returned.
	 */
	private String uri;
	
	/**
	 * Name of the http method.
	 */
	private String method;

	/**
	 * Headers to be sent as part of response.
	 */
	private Map<String, String> headers;

	/**
	 * Status code to be sent as part response.
	 */
	private int statusCode;
	
	/**
	 * Body to be set as part of response.
	 */
	private String body;
	
	public MockResponse()
	{}
	
	public MockResponse(String uri, String method, Map<String, String> headers, int statusCode, String body)
	{
		this.uri = uri;
		this.method = method;
		this.headers = headers;
		this.statusCode = statusCode;
		this.body = body;
	}

	/**
	 * Checks if current response can be sent for specified request.
	 * @param request request to be served
	 * @return true, if current response is matching with specified request
	 */
	boolean isMatchingRequest(MockRequest request)
	{
		if(!uri.equals(request.getUri()))
		{
			return false;
		}
		
		//TODO: more dynamic conditions, like url patterns, parameter checking etc should be done here
		// in multi threaded env, context matching should be done here
		
		return true;
	}
	
	void writeTo(HttpServletResponse response)
	{
		response.setStatus(statusCode);

		try
		{
			if(body != null)
			{
				PrintWriter pw = response.getWriter();
				pw.print(body);
				pw.close();
			}
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while writing response", ex);
		}
		
	}
	
	/**
	 * This will be invoked once this response is served. This method should return true, if this
	 * response still needs to be retained for future request.
	 * @return
	 */
	boolean canServeMore()
	{
		//TODO: As of now one response can be served only once. Later logic needs to be added for-ever
			// or specific number of times, etc
		return false;
	}
	
	public String getUri()
	{
		return uri;
	}

	public void setUri(String uri)
	{
		this.uri = uri;
	}

	/**
	 * Gets the status code of response.
	 *
	 * @return the status code of response
	 */
	public int getStatusCode() 
	{
		return statusCode;
	}

	/**
	 * Sets the status code of response.
	 *
	 * @param statusCode the new status code of response
	 */
	public void setStatusCode(int statusCode) 
	{
		this.statusCode = statusCode;
	}

	/**
	 * Gets the response body.
	 *
	 * @return the response body
	 */
	public String getBody() 
	{
		return body;
	}

	/**
	 * Sets the response body.
	 *
	 * @param body the new response body
	 */
	public void setBody(String body) 
	{
		this.body = body;
	}

	/**
	 * Gets the headers of the request.
	 *
	 * @return the headers of the request
	 */
	public Map<String, String> getHeaders() 
	{
		return headers;
	}

	/**
	 * Sets the headers of the request.
	 *
	 * @param headers the new headers of the request
	 */
	public void setHeaders(Map<String, String> headers) 
	{
		this.headers = headers;
	}

	/**
	 * Gets the name of the http method.
	 *
	 * @return the name of the http method
	 */
	public String getMethod() 
	{
		return method;
	}

	/**
	 * Sets the name of the http method.
	 *
	 * @param method the new name of the http method
	 */
	public void setMethod(String method) 
	{
		this.method = method;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Req Uri: ").append(uri);
		builder.append(", ").append("Req Method: ").append(method);
		builder.append(", ").append("Res Status Code: ").append(statusCode);
		builder.append(",\n").append("Res Headers: ").append(headers);
		builder.append(",\n").append("Res Body: ").append(body);

		builder.append("]");
		return builder.toString();
	}
}
