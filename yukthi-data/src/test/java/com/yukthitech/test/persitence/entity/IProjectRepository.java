package com.yukthitech.test.persitence.entity;

import java.util.List;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.SearchFunction;
import com.yukthitech.persistence.repository.annotations.SearchResult;
import com.yukthitech.persistence.repository.search.SearchQuery;

public interface IProjectRepository extends ICrudRepository<Project>
{
	@SearchFunction
	@SearchResult
	public List<ProjectSearchResult> find(SearchQuery query);
	
	@SearchResult
	public ProjectSearchResult findByName(@Condition("name") String name);
}
