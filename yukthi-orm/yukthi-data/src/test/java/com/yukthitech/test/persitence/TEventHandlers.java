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

import java.util.HashSet;
import java.util.Set;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.yukthitech.test.persitence.entity.Employee;
import com.yukthitech.test.persitence.entity.IEmployeeRepository;
import com.yukthitech.persistence.listeners.EntityEvent;
import com.yukthitech.persistence.listeners.EntityEventHandler;
import com.yukthitech.persistence.listeners.EntityEventType;
import com.yukthitech.persistence.repository.RepositoryFactory;

/**
 * Test cases to test basic CRUD functionality
 * @author akiran
 */
public class TEventHandlers extends TestSuiteBase
{
	public class EventHandler
	{
		@EntityEventHandler(eventType = EntityEventType.PRE_SAVE)
		public void presave()
		{
			events.add("save");
		}

		@EntityEventHandler(eventType = EntityEventType.PRE_SAVE)
		public void presave1(EntityEvent event)
		{
			Assert.assertEquals(event.getEventType(), EntityEventType.PRE_SAVE);
			Assert.assertNotNull(event.getEntity());
			Assert.assertNotNull(event.getRepositoryFactory());
			events.add("save1");
		}

		@EntityEventHandler(eventType = EntityEventType.POST_SAVE)
		public void postsave(EntityEvent event)
		{
			Assert.assertEquals(event.getEventType(), EntityEventType.POST_SAVE);
			Assert.assertNotNull(event.getEntity());
			Assert.assertNotNull(event.getRepositoryFactory());
			events.add("post-save");
		}

		@EntityEventHandler(eventType = EntityEventType.PRE_UPDATE)
		public void preupdate(EntityEvent event)
		{
			Assert.assertEquals(event.getEventType(), EntityEventType.PRE_UPDATE);
			Assert.assertNotNull(event.getEntity());
			Assert.assertNotNull(event.getRepositoryFactory());
			events.add("pre-update");
		}
		
		@EntityEventHandler(eventType = EntityEventType.POST_UPDATE)
		public void postupdate(EntityEvent event)
		{
			Assert.assertEquals(event.getEventType(), EntityEventType.POST_UPDATE);
			Assert.assertNotNull(event.getEntity());
			Assert.assertNotNull(event.getRepositoryFactory());
			events.add("post-update");
		}
	}
	
	private Set<String> events = new HashSet<>();
	
	
	
	@AfterMethod
	public void cleanup(ITestResult result)
	{
		Object params[] = result.getParameters();
		RepositoryFactory factory = (RepositoryFactory)params[0];
		
		//cleanup the emp table
		factory.dropRepository(Employee.class);
	}
	

	/**
	 * Tests the update functionality
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testForEvents(RepositoryFactory factory)
	{
		factory.registerListeners(new EventHandler());
		events.clear();
		
		IEmployeeRepository empRepository = factory.getRepository(IEmployeeRepository.class);
	
		Assert.assertEquals(events.size(), 0);
		
		Employee emp = new Employee("12345", "kranthi@kk.com", "kranthi", "90232333", 28);
		empRepository.save(emp);
		
		Assert.assertEquals(events.size(), 3);
		
		//update the emp with different emp id and email id
		Employee empForUpdate = new Employee("12345", "kranthi123@kk.com", "kranthi12", "12390232333", 28);
		empForUpdate.setId(emp.getId());
		Assert.assertTrue(empRepository.update(empForUpdate));

		Assert.assertEquals(events.size(), 5);
	}
}
