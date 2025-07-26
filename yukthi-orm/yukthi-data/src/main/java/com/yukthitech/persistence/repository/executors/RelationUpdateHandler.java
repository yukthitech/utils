package com.yukthitech.persistence.repository.executors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.persistence.EntityDetails;
import com.yukthitech.persistence.FieldDetails;
import com.yukthitech.persistence.ForeignConstraintDetails;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.IDataStore;
import com.yukthitech.persistence.JoinTableDetails;
import com.yukthitech.persistence.query.ColumnParam;
import com.yukthitech.persistence.query.DeleteQuery;
import com.yukthitech.persistence.query.FinderQuery;
import com.yukthitech.persistence.query.QueryCondition;
import com.yukthitech.persistence.query.QueryResultField;
import com.yukthitech.persistence.query.SaveQuery;
import com.yukthitech.persistence.query.UpdateColumnParam;
import com.yukthitech.persistence.query.UpdateQuery;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.persistence.repository.annotations.JoinOperator;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.annotations.RelationUpdateType;
import com.yukthitech.persistence.repository.annotations.UpdateOperator;
import com.yukthitech.utils.ObjectWrapper;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Handles update operations for entity relations in update methods.
 */
public class RelationUpdateHandler
{

	/**
	 * Holds the difference between current and incoming relations.
	 */
	private static class RelationDiff
	{
		Set<Object> entitiesToAdd = new HashSet<Object>();
		Set<Object> entityIdsToAdd = new HashSet<Object>();
		Set<Object> entitiesToUpdate = new HashSet<Object>();
		Set<Object> entityIdsToUpdate = new HashSet<Object>();
		Set<Object> entityIdsToRemove = new HashSet<Object>();
	}
	
	private static class RelationInfo
	{
		EntityDetails entityDetails;
		ForeignConstraintDetails foreignConstraintDetails;
		JoinTableDetails joinTableDetails;
		EntityDetails targetEntityDetails;
		FieldDetails mappedByRevDetails;
		
		public RelationInfo(EntityDetails entityDetails, FieldDetails relationField)
		{
			this.entityDetails = entityDetails;
			foreignConstraintDetails = relationField.getForeignConstraintDetails();
			joinTableDetails = foreignConstraintDetails.getJoinTableDetails();
			targetEntityDetails = foreignConstraintDetails.getTargetEntityDetails();
			
			// In case of reverse relationship fetch join table from mapped by field
			if(joinTableDetails == null)
			{
				mappedByRevDetails = targetEntityDetails.getFieldDetailsByField(foreignConstraintDetails.getMappedBy());
				// In case of reverse relation, get join table details in reverse way
				joinTableDetails = mappedByRevDetails.getForeignConstraintDetails().getJoinTableDetails();
			}
		}
		
		public boolean isReverseRelation()
		{
			if(joinTableDetails == null)
			{
				return false;
			}
			
			EntityDetails ownerEntityDetails = joinTableDetails.getOwnerEntityDetails();
			return ownerEntityDetails.getTableName().equals(targetEntityDetails.getTableName());
		}
	}

	/**
	 * Handles the update of a relation field based on the specified update
	 * type.
	 *
	 * @param repositoryFactory The repository factory to use for DB operations
	 * @param finderQuery The FinderQuery representing the update context; can
	 *        be used to fetch parent entity ids
	 * @param entityDetails Entity details on which update op is being executed
	 * @param relationField The FieldDetails representing the relation field
	 * @param updateType The type of relation update operation to perform
	 * @param value The value (collection) provided for the relation field
	 */
	public int handleRelationUpdate(RepositoryFactory repositoryFactory, FinderQuery finderQuery, 
			EntityDetails entityDetails, FieldDetails relationField, 
			RelationUpdateType updateType, Collection<?> inputChildEntities)
	{
		int totalUpdates = 0;
		IDataStore dataStore = repositoryFactory.getDataStore();
		Object parentId = fetchParentEntityId(dataStore, finderQuery);
		
		if(parentId == null)
		{
			return 0;
		}
		
		RelationInfo relationInfo = new RelationInfo(entityDetails, relationField);

		switch (updateType)
		{
			case SYNC_RELATION:
			{
				// Fetch all current child relations for the single parent id
				Set<Object> currentChildIds = fetchAllChildRelations(dataStore, relationInfo, parentId);

				RelationDiff diff = matchCurrentVsIncoming(relationField, currentChildIds, inputChildEntities);
				totalUpdates += addRelations(dataStore, relationInfo, parentId, diff);
				totalUpdates += deleteRelations(dataStore, relationInfo, parentId, diff.entityIdsToRemove);
				break;
			}
			case CASCADE:
			{
				Set<Object> currentChildIds = fetchAllChildRelations(dataStore, relationInfo, parentId);
				RelationDiff diff = matchCurrentVsIncoming(relationField, currentChildIds, inputChildEntities);
				totalUpdates += updateRelationObjects(repositoryFactory, relationInfo, parentId, diff);
				totalUpdates += deleteRelations(dataStore, relationInfo, parentId, diff.entityIdsToRemove);
				break;
			}
			default:
				throw new InvalidStateException("Invalid relation update type: {}", updateType);
		}
		return totalUpdates;
	}

	/**
	 * Fetches the list of parent entity ids using the provided finder query.
	 *
	 * @param dataStore The data store to use for DB operations
	 * @param finderQuery The FinderQuery representing the update context
	 * 
	 * @return List of parent entity ids
	 */
	private Object fetchParentEntityId(IDataStore dataStore, FinderQuery finderQuery)
	{
		List<Object> ids = new ArrayList<>();
		
		// Use executeFinder to fetch records, then extract the id from each record
		List<com.yukthitech.persistence.Record> records = dataStore.executeFinder(finderQuery, finderQuery.getEntityDetails(), null);
		
		if(records != null)
		{
			for(com.yukthitech.persistence.Record rec : records)
			{
				ids.add(rec.getObject(0)); // Assumes id is the first column
			}
		}
		
		if(ids.size() > 1)
		{
			throw new InvalidStateException("Relation update operation expects a single parent entity, but found {} entities for the given conditions.", ids.size());
		}
		
		if(ids.isEmpty())
		{
			return null;
		}
		
		return ids.get(0);
	}

	/**
	 * Fetches all child ids for the given parent id in one query.
	 */
	private Set<Object> fetchAllChildRelations(IDataStore dataStore, RelationInfo relationInfo, Object parentId)
	{
		Set<Object> childIds = new HashSet<>();
		FinderQuery query = null;
		
		// in case of mapped relation, set the condition to match with current entity id
		if(relationInfo.joinTableDetails == null)
		{
			String childIdDbColumn = relationInfo.targetEntityDetails.getIdField().getDbColumnName();
			query = new FinderQuery(relationInfo.targetEntityDetails);
			query.addResultField(new QueryResultField(query.getDefaultTableCode(), childIdDbColumn, "childId"));
			query.addCondition(new QueryCondition(query.getDefaultTableCode(), relationInfo.mappedByRevDetails.getDbColumnName(), Operator.EQ, parentId, null, false));
		}
		// when relation is via join table
		else
		{
			EntityDetails joinEntityDetails = relationInfo.joinTableDetails.toEntityDetails();
			query = new FinderQuery(joinEntityDetails);
			
			if(relationInfo.isReverseRelation())
			{
				query.addResultField(new QueryResultField(query.getDefaultTableCode(), relationInfo.joinTableDetails.getJoinColumn(), "childId"));
				query.addCondition(new QueryCondition(query.getDefaultTableCode(), relationInfo.joinTableDetails.getInverseJoinColumn(), Operator.EQ, parentId, null, false));
			}
			else
			{
				query.addResultField(new QueryResultField(query.getDefaultTableCode(), relationInfo.joinTableDetails.getInverseJoinColumn(), "childId"));
				query.addCondition(new QueryCondition(query.getDefaultTableCode(), relationInfo.joinTableDetails.getJoinColumn(), Operator.EQ, parentId, null, false));
			}
		}
		
		List<com.yukthitech.persistence.Record> records = dataStore.executeFinder(query, query.getEntityDetails(), null);
		
		if(records != null)
		{
			for(com.yukthitech.persistence.Record rec : records)
			{
				childIds.add(rec.getLong(0)); // childId
			}
		}
		return childIds;
	}

	/**
	 * Computes which child ids need to be added and which need to be removed.
	 */
	private RelationDiff matchCurrentVsIncoming(FieldDetails relationField, Set<Object> currentChildIds, Collection<?> inputChildEntities)
	{
		ForeignConstraintDetails foreignConstraintDetails = relationField.getForeignConstraintDetails();
		EntityDetails childEntityDetails = foreignConstraintDetails.getTargetEntityDetails();

		Set<Object> inputChildIds = new HashSet<>();
		Map<Object, Object> inputEntityMap = new HashMap<>();
		RelationDiff diff = new RelationDiff();

		for(Object childEntity : inputChildEntities)
		{
			Object childId = childEntityDetails.getIdField().getValue(childEntity);

			if(currentChildIds.contains(childId))
			{
				diff.entitiesToUpdate.add(childEntity);
				diff.entityIdsToUpdate.add(childId);
			}
			else
			{
				diff.entitiesToAdd.add(childEntity);
				diff.entityIdsToAdd.add(childId);
			}

			inputChildIds.add(childId);
			inputEntityMap.put(childId, childEntity);
		}

		diff.entityIdsToRemove = new HashSet<>(currentChildIds);
		diff.entityIdsToRemove.removeAll(inputChildIds);

		return diff;
	}

	/**
	 * Adds relations for the given parent id and child ids. Returns the number
	 * of relations added.
	 */
	private int addRelations(IDataStore dataStore, RelationInfo relationInfo, Object parentId, RelationDiff diff)
	{
		if(CollectionUtils.isEmpty(diff.entityIdsToAdd))
		{
			return 0;
		}

		int count = 0;

		if(relationInfo.joinTableDetails == null)
		{
			UpdateQuery updateQuery = new UpdateQuery(relationInfo.targetEntityDetails);
			String idCol = relationInfo.targetEntityDetails.getIdField().getDbColumnName();
			
			updateQuery.addColumn(new UpdateColumnParam(relationInfo.mappedByRevDetails.getDbColumnName(), parentId, 1, UpdateOperator.NONE));
			updateQuery.addCondition(new QueryCondition(updateQuery.getDefaultTableCode(), idCol, Operator.IN, diff.entityIdsToAdd, null, false));
			count = dataStore.update(updateQuery, relationInfo.targetEntityDetails);
		}
		else
		{
			EntityDetails joinEntityDetails = relationInfo.joinTableDetails.toEntityDetails();
			boolean reverseRelation = relationInfo.isReverseRelation();

			for(Object childId : diff.entityIdsToAdd)
			{
				SaveQuery insertQuery = new SaveQuery(joinEntityDetails);
				
				if(reverseRelation)
				{
					insertQuery.addColumn(new ColumnParam(relationInfo.joinTableDetails.getInverseJoinColumn(), parentId, 1));
					insertQuery.addColumn(new ColumnParam(relationInfo.joinTableDetails.getJoinColumn(), childId, 2));
				}
				else
				{
					insertQuery.addColumn(new ColumnParam(relationInfo.joinTableDetails.getJoinColumn(), parentId, 1));
					insertQuery.addColumn(new ColumnParam(relationInfo.joinTableDetails.getInverseJoinColumn(), childId, 2));
				}
				
				dataStore.save(insertQuery, joinEntityDetails, new ObjectWrapper<Object>(null));
			}

			count = diff.entityIdsToAdd.size();
		}

		return count;
	}

	/**
	 * Adds relations for the given parent id and child ids. Returns the number
	 * of relations added.
	 */
	@SuppressWarnings("unchecked")
	private int updateRelationObjects(RepositoryFactory repositoryFactory, RelationInfo relationInfo, Object parentId, RelationDiff diff)
	{
		if(CollectionUtils.isEmpty(diff.entityIdsToAdd) && CollectionUtils.isEmpty(diff.entityIdsToUpdate))
		{
			return 0;
		}

		int count = 0;

		ICrudRepository<Object> repository = (ICrudRepository<Object>) repositoryFactory.getRepositoryForEntity(relationInfo.targetEntityDetails.getEntityType());
		EntityDetails joinEntityDetails = (relationInfo.joinTableDetails == null) ? null : relationInfo.joinTableDetails.toEntityDetails();
		IDataStore dataStore = repositoryFactory.getDataStore();
		boolean isRevRelation = relationInfo.isReverseRelation();

		// add new relations (new entity (with null id) or existing entity)
		for(Object childEntity : diff.entitiesToAdd)
		{
			Object childId = relationInfo.targetEntityDetails.getIdField().getValue(childEntity);
			boolean childEntityExists = childId == null ? false : repository.checkExistsById(childId);

			if(relationInfo.joinTableDetails == null)
			{
				try
				{
					Object parent = relationInfo.entityDetails.getEntityType().getConstructor().newInstance();
					relationInfo.entityDetails.getIdField().setValue(parent, parentId);
					relationInfo.mappedByRevDetails.setValue(childEntity, parent);
				}catch(Exception ex)
				{
					throw new InvalidStateException("An error occurred while creating entity instance of type: {}", 
							relationInfo.entityDetails.getEntityType().getName(), ex);
				}
			}

			if(!childEntityExists)
			{
				repository.save(childEntity);
				childId = relationInfo.targetEntityDetails.getIdField().getValue(childEntity);
				count++;
			}
			else
			{
				repository.update(childEntity);
				count++;
			}

			if(relationInfo.joinTableDetails != null)
			{
				SaveQuery insertQuery = new SaveQuery(relationInfo.joinTableDetails.toEntityDetails());

				if(isRevRelation)
				{
					insertQuery.addColumn(new ColumnParam(relationInfo.joinTableDetails.getJoinColumn(), childId, 1));
					insertQuery.addColumn(new ColumnParam(relationInfo.joinTableDetails.getInverseJoinColumn(), parentId, 2));
				}
				else
				{
					insertQuery.addColumn(new ColumnParam(relationInfo.joinTableDetails.getJoinColumn(), parentId, 1));
					insertQuery.addColumn(new ColumnParam(relationInfo.joinTableDetails.getInverseJoinColumn(), childId, 2));
				}
				
				dataStore.save(insertQuery, joinEntityDetails, new ObjectWrapper<Object>(null));
			}

			count++;
		}

		// update existing relations (only enity needs update)
		for(Object childEntity : diff.entitiesToUpdate)
		{
			repository.update(childEntity);
			count++;
		}

		return count;
	}

	/**
	 * Deletes relations for the given parent id and child ids. Returns the
	 * number of relations deleted.
	 */
	private int deleteRelations(IDataStore dataStore, RelationInfo relationInfo, Object parentId, Set<Object> toRemove)
	{
		if(CollectionUtils.isEmpty(toRemove))
		{
			return 0;
		}
		
		int count = 0;

		if(relationInfo.joinTableDetails == null)
		{
			UpdateQuery updateQuery = new UpdateQuery(relationInfo.targetEntityDetails);
			String idCol = relationInfo.targetEntityDetails.getIdField().getDbColumnName();
			String mapCol = relationInfo.mappedByRevDetails.getDbColumnName();
			
			updateQuery.addColumn(new UpdateColumnParam(mapCol, null, 1, UpdateOperator.NONE));
			updateQuery.addCondition(new QueryCondition(updateQuery.getDefaultTableCode(), idCol, Operator.IN, toRemove, null, false));
			updateQuery.addCondition(new QueryCondition(updateQuery.getDefaultTableCode(), mapCol, Operator.EQ, parentId, JoinOperator.AND, false));
			count = dataStore.update(updateQuery, relationInfo.targetEntityDetails);
		}
		else
		{
			EntityDetails joinEntityDetails = relationInfo.joinTableDetails.toEntityDetails();
			
			boolean reverseRelation = relationInfo.isReverseRelation();
			DeleteQuery deleteQuery = new DeleteQuery(joinEntityDetails);
			
			if(reverseRelation)
			{
				deleteQuery.addCondition(new QueryCondition(deleteQuery.getDefaultTableCode(), relationInfo.joinTableDetails.getJoinColumn(), Operator.IN, toRemove, null, false));
			}
			else
			{
				deleteQuery.addCondition(new QueryCondition(deleteQuery.getDefaultTableCode(), relationInfo.joinTableDetails.getInverseJoinColumn(), Operator.IN, toRemove, null, false));
			}
			
			count = dataStore.delete(deleteQuery, joinEntityDetails);
		}

		return count;
	}
}