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

import org.testng.annotations.Test;

import com.yukthitech.persistence.repository.RepositoryFactory;

/**
 * Contains test cases to test foreign constrain loading
 * @author akiran
 */
public class TForeignConstraintLoading extends TestSuiteBase
{
	/**
	 * Tests positive cases for relation loading for all types
	 * 		One to One
	 * 		One to Many
	 * 		Many to One
	 * 		Many to Many
	 * 
	 * Also test cases where join table is specified.
	 * 
	 * Test cases where child is loaded first and then child and vise versa.
	 * 
	 * Join tables
	 * 		when column names are specified and when they are not specified
	 * 
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testRelationLoading(RepositoryFactory factory)
	{
		
	}

	/**
	 * For each relation type, error test cases when collection is found when not expected
	 * and vise versa
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testError_unexpectedCollection(RepositoryFactory factory)
	{
		
	}

	/**
	 * For each relation, error test cases when mappedBy is mentioned on both sides of relation 
	 * and cases where it is not specified on both sides
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testError_doubleMapping(RepositoryFactory factory)
	{
		
	}

	/**
	 * Error test cases where reverse mapping is not proper
	 * Invalid mapping property
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testError_invalidReverseMapping(RepositoryFactory factory)
	{
		
	}

	/**
	 * Error test cases where join tables are mentioned on both ends
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testError_doubleJoinTables(RepositoryFactory factory)
	{
		
	}

	/**
	 * Test cases where join table is not specified for many-to-many relation
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testError_missingJoinTable(RepositoryFactory factory)
	{
		
	}

	/**
	 * Error cases where non-entity is specified for collection relation
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testError_nonEntityCollection(RepositoryFactory factory)
	{
		
	}

	/**
	 * Error cases where type variables are used in collection
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testError_typeVariableCollection(RepositoryFactory factory)
	{
		
	}
}
