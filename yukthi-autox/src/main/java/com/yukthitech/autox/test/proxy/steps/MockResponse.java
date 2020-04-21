package com.yukthitech.autox.test.proxy.steps;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Response being mocked with request-details to be matched.
 * @author akiran
 */
public class MockResponse implements Serializable 
{
	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(MockResponse.class);

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
	
	/**
	 * Count left after which this response will expire.
	 */
	private int countLeft = 1;
	
	/**
	 * Wait configuration to be used.
	 */
	private WaitConfig waitConfig;
	
	/**
	 * Flag indicating if this response should stop serving requests.
	 */
	private boolean stopped = false;
	
	/**
	 * Instantiates a new mock response.
	 */
	public MockResponse()
	{}
	
	/**
	 * Instantiates a new mock response.
	 *
	 * @param uri the uri
	 * @param method the method
	 * @param headers the headers
	 * @param statusCode the status code
	 * @param body the body
	 * @param waitConfig the wait config
	 */
	public MockResponse(String uri, String method, Map<String, String> headers, int statusCode, String body, WaitConfig waitConfig)
	{
		if(!uri.endsWith("/"))
		{
			uri += "/";
		}
		
		this.uri = uri;
		this.method = method;
		this.headers = headers;
		this.statusCode = statusCode;
		this.body = body;
		this.waitConfig = waitConfig;
	}
	
	/**
	 * Gets the count left after which this response will expire.
	 *
	 * @return the count left after which this response will expire
	 */
	public int getCountLeft()
	{
		return countLeft;
	}

	/**
	 * Sets the count left after which this response will expire.
	 *
	 * @param countLeft the new count left after which this response will expire
	 */
	public void setCountLeft(int countLeft)
	{
		this.countLeft = countLeft;
	}
	
	/**
	 * Sets the wait configuration to be used.
	 *
	 * @param waitConfig the new wait configuration to be used
	 */
	public void setWaitConfig(WaitConfig waitConfig)
	{
		this.waitConfig = waitConfig;
	}

	/**
	 * Checks if current response can be sent for specified request.
	 * @param request request to be served
	 * @return true, if current response is matching with specified request
	 */
	boolean isMatchingRequest(MockRequest request)
	{
		String reqUri = request.getUri();
		reqUri = reqUri.endsWith("/") ? reqUri : (reqUri + "/");
		
		if(!uri.equals(reqUri))
		{
			return false;
		}
		
		//TODO: more dynamic conditions, like url patterns, parameter checking etc should be done here
		// in multi threaded env, context matching should be done here
		return true;
	}
	
	/**
	 * Write to.
	 *
	 * @param response the response
	 */
	void writeTo(HttpServletResponse response)
	{
		if(waitConfig != null)
		{
			if(waitConfig.getTime() > 0)
			{
				AutomationContext automationContext = AutomationContext.getInstance();
				
				if(automationContext.getExecutionLogger() != null)
				{
					automationContext.getExecutionLogger().debug("Before sending response, waiting for time: {} millis", waitConfig.getTime());
				}
				else
				{
					logger.debug("Before sending response, waiting for time: {} millis", waitConfig.getTime());
				}

				synchronized(this)
				{
					try
					{
						super.wait(waitConfig.getTime());
					}catch(Exception ex)
					{}
				}
			}
			
			if(StringUtils.isNotBlank(waitConfig.getForAttr()))
			{
				AutomationContext automationContext = AutomationContext.getInstance();
				
				if(automationContext.getExecutionLogger() != null)
				{
					automationContext.getExecutionLogger().debug("Before sending response, waiting for attr: {}", waitConfig.getForAttr());
				}
				else
				{
					logger.debug("Before sending response, waiting for attr: {}", waitConfig.getForAttr());					
				}
				
				synchronized(this)
				{
					while(automationContext.getAttribute(waitConfig.getForAttr()) == null && !stopped)
					{
						try
						{
							super.wait(100);
						}catch(Exception ex)
						{}
					}
				}
			}
		}
		
		response.setStatus(statusCode);

		try
		{
			if(body != null)
			{
				PrintWriter pw = response.getWriter();
				pw.print(body);
				pw.close();
			}
			
			countLeft --;
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
		return (countLeft > 0);
	}
	
	/**
	 * Gets the uri for which this response should be returned.
	 *
	 * @return the uri for which this response should be returned
	 */
	public String getUri()
	{
		return uri;
	}

	/**
	 * Sets the uri for which this response should be returned.
	 *
	 * @param uri the new uri for which this response should be returned
	 */
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
	
	/**
	 * Stops the current response from serving any current requests.
	 */
	public synchronized void stop()
	{
		this.stopped = true;
		super.notifyAll();
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
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
		builder.append(", ").append("Available count: ").append(countLeft);
		builder.append(",\n").append("Res Headers: ").append(headers);
		builder.append(",\n").append("Res Body: ").append(body);

		builder.append("]");
		return builder.toString();
	}
}
