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
package com.yukthitech.persistence;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.persistence.annotations.AccessType;
import com.yukthitech.persistence.annotations.AutoFetchType;
import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.Extendable;
import com.yukthitech.persistence.annotations.ExtendedFields;
import com.yukthitech.persistence.annotations.FieldAccess;
import com.yukthitech.persistence.annotations.Index;
import com.yukthitech.persistence.annotations.Indexed;
import com.yukthitech.persistence.annotations.Indexes;
import com.yukthitech.persistence.annotations.Transient;
import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.annotations.UniqueConstraints;
import com.yukthitech.persistence.monitor.EntityDetailsMonitor;
import com.yukthitech.persistence.monitor.IEntityCreateTableListener;
import com.yukthitech.persistence.query.CreateIndexQuery;
import com.yukthitech.persistence.query.CreateTableQuery;
import com.yukthitech.utils.CaseInsensitiveComparator;
import com.yukthitech.utils.annotations.RecursiveAnnotationFactory;
import com.yukthitech.utils.exceptions.InvalidConfigurationException;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class EntityDetailsFactory
{
	private static Logger logger = LogManager.getLogger(EntityDetailsFactory.class);
	private static final String SPECIAL_CHAR_PATTERN = "[\\W\\_]+";
	
	private Map<Class<?>, EntityDetails> typeToDetails = new HashMap<>();

	private EntityDetailsMonitor entityDetailsMonitor = new EntityDetailsMonitor();
	
	private Map<String, Object> nameToConstraints = new TreeMap<>(new CaseInsensitiveComparator());
	
	private RecursiveAnnotationFactory recursiveAnnotationFactory = new RecursiveAnnotationFactory();
	
	/**
	 * Flag indicating if unique id column should be added during table creation. This in turn 
	 * will be used to fetch auto generated id on need basis.
	 */
	private boolean addUniqueIdColumnEnabled = false;
	
	/**
	 * Sets the flag indicating if unique id column should be added during table creation. This in turn will be used to fetch auto generated id on need basis.
	 *
	 * @param addUniqueIdColumnEnabled the new flag indicating if unique id column should be added during table creation
	 */
	public void setAddUniqueIdColumnEnabled(boolean addUniqueIdColumnEnabled)
	{
		this.addUniqueIdColumnEnabled = addUniqueIdColumnEnabled;
	}
	
	/**
	 * Gets the flag indicating if unique id column should be added during table creation. This in turn will be used to fetch auto generated id on need basis.
	 *
	 * @return the flag indicating if unique id column should be added during table creation
	 */
	public boolean isAddUniqueIdColumnEnabled()
	{
		return addUniqueIdColumnEnabled;
	}

	/**
	 * Removes non aplha numeric characters (including underscore) from column names and sets it as key and the actual column
	 * name as value of the resultant map. This can be used to find column mapping for undeclared columns.
	 * 
	 * @param entityType
	 * @param dataStore
	 * @return
	 */
	private Map<String, String> flattenColumnNames(EntityDetails entityDetails, IDataStore dataStore)
	{
		String tableName = entityDetails.getTableName();
		
		Set<String> columns = dataStore.getColumnNames(tableName);
		String flattenName = null;
		
		Map<String, String> map = new HashMap<>();
		
		for(String column: columns)
		{
			entityDetails.addColumn(column);
			
			//remove all special characters including underscore
			flattenName = column.replaceAll(SPECIAL_CHAR_PATTERN, "");
			
			//convert flatten name to lower case to finalize the flattening
			map.put(flattenName.toLowerCase(), column);
		}
		
		logger.trace("Got columns for table '{}' as {}", tableName, map);
		
		return map;
	}
	
	private void checkFieldValidity(EntityDetails entityDetails, String fields[], String constraintType, String name, String parentClass)
	{
		for(String fieldName: fields)
		{
			if(!entityDetails.hasField(fieldName))
			{
				throw new InvalidMappingException("Invalid field '" + fieldName + "' specified in @" + constraintType + " '" + name + "' in class - " + parentClass);
			}
		}
	}
	
	private void buildUniqueConstraint(UniqueConstraint uniqueConstraint, EntityDetails entityDetails, Field field)
	{
		if(field == null && uniqueConstraint.fields().length == 0)
		{
			throw new InvalidMappingException("No fields are defined in @UniqueConstraint '" + uniqueConstraint.name() 
					+ "' specified at class level in class: " + entityDetails.getEntityType().getName());
		}
		
		String fields[] = uniqueConstraint.fields();
		
		if(field != null)
		{
			fields = Arrays.copyOf(fields, fields.length + 1);
			fields[fields.length - 1] = field.getName();
		}
		
		checkFieldValidity(entityDetails, fields, UniqueConstraint.class.getSimpleName(), uniqueConstraint.name(), entityDetails.getEntityType().getName());
		
		UniqueConstraintDetails constraint = new UniqueConstraintDetails(entityDetails, uniqueConstraint.name(), fields, 
				uniqueConstraint.message(), uniqueConstraint.validate(), uniqueConstraint.finalName());
		entityDetails.addUniqueKeyConstraint(constraint);
		
		logger.trace("Added unique-constraint {} to entity: {}", uniqueConstraint, entityDetails);
	}

	/**
	 * Fetches foreign constraint from the specified field, if one is present. If present, the same is added to current entity-details
	 * target-entity details and to the field details.
	 * @param entityDetails
	 * @param fieldDetails
	 * @param dataStore
	 * @param createTables
	 */
	private void buildForeignConstraint(EntityDetails entityDetails, FieldDetails fieldDetails, final IDataStore dataStore, final boolean createTables)
	{
		Field field = fieldDetails.getField();
		
		//fetch foreign key constraint details if present
		ForeignConstraintDetails foreignConstraintDetails = ForeignConstraintDetails.fetchForeignConstraint(entityDetails, field, new Function<Class<?>, EntityDetails>()
		{
			@Override
			public EntityDetails apply(Class<?> entityType)
			{
				return getEntityDetails(entityType, dataStore, createTables);
			}
		});
		
		//if no foreign constraints are defined
		if(foreignConstraintDetails == null)
		{
			return;
		}
		
		//add foreign constraint at entity level to source and target entity
		entityDetails.addForeignConstraintDetails(foreignConstraintDetails);
		foreignConstraintDetails.getTargetEntityDetails().addChildConstraint(foreignConstraintDetails);
		
		//add constraint at field level
		fieldDetails.setForeignConstraintDetails(foreignConstraintDetails);
		
		logger.trace("Added foreign-constraint {} to entity: {}", foreignConstraintDetails, entityDetails);
	}
	
	private void buildIndexDetails(EntityDetails entityDetails, String name, String... fields)
	{
		if(fields == null || fields.length == 0)
		{
			throw new InvalidPersistenceConfigurationException("No/empty list of fields specified for indexing");
		}
		
		if(name == null || name.trim().length() == 0)
		{
			StringBuilder nameBuilder = new StringBuilder("IDX_").append(entityDetails.getEntityType().getSimpleName().toUpperCase());
			
			for(String field: fields)
			{
				if(!entityDetails.hasField(field))
				{
					throw new InvalidMappingException("Invalid field name '" + field + "' encountered for indexing");
				}
				
				nameBuilder.append("_").append(field.toUpperCase());
			}
			
			name = nameBuilder.toString();
		}
		
		entityDetails.addIndexDetails(new IndexDetails(name, fields));
	}
	
	/**
	 * Fetches the entity details for specified type from cache. If not found in cache, returns null.
	 * @param entityType
	 * @return
	 */
	public synchronized EntityDetails getEntityDetailsFromCache(Class<?> entityType)
	{
		//check in cache
		return typeToDetails.get(entityType);
	}
	
	private void loadConstraintDetails(EntityDetails entityDetails)
	{
		Collection<UniqueConstraintDetails> uniqueConstraints = entityDetails.getUniqueConstraints();
		Collection<ForeignConstraintDetails> foreignConstraints = entityDetails.getForeignConstraints();
		
		//load unique constraints on to map 
		for(UniqueConstraintDetails constraint : uniqueConstraints)
		{
			if(nameToConstraints.containsKey(constraint.getConstraintName()))
			{
				throw new InvalidStateException("Multiple constraint(s) found with same name - {} [Entity: {}]", constraint.getConstraintName(), entityDetails.getEntityType().getName());
			}
			
			nameToConstraints.put(constraint.getConstraintName(), constraint);
		}
		
		//load foreign key constraints
		for(ForeignConstraintDetails constraint : foreignConstraints)
		{
			if(nameToConstraints.containsKey(constraint.getConstraintName()))
			{
				throw new InvalidStateException("Multiple constraint(s) found with same name - {} [Entity: {}]", constraint.getConstraintName(), entityDetails.getEntityType().getName());
			}
			
			nameToConstraints.put(constraint.getConstraintName(), constraint);
		}
	}
	
	public synchronized EntityDetails getEntityDetails(Class<?> entityType, IDataStore dataStore, boolean createTables)
	{
		EntityDetails entityDetails = typeToDetails.get(entityType);
		
		if(entityDetails != null)
		{
			return entityDetails;
		}

		logger.trace("*********************************************************");
		logger.trace("Building entity details for type: " + entityType.getName());
		
		Table table = entityType.getAnnotation(Table.class);
		
		if(table == null)
		{
			throw new InvalidMappingException("No @Table annotation found on entity type: " + entityType.getName());
		}

		FieldAccess fieldAccess = entityType.getAnnotation(FieldAccess.class);
		entityDetails = new EntityDetails(table.name(), entityType);
		AccessType accessType = (fieldAccess == null) ? AccessType.ALL : fieldAccess.value(); 
		
		Class<?> cls = entityType;
		Map<String, String> flattenColumnMap = null;
		boolean tableExists = false;
		
		try
		{
			flattenColumnMap = flattenColumnNames(entityDetails, dataStore);
			entityDetails.setTableCreated(true);
			tableExists = true;
		}catch(RuntimeException ex)
		{
			if(!createTables)
			{
				logger.error("An error occurred while fetching coumns from table - " + entityDetails.getTableName(), ex);
				throw ex;
			}
			
			logger.info("An error occurred while fetching column details for table '" + entityDetails.getTableName() + "'. Assuming table does not exist and needs to be created");
		}
		
		//loop through the class hierarchy and fetch column mappings
		while(true)
		{
			if(cls.getName().startsWith("java"))
			{
				break;
			}
			
			fetchFieldMappings(cls, entityDetails, accessType, flattenColumnMap);
			cls = cls.getSuperclass();
		}

		//set entity details on map, set it before processing constraints
			// so that self linking will not cause recursion
		typeToDetails.put(entityType, entityDetails);
		
		UniqueConstraints uniqueConstraints = null;
		//ForeignConstraints foreignConstraints = null;
		Indexes indexes = null;
		cls = entityType;
		
		//loop through the class hierarchy and fetch constraint details at class level
		while(true)
		{
			if(cls.getName().startsWith("java"))
			{
				break;
			}
			
			//fetch class level unique constraint details
			uniqueConstraints = cls.getAnnotation(UniqueConstraints.class);
			
			if(uniqueConstraints != null)
			{
				for(UniqueConstraint constraint: uniqueConstraints.value())
				{
					buildUniqueConstraint(constraint, entityDetails, null);
				}
			}
			
			indexes = cls.getAnnotation(Indexes.class);
			
			if(indexes != null)
			{
				for(Index index: indexes.value())
				{
					buildIndexDetails(entityDetails, index.name(), index.fields());
				}
			}
			
			cls = cls.getSuperclass();
		}
		
		//fetch constraints at field level
			//Note: this is done at end to ensure all field details are loaded first. Which is required during cross recursion
		fetchFieldConstraints(entityDetails, dataStore, createTables);

		fetchExtendedTableDetails(entityType, entityDetails);
		
		if(flattenColumnMap == null)
		{
			logger.debug("As no column mapping found, assuming table needs to be created.");
			createRequiredTable(entityDetails, dataStore);
		}

		//check if id field is specified
		if(entityDetails.getIdField() == null)
		{
			throw new InvalidMappingException("No id field is specified for entity-type: " + entityType.getName());
		}
		
		/*
		if(!entityDetails.isTableCreated())
		{
			throw new IllegalStateException("Entity table is not found or not created - " + entityType.getName());
		}
		*/
		
		if(tableExists)
		{
			entityDetailsMonitor.addEntityWithTable(entityDetails);
		}
		
		logger.trace("Completed building of entity details {}.", entityDetails);
		logger.trace("*********************************************************");
		
		loadConstraintDetails(entityDetails);
		return entityDetails;
	}
	
	private void fetchFieldMappings(Class<?> cls, EntityDetails entityDetails, AccessType accessType, Map<String, String> flattenColumnMap)
	{
		Field fields[] = cls.getDeclaredFields();
		Column column = null;
		String columnName = null;
		Indexed indexed = null;
		DataType dbType = null;
		DataTypeMapping dataTypeMapping = null;
		String mappedColumn = null;
		boolean nullable = false;
		
		for(Field field: fields)
		{
			if(
				Modifier.isStatic(field.getModifiers())
				|| recursiveAnnotationFactory.findAnnotationRecursively(field, Transient.class) != null
				|| field.getAnnotation(javax.persistence.Transient.class) != null
			  )
			{
				continue;
			}
			
			column = field.getAnnotation(Column.class);
			dataTypeMapping = field.getAnnotation(DataTypeMapping.class);
			
			if(column == null && accessType == AccessType.DECLARED_ONLY)
			{
				continue;
			}
			
			columnName = (column != null && column.name().length() > 0) ? column.name().trim() : field.getName();
			dbType = (dataTypeMapping != null) ? dataTypeMapping.type() : DataType.getDataType(field.getType());
			
			if(dbType == null)
			{
				dbType = DataType.UNKNOWN;
			}
			
			//flatten the column name
			String flatColumnName = columnName.replaceAll(SPECIAL_CHAR_PATTERN, "");
			flatColumnName = flatColumnName.toLowerCase();
			
			//get actual column name from flatten column map
			mappedColumn = (flattenColumnMap != null) ? flattenColumnMap.get(flatColumnName) : null;
			
			//ensure column name is present
			if(mappedColumn == null)
			{
				//if flattenColumnMap is not null, it indicates the table is already existing
				// but field is missing
				if(flattenColumnMap != null)
				{
					//ignore fields which are not owned by this table
					if(!ForeignConstraintDetails.isTableOwnedRelation(field))
					{
						logger.trace("Ignoring column mapping for field {} as it is not owned by table", field.getName());
						buildFieldDetails(field, columnName, dbType, entityDetails, nullable);
						continue;
					}

					throw new InvalidMappingException("Failed to find mapping-column for field '{}' in entity: {}", field.getName(), entityDetails.getEntityType().getName());
				}
			}
			else
			{
				columnName = mappedColumn;
			}
			
			nullable = (column == null || column.nullable());
			
			//build the field details
			buildFieldDetails(field, columnName, dbType, entityDetails, nullable);
			/*
			idField = field.getAnnotation(IdField.class);

			if(idField == null)
			{
				fieldDetails = new FieldDetails(field, columnName, (field.getAnnotation(ReadOnly.class) != null) );
				
				logger.trace("Adding field details {} to entity {}", fieldDetails, entityDetails);
			}
			else
			{
				String sequenceName = idField.sequenceName();
				
				if(idField.autogeneration() == AutogenerationType.SEQUENCE && (sequenceName == null || sequenceName.trim().length() == 0))
				{
					sequenceName = "SEQ_" + entityDetails.getEntityType().getSimpleName().toUpperCase() + "_" + field.getName().toUpperCase();
				}
				
				fieldDetails = new FieldDetails(field, columnName, true, idField.autogeneration(), idField.autofetch(), true, sequenceName);
				
				logger.trace("Adding ID field details {} to entity {}", fieldDetails, entityDetails);
			}
			
			entityDetails.addFieldDetails(fieldDetails);
			*/
			
			indexed = field.getAnnotation(Indexed.class);
			
			if(indexed != null)
			{
				buildIndexDetails(entityDetails, indexed.name(), field.getName());
			}
		}
	}
	
	private FieldDetails buildFieldDetails(Field field, String columnName, DataType dataType, EntityDetails entityDetails, boolean nullable)
	{
		FieldDetails fieldDetails = null;
		Id idField = field.getAnnotation(Id.class);
		GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
		Version version = field.getAnnotation(Version.class);

		if(idField == null)
		{
			fieldDetails = new FieldDetails(field, dataType, (version != null), nullable);
			
			logger.trace("Adding field details {} to entity {}", fieldDetails, entityDetails);
		}
		else
		{
			AutoFetchType autoFetchAnnot = field.getAnnotation(AutoFetchType.class);
			
			String sequenceName = (generatedValue != null) ? generatedValue.generator() : null;
			GenerationType generationType = (generatedValue != null) ? generatedValue.strategy() : null;
			boolean autoFetch = (autoFetchAnnot != null) ? autoFetchAnnot.value() : true;
			
			//if invalid generation type is specified but not supported
			if(generationType != null && generationType != GenerationType.IDENTITY && generationType != GenerationType.SEQUENCE)
			{
				throw new IllegalStateException(String.format("Invalid generation-type '%s' specified for field '%s' of entity - %s", 
						generationType, field.getName(), entityDetails.getEntityType().getName()));
			}
			
			//if generation type is sequence, get sequence name
			if(generationType == GenerationType.SEQUENCE && (sequenceName == null || sequenceName.trim().length() == 0))
			{
				sequenceName = "SEQ_" + entityDetails.getEntityType().getSimpleName().toUpperCase() + "_" + field.getName().toUpperCase();
			}
			
			fieldDetails = new FieldDetails(field, dataType, true, generationType, autoFetch, sequenceName, false);
			
			logger.trace("Adding ID field details {} to entity {}", fieldDetails, entityDetails);
		}
		
		entityDetails.addFieldDetails(fieldDetails);
		return fieldDetails;
	}

	/**
	 * Fetches constraints defined at fied level like - Unique constraint, foreign key constraint etc
	 * @param entityDetails
	 * @param dataStore
	 * @param createTables
	 */
	private void fetchFieldConstraints(EntityDetails entityDetails, IDataStore dataStore, boolean createTables)
	{
		Field field = null;
		UniqueConstraint uniqueConstraint = null;
		
		for(FieldDetails fieldDetails: entityDetails.getFieldDetails())
		{
			field = fieldDetails.getField();
			
			uniqueConstraint = field.getAnnotation(UniqueConstraint.class);
			
			if(uniqueConstraint != null)
			{
				buildUniqueConstraint(uniqueConstraint, entityDetails, field);
			}
			
			//fetch foreign constraint details, if any
			buildForeignConstraint(entityDetails, fieldDetails, dataStore, createTables);
		}
	}
	
	/**
	 * Fetches extension field details for specific entity details.
	 * @param entityType
	 * @param entityDetails
	 */
	private void fetchExtendedTableDetails(Class<?> entityType, EntityDetails entityDetails)
	{
		Extendable extendable = recursiveAnnotationFactory.findAnnotationRecursively(entityType, Extendable.class);
		
		if(extendable == null)
		{
			return;
		}
		
		String tableName = extendable.tableName();
		
		if(tableName.trim().length() == 0)
		{
			tableName = "EXT_" + entityDetails.getTableName();
		}
		
		ExtendedTableDetails extendedTableDetails = new ExtendedTableDetails(tableName, extendable);
		entityDetails.setExtendedTableDetails(extendedTableDetails);
		
		ParameterizedType parameterizedType = null;
		Type mapParamTypes[] = null;
		Class<?> cls = entityType;
		
		//find extension field data holder recursively over the inheritance hierarchy
		OUTER: while(true)
		{
			if(cls.getName().startsWith("java"))
			{
				break;
			}
			
			//find entity field which will hold extended fields
			for(Field field : cls.getDeclaredFields())
			{
				if(field.getAnnotation(ExtendedFields.class) == null)
				{
					continue;
				}
				
				if(!Map.class.isAssignableFrom(field.getType()))
				{
					throw new InvalidConfigurationException("In entity '{}' extended field holder field '{}' is of non-map type", entityType.getName(), field.getName());
				}
	
				extendedTableDetails.setEntityField(field);
				
				parameterizedType = (ParameterizedType)field.getGenericType();
				mapParamTypes = parameterizedType.getActualTypeArguments();
				
				if(mapParamTypes.length != 2 || !String.class.equals(mapParamTypes[0]))
				{
					throw new InvalidConfigurationException("In entity '{}' extended field holder field '{}' is defined as map with non-string type key", entityType.getName(), field.getName());
				}
				
				break OUTER;
			}
			
			cls = cls.getSuperclass();
		}
		
		//if no field is found to hold extended field information, throw error
		if(extendedTableDetails.getEntityField() == null)
		{
			throw new InvalidConfigurationException("Entity '{}' is defined as extendable but no field is defined to hold extended field information", entityType.getName());
		}
	}
	
	/**
	 * Creates required tables, sequences, indexes etc, required by this entity
	 * @param entityDetails
	 * @param dataStore
	 */
	private void createRequiredTable(EntityDetails entityDetails, IDataStore dataStore)
	{
		Collection<ForeignConstraintDetails> foreignConstraintsLst = entityDetails.getForeignConstraints();
		Set<Class<?>> requiredParentEntitiesSet = new HashSet<>();
		List<JoinTableDetails> joinTableList = new ArrayList<>();
		
		Class<?> targetEntity = entityDetails.getEntityType();
		
		//fetch required parent tables based on foreign key constraint on this entity
		if(foreignConstraintsLst != null)
		{
			for(ForeignConstraintDetails constraint : foreignConstraintsLst)
			{
				//if the relation is maintained by target entity, ignore
				if(constraint.isMappedRelation())
				{
					continue;
				}
				
				//if current table is self joined, avoid recursive wait
				if(!targetEntity.equals(constraint.getTargetEntityDetails().getEntityType()))
				{
					requiredParentEntitiesSet.add(constraint.getTargetEntityDetails().getEntityType());
				}
				
				//track required join tables
				if(constraint.getJoinTableDetails() != null)
				{
					joinTableList.add(constraint.getJoinTableDetails());
				}
			}
		}
		
		Class<?> requiredParentEntities[] = requiredParentEntitiesSet.toArray(new Class<?>[0]);

		//create a listener which can create tables for current entity after all dependency tables are created
		IEntityCreateTableListener listener = new IEntityCreateTableListener()
		{
			@Override
			public void tableCreated(EntityDetails parentEntityDetails)
			{
				//if table is already created, ignore this call.
					// multiple invocations can happen when multiple dependencies are involved
				if(entityDetails.isTableCreated())
				{
					return;
				}
				
				//wait till all required entity tables are created
				if(!entityDetailsMonitor.isTablesCreated(requiredParentEntities))
				{
					return;
				}
				
				createEntityTable(entityDetails, dataStore, !dataStore.isUniqueIdColumnRequired());
				
				for(JoinTableDetails joinTable : joinTableList)
				{
					createEntityTable(joinTable.toEntityDetails(), dataStore, true);
				}
				
				createExtendedTable(entityDetails, dataStore);
				
				//inform the monitor current entity table is created, so that dependency tables waiting
					//	 for this table can be created
				entityDetailsMonitor.tablesCreatedForEntity(entityDetails);
				
				entityDetails.setTableCreated(true);
			}
		};
		
		//if there are no parent table dependencies
		if(requiredParentEntities.length > 0)
		{
			logger.debug("{} waiting for entity table creation - {}", entityDetails, Arrays.toString(requiredParentEntities));
			entityDetailsMonitor.addCreateTableListener(listener, requiredParentEntities);
		}
		//if parent table dependencies are present
		else
		{
			logger.debug("No dependency tables found, so executing create-table query");
			listener.tableCreated(null);
		}
	}
	
	/**
	 * Creates table for specified entity details
	 * @param entityDetails
	 * @param dataStore
	 */
	private void createEntityTable(EntityDetails entityDetails, IDataStore dataStore, boolean isUniqueKeyDisable)
	{
		//create the main table for the entity type
		CreateTableQuery createTableQuery = new CreateTableQuery(entityDetails, isUniqueKeyDisable);
		dataStore.createTable(createTableQuery);
		
		//add unique id column getting added to table on need basis
		if(!isUniqueKeyDisable)
		{
			entityDetails.addColumn(EntityDetails.COL_UQ_ENTITY_ID);
		}

		//reset the column mapping, to take new column names (if any) into consideration
		entityDetails.resetColumnMapping(createTableQuery.getTableStructure().getFieldMapping());
		
		//create required table indexes
		String columns[] = null, fields[] = null;
		int idx = 0;
		String indexName = null;
		FieldDetails fieldDetails = null;
		
		//check and create required indexes
		for(IndexDetails index: entityDetails.getIndexDetailsList())
		{
			fields = index.getFields();
			columns = new String[fields.length];
			idx = 0;
			
			for(String field: fields)
			{
				fieldDetails = entityDetails.getFieldDetailsByField(field);
				
				if(fieldDetails == null)
				{
					throw new InvalidConfigurationException("Invalid field name '{}' specified in index '{}' of entity - {} ", 
							field, index.getName(), entityDetails.getEntityType().getName());
				}
				
				columns[idx] = fieldDetails.getDbColumnName();
				idx++;
			}
			
		
			indexName = entityDetails.getTableName() + "_" + index.getName();
			dataStore.createIndex(new CreateIndexQuery(entityDetails,  indexName.toUpperCase(), columns));
		}
	}
	
	private void createExtendedTable(EntityDetails entityDetails, IDataStore dataStore)
	{
		ExtendedTableDetails extendedTableDetails = entityDetails.getExtendedTableDetails();
		
		if(extendedTableDetails == null)
		{
			return;
		}
		
		CreateTableQuery createTableQuery = new CreateTableQuery(extendedTableDetails.toEntityDetails(entityDetails), true);
		createTableQuery.setCharset(extendedTableDetails.getCharset());
		
		dataStore.createTable(createTableQuery);
	}
	
	/**
	 * Removed specified entity details from local cache. This is mainly required while the
	 * corresponding entity table is dropped
	 * @param entityType
	 */
	public void removeEntityDetails(Class<?> entityType)
	{
		typeToDetails.remove(entityType);
		entityDetailsMonitor.entityRemoved(entityType);
		
		Set<String> constraintNames = new HashSet<>(this.nameToConstraints.keySet());
		Object constraint = null;
		UniqueConstraintDetails uniqueConstraint = null;
		ForeignConstraintDetails foreingConstraint = null;
		
		for(String name : constraintNames)
		{
			constraint = nameToConstraints.get(name);
			
			if(constraint instanceof UniqueConstraintDetails)
			{
				uniqueConstraint = (UniqueConstraintDetails)constraint;
				
				if(uniqueConstraint.getEntityDetails().getEntityType().equals(entityType))
				{
					nameToConstraints.remove(name);
				}
			}
			else
			{
				foreingConstraint = (ForeignConstraintDetails)constraint;
				
				if(foreingConstraint.getOwnerEntityDetails().getEntityType().equals(entityType))
				{
					nameToConstraints.remove(name);
				}
			}
		}
		
	}
	
	/**
	 * Obtains constraint names from all entities
	 * @return
	 */
	public Set<String> getConstraintNames()
	{
		return this.nameToConstraints.keySet();
	}
	
	/**
	 * Obtains constraint object with specified name
	 * @param name
	 * @return
	 */
	public Object getConstraint(String name)
	{
		return nameToConstraints.get(name);
	}
}
