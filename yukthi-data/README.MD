## Yukthi Data

**Yukthi Data** is an ORM framework inspired by "Sprint Data" but designed to support more complex use cases.

Following is the list of the high level use case supported:
1. **Simple Repository Methods**: Like spring-data repository methods are supported based on method names
	public Employee findByEmployeeNo(String empNo);
	public boolean deleteByMailId(@Condition("emailId") String mail);
	
2. **Annotation based Conditions**: Conditions can be specified on parameters using annotations
	public Employee findEmpByEmailIgnoreCase(@Condition(value = "emailId", ignoreCase = true) String mail);
	public List<Employee> findEmpByName2(@Condition(value = "name", op = Operator.NE, nullable = true) String name);

3. **Non parameter Conditions**: Non-parameter based conditions can be specified by method annotations
	@MethodConditions(
			nullChecks = {
					@NullCheck(field = "name")
			}
	)
	public List<Employee> findEmpWithNoName(@Condition("phoneNo") String phoneNo);

4. **Result Customizations**: Field to be fetched or extra specifications on results can be specified by annotations as below
	@Field("emailId")
	public String findEmailByEmployeeNo(String empNo);

	@SearchResult
	@OrderBy("name")
	public List<EmpSearchResult> findResultsByNameWithOrder(@Condition(value = "name", op = Operator.LIKE) String name);

	@SearchResult(mappings = {
		@ResultMapping(entityField = "name", property = "key"),
		@ResultMapping(entityField = "age", property = "value")
	})
	public List<KeyValueBean> findKeyValues(@Condition(value = "phoneNo", op = Operator.LIKE) String phone);

5. **Nested field conditions**: Nested field conditions (which needs join queries) can also be specified as normal fields.

6. For complex results or conditions, the annotations are supported withing query beans or resule beans.

7. **Native Queries**: Native queries are also supported. The native queries themselves are defined in externalized xml files which in turn can have free marker expressions. These freemarker expressions can refer to the parameters being passed to the methods. And the columns of query result are mapped by name to the properties of result bean, allowing custom return types for native queries.
	@NativeQuery(name = "insertQuery", type = NativeQueryType.INSERT)
	public boolean addEmployee(Employee employee);

	@NativeQuery(name = "readQuery", type = NativeQueryType.READ)
	public Employee readEmployee1(EmpSearchQuery query);

	
8. **Live Entities**: The entities returned by the repository methods are tied to repository like Hibernate and other JPA frameworks. But unlike them they are not tied to live transaction. That means sub orms can be accessed anytime. And also unlike other standard frameworks, updations of entity beans are not mapped to db changes.

9. **Custom Constraint Exception messages**: Unlike other frameworks where weird messages are thrown when constraints fail, in this framework custom messages can be specified which should be used when a constrain fails. Example, in an entity for a field unique constrain custom message can be specified as follows (on same lines custom messaged from foreign key constraint can also be specified). And exceptions thrown also will be specific to constraint being failed like - UniqueConstraintViolationException, ForeignConstraintViolationException
	
	@UniqueConstraint(name = "EmailId1", message = ERROR_MESSAGE_DUPLICATE_EMAIL)
	private String emailId;

10. **Custom way of persisting**: A field can be marked to be persisted in custom formats. For example, below indicates code indicate that target field should be persisted in json format
		@Column(name = "ADDRESS", length = 2000)
		@DataTypeMapping(type = DataType.STRING, converterType = JsonConverter.class)
		private Address address;
  

##Supported Persistence Storages
Currently only MYSQL and derby DBs are supported. Almost all RDBMS databases can be supported without any change (or with minimal coding - one free marker template or to max one implementation of interface com.yukthitech.persistence.IDataStore).

The design is done in such a way that any type of storage can be supported. For example, in order to add support for NOSQL DB like MongoDB, an implementation of com.yukthitech.persistence.IDataStore will be sufficient. Which has to handle tables (or collections) creation, data CRUD operations, transaction management. 

In order to support non-transactional storages like MongoDB, instead of using standard transaction management, simple transaction management is employed. Along with custom data-store to have custom transation management, an implementation com.yukthitech.persistence.ITransactionManager has to be provided.


##Pending Works
In order to make this framework fully functional following pending items has to be completed:
1. Currently multi valued relation fetching is not possible. This needs to be fixed.
2. Support for sub queries inside delete and update queries (for conditions and values).
3. Create formal document about the usage and using different features of the framework.
  

