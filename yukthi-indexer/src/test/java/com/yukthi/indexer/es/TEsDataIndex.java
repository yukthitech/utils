package com.yukthi.indexer.es;

import java.util.Arrays;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yukthi.indexer.IDataIndex;

public class TEsDataIndex
{
	private ElasticSearchIndexer indexer = new ElasticSearchIndexer("localhost", 9300, 9200);
	
	private IDataIndex dataIndex;
	
	@BeforeClass
	public void setup()
	{
		dataIndex = indexer.getIndex("test");
	}
	
	@Test
	public void testIndexing()
	{
		TestBean testBean = new TestBean("test123", "This is text of 123", Arrays.asList("test", "123"), 20);
		dataIndex.indexObject(testBean, testBean);
	}
}
