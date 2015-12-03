package com.fw.test.persitence.entity;

import java.util.List;

import com.fw.test.persitence.queries.EmpSearchQuery;
import com.fw.test.persitence.queries.EmpSearchResult;
import com.fw.test.persitence.queries.KeyValueBean;
import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.repository.annotations.Condition;
import com.yukthi.persistence.repository.annotations.ConditionBean;
import com.yukthi.persistence.repository.annotations.CountFunction;
import com.yukthi.persistence.repository.annotations.Field;
import com.yukthi.persistence.repository.annotations.JoinOperator;
import com.yukthi.persistence.repository.annotations.MethodConditions;
import com.yukthi.persistence.repository.annotations.NullCheck;
import com.yukthi.persistence.repository.annotations.Operator;
import com.yukthi.persistence.repository.annotations.OrderBy;
import com.yukthi.persistence.repository.annotations.ResultMapping;
import com.yukthi.persistence.repository.annotations.SearchResult;

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
	
	@CountFunction
	public long getCountByMailId(@Condition("emailId") String mail);

	public void deleteAll();
}
