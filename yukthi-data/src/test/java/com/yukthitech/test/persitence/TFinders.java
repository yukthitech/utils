package com.yukthitech.test.persitence;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.test.persitence.entity.Employee;
import com.yukthitech.test.persitence.entity.IEmployeeRepository;
import com.yukthitech.test.persitence.queries.EmpSearchQuery;
import com.yukthitech.test.persitence.queries.EmpSearchResult;
import com.yukthitech.test.persitence.queries.KeyValueBean;
import com.yukthitech.persistence.FilterAction;
import com.yukthitech.persistence.IDataFilter;
import com.yukthitech.persistence.RecordCountMistmatchException;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.persistence.repository.annotations.JoinOperator;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.annotations.SearchResult;
import com.yukthitech.persistence.repository.search.SearchCondition;
import com.yukthitech.persistence.repository.search.SearchQuery;
import com.yukthitech.utils.CommonUtils;


public class TFinders extends TestSuiteBase
{
	@Override
	protected void initFactoryBeforeClass(RepositoryFactory factory)
	{
		cleanFactoryAfterClass(factory);
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		repo.save(new Employee("1230", "user0@test.com", "user1", "1234560", 20));
		repo.save(new Employee("1231", "user1@test.com", "user2", "1234561", 25));
		repo.save(new Employee("1232", "user2@test.com", "user2", "1234562", 30));
		repo.save(new Employee("1233", "user3@test.com", "user3", "1234563", 35));
		repo.save(new Employee("1234", "user4@test.com", "user4", "1234564", 40));
		repo.save(new Employee("1235", "user5@test.com", "user5", "12345644", 45));
		
		repo.save(new Employee("1236", "user6@test.com", null, "12345646", 46));
	}

	@Override
	protected void cleanFactoryAfterClass(RepositoryFactory factory)
	{
		//cleanup the emp table
		factory.dropRepository(Employee.class);
	}
	
	@Test(dataProvider = "repositoryFactories")
	public void testWithIn(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);

		//ensure case is not ignored by default
		List<Employee> empLst = repo.findEmpWithEmails(CommonUtils.toSet("user0@test.com", "useR1@test.com"));
		Assert.assertEquals(empLst.size(), 2);
		
		Set<String> empNos = empLst.stream().map(emp -> emp.getEmployeeNo()).collect(Collectors.toSet());

		Assert.assertEquals(empNos, CommonUtils.toSet("1230", "1231"));
	}

	@Test(dataProvider = "repositoryFactories")
	public void testWithIgnoreCase(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);

		//ensure case is not ignored by default
		Employee e = repo.findEmpByEmailIgnoreCase("USER0@tEst.com");
		Assert.assertNotNull(e);
		Assert.assertEquals(e.getEmployeeNo(), "1230");

		e = repo.findByEmailPattern("USeR0%");
		Assert.assertNotNull(e);
		Assert.assertEquals(e.getEmployeeNo(), "1230");
	}

	/**
	 * Tests finder based on finder method name
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testFindByMethodName(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		Assert.assertEquals(repo.findEmailByEmployeeNo("1234"), "user4@test.com");
	}
	
	/**
	 * Tests finder whose conditions specified
	 * using annotation
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testFindWithCondition(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		Assert.assertEquals(repo.findAge("user2", "1234562"), 30);
	}
	
	/**
	 * Tests entity fetching
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testFindForEntity(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		Employee e = repo.findByEmployeeNo("1234");
		Assert.assertEquals(e.getAge(), 40);
		Assert.assertEquals(e.getEmailId(), "user4@test.com");
		Assert.assertEquals(e.getEmployeeNo(), "1234");
		Assert.assertEquals(e.getName(), "user4");
		Assert.assertEquals(e.getPhoneNo(), "1234564");
		Assert.assertTrue(e.getId() > 0);
	}

	/**
	 * Test finder to fetch collection of employees
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testFindForEntityCollection(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		List<Employee> empLst = repo.findByPhoneNo("%64%");
		Assert.assertEquals(empLst.size(), 3);

		Assert.assertEquals(CommonUtils.toSet(empLst.get(0).getAge(), empLst.get(1).getAge()) , CommonUtils.toSet(40, 45));
		Assert.assertEquals(CommonUtils.toSet(empLst.get(0).getEmailId(), empLst.get(1).getEmailId()) , CommonUtils.toSet("user4@test.com", "user5@test.com"));
		Assert.assertEquals(CommonUtils.toSet(empLst.get(0).getEmployeeNo(), empLst.get(1).getEmployeeNo()) , CommonUtils.toSet("1234", "1235"));
		Assert.assertEquals(CommonUtils.toSet(empLst.get(0).getName(), empLst.get(1).getName()) , CommonUtils.toSet("user4", "user5"));
		Assert.assertEquals(CommonUtils.toSet(empLst.get(0).getPhoneNo(), empLst.get(1).getPhoneNo()) , CommonUtils.toSet("1234564", "12345644"));
	}

	/**
	 * Tests entity fetch based on query object
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testQueryByConditionBean(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		List<Employee> empLst = repo.find(new EmpSearchQuery(null, "%64%", 5, 100));
		Assert.assertEquals(empLst.size(), 3);

		Assert.assertEquals(CommonUtils.toSet(empLst.get(0).getAge(), empLst.get(1).getAge()) , CommonUtils.toSet(40, 45));
		Assert.assertEquals(CommonUtils.toSet(empLst.get(0).getEmailId(), empLst.get(1).getEmailId()) , CommonUtils.toSet("user4@test.com", "user5@test.com"));
		Assert.assertEquals(CommonUtils.toSet(empLst.get(0).getEmployeeNo(), empLst.get(1).getEmployeeNo()) , CommonUtils.toSet("1234", "1235"));
		Assert.assertEquals(CommonUtils.toSet(empLst.get(0).getName(), empLst.get(1).getName()) , CommonUtils.toSet("user4", "user5"));
		Assert.assertEquals(CommonUtils.toSet(empLst.get(0).getPhoneNo(), empLst.get(1).getPhoneNo()) , CommonUtils.toSet("1234564", "12345644"));
		
		empLst = repo.find(new EmpSearchQuery("user2", "1234561", null, null));
		Assert.assertEquals(empLst.size(), 1);

		empLst = repo.find(new EmpSearchQuery("user2", null, null, null));
		Assert.assertEquals(empLst.size(), 2);

		empLst = repo.find(new EmpSearchQuery(null, null, 35, null));
		Assert.assertEquals(empLst.size(), 4);

		empLst = repo.find(new EmpSearchQuery(null, null, 35, 40));
		Assert.assertEquals(empLst.size(), 2);
	}

	/**
	 * Tests finder method which returns different bean collection other than entity or simple field (using {@link SearchResult} annotation)
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testFetchDiffBeanCollection(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		List<EmpSearchResult> results = repo.findResultsByName("user2");
		Assert.assertEquals(CommonUtils.toSet(results.get(0).getEmpName(), results.get(1).getEmpName()) , CommonUtils.toSet("user2"));
		Assert.assertEquals(CommonUtils.toSet(results.get(0).getAge(), results.get(1).getAge()) , CommonUtils.toSet(25, 30));
	}
	
	/**
	 * Tests finder method which returns different bean other than entity or simple field (using {@link SearchResult} annotation)
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories", expectedExceptions = RecordCountMistmatchException.class)
	public void testFetchDiffBean(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);

		EmpSearchResult res = repo.findResultByName("user2");
		Assert.assertEquals(res.getEmpName(), "user2");
		Assert.assertEquals(res.getAge(), 20);

		//this results in multiple rows but expected is single row. So results in error
		repo.findResultByName("user2");
	}

	/**
	 * Tests finder with {@link SearchResult} annotation with mappings
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testFinderWithMappings(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		List<KeyValueBean> results = repo.findKeyValues("1234564%");
		Assert.assertEquals(results.size(), 3);
		Assert.assertEquals(CommonUtils.toSet(results.get(0).getKey(), results.get(1).getKey()) , CommonUtils.toSet("user4", "user5"));
		Assert.assertEquals(CommonUtils.toSet(results.get(0).getValue(), results.get(1).getValue()) , CommonUtils.toSet("40", "45"));
	}

	/**
	 * Tests finder with OR operator
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testFinderWithOrcondition(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		List<Employee> results = repo.findByNameOrPhone("user1", "1234563");
		
		Assert.assertEquals(results.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(results.get(0).getEmployeeNo(), results.get(1).getEmployeeNo()) , CommonUtils.toSet("1230", "1233"));
	}

	/**
	 * Checks repository methods with null values for = and != operators
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testFinderWithNull(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		//check with = operator with null value
		List<Employee> results = repo.findEmpByName1(null);
		
		Assert.assertEquals(results.size(), 1);
		Assert.assertEquals(results.get(0).getEmployeeNo(), "1236");

		//check = operator with not null value
		results = repo.findEmpByName1("user4");
		Assert.assertEquals(results.size(), 1);
		Assert.assertEquals(results.get(0).getEmployeeNo(), "1234");

		//check != operator with null value
		results = repo.findEmpByName2(null);
		Assert.assertEquals(results.size(), 6);
		
		//check != operator with not null value 
		results = repo.findEmpByName2("user4");
		Assert.assertEquals(results.size(), 5);
	}

	@Test(dataProvider = "repositoryFactories")
	public void testFinderWithMethodConditions(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		//check when no other condition is involved
		List<Employee> results = repo.findEmpWithNoName(null);
		Assert.assertEquals(results.size(), 1);
		Assert.assertEquals(results.get(0).getEmployeeNo(), "1236");
		
		//check when other conditions are involve
		results = repo.findEmpWithNoName("12345646");
		Assert.assertEquals(results.size(), 1);
		Assert.assertEquals(results.get(0).getEmployeeNo(), "1236");
		
		//check when no results are present
		results = repo.findEmpWithNoName("142");
		Assert.assertEquals(results.size(), 0);
	}

	@Test(dataProvider = "repositoryFactories")
	public void testSearch(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		List<Employee> results = repo.search(new SearchQuery(
				new SearchCondition("name", Operator.EQ, "user2")
		));
		
		Assert.assertEquals(results.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(results.get(0).getEmployeeNo(), results.get(1).getEmployeeNo()) , CommonUtils.toSet("1231", "1232"));
		
		//ensure search count is executed properly
		Assert.assertEquals(repo.searchCount(new SearchQuery(
				new SearchCondition("name", Operator.EQ, "user2")
		)), 2);
		
		results = repo.search(new SearchQuery(
				new SearchCondition("name", Operator.EQ, "user2"),
				new SearchCondition("phoneNo", Operator.EQ, "1234561")
		));
		
		Assert.assertEquals(results.size(), 1);
		Assert.assertEquals(results.get(0).getEmployeeNo() , "1231");

		//ensure search count is executed properly
		Assert.assertEquals(repo.searchCount(new SearchQuery(
				new SearchCondition("name", Operator.EQ, "user2"),
				new SearchCondition("phoneNo", Operator.EQ, "1234561")
		)), 1);
	}

	/**
	 * Tests search query with null values
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testSearchWithNull(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		List<Employee> results = repo.search(new SearchQuery(
				new SearchCondition("name", Operator.EQ, null, true)
		));
		
		Assert.assertEquals(results.size(), 1);
		Assert.assertEquals(results.get(0).getEmployeeNo(), "1236");
	}

	/**
	 * Tests search query with group conditions
	 * @param factory
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testSearchWithConditionGroups(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		SearchQuery query = new SearchQuery();
		query.addCondition(
				new SearchCondition("name", Operator.EQ, "user2")
					.addCondition(new SearchCondition("age", Operator.EQ, 30))
		).addCondition(new SearchCondition(JoinOperator.OR, "name", Operator.EQ, "user3"));
		
		List<Employee> results = repo.search(query);
		
		Assert.assertEquals(results.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(results.get(0).getEmployeeNo(), results.get(1).getEmployeeNo()) , CommonUtils.toSet("1232", "1233"));
	}

	@Test(dataProvider = "repositoryFactories")
	public void testSearchWithDefaultConditions(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		//find old employees without normal condition (only with default condition)
		List<Employee> results = repo.findOldEmployees(null);
		
		Assert.assertEquals(results.size(), 3);
		Assert.assertEquals(CommonUtils.toSet(results.get(0).getEmployeeNo(), results.get(1).getEmployeeNo(), results.get(2).getEmployeeNo()) , 
				CommonUtils.toSet("1234", "1235", "1236"));
		
		//test with dynamic expressions
		repo.setExecutionContext(CommonUtils.toMap("oldAge", 46));
		results = repo.findOldEmployees(null);
		
		Assert.assertEquals(results.size(), 1);
		Assert.assertEquals(CommonUtils.toSet(results.get(0).getEmployeeNo()) , 
				CommonUtils.toSet("1236"));

		//find old employees with normal condition (default condition with normal conditions)
		results = repo.findOldEmployees("12345646");
		
		Assert.assertEquals(results.size(), 1);
		Assert.assertEquals(results.get(0).getEmployeeNo() , "1236");
	}
	
	@Test(dataProvider = "repositoryFactories")
	public void testFinderWithFilter(RepositoryFactory factory)
	{
		IEmployeeRepository repo = factory.getRepository(IEmployeeRepository.class);
		
		//test when no records are filtered
		List<Employee> results = repo.findByAge(0, new IDataFilter<Employee>()
		{
			@Override
			public FilterAction filter(Employee data)
			{
				return FilterAction.ACCEPT;
			}
		});
		
		Assert.assertTrue(results.size() >= 4);

		//test when certain records are rejected
		results = repo.findByAge(20, new IDataFilter<Employee>()
		{
			@Override
			public FilterAction filter(Employee data)
			{
				return (data.getAge() <= 25) ? FilterAction.ACCEPT : FilterAction.REJECT;
			}
		});
		
		Assert.assertEquals(results.size(), 2);
		Assert.assertEquals(CommonUtils.toSet(results.get(0).getEmployeeNo(), results.get(1).getEmployeeNo()), CommonUtils.toSet("1230", "1231"));

		//test when certain records are limited by count
		AtomicInteger recCount = new AtomicInteger(0);
		
		results = repo.findByAge(20, new IDataFilter<Employee>()
		{
			@Override
			public FilterAction filter(Employee data)
			{
				recCount.incrementAndGet();
				return recCount.get() <= 2 ? FilterAction.ACCEPT : FilterAction.REJECT_AND_STOP;
			}
		});
		
		Assert.assertEquals(results.size(), 2);
	}
}
