package com.yukthi.indexer.es;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yukthi.indexer.IDataIndex;
import com.yukthi.indexer.IndexSearchResult;
import com.yukthi.indexer.search.SearchSettings;

public class TEsDataIndex
{
	private ElasticSearchIndexer indexer = new ElasticSearchIndexer("localhost", 9300, 9200);
	
	private IDataIndex dataIndex;
	
	@BeforeClass
	public void setup()
	{
		dataIndex = indexer.getIndex("test");
	}
	
	private void indexObject(TestBean testBean)
	{
		dataIndex.indexObject(testBean, testBean);
	}
	
	@Test
	public void testIndexing()
	{
		TestBean testBean = new TestBean("test123", "This is text of 123", Arrays.asList("test", "123"), 20);
		dataIndex.indexObject(testBean, testBean);
	}
	
	@Test
	public void testSearch()
	{
		indexObject( new TestBean("abc123", "When working with exact values, you will be working with non-scoring", Arrays.asList("abc", "123", "345"), 10) );
		indexObject( new TestBean("cde345", "ltering queries. Filters are important because they are very fast", Arrays.asList("rty", "435", "654"), 10) );
		indexObject( new TestBean("ghi456", "We’ll talk about the performance benefits of filters later in All About Caching", Arrays.asList("fgh", "123", "abc"), 20) );
		indexObject( new TestBean("bvc456", "We are going to explore the term query first ", Arrays.asList("ree", "322", "sds"), 30) );
		indexObject( new TestBean("pop567", "start by indexing some documents representing products", Arrays.asList("sds", "323", "sds"), 40) );
		indexObject( new TestBean("xop345", " value that we specify. By itself, a term query is simple. It accepts", Arrays.asList("abc", "sdd", "345"), 40) );
		
		SearchSettings searchSettings = new SearchSettings();
		
		IndexSearchResult<TestBean> results = dataIndex.search(new TestBeanSearchQuery1("abc123", null), searchSettings);
		Assert.assertEquals(results.getResultDetails().size(), 1);
		Assert.assertEquals(results.getResults().get(0).getName(), "abc123");
		Assert.assertEquals(results.getResults().get(0).getValue(), 10);
	}
	
	@AfterClass
	public void cleanup()
	{
		dataIndex.clean();
	}
}
