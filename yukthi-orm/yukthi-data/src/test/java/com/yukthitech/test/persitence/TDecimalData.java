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

import java.math.BigDecimal;
import java.util.List;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.test.persitence.entity.DecimalDataEntity;
import com.yukthitech.test.persitence.entity.IDecimalDataRepository;

/**
 * Test cases to validate BigDecimal / DECIMAL field support.
 * @author akiran
 */
public class TDecimalData extends TestSuiteBase
{
	@AfterMethod
	public void cleanup(ITestResult result)
	{
		Object params[] = result.getParameters();
		RepositoryFactory factory = (RepositoryFactory)params[0];
		
		factory.dropRepository(DecimalDataEntity.class);
	}
	
	private void assertDecimalEquals(BigDecimal expected, BigDecimal actual)
	{
		Assert.assertNotNull(actual, "Expected decimal value to be non-null");
		Assert.assertEquals(actual.compareTo(expected), 0, 
				"Expected: " + expected + ", Actual: " + actual);
	}

	/**
	 * Tests save and findById round-trip for BigDecimal fields.
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testSaveAndFetch(RepositoryFactory factory)
	{
		IDecimalDataRepository repository = factory.getRepository(IDecimalDataRepository.class);

		DecimalDataEntity data = new DecimalDataEntity("item1", 
				new BigDecimal("12345.6789"), 
				new BigDecimal("18.5000"));
		repository.save(data);
		
		DecimalDataEntity fetched = repository.findById(data.getId());
		Assert.assertNotNull(fetched);
		Assert.assertEquals(fetched.getName(), "item1");
		assertDecimalEquals(new BigDecimal("12345.6789"), fetched.getAmount());
		assertDecimalEquals(new BigDecimal("18.5000"), fetched.getTaxRate());
	}

	/**
	 * Tests update functionality for BigDecimal fields.
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testForUpdate(RepositoryFactory factory)
	{
		IDecimalDataRepository repository = factory.getRepository(IDecimalDataRepository.class);

		DecimalDataEntity data1 = new DecimalDataEntity("item1", 
				new BigDecimal("100.2500"), 
				new BigDecimal("5.0000"));
		repository.save(data1);
		
		DecimalDataEntity data2 = new DecimalDataEntity("item2", 
				new BigDecimal("200.5000"), 
				new BigDecimal("10.0000"));
		repository.save(data2);
		
		DecimalDataEntity updated = new DecimalDataEntity("item1", 
				new BigDecimal("150.7500"), 
				new BigDecimal("7.2500"));
		updated.setId(data1.getId());
		Assert.assertTrue(repository.update(updated));
		
		DecimalDataEntity fetched = repository.findById(data1.getId());
		Assert.assertEquals(fetched.getName(), "item1");
		assertDecimalEquals(new BigDecimal("150.7500"), fetched.getAmount());
		assertDecimalEquals(new BigDecimal("7.2500"), fetched.getTaxRate());
	}

	/**
	 * Tests finder conditions using BigDecimal values.
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testFinders(RepositoryFactory factory)
	{
		IDecimalDataRepository repository = factory.getRepository(IDecimalDataRepository.class);
		
		DecimalDataEntity data1 = new DecimalDataEntity("item1", 
				new BigDecimal("100.0000"), 
				new BigDecimal("12.0000"));
		repository.save(data1);
		
		DecimalDataEntity data2 = new DecimalDataEntity("item2", 
				new BigDecimal("250.0000"), 
				new BigDecimal("5.0000"));
		repository.save(data2);

		List<DecimalDataEntity> resLst = repository.fetchByAmountGreaterThan(new BigDecimal("150.0000"));
		Assert.assertEquals(resLst.size(), 1);
		Assert.assertEquals(resLst.get(0).getName(), "item2");
		assertDecimalEquals(new BigDecimal("250.0000"), resLst.get(0).getAmount());
		
		resLst = repository.fetchByTaxRateLessThan(new BigDecimal("10.0000"));
		Assert.assertEquals(resLst.size(), 1);
		Assert.assertEquals(resLst.get(0).getName(), "item2");
		
		DecimalDataEntity byName = repository.findByName("item1");
		Assert.assertNotNull(byName);
		assertDecimalEquals(new BigDecimal("100.0000"), byName.getAmount());
		assertDecimalEquals(new BigDecimal("12.0000"), byName.getTaxRate());
	}

	/**
	 * Tests null BigDecimal fields are persisted and fetched correctly.
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testNullValues(RepositoryFactory factory)
	{
		IDecimalDataRepository repository = factory.getRepository(IDecimalDataRepository.class);

		DecimalDataEntity data = new DecimalDataEntity("item-null", 
				new BigDecimal("99.9900"), 
				null);
		repository.save(data);
		
		DecimalDataEntity fetched = repository.findById(data.getId());
		Assert.assertNotNull(fetched);
		assertDecimalEquals(new BigDecimal("99.9900"), fetched.getAmount());
		Assert.assertNull(fetched.getTaxRate());
	}
}
