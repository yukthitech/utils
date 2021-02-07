package com.yukthitech.indexer.lucene;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yukthitech.indexer.IndexSearchResult;
import com.yukthitech.indexer.search.SearchSettings;

public class TestLuceneIndexing
{
	private LuceneDataIndex dataIndex;
	
	@BeforeClass
	private void setup() throws Exception
	{
		File dataDir = new File("tmp-data-dir");
		
		if(dataDir.exists())
		{
			FileUtils.deleteDirectory(dataDir);
		}
		
		dataIndex = new LuceneDataIndex(dataDir);
	}
	
	private void indexDir(File... folders) throws Exception
	{
		for(File folder : folders)
		{
			File files[] = folder.listFiles();
			List<FileInfo> objLst = new ArrayList<FileInfo>();
			
			for(File file : files)
			{
				if(file.isDirectory())
				{
					continue;
				}
				
				objLst.add(new FileInfo(file));
			}
			
			dataIndex.indexObjects(objLst);
		}
	}
	
	@Test
	public void testIndexing() throws Exception
	{
		indexDir(
			new File("./src/main/java/com/yukthitech/indexer"),
			new File("./src/main/java/com/yukthitech/indexer/lucene")
		);
	}
	
	@Test(dependsOnMethods = "testIndexing")
	public void testSearching() throws Exception
	{
		FileSearchQuery query = new FileSearchQuery("IndexField.java".toLowerCase(), null);
		IndexSearchResult<FileInfo> fileRes = dataIndex.search(query, new SearchSettings());
		
		List<FileInfo> files = fileRes.getResults();
		Assert.assertEquals(files.size(), 1);
		Assert.assertEquals(files.get(0).getFileName(), "IndexField.java");
		Assert.assertEquals(files.get(0).getNameParts(), Arrays.asList("index", "field", ".java"));
		Assert.assertTrue(files.get(0).getSize() > 0);
	}

	@Test(dependsOnMethods = "testSearching")
	public void testContentSearching() throws Exception
	{
		FileSearchQuery query = new FileSearchQuery(null, "how indexing be done ");
		IndexSearchResult<FileInfo> fileRes = dataIndex.search(query, new SearchSettings());
		
		List<FileInfo> files = fileRes.getResults();
		Assert.assertEquals(files.size(), 1);
		Assert.assertEquals(files.get(0).getFileName(), "IndexField.java");
		Assert.assertEquals(files.get(0).getNameParts(), Arrays.asList("index", "field", ".java"));
		Assert.assertTrue(files.get(0).getSize() > 0);
	}
}
