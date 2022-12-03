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
package com.yukthitech.test.persitence.config;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.persistence.repository.RepositoryFactory;

/**
 * Provides data source configuration for test cases
 * 
 * @author akiran
 */
public class TestConfiguration
{
	private static TestConfiguration instance = null;

	private List<RepositoryFactory> repositoryFactories = new ArrayList<>();

	private TestConfiguration()
	{}

	public static synchronized TestConfiguration getTestConfiguration()
	{
		if(instance == null)
		{
			instance = new TestConfiguration();
			XMLBeanParser.parse(TestConfiguration.class.getResourceAsStream("/test-configuration.xml"), instance);
		}

		return instance;
	}

	/**
	 * Adds value to {@link #repositoryFactories repositoryFactories}
	 *
	 * @param repositoryFactory
	 *            repositoryFactory to be added
	 */
	public void addRepositoryFactory(RepositoryFactory repositoryFactory)
	{
		if(repositoryFactories == null)
		{
			repositoryFactories = new ArrayList<RepositoryFactory>();
		}

		repositoryFactories.add(repositoryFactory);
	}

	/**
	 * Fetches configured repository factories
	 * @return
	 */
	public List<RepositoryFactory> getRepositoryFactories()
	{
		return repositoryFactories;
	}
}
