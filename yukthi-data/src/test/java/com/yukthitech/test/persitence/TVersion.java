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

import com.yukthitech.test.persitence.entity.EntityWithVersion;
import com.yukthitech.test.persitence.entity.IEntityWVerRepo;
import com.yukthitech.persistence.repository.RepositoryFactory;

/**
 * @author akiran
 *
 */
public class TVersion extends TestSuiteBase
{
	@Override
	protected void cleanFactoryAfterClass(RepositoryFactory factory)
	{
		//cleanup the emp table
		factory.dropRepository(EntityWithVersion.class);
	}

	/**
	 * Tests version field functionality
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testVersion(RepositoryFactory factory)
	{
		IEntityWVerRepo repo = factory.getRepository(IEntityWVerRepo.class);
		
		//save the entity with initial value
		EntityWithVersion entity = new EntityWithVersion(20);
		repo.save(entity);
		
		//invoke ADD operation and verify value and version
		repo.incrementAge(10, entity.getId());

		EntityWithVersion updatedEntity = repo.findById(entity.getId());
		Assert.assertEquals(updatedEntity.getAge(), 30);
		Assert.assertEquals((int)updatedEntity.getVersion(), 2);
		
		//invoke subtract operation and verify
		repo.decrementAge(5, entity.getId());

		updatedEntity = repo.findById(entity.getId());
		Assert.assertEquals(updatedEntity.getAge(), 25);
		Assert.assertEquals((int)updatedEntity.getVersion(), 3);
	}
}
