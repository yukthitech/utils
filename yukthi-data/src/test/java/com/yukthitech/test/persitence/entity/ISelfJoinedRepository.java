package com.yukthitech.test.persitence.entity;

import com.yukthitech.test.persitence.queries.SelfJoinedSearchResult;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.SearchResult;

public interface ISelfJoinedRepository extends ICrudRepository<SelfJoinedEntity>
{
	@SearchResult
	public SelfJoinedSearchResult findByName(String name);
}
