package com.fw.test.persitence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fw.test.persitence.entity.Employee;
import com.fw.test.persitence.entity.IEmployeeRepository;
import com.fw.test.persitence.queries.EmpSearchResult;
import com.yukthi.persistence.repository.RepositoryFactory;
import com.yukthi.persistence.repository.search.SearchQuery;


public class TFindersWithOrder extends TestSuiteBase
{
	@Override
	protected void initFactoryBeforeClass(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		repo.save(new Employee("1230", "user0@test.com", "abc", "1234560", 20));
		repo.save(new Employee("1231", "user1@test.com", "xyz", "1234561", 25));
		repo.save(new Employee("1232", "user2@test.com", "def", "1234562", 30));
		repo.save(new Employee("1233", "user3@test.com", "zie", "1234563", 35));
		repo.save(new Employee("1234", "user4@test.com", "ghi", "1234564", 40));
	}

	@Override
	protected void cleanFactoryAfterClass(RepositoryFactory factory)
	{
		//cleanup the emp table
		factory.dropRepository(Employee.class);
	}
	
	/**
	 * Tests finder based on finder method name
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testFindWithOrder(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		List<EmpSearchResult> results = repo.findResultsByNameWithOrder(null);

		List<String> names = new ArrayList<>();
		
		for(EmpSearchResult res : results)
		{
			names.add(res.getEmpName());
		}
		
		
		Assert.assertEquals(names, Arrays.asList("abc", "def", "ghi", "xyz", "zie"));
	}

	/**
	 * Tests search query when order columns are specified
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testSearchWithOrder(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		SearchQuery searchQuery = new SearchQuery(
		);
		
		searchQuery.setOrderByFields(Arrays.asList("name"));
		
		List<Employee> results = repo.search(searchQuery);
		
		List<String> names = new ArrayList<>();
		
		for(Employee res : results)
		{
			names.add(res.getName());
		}
		
		Assert.assertEquals(names, Arrays.asList("abc", "def", "ghi", "xyz", "zie"));
	}

	/**
	 * Tests search query when limit is specified
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testSearchWithLimit(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		SearchQuery searchQuery = new SearchQuery(
		);
		
		searchQuery.setOrderByFields(Arrays.asList("name"));
		searchQuery.setResultsLimitCount(3);
		
		List<Employee> results = repo.search(searchQuery);
		
		List<String> names = new ArrayList<>();
		
		for(Employee res : results)
		{
			names.add(res.getName());
		}
		
		Assert.assertEquals(names, Arrays.asList("abc", "def", "ghi"));
	}
}
