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

package com.fw.test.persitence;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fw.test.persitence.entity.EntityWithUpdateOperator;
import com.fw.test.persitence.entity.IEntityWUpdateOpRepo;
import com.yukthi.persistence.repository.RepositoryFactory;

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
}
