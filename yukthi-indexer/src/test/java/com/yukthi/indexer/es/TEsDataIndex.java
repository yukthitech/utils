package com.yukthi.indexer.es;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yukthi.indexer.IDataIndex;
import com.yukthi.indexer.IndexSearchResult;
import com.yukthi.indexer.IndexSearchResult.ResultDetails;
import com.yukthi.indexer.search.SearchSettings;
import com.yukthi.utils.CommonUtils;

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
		//index without id
		TestBean testBean = new TestBean(null, "test123", "This is text of 123", Arrays.asList("test", "123"), 20);
		String id = dataIndex.indexObject(testBean, testBean);
		
		Assert.assertNotNull(id);
		
		TestBean resBean = dataIndex.getObject(TestBean.class, id);
		Assert.assertEquals(resBean.getName(), "test123");
		Assert.assertEquals(resBean.getText(), "This is text of 123");
		Assert.assertEquals(resBean.getKeys(), Arrays.asList("test", "123"));
		Assert.assertEquals(resBean.getValue(), 20);
		
		//index with id
		testBean = new TestBean(123L, "test456", "This is text of 123", Arrays.asList("test", "123"), 20);
		id = dataIndex.indexObject(testBean, testBean);
		
		Assert.assertEquals(id, "123");

		resBean = dataIndex.getObject(TestBean.class, 123L);
		Assert.assertEquals(resBean.getName(), "test456");
		
		//test get with non-existing id
		resBean = dataIndex.getObject(TestBean.class, 123434434L);
		Assert.assertNull(resBean);
	}
	
	@Test
	public void testUpdateIndexing()
	{
		//index without id
		TestBean testBean = new TestBean(null, "updtTest123", "This is text of 123", Arrays.asList("test", "123"), 20);
		String id = dataIndex.indexObject(testBean, testBean);
		
		Assert.assertNotNull(id);
		

		//update bean with new values
		testBean = new TestBean(null, "updtTest12345", "This is text of 12345", Arrays.asList("test", "123", "456"), 10);
		dataIndex.updateObject(id, testBean, testBean);
		
		//validate the update op
		TestBean resBean = dataIndex.getObject(TestBean.class, id);
		Assert.assertEquals(resBean.getName(), "updtTest12345");
		Assert.assertEquals(resBean.getText(), "This is text of 12345");
		Assert.assertEquals(resBean.getKeys(), Arrays.asList("test", "123", "456"));
		Assert.assertEquals(resBean.getValue(), 10);
	}
	
	/*
	@Test
	public void testUpdate()
	{
		//index without id
		TestBean testBean = new TestBean("test123", "This is text of 123", Arrays.asList("test", "123"), 20);
		String id = dataIndex.indexObject(testBean, testBean);

		TestBeanUpdateQuery updateQuery = new TestBeanUpdateQuery("This is text of 345", null, Arrays.asList("345", "678"), 10);
		dataIndex.updateObject(TestBean.class, updateQuery, id);
		
		TestBean beanFromEs = dataIndex.getObject(TestBean.class, id);
		Assert.assertEquals(beanFromEs.getName(), "test123");
		Assert.assertEquals(beanFromEs.getText(), "This is text of 345");
		Assert.assertEquals(beanFromEs.getKeys(), Arrays.asList("test", "123", "345", "678"));
		Assert.assertEquals(beanFromEs.getValue(), 30);
	}
	*/

	@Test
	public void testSearch()
	{
		indexObject( new TestBean("abc123", "When working with exact values, you will be working with non-scoring", Arrays.asList("abc", "123", "345"), 10) );
		indexObject( new TestBean("cde345", "ltering queries. Filters are important because they are very fast", Arrays.asList("rty", "435", "654"), 10) );
		indexObject( new TestBean("ghi456", "We’ll talk about the performance benefits of filters later in All About Caching", Arrays.asList("fgh", "123", "abc"), 20) );
		indexObject( new TestBean("bvc456", "We are going to explore the term query first ", Arrays.asList("ree", "322", "sds"), 30) );
		indexObject( new TestBean("pop567", "start by indexing some documents representing products", Arrays.asList("sds", "323", "sds"), 40) );
		indexObject( new TestBean("xop345", " value that we specify. By itself, a Term query is simple. It accepts", Arrays.asList("abc", "sdd", "345"), 40) );
		
		SearchSettings searchSettings = new SearchSettings();
		
		//Search with name alone
		IndexSearchResult<TestBean> results = dataIndex.search(new TestBeanSearchQuery1("abc123", null, null, null), searchSettings);
		Assert.assertEquals(results.getResultDetails().size(), 1);
		Assert.assertEquals(results.getResults().get(0).getName(), "abc123");
		Assert.assertEquals(results.getResults().get(0).getValue(), 10);
		
		//Search with text
		results = dataIndex.search(new TestBeanSearchQuery1(null, "working TERM QUERY", null, null), searchSettings);
		Assert.assertEquals(results.getResultDetails().size(), 3);
		
		Set<String> names = results.getResults().stream()
				.map(res -> res.getName())
				.collect(Collectors.toSet());
		
		Assert.assertEquals(names, CommonUtils.toSet("bvc456", "xop345", "abc123"));
		
		//search with tokens
		results = dataIndex.search(new TestBeanSearchQuery1(null, null,  Arrays.asList("123", "345"), null), searchSettings);
		Assert.assertEquals(results.getResultDetails().size(), 3);
		
		names = results.getResults().stream()
				.map(res -> res.getName())
				.collect(Collectors.toSet());
		
		Assert.assertEquals(names, CommonUtils.toSet("xop345", "ghi456", "abc123"));
		
		//search with int value
		results = dataIndex.search(new TestBeanSearchQuery1(null, null,  null, 30), searchSettings);
		Assert.assertEquals(results.getResultDetails().size(), 3);
		
		names = results.getResults().stream()
				.map(res -> res.getName())
				.collect(Collectors.toSet());
		
		Assert.assertEquals(names, CommonUtils.toSet("bvc456", "pop567", "xop345"));
		
		//search with int value and string array comb
		results = dataIndex.search(new TestBeanSearchQuery1(null, null,  Arrays.asList("123", "345"), 30), searchSettings);
		Assert.assertEquals(results.getResultDetails().size(), 1);
		
		names = results.getResults().stream()
				.map(res -> res.getName())
				.collect(Collectors.toSet());
		
		Assert.assertEquals(names, CommonUtils.toSet("xop345"));

		//Search with text and number
		results = dataIndex.search(new TestBeanSearchQuery1(null, "working TERM QUERY", null, 30), searchSettings);
		Assert.assertEquals(results.getResultDetails().size(), 2);
		
		names = results.getResults().stream()
				.map(res -> res.getName())
				.collect(Collectors.toSet());
		
		Assert.assertEquals(names, CommonUtils.toSet("bvc456", "xop345"));
	}
	
	@Test
	public void testFieldBoosting()
	{
		indexObject( new TestBean("abc123", "This is test as 1", null, 1) );
		indexObject( new TestBean("cde345", "This was test as 2", null, 1) );
		indexObject( new TestBean("ghi456", "This has test 3", null, 10) );

		//test with normal query
		SearchSettings searchSettings = new SearchSettings();
		
		IndexSearchResult<TestBean> results = dataIndex.search(new TestBeanSearchQuery1(null, "This test as",  null, null), searchSettings);
		Assert.assertEquals(results.getResultDetails().size(), 3);
		
		Map<String, Double> scores = results.getResultDetails().stream()
										.collect(Collectors.<ResultDetails<TestBean>, String, Double>toMap(resDet -> resDet.getResult().getName(), ResultDetails::getScore));
		
		System.out.println("\n==========>" + scores + "\n");
		Assert.assertTrue(scores.get("ghi456") < scores.get("cde345"));
		Assert.assertTrue(scores.get("ghi456") < scores.get("abc123"));
		
		/////////////////////////////////////////////////
		//test with boost query
		results = dataIndex.search(new TestBeanSearchQueryWithBoost(null, "This test as",  null, null), searchSettings);
		Assert.assertEquals(results.getResultDetails().size(), 3);
		
		scores = results.getResultDetails().stream()
										.collect(Collectors.<ResultDetails<TestBean>, String, Double>toMap(resDet -> resDet.getResult().getName(), ResultDetails::getScore));
		
		System.out.println("\n==========>" + scores + "\n");
		Assert.assertTrue(scores.get("ghi456") > scores.get("cde345"));
		Assert.assertTrue(scores.get("ghi456") > scores.get("abc123"));
	}
	
	@AfterMethod
	public void cleanup()
	{
		dataIndex.clean();
	}
}
