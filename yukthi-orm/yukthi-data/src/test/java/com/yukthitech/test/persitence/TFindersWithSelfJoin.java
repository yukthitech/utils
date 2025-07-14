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
package com.yukthitech.test.persitence;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.test.persitence.entity.ISelfJoinedRepository;
import com.yukthitech.test.persitence.entity.SelfJoinedEntity;
import com.yukthitech.test.persitence.queries.SelfJoinedSearchResult;
import com.yukthitech.persistence.repository.RepositoryFactory;


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
