package com.yukthi.indexer.es;

import java.net.InetAddress;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.yukthi.indexer.IDataIndex;
import com.yukthi.indexer.IDataIndexManager;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.utils.rest.RestClient;

/**
 * Embedded elastic search implementation of index manager.
 * 
 * @author akiran
 */
public class ElasticSearchIndexer implements IDataIndexManager
{
	private Client client;
	
	private RestClient restClient;
	
	public ElasticSearchIndexer(String host, int port, int httpPort)
	{
		/*
		Settings.Builder elasticsearchSettings = Settings.settingsBuilder().put("http.enabled", "false").put("path.home", dataDirectory);

		Node node = new NodeBuilder().local(true).settings(elasticsearchSettings.build()).node();
		this.client = node.client();
		*/
		
		try
		{
			client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
		}catch(Exception ex)
		{
			throw new InvalidStateException(ex, "An error occurred while creating client");
		}
		
		this.restClient = new RestClient("http://" + host + ":" + httpPort);
	}

	/* (non-Javadoc)
	 * @see com.fw.ai.agent.persistence.index.IIndexManager#close()
	 */
	public void close()
	{
		client.close();
		restClient.close();
	}

	public IDataIndex getIndex(String name)
	{
		return new EsDataIndex(name, client, restClient);
	}
}
