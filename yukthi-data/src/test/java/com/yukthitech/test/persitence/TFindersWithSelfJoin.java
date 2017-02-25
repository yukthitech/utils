package com.fw.test.persitence;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.test.persitence.entity.ISelfJoinedRepository;
import com.yukthitech.test.persitence.entity.SelfJoinedEntity;
import com.yukthitech.test.persitence.queries.SelfJoinedSearchResult;


public class TFindersWithSelfJoin extends TestSuiteBase
{
	@Override
	protected void cleanFactoryAfterClass(RepositoryFactory factory)
	{
		//cleanup the emp table
		factory.dropRepository(SelfJoinedEntity.class);
	}
	
	@Test(dataProvider = "repositoryFactories")
	public void testWithAllValues(RepositoryFactory factory)
	{
		ISelfJoinedRepository repo = factory.getRepository(ISelfJoinedRepository.class);

		SelfJoinedEntity user1 = new SelfJoinedEntity("CreatingUser", null, null);
		SelfJoinedEntity user2 = new SelfJoinedEntity("UpdatingUser", null, null);
		
		repo.save(user1);
		repo.save(user2);
		
		SelfJoinedEntity testUser = new SelfJoinedEntity("user", user1, user2);
		repo.save(testUser);
		
		SelfJoinedSearchResult result = repo.findByName("user");
		
		Assert.assertEquals(result.getName(), "user");
		Assert.assertEquals(result.getCreatedBy(), "CreatingUser");
		Assert.assertEquals(result.getUpdatedBy(), "UpdatingUser");
	}

	@Test(dataProvider = "repositoryFactories")
	public void testWithNullValues(RepositoryFactory factory)
	{
		ISelfJoinedRepository repo = factory.getRepository(ISelfJoinedRepository.class);

		SelfJoinedEntity user1 = new SelfJoinedEntity("CreatingUser2", null, null);
		SelfJoinedEntity user2 = new SelfJoinedEntity("UpdatingUser2", null, null);
		
		repo.save(user1);
		repo.save(user2);
		
		SelfJoinedEntity testUser = new SelfJoinedEntity("user2", user1, null);
		repo.save(testUser);
		
		SelfJoinedSearchResult result = repo.findByName("user2");
		
		Assert.assertEquals(result.getName(), "user2");
		Assert.assertEquals(result.getCreatedBy(), "CreatingUser2");
		Assert.assertNull(result.getUpdatedBy());
	}
}
