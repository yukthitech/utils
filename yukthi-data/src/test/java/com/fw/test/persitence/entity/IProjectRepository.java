package com.fw.test.persitence.entity;

import java.util.List;

import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.repository.annotations.Condition;
import com.yukthi.persistence.repository.annotations.SearchFunction;
import com.yukthi.persistence.repository.annotations.SearchResult;
import com.yukthi.persistence.repository.search.SearchQuery;

public interface IProjectRepository extends ICrudRepository<Project>
{
	@SearchFunction
	@SearchResult
	public List<ProjectSearchResult> find(SearchQuery query);
	
	@SearchResult
	public ProjectSearchResult findByName(@Condition("name") String name);
}
