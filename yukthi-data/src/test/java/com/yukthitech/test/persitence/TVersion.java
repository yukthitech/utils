/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
