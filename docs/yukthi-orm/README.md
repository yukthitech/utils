# Yukthi ORM (`yukthi-data`) — AI Reference

> **Audience:** Cursor / AI agents integrating or using this ORM in other projects.  
> **Source modules:** `yukthi-orm/yukthi-data` (runtime) + `yukthi-orm/yukthi-data-mapping` (annotations).  
> **License:** Apache 2.0 · **Java:** 17

---

## 1. What this module is

**Yukthi Data** is a lightweight, annotation-driven Java ORM. You map POJOs to RDBMS tables and access data through **repository interfaces** (JDK dynamic proxies). Most SQL is generated from method names and annotations; complex SQL can use FreeMarker native-query XML.

| Module | Artifact | Role |
|--------|----------|------|
| Runtime (depend on this) | `com.yukthitech:yukthi-data` | `RepositoryFactory`, RDBMS store, executors, transactions |
| Annotations (transitive) | `com.yukthitech:yukthi-data-mapping` | Annotations, converters SPI, search result types |

There is **no** JPA `persistence.xml` / EntityManager. Configuration is: `DataSource` → `RdbmsDataStore` → `RepositoryFactory`.

**Not supported:** soft-delete, second-level entity cache.

---

## 2. Maven dependency

```xml
<dependency>
    <groupId>com.yukthitech</groupId>
    <artifactId>yukthi-data</artifactId>
    <version>1.3.13-SNAPSHOT</version>
</dependency>
```

Related Yukthi deps used internally: `yukthi-xml-mapper`, `yukthi-free-marker`, `javax.persistence:persistence-api`, commons-dbcp2, byte-buddy, jasypt.

---

## 3. Mental model (how to use it)

```
1. Configure DataSource + RdbmsDataStore(dbType) + RepositoryFactory
2. Annotate entity POJOs (@Table, @Id, columns, relations)
3. Define IXxxRepository extends ICrudRepository<Entity>
4. Add finder/update/delete/native methods with naming + annotations
5. repositoryFactory.getRepository(IXxxRepository.class)
6. Call methods; wrap multi-step work in executeInTransaction(...)
```

---

## 4. Setup / configuration

### Spring (or XMLBeanParser) style

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
            <!-- must match a template: mysql | derby | h2 | postgres | oracle -->
            <constructor-arg type="java.lang.String" value="${app.db.type}"/>
            <property name="dataSource" ref="dataSource"/>
        </bean>
    </property>
</bean>
```

### Important factory flags

| Setting | Meaning |
|---------|---------|
| `createTables=true` | Auto-create tables/indexes/join tables from entity metadata |
| `createTables=false` | Tables must already exist (`NoTableExistsException` otherwise) |
| Native queries | `NativeQueryFactory` + `addResource("/…xml")` then `dataStore.setNativeQueryFactory(…)` |

Supported DB templates (resources under `yukthi-data`): `mysql`, `derby`, `h2`, `postgres`, `oracle`.

---

## 5. Entities

```java
@Table(name = "EMPLOYEE")
@Indexes({ @Index(name = "E1_PH_NAME", fields = {"phoneNo", "name"}) })
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "EMP_NO")
    @UniqueConstraint(name = "UK_EMP_NO")
    private String employeeNo;

    @UniqueConstraint(name = "UK_EMP_EMAIL", message = "Duplicate email")
    private String emailId;

    @Indexed
    @Column(name = "ENAME")
    private String name;

    private String phoneNo;
    private int age;

    // getters / setters
}
```

### Entity / field annotations (cheat sheet)

| Annotation | Purpose |
|------------|---------|
| `@Table` (JPA) | Table name (**required**) |
| `@Id`, `@GeneratedValue` | Primary key (IDENTITY typical) |
| `@Column` | Column mapping |
| `@UniqueConstraint` | Unique constraint (`name`, `fields[]`, `message`, `finalName`) |
| `@Index` / `@Indexes` / `@Indexed` | Indexes |
| `@DataTypeMapping` | DB `DataType` + optional converter (`JSON`, `JSON_WITH_TYPE`, custom) |
| `@Extendable` / `@ExtendedFields` | Side-table extension fields (`EXT_[table]`) |
| `@DeleteWithParent` | Delete child when parent deleted |
| `@Transient`, `@NotUpdateable` | Skip persist / skip update |
| `@Version` (JPA) | Optimistic locking |
| `@OneToMany` / `@ManyToOne` / `@OneToOne` / `@ManyToMany` | Relations (+ cascade) |

`DataType` enum: `STRING`, `INT`, `LONG`, `FLOAT`, `DOUBLE`, `BOOLEAN`, `DATE`, `BLOB`, `ZIP_BLOB`, `CLOB`, `DATE_TIME`, `UNKNOWN`.

Built-in converters: `JsonConverter`, `JsonWithTypeConverter`, `XmlConverter`, `SerializationConverter`, `PasswordEncryptionConverter`, plus Blob/Clob/Date converters.

---

## 6. Repositories

```java
public interface IEmployeeRepository extends ICrudRepository<Employee> {
    Employee findByEmployeeNo(String empNo);

    @Condition(value = "name", op = Operator.LIKE)
    List<Employee> findByName(String name);

    List<Employee> find(@ConditionBean EmpSearchQuery query);
}
```

Obtain proxy:

```java
IEmployeeRepository repo = repositoryFactory.getRepository(IEmployeeRepository.class);
repo.save(employee);
Employee e = repo.findByEmployeeNo("12345");
```

### `ICrudRepository<E>` core API

- CRUD: `save`, `update`, `deleteById`, `findById`, `findFullById`, `getCount`
- Dynamic search: `search(SearchQuery)`, `searchCount(SearchQuery)`
- Transactions: `newTransaction()`, `currentTransaction()`, `newOrExistingTransaction()`, `executeInTransaction(boolean onlyNew, IAction)`

---

## 7. Query types (how methods are resolved)

Method → executor via annotation and/or name prefix:

| Type | Match | Notes |
|------|-------|-------|
| Finder / fetch | prefix `find` / `fetch` | Conditions from naming or `@Condition` |
| Search | prefix `search` or `@SearchFunction` | Dynamic `SearchQuery` |
| Save | prefix `save` | Insert |
| Update | prefix `update` or `@UpdateFunction` | Partial / operator updates |
| Delete | prefix `delete` | By conditions |
| Aggregate | `@AggregateFunction` | COUNT / MIN / MAX / AVG |
| Native | `@NativeQuery` | FreeMarker SQL from XML |

### Finder patterns

```java
// Naming convention
Employee findByEmployeeNo(String empNo);

// Explicit condition + operator
@Condition(value = "age", op = Operator.GE)
List<Employee> findByAge(int age);

// Projection
@Field("age")
int findAge(@Condition("id") long id);

// DTO mapping
@SearchResult
EmpSummary findSummary(@Condition("id") long id);

// Order / limit
@OrderBy("name")
List<Employee> findAllOrdered();

List<Employee> findTop(@Condition("age") int age, @LimitRows int limit);
```

**Operators (`Operator`):** `EQ`, `LT`, `LE`, `GT`, `GE`, `NE`, `LIKE`, `IN`, `NOT_IN`.

Join-path conditions: `@Condition("customer.name")`, aggregates on related fields similarly.

### Condition beans

```java
public class EmpSearchQuery {
    @Condition("name")
    private String name;

    @Condition(op = Operator.LIKE)
    private String phoneNo;

    @Condition(value = "age", op = Operator.GE)
    private Integer minAge;
}

List<Employee> find(@ConditionBean EmpSearchQuery query);
```

### Updates

```java
@UpdateFunction
boolean updateAge(
    @Field(value = "age", updateOp = UpdateOperator.ADD) int ageToAdd,
    @Condition("id") long id);
```

`UpdateOperator`: `ADD`, `SUBTRACT`, `MULTIPLY`, `DIVIDE` (and plain set when omitted).

**Relation updates** on collection fields:

- `RelationUpdateType.SYNC_RELATION` — sync links only (add/unlink; no cascade to child rows)
- `RelationUpdateType.CASCADE` — sync links **and** cascade update children

### Deletes

```java
boolean deleteByEmailId(@Condition("emailId") String mail);
int deleteByName(@Condition("name") String name);
```

### Aggregates

```java
@AggregateFunction(type = AggregateFunctionType.COUNT)
int getOrderCount(@Condition("customer.name") String customerName);

@AggregateFunction(type = AggregateFunctionType.MAX, field = "cost")
float getMaxCost(@Condition("customer.name") String customerName);
```

### Dynamic `SearchQuery`

```java
SearchQuery sq = new SearchQuery();
// add SearchCondition(s), order-by, resultsOffset / resultsLimit, include/exclude fields
// subquery: SearchQuery.subquery(entityType, resultField, conditions…)
List<Employee> list = repo.search(sq);
```

### Native queries (FreeMarker XML)

```xml
<native-queries>
    <query name="readQuery"><![CDATA[
        SELECT ID, EMP_NO, EMAIL_ID, NAME, PHONE_NO, AGE
        FROM EMPLOYEE
        WHERE NAME LIKE '%${query.name}%'
    ]]></query>
</native-queries>
```

```java
@NativeQuery(name = "readQuery", type = NativeQueryType.READ)
List<Employee> readEmployee(EmpSearchQuery query);
```

`NativeQueryType`: `READ`, `INSERT`, `UPDATE`, `DELETE`.  
Bind params can also use `?{propPath}` for prepared-statement style.

Wire factory:

```java
NativeQueryFactory nqf = new NativeQueryFactory();
nqf.addResource("/native-queries.xml");
repositoryFactory.getDataStore().setNativeQueryFactory(nqf);
```

---

## 8. Transactions

```java
empRepository.executeInTransaction(true, () -> {
    empRepository.save(emp);
    otherRepo.save(other); // failure rolls back all
});
```

- Thread-bound via `RdbmsTransactionManager`
- `onlyNew=true` → always start a new transaction
- Commit on success, rollback on exception

---

## 9. Relationships

Use standard JPA relation annotations. Lazy collections use ByteBuddy proxies. Child cleanup: JPA `cascade` and/or Yukthi `@DeleteWithParent`.

```java
@OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
private List<Employee> employees;
```

Condition paths can traverse relations: `"items.itemName"`, `"customer.name"`.

---

## 10. Listeners & converters

### Entity listeners

```java
factory.registerListeners(new Object() {
    @EntityEventHandler(eventType = EntityEventType.PRE_SAVE)
    public void beforeSave(EntityEvent event) { /* ... */ }
});
```

Events: `PRE_SAVE`, `POST_SAVE`, `PRE_UPDATE`, `POST_UPDATE`, `PRE_DELETE`, `POST_DELETE`.

### Custom converters

Implement `IPersistenceConverter` and attach via `@DataTypeMapping(converterType = …)` or built-in `converter` enum values.

---

## 11. Extendable entities

`@Extendable(tableName=…, fieldPrefix=…, count=…)` creates a side table with `field0…fieldN`. Load selected extensions with `findFullById(id, fieldNames)` / `@ExtendedFieldNames`.

---

## 12. Key types (package map)

| Type | Package / role |
|------|----------------|
| `RepositoryFactory` | `…repository` — central entry |
| `ICrudRepository` | root — CRUD + txn |
| `RdbmsDataStore` | `…rdbms` — JDBC implementation |
| `EntityDetails` | metadata from annotations |
| `NativeQueryFactory` | FreeMarker native SQL |
| `SearchQuery` / `SearchCondition` | dynamic search |
| `IDataFilter` | stream-filter find/native rows |
| Exceptions | `UniqueConstraintViolationException`, `ForeignConstraintViolationException`, `TransactionException`, … |

Annotations live mainly in:

- `com.yukthitech.persistence.repository.annotations`
- `com.yukthitech.persistence.annotations`
- JPA: `javax.persistence.*`

---

## 13. Guidance for AI agents

1. Prefer **annotation finders** over native SQL.
2. Always set `RdbmsDataStore` constructor arg to a **supported template name**.
3. Entity type is inferred from `ICrudRepository<E>` — keep generics correct.
4. For multi-repo atomic work, use **`executeInTransaction`**.
5. Unique/FK failures surface as typed exceptions — catch those, not generic SQLException.
6. When writing new repository methods: choose the right **prefix** or annotation so `ExecutorFactory` picks the correct executor.
7. Native query XML is FreeMarker — escape carefully; prefer `?{path}` for bind params when possible.
8. Do not assume soft-delete or 2nd-level cache exist.

### Useful test references (in repo)

`TCrudFunctionality`, `TFinders`, `TFindersWithRelations`, `TNativeQueries`, `TTransactions`, `TEventHandlers`, `TAggregateFunctions`, `TExtendedEntity`, `TVersion`, `TestRelationUpdates`, `TSubquery`.
