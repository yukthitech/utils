# Yukthi Data ORM Library

A powerful and flexible Object-Relational Mapping (ORM) library for Java applications. This library simplifies database interactions by enabling developers to work with plain old Java objects (POJOs) and annotations, eliminating the need for manual SQL query writing.

## License

This project is licensed under the Apache License 2.0. For more details, see the LICENSE file.

## Overview

The library is divided into two main modules:

-   `yukthi-data-mapping`: Contains all the annotations used to define entities, repositories, and queries.
-   `yukthi-data`: Provides the core ORM implementation, including the repository factory, query execution engine, and transaction management.

## Features

-   **Annotation-Driven:** Use annotations to map Java objects to database tables, define relationships, and create queries.
-   **Repository Pattern:** Define repository interfaces to abstract data access logic and simplify CRUD operations.
-   **Type-Safe Queries:** Write queries using Java methods and annotations, reducing the risk of SQL injection and syntax errors.
-   **Extensible:** Support for custom data type converters and native queries for complex scenarios.
-   **Transaction Management:** Built-in support for declarative transaction management.

## Getting Started

### 1. Add Dependencies

Add the following Maven dependencies to your project's `pom.xml` file:

```xml
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-data</artifactId>
    <version>1.3.13-SNAPSHOT</version>
</dependency>
```

### 2. Configure the Repository Factory

Create a Spring bean configuration file to set up the `RepositoryFactory`:

```xml
<bean id="dataSource" class="org.apache.commons.dbcp2.BasicDataSource">
    <property name="driverClassName" value="${app.db.driver}" />
    <property name="url" value="${app.db.url}" />
    <property name="username" value="${app.db.user}" />
    <property name="password" value="${app.db.password}" />
</bean>

<bean id="repositoryFactory" class="com.yukthitech.persistence.repository.RepositoryFactory">
    <property name="createTables" value="true" />
    <property name="dataStore">
        <bean class="com.yukthitech.persistence.rdbms.RdbmsDataStore">
            <constructor-arg type="java.lang.String" value="${app.db.type}"/>
            <property name="dataSource" ref="dataSource"/>
        </bean>
    </property>
</bean>
```

### 3. Define an Entity

Create a POJO and annotate it with `@Table` to map it to a database table:

```java
@Table(name = "EMPLOYEE")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "EMP_NO")
    @UniqueConstraint(name = "UK_EMP_NO")
    private String employeeNo;

    @Column(name = "EMAIL_ID")
    @UniqueConstraint(name = "UK_EMP_EMAIL")
    private String emailId;

    private String name;
    private String phoneNo;
    private int age;

    // Getters and setters
}
```

### 4. Create a Repository Interface

Define a repository interface that extends `ICrudRepository`:

```java
public interface IEmployeeRepository extends ICrudRepository<Employee> {
    // Custom finder methods
}
```

### 5. Use the Repository

Inject the repository into your service classes and use it to perform database operations:

```java
@Service
public class EmployeeService {
    @Autowired
    private IEmployeeRepository employeeRepository;

    public void createEmployee(Employee employee) {
        employeeRepository.save(employee);
    }
}
```

## All Annotations

### Entity Level Annotations

| Annotation | Description | Attributes |
| :--- | :--- | :--- |
| `@Table` | Specifies the table name for an entity. | `name` (String): The name of the table. |
| `@UniqueConstraints` | A group of `@UniqueConstraint` annotations. | `value` (UniqueConstraint[]): An array of unique constraints. |
| `@UniqueConstraint` | Defines a unique constraint on one or more columns. | `name` (String): The name of the constraint. <br> `fields` (String[]): The names of the fields in the constraint. <br> `message` (String): The error message for constraint violations. <br> `finalName` (boolean): Whether the constraint name is final. |
| `@Indexes` | A group of `@Index` annotations. | `value` (Index[]): An array of indexes. |
| `@Index` | Defines an index on one or more columns. | `name` (String): The name of the index. <br> `fields` (String[]): The names of the fields in the index. |

### Field Level Annotations

| Annotation | Description | Attributes |
| :--- | :--- | :--- |
| `@Id` | Marks a field as the primary key. | | 
| `@GeneratedValue` | Specifies the generation strategy for the primary key. | `strategy` (GenerationType): The generation strategy. |
| `@Column` | Maps a field to a database column. | `name` (String): The name of the column. |
| `@DataTypeMapping` | Defines a custom data type and converter for a field. | `type` (DataType): The target database type. <br> `converterType` (Class<? extends IDataTypeConverter>): The converter implementation. |
| `@Version` | Implements optimistic locking for an entity. | | 
| `@OneToOne` | Defines a one-to-one relationship between two entities. | | 
| `@ManyToOne` | Defines a many-to-one relationship between two entities. | | 
| `@OneToMany` | Defines a one-to-many relationship between two entities. | `mappedBy` (String): The field that owns the relationship. |
| `@ManyToMany` | Defines a many-to-many relationship between two entities. | | 

### Repository Method Annotations

| Annotation | Description | Attributes |
| :--- | :--- | :--- |
| `@Condition` | Specifies a condition for a finder method. | `value` (String): The name of the entity field. <br> `op` (Operator): The operator to use in the condition. <br> `joinWith` (JoinOperator): The join operator to use. <br> `nullable` (boolean): Whether the condition can hold null values. <br> `ignoreCase` (boolean): Whether to ignore case during condition evaluation. |
| `@Conditions` | A group of `@Condition` annotations. | `value` (Condition[]): An array of conditions. <br> `joinWith` (JoinOperator): The join operator to use for the group. |
| `@Field` | Selects a specific field to be returned by a finder method. | `value` (String): The name of the entity field. <br> `updateOp` (UpdateOperator): The update operator to use. |
| `@UpdateFunction` | Marks a method as an update operation. | | 
| `@AggregateFunction` | Performs an aggregate operation (e.g., COUNT, SUM, AVG). | `type` (AggregateFunctionType): The type of aggregate function. <br> `field` (String): The field to use in the function. |
| `@NativeQuery` | Executes a native SQL query. | `name` (String): The name of the native query. <br> `type` (NativeQueryType): The type of native query. |
| `@SearchResult` | Maps the result of a query to a custom search result object. | `mappings` (ResultMapping[]): An array of result mappings. |
| `@MethodConditions` | Specifies conditions at the method level. | `nullChecks` (NullCheck[]): An array of null checks. <br> `conditions` (DefaultCondition[]): An array of default conditions. |
| `@OrderBy` | Specifies the ordering for finder method results. | `value` (String[]): An array of fields to order by. <br> `fields` (OrderByField[]): An array of `OrderByField` annotations. |

### Query Bean Annotations

| Annotation | Description | Attributes |
| :--- | :--- | :--- |
| `@ConditionBean` | Marks a parameter as a container for conditions. | | 
| `@QueryBean` | Marks a parameter as a container for conditions and fields. | | 

## Finder Methods

### Basic Finders

Define finder methods in your repository interface by following a naming convention:

```java
public interface IEmployeeRepository extends ICrudRepository<Employee> {
    Employee findByEmployeeNo(String empNo);
    Employee findByEmailId(String emailId);
}
```

### Conditional Finders

Use the `@Condition` annotation to create more complex finder methods:

```java
public interface IEmployeeRepository extends ICrudRepository<Employee> {
    @Condition(value = "name", op = Operator.LIKE)
    List<Employee> findByName(String name);

    @Condition(value = "age", op = Operator.GE)
    List<Employee> findByAge(int age);
}
```

### Condition Beans

Use a condition bean to group multiple search criteria into a single object:

```java
public class EmpSearchQuery {
    @Condition("name")
    private String name;

    @Condition(op = Operator.LIKE)
    private String phoneNo;

    @Condition(value = "age", op = Operator.GE)
    private Integer minAge;
}

public interface IEmployeeRepository extends ICrudRepository<Employee> {
    List<Employee> find(@ConditionBean EmpSearchQuery query);
}
```

## Update Methods

Use the `@UpdateFunction` annotation to create update methods:

```java
public interface IEmployeeRepository extends ICrudRepository<Employee> {
    @UpdateFunction
    boolean updateAge(@Field(value = "age", updateOp = UpdateOperator.ADD) int ageToAdd, @Condition("id") long id);
}
```

### Relation Updates

Yukthi Data ORM provides powerful options for managing relation updates through the `relationUpdate` attribute in the `@Field` annotation. This attribute accepts a `RelationUpdateType` enum, which can be one of the following:

-   `SYNC_RELATION`: This option synchronizes the relationship between entities. It adds new relations, ignores existing ones, and severs missing ones. Importantly, this option does not cascade updates to the child entities themselves; it only modifies the relation links.

-   `CASCADE`: This option provides a full cascade update. It not only synchronizes the relations but also updates the child entities. New child entities are added, existing ones are updated, and those not present in the input list are unlinked.

#### Example

```java
public interface IOrderRepository extends ICrudRepository<Order> {
    @UpdateFunction
    boolean updateItems(
        @Condition("title") String title,
        @Field("orderNo") int orderNo,
        @Field(value = "items", relationUpdate = RelationUpdateType.CASCADE) List<OrderItem> items
    );
}
```

## Delete Methods

Define delete methods in your repository interface:

```java
public interface IEmployeeRepository extends ICrudRepository<Employee> {
    boolean deleteByEmailId(@Condition("emailId") String mail);
    int deleteByName(@Condition("name") String name);
}
```

## Aggregate Methods

Use the `@AggregateFunction` annotation to perform aggregate operations:

```java
public interface IOrderRepository extends ICrudRepository<Order> {
    @AggregateFunction(type = AggregateFunctionType.COUNT)
    int getOrderCount(@Condition("customer.name") String customerName);

    @AggregateFunction(type = AggregateFunctionType.MAX, field = "cost")
    float getMaxCost(@Condition("customer.name") String customerName);
}
```

## Native Queries

For complex scenarios, you can use native SQL queries:

1.  **Define the query in an XML file:**

    ```xml
    <native-queries>
        <query name="readQuery"><![CDATA[
            SELECT ID, EMP_NO, EMAIL_ID, NAME, PHONE_NO, AGE
            FROM EMPLOYEE
            WHERE NAME LIKE '%${query.name}%'
        ]]></query>
    </native-queries>
    ```

2.  **Create a repository method with the `@NativeQuery` annotation:**

    ```java
    public interface IEmployeeRepository extends ICrudRepository<Employee> {
        @NativeQuery(name = "readQuery", type = NativeQueryType.READ)
        List<Employee> readEmployee(EmpSearchQuery query);
    }
    ```

## Advanced Topics

### Relationship Mapping

Yukthi Data ORM supports various relationship mappings using standard JPA annotations like `@OneToMany`, `@ManyToOne`, `@OneToOne`, and `@ManyToMany`.

#### `@OneToMany` Example

To define a one-to-many relationship, for example, a `Department` having multiple `Employee`s:

```java
// Department.java
@Table(name = "DEPARTMENTS")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "department", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Employee> employees;

    // Getters and setters
}

// Employee.java
@Table(name = "EMPLOYEES")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    @Column(name = "DEPT_ID")
    private Department department;

    // Getters and setters
}
```

#### `@ManyToOne` Example

To define a many-to-one relationship, for example, multiple `Employee`s belonging to one `Department`:

```java
// Employee.java (as above)
@Table(name = "EMPLOYEES")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    @Column(name = "DEPT_ID")
    private Department department;

    // Getters and setters
}

// Department.java (as above)
@Table(name = "DEPARTMENTS")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "department", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Employee> employees;

    // Getters and setters
}
```

### Transaction Management

Yukthi Data ORM provides functional interfaces for managing transactions. You can execute a block of code within a transaction using the `executeInTransaction` method on your repository. This allows for atomic operations where all changes are committed together or rolled back if any part fails.

```java
// Example usage in a service class
public class MyService {
    @Autowired
    private IEmployeeRepository employeeRepository;

    public void performTransactionalOperation() {
        employeeRepository.executeInTransaction(true, () -> {
            // Perform multiple database operations here
            employeeRepository.save(new Employee("EMP001", "john.doe@example.com", "John Doe", "1234567890", 30));
            employeeRepository.updatePhone("john.doe@example.com", "0987654321");

            // If an exception occurs here, all changes within this transaction will be rolled back
            // throw new RuntimeException("Simulating an error");
        });
    }
}
```

### Custom Type Converters

You can define custom data type converters using the `@DataTypeMapping` annotation. This is useful for storing complex Java objects as simple database types (e.g., JSON strings or encrypted data).

#### Using `@DataTypeMapping`

```java
// UserProfile.java
@Table(name = "USER_PROFILES")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SETTINGS_JSON", length = 2000)
    @DataTypeMapping(type = DataType.STRING, converterType = JsonConverter.class)
    private UserSettings userSettings;

    // Getters and setters
}

// UserSettings.java (a simple POJO to be stored as JSON)
public class UserSettings {
    private boolean emailNotifications;
    private String theme;

    // Getters and setters
}
```

#### Implementing a Custom Converter

Your custom converter must implement the `IPersistenceConverter` interface. For example, a `JsonConverter` might look like this:

```java
package com.yukthitech.persistence.conversion.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.conversion.IPersistenceConverter;

import java.lang.reflect.Field;

public class JsonConverter implements IPersistenceConverter {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Object convertToJavaType(Object dbObject, DataType dbType, Class<?> javaTypeCls, Field field) {
        if (dbObject == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(dbObject.toString(), javaTypeCls);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert JSON to object", e);
        }
    }

    @Override
    public Object convertToDBType(Object javaObject, DataType dbType) {
        if (javaObject == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(javaObject);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert object to JSON", e);
        }
    }
}
```

### Exception Handling

Yukthi Data ORM throws specific exceptions for various scenarios, such as `UniqueConstraintViolationException` when a unique constraint is violated. It's important to handle these exceptions gracefully in your application logic.

```java
import com.yukthitech.persistence.UniqueConstraintViolationException;

public class UserService {
    @Autowired
    private IUserRepository userRepository;

    public boolean registerUser(User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (UniqueConstraintViolationException e) {
            // Handle specific unique constraint violation
            System.err.println("User with this email or username already exists: " + e.getMessage());
            return false;
        } catch (Exception e) {
            // Handle other potential exceptions during save operation
            System.err.println("An unexpected error occurred: " + e.getMessage());
            return false;
        }
    }
}
```