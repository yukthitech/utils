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

import com.yukthitech.test.persitence.entity.EntityWithUpdateOperator;
import com.yukthitech.test.persitence.entity.IEntityWUpdateOpRepo;
import com.yukthitech.persistence.repository.RepositoryFactory;

/**
 * @author akiran
 *
 */
public class TUpdateOperator extends TestSuiteBase
{
	@Override
	protected void cleanFactoryAfterClass(RepositoryFactory factory)
	{
		//cleanup the emp table
		factory.dropRepository(EntityWithUpdateOperator.class);
	}

	/**
	 * Tests update operations with update operators
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testUpdateOp(RepositoryFactory factory)
	{
		IEntityWUpdateOpRepo repo = factory.getRepository(IEntityWUpdateOpRepo.class);
		
		//save the entity with initial value
		EntityWithUpdateOperator entity = new EntityWithUpdateOperator(20);
		repo.save(entity);
		
		//invoke ADD operation and verify
		repo.incrementAge(10, entity.getId());

		EntityWithUpdateOperator updatedEntity = repo.findById(entity.getId());
		Assert.assertEquals(updatedEntity.getAge(), 30);
		
		//invoke subtract operation and verify
		repo.decrementAge(5, entity.getId());

		updatedEntity = repo.findById(entity.getId());
		Assert.assertEquals(updatedEntity.getAge(), 25);
	}

	@Test(dataProvider = "repositoryFactories")
	public void testUpdateWithOrder(RepositoryFactory factory)
	{
		IEntityWUpdateOpRepo repo = factory.getRepository(IEntityWUpdateOpRepo.class);
		
		//save the entity with initial value
		repo.save(new EntityWithUpdateOperator(100));
		repo.save(new EntityWithUpdateOperator(200));
		repo.save(new EntityWithUpdateOperator(300));
		repo.save(new EntityWithUpdateOperator(400));
		repo.save(new EntityWithUpdateOperator(500));
		
		Assert.assertTrue(repo.updateAges(200, 1) >= 4);
	}
}
