package com.yukthitech.indexer.lucene;

import java.util.Set;

import com.yukthitech.indexer.search.SearchCondition;

public class FileSearchQuery
{
	@SearchCondition
	private Set<String> nameParts;
	
	@SearchCondition
	private String fileName;
	
	@SearchCondition
	private String content;

	public FileSearchQuery(Set<String> nameParts)
	{
		this.nameParts = nameParts;
	}

	public FileSearchQuery(String fileName, String content)
	{
		this.fileName = fileName;
		this.content = content;
	}
}
