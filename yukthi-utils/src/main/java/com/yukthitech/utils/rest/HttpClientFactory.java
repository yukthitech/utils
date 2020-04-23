/**
 * 
 */
package com.yukthitech.utils.rest;

import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
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
			public long getKeepAliveDuration(HttpResponse response, HttpContext context)
			{
				HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));

				while(it.hasNext())
				{
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();

					if(StringUtils.isNotBlank(value) && param.equalsIgnoreCase("timeout"))
					{
						return Long.parseLong(value) * 1000;
					}
				}

				return 5 * 1000;
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
