package com.yukthitech.autox.test.rest.steps;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ResponseHandler;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.config.RestPlugin;
import com.yukthitech.utils.rest.RestClient;
import com.yukthitech.utils.rest.RestRequest;
import com.yukthitech.utils.rest.RestResult;

/**
 * Base class for the rest based steps.
 * @author akiran
 */
public abstract class AbstractRestStep extends AbstractStep
{
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Base Url to be used. 
	 */
	@Param(description = "Base url to be used. If specified, this will be used instead of using base url from plugin.", required = false)
	private String baseUrl;
	
	/**
	 * Uri to be invoked.
	 */
	@Param(description = "Uri to be invoked.")
	protected String uri;
	
	/**
	 * Headers to be added to the request.
	 */
	@Param(description = "Headers to be added to the request.", required = false)
	protected Map<String, String> headers = new HashMap<>();
	
	/**
	 * Path variables to be replaced.
	 */
	@Param(description = "Path variables to be replaced.", required = false)
	protected Map<String, String> pathVariables = new HashMap<>();
	
	/**
	 * Path variables to be replaced.
	 */
	@Param(description = "Path variables to be replaced.", required = false)
	protected Map<String, String> params = new HashMap<>();
	
	/**
	 * Expected response type. Default: String.
	 */
	@Param(description = "Expected response type. Default: java.lang.Object", required = false)
	protected Class<?> expectedResponseType = Object.class;
	
	/**
	 * Context attribute name on which result object will be placed, which can be used to fetch status code. default: restResult.
	 */
	@Param(description = "Context attribute name on which result object will be placed, which can be used to fetch status code. default: restResult", required = false)
	protected String resultContextAttribute = "result";
	
	/**
	 * Context attribute name on which response object will be placed. default: response.
	 */
	@Param(description = "Context attribute name on which response object will be placed. default: response", required = false)
	protected String responseContextAttribure = "response";
	
	/**
	 * Request content type to be used. default: null.
	 */
	@Param(description = "Request content type to be used. default: " + IRestConstants.JSON_CONTENT_TYPE, required = false)
	protected String contentType = IRestConstants.JSON_CONTENT_TYPE;
	
	/**
	 * Sets the base Url to be used.
	 *
	 * @param baseUrl the new base Url to be used
	 */
	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}
	
	/**
	 * Sets the uri to be invoked.
	 *
	 * @param uri the new uri to be invoked
	 */
	public void setUri(String uri)
	{
		this.uri = uri;
	}
	
	/**
	 * Request header to be set.
	 * @param name Name of the header.
	 * @param value Value of the header.
	 */
	public void addHeader(String name, String value)
	{
		headers.put(name, value);
	}
	
	/**
	 * Path variables to be set.
	 * @param name name of the variable.
	 * @param value value of the variable.
	 */
	public void addPathVariable(String name, String value)
	{
		pathVariables.put(name, value);
	}
	
	/**
	 * Sets the expected response type. Default: String.
	 *
	 * @param expectedResponseType the new expected response type
	 */
	public void setExpectedResponseType(Class<?> expectedResponseType)
	{
		this.expectedResponseType = expectedResponseType;
	}

	/**
	 * Sets the context attribute name on which result object will be placed, which can be used to fetch status code. default: restResult.
	 *
	 * @param resultContextAttribute the new context attribute name on which result object will be placed, which can be used to fetch status code
	 */
	public void setResultContextAttribute(String resultContextAttribute)
	{
		this.resultContextAttribute = resultContextAttribute;
	}

	/**
	 * Sets the context attribute name on which response object will be placed. default: response.
	 *
	 * @param responseContextAttribure the new context attribute name on which response object will be placed
	 */
	public void setResponseContextAttribure(String responseContextAttribure)
	{
		this.responseContextAttribure = responseContextAttribure;
	}

	/**
	 * Request parameter to be sent.
	 * @param name name of the param
	 * @param value value of the param
	 */
	public void addParam(String name, String value)
	{
		params.put(name, value);
	}
	
	/**
	 * Sets the request content type to be used. default: null.
	 *
	 * @param contentType the new request content type to be used
	 */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	/**
	 * Populates the request with specified headers, params and path variables.
	 * @param context Context to be used to get plugin and default headers
	 * @param request Request to be populated
	 */
	protected void populate(AutomationContext context, RestRequest<?> request, ExecutionLogger exeLogger)
	{
		exeLogger.debug("Populating {} with uri {}"
				+ "\n\tHeaders: {}"
				+ "\n\tPath Variables: {}"
				+ "\n\tParams: {}", request.getClass().getSimpleName(), uri, headers, pathVariables, params);
		
		RestPlugin restPlugin = context.getPlugin(RestPlugin.class);
		
		Map<String, String> defaultHeaders = new HashMap<>( restPlugin.getDefaultHeaders() );
		
		if(!defaultHeaders.isEmpty())
		{
			//replace the expressions if any
			defaultHeaders = AutomationUtils.replaceExpressions(context, defaultHeaders);
			
			for(String name : defaultHeaders.keySet())
			{
				//if default header is overridden dont set it
				if(headers.containsKey(name))
				{
					continue;
				}
				
				request.addHeader(name, defaultHeaders.get(name));
			}
		}
		
		for(String name : headers.keySet())
		{
			request.addHeader(name, headers.get(name));
		}
		
		for(String name : pathVariables.keySet())
		{
			request.addPathVariable(name, pathVariables.get(name));
		}
		
		for(String name : params.keySet())
		{
			request.addParam(name, params.get(name));
		}

		if(contentType != null)
		{
			request.setContentType(contentType);
		}
	}
	
	/**
	 * Invokes the specified rest request.
	 *
	 * @param context Context to be used to get plugin and default headers
	 * @param request Request to be populated
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void invoke(AutomationContext context, RestRequest<?> request, ExecutionLogger exeLogger)
	{
		RestPlugin restPlugin = context.getPlugin(RestPlugin.class);
		
		if(baseUrl != null)
		{
			exeLogger.debug("With [Base url: {}, Expected Response Type: {}] invoking request: \n {}", baseUrl, expectedResponseType, request);
		}
		else
		{
			exeLogger.debug("With [Base url: {}, Expected Response Type: {}] invoking request: \n {}", restPlugin.getBaseUrl(), expectedResponseType, request);
		}
		
		RestClient client = restPlugin.getRestClient(baseUrl);
		
		RestResult<Object> result = null;
		
		ResponseHandler<RestResult<?>> handler = getRestResultHandler(exeLogger);
		
		if(handler != null)
		{
			exeLogger.debug("Using current step handler for processing response and building the result");
			
			result = (RestResult) client.invokeRequest( (RestRequest) request, (ResponseHandler) handler);
		}
		else
		{
			if(expectedResponseType == null)
			{
				result = (RestResult) client.invokeRequest(request);
			}
			else
			{
				result = (RestResult) client.invokeJsonRequest(request, expectedResponseType);
			}
		}
		
		exeLogger.debug("Using context attributes [Result attribute: {}, Response attribute: {}]. Obtained result:\n{}", resultContextAttribute, responseContextAttribure, result);
		
		context.setAttribute(resultContextAttribute, result);
		
		if(result.getValue() != null)
		{
			context.setAttribute(responseContextAttribure, result.getValue());
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.automation.AbstractStep#clone()
	 */
	@Override
	public IStep clone()
	{
		return AutomationUtils.deepClone(this);
	}
	
	protected ResponseHandler<RestResult<?>> getRestResultHandler(ExecutionLogger exeLogger)
	{
		return null;
	}
}
