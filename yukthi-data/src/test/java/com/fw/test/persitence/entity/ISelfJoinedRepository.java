package com.fw.test.persitence.entity;

import com.fw.test.persitence.queries.SelfJoinedSearchResult;
import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.repository.annotations.SearchResult;

public interface ISelfJoinedRepository extends ICrudRepository<SelfJoinedEntity>
{
	@SearchResult
	public SelfJoinedSearchResult findByName(String name);
}
