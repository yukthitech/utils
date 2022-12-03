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
package com.yukthitech.indexer.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.yukthitech.indexer.IDataIndex;
import com.yukthitech.indexer.IndexSearchResult;
import com.yukthitech.indexer.search.SearchSettings;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class LuceneDataIndex implements IDataIndex
{
	private static Logger logger = LogManager.getLogger(LuceneDataIndex.class);
	
	private StandardAnalyzer indexAnalyzer = new StandardAnalyzer();
	
	private Directory indexDirectory;
	
	/**
	 * Used to build lucene query strings from query objects.
	 */
	private LuceneQueryBuilder queryBuilder = new LuceneQueryBuilder();
	
	private IndexReader reader;
	
	private IndexSearcher searcher;
	
	private IndexWriter writer;
	
	/**
	 * Used to map pojo to doc and vise-versa.
	 */
	private DocumentMapper documentMapper = new DocumentMapper();
	
	public LuceneDataIndex(File folder)
	{
		try
		{
			if(folder.exists())
			{
				FileUtils.forceMkdir(folder);
			}
			
			indexDirectory = FSDirectory.open(folder.toPath());
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while creating index folder: " + folder.getPath(), ex);
		}
	}
	
	/**
	 * Closes the reader if its open. And opens and returns the writer.
	 * @return
	 * @throws IOException
	 */
	private synchronized IndexWriter getWriter() throws IOException
	{
		if(writer != null)
		{
			return writer;
		}
		
		if(reader != null)
		{
			logger.trace("Closing the reader...");
			reader.close();
			reader = null;
			searcher = null;
		}
		
		logger.trace("Creating the writer...");
		
		IndexWriterConfig config = new IndexWriterConfig(indexAnalyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		
		writer = new IndexWriter(indexDirectory, config);
		
		return writer;
	}
	
	/**
	 * Closes the writer if open. Then opens and returns reader.
	 * @return
	 * @throws IOException
	 */
	private synchronized IndexSearcher getSearcher() throws IOException
	{
		if(searcher != null)
		{
			return searcher;
		}
		
		if(writer != null)
		{
			logger.trace("Closing the writer...");
			
			writer.close();
			writer = null;
		}
		
		logger.trace("Opening reader...");
		IndexReader reader = DirectoryReader.open(indexDirectory);
		searcher = new IndexSearcher(reader);
		
		logger.debug("Number of docs in index: " + reader.maxDoc()); 
		
		return searcher;
	}
	
	@Override
	public synchronized void indexObjects(Collection<? extends Object> objects)
	{
		try
		{
			IndexWriter writer = getWriter();
			List<IndexableField> indexes = null;
			
			for(Object object : objects)
			{
				indexes = documentMapper.toDocument(object);
				
				if(logger.isTraceEnabled())
				{
					String str = indexes.stream()
							.map(fld -> fld.name() + " = " + fld.stringValue())
							.collect(Collectors.joining("\n\t"));
					
					logger.trace("Adding new document with fields: \n\t{}", str);
				}
				
				writer.addDocument(indexes);
			}
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while indexing objects", ex);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> IndexSearchResult<T> executeQuery(String query, SearchSettings searchSettings)
	{
		try
		{
			QueryParser parser = new QueryParser("dummy", indexAnalyzer);
			Query luceneQuery = parser.parse(query);
			
			IndexSearcher searcher = getSearcher();
			TopScoreDocCollector collector = TopScoreDocCollector.create(searchSettings.getResultsLimit(), searchSettings.getResultsLimit());
	        searcher.search(luceneQuery, collector);
	        ScoreDoc[] hits = collector.topDocs().scoreDocs;
	        
	        logger.debug("From search query got number of documents as: {}", hits.length);
			
	        IndexSearchResult<Object> searchResults = new IndexSearchResult<Object>();
	        
	        for(ScoreDoc doc : hits)
	        {
	        	Document filteredDoc = searcher.doc(doc.doc);
	        	searchResults.addResult(documentMapper.mapDocument(filteredDoc), doc.score);
	        }
	        
			return (IndexSearchResult) searchResults;
		} catch(IOException | ParseException ex)
		{
			throw new InvalidStateException("An error occurred while executing search query", ex);
		}
	}

	@Override
	public synchronized <T> IndexSearchResult<T> search(Object queryObj, SearchSettings searchSettings)
	{
		String query = queryBuilder.buildQuery(queryObj);
		logger.debug("From search-query-object of type '{}' constructed lucene search-query as: \n\t{}", queryObj.getClass().getName(), query);
		
		return executeQuery(query, searchSettings);
	}

	@Override
	public long deleteObject(Object deleteQueryObj)
	{
		try
		{
			String query = queryBuilder.buildQuery(deleteQueryObj);
			logger.debug("From delete-query-object of type '{}' constructed lucene delete-query as: {}", deleteQueryObj.getClass().getName(), query);

			QueryParser parser = new QueryParser("dummy", indexAnalyzer);
			Query luceneQuery = parser.parse(query);
			
			IndexWriter indexWriter = getWriter();
			
			long delCount = indexWriter.deleteDocuments(luceneQuery);
			logger.debug("Number of documents deleted: {}", delCount);
			
			return delCount;
		} catch(IOException | ParseException ex)
		{
			throw new InvalidStateException("An error occurred while executing delete query", ex);
		}
	}

	@Override
	public void close()
	{
		try
		{
			if(reader != null)
			{
				reader.close();
				reader = null;
				searcher = null;
			}
	
			if(writer != null)
			{
				writer.close();
				writer = null;
			}
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while closing the index", ex);
		}
	}
}
