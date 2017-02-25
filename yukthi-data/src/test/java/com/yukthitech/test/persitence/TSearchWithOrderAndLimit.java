package com.fw.test.persitence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.persistence.OrderByField;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.annotations.OrderByType;
import com.yukthitech.persistence.repository.search.SearchCondition;
import com.yukthitech.persistence.repository.search.SearchQuery;
import com.yukthitech.test.persitence.entity.Employee;
import com.yukthitech.test.persitence.entity.IEmployeeRepository;
import com.yukthitech.test.persitence.queries.DynamicEmpSearchResult;
import com.yukthitech.test.persitence.queries.EmpSearchResult;


public class TSearchWithOrderAndLimit extends TestSuiteBase
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
		
		searchQuery.setOrderByFields(Arrays.asList(
			new OrderByField("name", OrderByType.ASC)
		));
		
		List<Employee> results = repo.search(searchQuery);
		
		List<String> names = new ArrayList<>();
		
		results.forEach(e -> names.add(e.getName()));
		
		Assert.assertEquals(names, Arrays.asList("abc", "def", "ghi", "xyz", "zie"));
		
		//test descending order
		searchQuery.setOrderByFields(Arrays.asList(
			new OrderByField("name", OrderByType.DESC)
		));
		
		results = repo.search(searchQuery);
		names.clear();
		results.forEach(e -> names.add(e.getName()));
		
		Assert.assertEquals(names, Arrays.asList( "zie", "xyz", "ghi", "def", "abc"));
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
		
		searchQuery.setOrderByFields(Arrays.asList(
			new OrderByField("name", OrderByType.ASC)
		));
		searchQuery.setResultsLimit(3);
		
		List<Employee> results = repo.search(searchQuery);
		
		List<String> names = new ArrayList<>();
		
		for(Employee res : results)
		{
			names.add(res.getName());
		}
		
		Assert.assertEquals(names, Arrays.asList("abc", "def", "ghi"));
	}

	/**
	 * Tests paging limits are working properly
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testPagingLimits(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		SearchQuery searchQuery = new SearchQuery(
		);
		
		searchQuery.setOrderByFields(Arrays.asList(
			new OrderByField("name", OrderByType.ASC)
		));
		searchQuery.setResultsOffset(2);
		searchQuery.setResultsLimit(2);
		
		List<Employee> results = repo.search(searchQuery);
		
		List<String> names = new ArrayList<>();
		
		for(Employee res : results)
		{
			names.add(res.getName());
		}
		
		Assert.assertEquals(names, Arrays.asList("ghi", "xyz"));
		
		//test paging end limit crosses available data
		searchQuery.setResultsLimit(50);
		
		results = repo.search(searchQuery);
		names = new ArrayList<>();
		
		for(Employee res : results)
		{
			names.add(res.getName());
		}
		
		Assert.assertEquals(names, Arrays.asList("ghi", "xyz", "zie"));
	}

	/**
	 * Tests offset without limit
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testOffsetWihoutLimit(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		SearchQuery searchQuery = new SearchQuery(
		);
		
		searchQuery.setOrderByFields(Arrays.asList(
			new OrderByField("name", OrderByType.ASC)
		));
		
		searchQuery.setResultsOffset(2);
		
		List<Employee> results = repo.search(searchQuery);
		
		List<String> names = new ArrayList<>();
		
		for(Employee res : results)
		{
			names.add(res.getName());
		}
		
		Assert.assertEquals(names, Arrays.asList("ghi", "xyz", "zie"));
	}
	
	@Test(dataProvider = "repositoryFactories")
	public void testEmpDynamicSearch(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		//search with plain conditions, no addition or subtraction of columns
		SearchQuery searchQuery = new SearchQuery(
			new SearchCondition("age", Operator.GE, 35)
		);
		
		searchQuery.setOrderByFields(Arrays.asList(
			new OrderByField("name", OrderByType.ASC)
		));
		
		List<DynamicEmpSearchResult> results = repo.searchByName(searchQuery);
		
		Assert.assertEquals(results.size(), 2);
		Assert.assertEquals(results.get(0).getEmpName(), "ghi");
		Assert.assertEquals(results.get(0).getAge(), (Integer)40);
		Assert.assertEquals(results.get(0).getExtraFields().size(), 0);
		
		Assert.assertEquals(results.get(1).getEmpName(), "zie");
		Assert.assertEquals(results.get(1).getAge(), (Integer)35);
		Assert.assertEquals(results.get(1).getExtraFields().size(), 0);

		//test by removing a column
		searchQuery.setExcludeFields(new HashSet<>(Arrays.asList("age")));
		results = repo.searchByName(searchQuery);
		
		Assert.assertEquals(results.size(), 2);
		Assert.assertEquals(results.get(0).getEmpName(), "ghi");
		Assert.assertNull(results.get(0).getAge());
		Assert.assertEquals(results.get(0).getExtraFields().size(), 0);
		
		Assert.assertEquals(results.get(1).getEmpName(), "zie");
		Assert.assertNull(results.get(1).getAge());
		Assert.assertEquals(results.get(1).getExtraFields().size(), 0);
		
		//test by adding extra columns
		searchQuery.setExcludeFields(new HashSet<>(Arrays.asList("age")));
		searchQuery.setAdditionalEntityFields(new HashSet<>(Arrays.asList("emailId", "phoneNo")));
		results = repo.searchByName(searchQuery);
		
		Assert.assertEquals(results.size(), 2);
		Assert.assertEquals(results.get(0).getEmpName(), "ghi");
		Assert.assertNull(results.get(0).getAge());
		Assert.assertEquals(results.get(0).getExtraFields().size(), 2);
		Assert.assertEquals(results.get(0).getExtraFields().get("emailId"), "user4@test.com");
		Assert.assertEquals(results.get(0).getExtraFields().get("phoneNo"), "1234564");
		
		Assert.assertEquals(results.get(1).getEmpName(), "zie");
		Assert.assertNull(results.get(1).getAge());
		Assert.assertEquals(results.get(1).getExtraFields().size(), 2);
		Assert.assertEquals(results.get(1).getExtraFields().get("emailId"), "user3@test.com");
		Assert.assertEquals(results.get(1).getExtraFields().get("phoneNo"), "1234563");
		
		//test by removing column which does not exists and adding fixed column in additional list
		//	and removing and adding same column. Addition will take high priority
		searchQuery.setExcludeFields(new HashSet<>(Arrays.asList("age", "emailId")));
		searchQuery.setAdditionalEntityFields(new HashSet<>(Arrays.asList("emailId", "phoneNo", "age")));
		results = repo.searchByName(searchQuery);
		
		Assert.assertEquals(results.size(), 2);
		Assert.assertEquals(results.get(0).getEmpName(), "ghi");
		Assert.assertNull(results.get(0).getAge());
		Assert.assertEquals(results.get(0).getExtraFields().size(), 3);
		Assert.assertEquals(results.get(0).getExtraFields().get("emailId"), "user4@test.com");
		Assert.assertEquals(results.get(0).getExtraFields().get("phoneNo"), "1234564");
		Assert.assertEquals(results.get(0).getExtraFields().get("age"), (Integer)40);
		
		Assert.assertEquals(results.get(1).getEmpName(), "zie");
		Assert.assertNull(results.get(1).getAge());
		Assert.assertEquals(results.get(1).getExtraFields().size(), 3);
		Assert.assertEquals(results.get(1).getExtraFields().get("emailId"), "user3@test.com");
		Assert.assertEquals(results.get(1).getExtraFields().get("phoneNo"), "1234563");
		Assert.assertEquals(results.get(1).getExtraFields().get("age"), (Integer)35);
	}
}
