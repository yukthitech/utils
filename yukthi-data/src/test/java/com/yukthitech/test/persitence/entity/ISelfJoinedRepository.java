package com.fw.test.persitence.entity;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.SearchResult;
import com.yukthitech.test.persitence.queries.SelfJoinedSearchResult;

public interface ISelfJoinedRepository extends ICrudRepository<SelfJoinedEntity>
{
	@SearchResult
	public SelfJoinedSearchResult findByName(String name);
}
