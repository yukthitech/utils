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

import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.message.BasicHeaderElementIterator;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author akiran
 *
 */
public class HttpClientFactory
{
	private static Logger logger = LogManager.getLogger(HttpClientFactory.class);

	private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 500;
	private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;
	public static final Pattern PROXY_PATTERN = Pattern.compile("([\\w\\.\\-]+)\\:(\\d+)");

	public static final String PROP_PROXY = "http.proxy";

	private static HttpClientFactory instance;

	PoolingHttpClientConnectionManager connectionManager = null;
	ConnectionKeepAliveStrategy keepAliveStrategy;
	HttpHost proxyHost;
	
	/**
	 * Creates ssl context which trust any certificate.
	 * @return
	 */
	private static SSLContext createSslContext()
	{
		try
		{
			/*
		     *  fix for
		     *    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
		     *       sun.security.validator.ValidatorException:
		     *           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
		     *               unable to find valid certification path to requested target
		     */
		    TrustManager[] trustAllCerts = new TrustManager[] {
		       new X509TrustManager() {
		          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		            return null;
		          }
	
		          public void checkClientTrusted(X509Certificate[] certs, String authType) {  }
	
		          public void checkServerTrusted(X509Certificate[] certs, String authType) {  }
	
		       }
		    };
		    
		    SSLContext sc = SSLContext.getInstance("SSL");
		    sc.init(null, trustAllCerts, new java.security.SecureRandom());
		    return sc;
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while initializing SSL context", ex);
		}
	}

	HttpClientFactory()
	{
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(createSslContext(), NoopHostnameVerifier.INSTANCE);

		final Registry<ConnectionSocketFactory> registry = RegistryBuilder
				.<ConnectionSocketFactory> create()
				.register("http", new PlainConnectionSocketFactory())
				.register("https", sslsf)
				.build();

		connectionManager = new PoolingHttpClientConnectionManager(registry);
		connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
		connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);

		keepAliveStrategy = new ConnectionKeepAliveStrategy()
		{
			public TimeValue getKeepAliveDuration(HttpResponse response, HttpContext context)
			{
				BasicHeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));

				while(it.hasNext())
				{
					HeaderElement he = it.next();
					String param = he.getName();
					String value = he.getValue();

					if(StringUtils.isNotBlank(value) && param.equalsIgnoreCase("timeout"))
					{
						return TimeValue.of(Long.parseLong(value), TimeUnit.SECONDS);
					}
				}

				return TimeValue.of(5, TimeUnit.SECONDS);
			}
		};

		String proxy = System.getProperty(PROP_PROXY);

		if(StringUtils.isNotBlank(proxy))
		{
			proxyHost = parseProxyHost(proxy);
			logger.debug("Using default http proxy {}:{}", proxyHost.getHostName(), proxyHost.getPort());
		}
	}
	
	private HttpHost parseProxyHost(String hostPort)
	{
		Matcher matcher = PROXY_PATTERN.matcher(hostPort);

		if(!matcher.matches())
		{
			throw new IllegalStateException(String.format("Invalid proxy specified - %s. Expected pattern - proxy-host:port", hostPort));
		}

		String host = matcher.group(1);
		Integer port = Integer.parseInt(matcher.group(2));
		return new HttpHost(host, port);
	}

	/**
	 * @return the instance
	 */
	public static synchronized HttpClientFactory getInstance()
	{
		if(instance == null)
		{
			instance = new HttpClientFactory();
		}

		return instance;
	}

	HttpClientBuilder newClientBuilder()
	{
		return HttpClients.custom();
	}

	DefaultProxyRoutePlanner newDefaultProxyRoutePlanner(HttpHost host)
	{
		return new DefaultProxyRoutePlanner(host);
	}

	/**
	 * Creates http client using proxy, is specified.
	 * @param proxyHostStr proxy host string in host:port format
	 * @return http client with proxy
	 */
	public CloseableHttpClient newHttpClient(String proxyHostStr)
	{
		HttpClientBuilder clientBuilder = newClientBuilder();
		clientBuilder.setConnectionManager(connectionManager);
		clientBuilder.setKeepAliveStrategy(keepAliveStrategy);
		clientBuilder.setConnectionManagerShared(true);

		if(StringUtils.isNotBlank(proxyHostStr))
		{
			HttpHost proxy = parseProxyHost(proxyHostStr);
			clientBuilder.setRoutePlanner(newDefaultProxyRoutePlanner(proxy));
		}
		else if(proxyHost != null)
		{
			clientBuilder.setRoutePlanner(newDefaultProxyRoutePlanner(proxyHost));
		}
		
		return clientBuilder.build();
	}

	public CloseableHttpClient newHttpClient()
	{
		return newHttpClient(null);
	}

	public static synchronized void reset()
	{
		if(instance == null)
		{
			return;
		}

		instance.connectionManager.close();
		instance = null;
	}
}
