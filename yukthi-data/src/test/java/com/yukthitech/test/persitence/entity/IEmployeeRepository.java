package com.fw.test.persitence.entity;

import java.util.List;
import java.util.Map;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.AggregateFunction;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.ConditionBean;
import com.yukthitech.persistence.repository.annotations.DefaultCondition;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.JoinOperator;
import com.yukthitech.persistence.repository.annotations.MethodConditions;
import com.yukthitech.persistence.repository.annotations.NativeQuery;
import com.yukthitech.persistence.repository.annotations.NativeQueryType;
import com.yukthitech.persistence.repository.annotations.NullCheck;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.annotations.OrderBy;
import com.yukthitech.persistence.repository.annotations.ResultMapping;
import com.yukthitech.persistence.repository.annotations.SearchFunction;
import com.yukthitech.persistence.repository.annotations.SearchResult;
import com.yukthitech.persistence.repository.search.SearchQuery;
import com.yukthitech.test.persitence.queries.DynamicEmpSearchResult;
import com.yukthitech.test.persitence.queries.EmpSearchQuery;
import com.yukthitech.test.persitence.queries.EmpSearchResult;
import com.yukthitech.test.persitence.queries.KeyValueBean;

public interface IEmployeeRepository extends ICrudRepository<Employee>
{
	public Employee findByEmployeeNo(String empNo);
	
	@Field("id")
	public long findIdByEmail(@Condition("emailId") String mail);
	
	public Employee findEmpByEmail(@Condition("emailId") String mail);

	public Employee findEmpByEmailIgnoreCase(@Condition(value = "emailId", ignoreCase = true) String mail);
	
	public Employee findByEmailPattern(@Condition(value = "emailId", ignoreCase = true, op = Operator.LIKE) String mail);

	public List<Employee> findEmpByName1(@Condition(value = "name", nullable = true) String name);
	public List<Employee> findEmpByName2(@Condition(value = "name", op = Operator.NE, nullable = true) String name);
	
	@MethodConditions(
			nullChecks = {
					@NullCheck(field = "name")
			}
	)
	public List<Employee> findEmpWithNoName(@Condition("phoneNo") String phoneNo);
	
	@MethodConditions(
		conditions = {
			@DefaultCondition(field = "age", op = Operator.GE, value = "${oldAge=40}")
		}
	)
	public List<Employee> findOldEmployees(@Condition("phoneNo") String phoneNo);
	
	@Field("age")
	public int findAge(@Condition("name") String name, @Condition("phoneNo") String phoneNo);

	@Field("emailId")
	public String findEmailByEmployeeNo(String empNo);
	
	public List<Employee> findByPhoneNo(@Condition(value = "phoneNo", op = Operator.LIKE) String phone);
	
	public List<Employee> findByNameOrPhone(@Condition(value = "name", op = Operator.LIKE, joinWith = JoinOperator.OR) String name, 
			@Condition(value = "phoneNo", op = Operator.LIKE, joinWith = JoinOperator.OR) String phone);

	public List<Employee> find(@ConditionBean EmpSearchQuery query);
	
	@SearchResult
	public List<EmpSearchResult> findResultsByName(@Condition("name") String name);
	
	@SearchResult
	@OrderBy("name")
	public List<EmpSearchResult> findResultsByNameWithOrder(@Condition(value = "name", op = Operator.LIKE) String name);
	
	@SearchResult
	public EmpSearchResult findResultByName(@Condition("name") String name);
	
	@SearchResult(mappings = {
		@ResultMapping(entityField = "name", property = "key"),
		@ResultMapping(entityField = "age", property = "value")
	})
	public List<KeyValueBean> findKeyValues(@Condition(value = "phoneNo", op = Operator.LIKE) String phone);
	
	public int updateAge(@Condition("name") String name, @Field("age") int age);
	
	public boolean updatePhone(@Condition("emailId") String mail, @Field("phoneNo") String phoneNo);
	
	public boolean deleteByMailId(@Condition("emailId") String mail);
	
	public int deleteByUserName(@Condition("name") String name);
	
	@AggregateFunction
	public long getCountByMailId(@Condition("emailId") String mail);

	public void deleteAll();
	
	@NativeQuery(name = "insertQuery", type = NativeQueryType.INSERT)
	public boolean addEmployee(Employee employee);
	
	@NativeQuery(name = "updateQuery", type = NativeQueryType.UPDATE)
	public int updateEmployee(Map<String, Object> employeeDet);
	
	@NativeQuery(name = "deleteQuery", type = NativeQueryType.DELETE)
	public int deleteEmployee(Map<String, Object> employeeDet);
	
	@NativeQuery(name = "readQuery", type = NativeQueryType.READ)
	public Employee readEmployee1(EmpSearchQuery query);

	/**
	 * Test query to ensure native fetch supports primitive return type.
	 * @param query
	 * @return
	 */
	@NativeQuery(name = "fetchCount", type = NativeQueryType.READ)
	public int fetchCount(EmpSearchQuery query);

	/**
	 * Fetch query to ensure wrapper conversion is supported.
	 * @param query
	 * @return
	 */
	@NativeQuery(name = "fetchCount", type = NativeQueryType.READ)
	public Long fetchCount1(EmpSearchQuery query);

	@NativeQuery(name = "readQuery", type = NativeQueryType.READ)
	public List<Employee> readEmployee2(EmpSearchQuery query);

	@SearchFunction
	@SearchResult
	public List<DynamicEmpSearchResult> searchByName(SearchQuery searchQuery);
}
