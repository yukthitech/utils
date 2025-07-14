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

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import com.yukthitech.test.persitence.config.TestConfiguration;
import com.yukthitech.persistence.repository.RepositoryFactory;

/**
 * Base class for test class. This initializes the required data sources and factories for 
 * test classes.
 * @author akiran
 */
public class TestSuiteBase 
{
	protected List<Object[]> factories = new ArrayList<>();
	
	/**
	 * Test NG data provider method to provide data stores and factories
	 * @return
	 */
	@DataProvider(name = "repositoryFactories")
	public Object[][] getDataStores()
	{
		return factories.toArray(new Object[0][]);
	}
	
	/**
	 * Testng before-class method to load required configurations for different data stores
	 */
	@BeforeClass
	public void initFactories()
	{
		//loop through configured data sources
		for(RepositoryFactory factory : TestConfiguration.getTestConfiguration().getRepositoryFactories())
		{
			factories.add(new Object[] {factory});
			initFactoryBeforeClass(factory);
		}
	}
	
	@AfterClass
	public void cleanupFactories()
	{
		RepositoryFactory factory  = null;
		
		for(Object data[] : this.factories)
		{
			try
			{
				factory = (RepositoryFactory)data[0];

				this.cleanFactoryAfterClass(factory);
			}catch(Exception ex)
			{
				throw new RuntimeException(ex);
			}
		}

	}
	
	protected void initFactoryBeforeClass(RepositoryFactory factory)
	{}
	
	protected void cleanFactoryAfterClass(RepositoryFactory factory)
	{}
}
