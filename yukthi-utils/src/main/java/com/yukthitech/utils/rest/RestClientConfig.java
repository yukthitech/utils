package com.yukthitech.utils.rest;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

public class RestClientConfig
{
	/**
	 * Time to establish connection.
	 */
	private Integer connectionTimeOutMillis;
	
	/**
	 * Time waiting for data (replaces socket timeout).
	 */
	private Integer responseTimeOutMillis;
	
	/**
	 * Time to grab connection from pool.
	 */
	private Integer connectionRequestTimeOutMillis;
	
	private Integer maxRetryAttempts;
	
	/**
	 * Retry duration. Defaults to 2.
	 */
	private Integer retryDurationSec;
	
	private Set<Class<? extends Exception>> retryExceptions;
	
	private BiFunction<HttpRequest, Exception, Boolean> retryFilter;

	public Integer getConnectionTimeOutMillis()
	{
		return connectionTimeOutMillis;
	}

	public RestClientConfig setConnectionTimeOutMillis(Integer connectionTimeOutMillis)
	{
		if(connectionTimeOutMillis != null && connectionTimeOutMillis <= 0)
		{
			throw new IllegalArgumentException("Timeout should be non-zero positive value");
		}
		
		this.connectionTimeOutMillis = connectionTimeOutMillis;
		return this;
	}

	public Integer getResponseTimeOutMillis()
	{
		return responseTimeOutMillis;
	}

	public RestClientConfig setResponseTimeOutMillis(Integer responseTimeOutMillis)
	{
		if(responseTimeOutMillis != null && responseTimeOutMillis <= 0)
		{
			throw new IllegalArgumentException("Timeout should be non-zero positive value");
		}
		
		this.responseTimeOutMillis = responseTimeOutMillis;
		return this;
	}

	public Integer getConnectionRequestTimeOutMillis()
	{
		return connectionRequestTimeOutMillis;
	}

	public RestClientConfig setConnectionRequestTimeOutMillis(Integer connectionRequestTimeOutMillis)
	{
		if(connectionRequestTimeOutMillis != null && connectionRequestTimeOutMillis <= 0)
		{
			throw new IllegalArgumentException("Timeout should be non-zero positive value");
		}
		
		this.connectionRequestTimeOutMillis = connectionRequestTimeOutMillis;
		return this;
	}

	public Integer getMaxRetryAttempts()
	{
		return maxRetryAttempts;
	}

	public Integer getRetryDurationSec()
	{
		return retryDurationSec;
	}

	public Set<Class<? extends Exception>> getRetryExceptions()
	{
		return retryExceptions;
	}

	public BiFunction<HttpRequest, Exception, Boolean> getRetryFilter()
	{
		return retryFilter;
	}

	public RestClientConfig setMaxRetryAttempts(Integer maxRetryAttempts)
	{
		if(maxRetryAttempts != null && maxRetryAttempts <= 0)
		{
			throw new IllegalArgumentException("Attempts should be non-zero positive value");
		}
		
		this.maxRetryAttempts = maxRetryAttempts;
		return this;
	}

	public RestClientConfig setRetryDurationSec(Integer retryDurationSec)
	{
		if(retryDurationSec != null && retryDurationSec <= 0)
		{
			throw new IllegalArgumentException("Duration should be non-zero positive value");
		}
		
		this.retryDurationSec = retryDurationSec;
		return this;
	}

	public RestClientConfig setRetryExceptions(Set<Class<? extends Exception>> retryExceptions)
	{
		this.retryExceptions = retryExceptions;
		return this;
	}

	public RestClientConfig setRetryFilter(BiFunction<HttpRequest, Exception, Boolean> retryFilter)
	{
		this.retryFilter = retryFilter;
		return this;
	}
	
	boolean hasRetryStrategy()
	{
		return maxRetryAttempts != null;
	}
	
	HttpRequestRetryStrategy buildRetryStrategy()
	{
		if(!hasRetryStrategy())
		{
			return null;
		}
		
		DefaultHttpRequestRetryStrategy retryStrategy = new DefaultHttpRequestRetryStrategy(
				maxRetryAttempts, 
				TimeValue.of(retryDurationSec != null ? retryDurationSec : 2, TimeUnit.SECONDS))
		{
			@Override
			protected boolean handleAsIdempotent(HttpRequest request)
			{
				return true;
			}
			
			@Override
			public boolean retryRequest(HttpRequest request, IOException exception, int execCount, HttpContext context)
			{
				if(retryExceptions != null && !retryExceptions.contains(exception.getClass()))
				{
					return false;
				}
				
				if(retryFilter != null)
				{
					return retryFilter.apply(request, exception);
				}
				
				return true;
			}
		};
		
		return retryStrategy;
	}
	
	boolean hasRequestConfig()
	{
		return this.connectionRequestTimeOutMillis != null 
				|| this.connectionTimeOutMillis != null 
				|| this.responseTimeOutMillis != null;
	}
	
	RequestConfig buildRequestConfig()
	{
		if(!hasRequestConfig())
		{
			return null;
		}
		
		RequestConfig.Builder builder = RequestConfig.custom();
		
		if(connectionRequestTimeOutMillis != null)
		{
			builder.setConnectionRequestTimeout(Timeout.of(connectionRequestTimeOutMillis, TimeUnit.MILLISECONDS));
		}
		
		if(connectionTimeOutMillis != null)
		{
			builder.setConnectTimeout(Timeout.of(connectionTimeOutMillis, TimeUnit.MILLISECONDS));
		}
		
		if(responseTimeOutMillis != null)
		{
			builder.setResponseTimeout(Timeout.of(responseTimeOutMillis, TimeUnit.MILLISECONDS));
		}
		
		return builder.build();
	}
	
}
