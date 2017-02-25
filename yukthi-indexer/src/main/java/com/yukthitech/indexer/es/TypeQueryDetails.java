package com.yukthitech.indexer.es;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.yukthitech.indexer.IndexType;
import com.yukthitech.indexer.search.ConditionOperator;
import com.yukthitech.indexer.search.FieldBooster;
import com.yukthitech.indexer.search.JoinOperator;
import com.yukthitech.indexer.search.NullCheck;
import com.yukthitech.indexer.search.SearchCondition;
import com.yukthitech.indexer.search.SearchQuery;
import com.yukthitech.indexer.search.Subquery;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.beans.BeanProperty;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class TypeQueryDetails
{
	private class Condition
	{
		private String field;
		private ConditionOperator conditionOperator;
		private int boost = 0;
		private String minMatch;
		
		private boolean nullCheck = false;
		private boolean notNullCheck = false;
		
		private BeanProperty beanProperty;

		public Condition(String field, BeanProperty beanProperty, SearchCondition condition)
		{
			this.field = field;
			this.conditionOperator = condition.op();
			this.boost = condition.boost();
			this.beanProperty = beanProperty;
			
			this.minMatch = condition.minMatch();
			this.minMatch = StringUtils.isBlank(minMatch) ? null : minMatch.trim();
		}

		public Condition(String field, boolean nullCheck, boolean notNullCheck, int boost)
		{
			this.field = field;
			this.nullCheck = nullCheck;
			this.notNullCheck = notNullCheck;
			this.boost = boost;
		}
		
		public List<Map<String, Object>> toQuery(Object queryObj, TypeIndexDetails indexDetails)
		{
			TypeIndexDetails.FieldIndexDetails fieldDet = indexDetails.getField(field);
			
			if(fieldDet == null)
			{
				throw new InvalidStateException("Invalid index field name '{}' specified in search query property - {}", field, beanProperty.getName());
			}
			
			if(nullCheck)
			{
				return Arrays.asList( CommonUtils.toMap("missing",
						CommonUtils.toMap("field", field)
					) );
			}
			
			if(notNullCheck)
			{
				return Arrays.asList( CommonUtils.toMap("exists",
						CommonUtils.toMap("field", field)
					) );
			}

			Object value = beanProperty.getValue(queryObj);
			
			if(value == null)
			{
				return null;
			}
			
			if(fieldDet.getEsDataType() == EsDataType.STRING && fieldDet.isIgnoreCase())
			{
				value = IndexUtils.toLowerCase(value);
			}
			
			if(value instanceof Map)
			{
				Map<?, ?> map = (Map<?, ?>)value;
				
				if(map.isEmpty())
				{
					return null;
				}
				
				List<Map<String, Object>> queries = new ArrayList<>();
				Object entryValue = null;
				
				for(Object key : map.keySet())
				{
					entryValue = map.get(key);
					
					if(entryValue == null)
					{
						continue;
					}
					
					if(entryValue instanceof Collection)
					{
						queries.add( CommonUtils.toMap("terms", 
								CommonUtils.toMap(field + "." + key, entryValue))
								);
					}
					else
					{
						queries.add( CommonUtils.toMap("term", 
								CommonUtils.toMap(field + "." + key, entryValue))
								);
					}
				}
				
				return queries.isEmpty() ? null : queries;
			}
			
			if(fieldDet.getEsDataType() != EsDataType.STRING || fieldDet.getIndexType() == IndexType.NOT_ANALYZED)
			{
				//perform exact value search
				if(conditionOperator == ConditionOperator.EQ)
				{
					if(value instanceof Collection)
					{
						return Arrays.asList( CommonUtils.toMap("terms", 
									CommonUtils.toMap(field, value)
								) );
					}
					
					return Arrays.asList( CommonUtils.toMap("term", 
								CommonUtils.toMap(field, value)
							) );
				}
				
				return Arrays.asList( CommonUtils.toMap("range", 
							CommonUtils.toMap(field,
								CommonUtils.toMap(conditionOperator.name().toLowerCase(), value)
							)
						) );
			}
			
			Map<String, Object> query = CommonUtils.toMap(field, CommonUtils.toMap("query", value));
			
			if(conditionOperator == ConditionOperator.AND)
			{
				query.put("operator", "and");
			}
			
			if(minMatch != null)
			{
				query.put("minimum_should_match", minMatch);
			}
			
			if(boost > 0)
			{
				query.put("boost", boost);
			}
			
			return Arrays.asList( CommonUtils.toMap("match", query) );
		}
	}
	
	private class FieldBoosterDetails
	{
		private String field;
		private int logFactor;
		
		public FieldBoosterDetails(FieldBooster booster)
		{
			this.field = booster.field();
			this.logFactor = booster.logFactor();
		}
		
		public Map<String, Object> toQuery()
		{
			Map<String, Object> query = CommonUtils.toMap("field", field);
			
			if(logFactor > 0)
			{
				query.put("modifier", "log1p");
				
				if(logFactor > 1)
				{
					query.put("factor", logFactor);
				}
			}
			
			return query;
		}
	}
	
	/**
	 * Conditions grouped using join operator.
	 */
	private Map<JoinOperator, List<Object>> conditionGroups = new HashMap<>();
	
	/**
	 * For sub queries, bean property to be used on main query object.
	 */
	private BeanProperty beanProperty;
	
	/**
	 * this query corresponding index type to be used.
	 */
	private Class<?> indexType;
	
	/**
	 * Flag indicating if score needs to be ignore. In other terms, whether constant score query
	 * needs to be built.
	 */
	private boolean ignoreScore;
	
	/**
	 * Field booster details for the query if any
	 */
	private FieldBoosterDetails fieldBoosterDetails;

	public TypeQueryDetails(Class<?> queryType)
	{
		loadClassLevelConditons(queryType);
		
		loadPropertyConditions(queryType);
		
		FieldBooster fieldBooster = queryType.getAnnotation(FieldBooster.class);
		
		if(fieldBooster != null)
		{
			this.fieldBoosterDetails = new FieldBoosterDetails(fieldBooster);
		}
	}
	
	private TypeQueryDetails(Class<?> subQueryType, BeanProperty beanProperty)
	{
		this.beanProperty = beanProperty;
		this.loadPropertyConditions(subQueryType);
	}
	
	/**
	 * Gets the this query corresponding index type to be used.
	 *
	 * @return the this query corresponding index type to be used
	 */
	public Class<?> getIndexType()
	{
		return indexType;
	}
	
	/**
	 * Loads conditions from search query annotation defined at class level.
	 * @param queryType
	 */
	private void loadClassLevelConditons(Class<?> queryType)
	{
		SearchQuery searchQuery = queryType.getAnnotation(SearchQuery.class);
		
		if(searchQuery == null)
		{
			throw new InvalidArgumentException("Specified type is not marked as search query - {}", queryType.getName());
		}
		
		this.indexType = searchQuery.indexType();
		this.ignoreScore = searchQuery.ignoreScore();
		
		//add null check conditions
		for(NullCheck check : searchQuery.nullFields())
		{
			addCondition(check.joinOperator(), new Condition(check.field(), true, false, check.boost()));
		}
		
		//add not null check conditions
		for(NullCheck check : searchQuery.notNullFields())
		{
			addCondition(check.joinOperator(), new Condition(check.field(), false, true, check.boost()));
		}
	}
	
	/**
	 * Adds specified condition to appropriate group of conditions.
	 * @param joinOperator
	 * @param condition
	 */
	private void addCondition(JoinOperator joinOperator, Object condition)
	{
		List<Object> conditions = this.conditionGroups.get(joinOperator);
		
		if(conditions == null)
		{
			conditions = new ArrayList<>();
			this.conditionGroups.put(joinOperator, conditions);
		}
		
		conditions.add(condition);
	}
	
	/**
	 * Loads property conditions from specified query type.
	 * @param queryType
	 */
	private void loadPropertyConditions(Class<?> queryType)
	{
		List<BeanProperty> properties = BeanProperty.loadProperties(queryType, true, false);
		
		if(properties == null)
		{
			throw new InvalidStateException("No properties found in specified query type - {}", queryType.getName());
		}
		
		SearchCondition searchCondition = null;
		Subquery subquery = null;
		String field = null;
		
		for(BeanProperty property : properties)
		{
			subquery = property.getAnnotation(Subquery.class);
			
			if(subquery != null)
			{
				addCondition( subquery.joinOperator(), new TypeQueryDetails(property.getType(), property) );
			}
			
			searchCondition = property.getAnnotation(SearchCondition.class);
			
			//ignore property which dont have search condition
			if(searchCondition == null)
			{
				continue;
			}
			
			field = searchCondition.field();
			field = StringUtils.isBlank(field) ? property.getName() : field;
			
			addCondition(searchCondition.joinOperator(), new Condition(field, property, searchCondition));
		}
	}
	
	private Map<String, Object> toBoolQuery(Object queryObj, TypeIndexDetails indexDetails)
	{
		Map<String, Object> conditionsGroupMap = new HashMap<>();
		List<Map<String, Object>> conditionMaps = null;
		
		List<Map<String, Object>> conditionQueries = null;
		Object subqueryObj = null;
		
		//loop through groups
		for(JoinOperator joinOp : this.conditionGroups.keySet())
		{
			conditionMaps = new ArrayList<>();

			//loop though conditions in group
			for(Object condition : this.conditionGroups.get(joinOp))
			{
				if(condition instanceof Condition)
				{
					conditionQueries = ((Condition)condition).toQuery(queryObj, indexDetails);
				}
				else
				{
					subqueryObj = beanProperty.getValue(queryObj);
					
					if(subqueryObj == null)
					{
						continue;
					}
					
					conditionQueries = Arrays.asList( ((TypeQueryDetails)condition).toBoolQuery(subqueryObj, indexDetails) );
				}
				
				if(conditionQueries != null)
				{
					conditionMaps.addAll(conditionQueries);
				}
			}
			
			//if conditions are found in this group
			if(!conditionMaps.isEmpty())
			{
				conditionsGroupMap.put(joinOp.name().toLowerCase(), conditionMaps);
			}
		}
		
		return CommonUtils.toMap("bool", conditionsGroupMap);
	}
	
	public Map<String, Object> buildQuery(Object queryObj, TypeIndexDetails indexDetails)
	{
		Map<String, Object> query = toBoolQuery(queryObj, indexDetails);
		
		if(ignoreScore)
		{
			query = CommonUtils.toMap("constant_score",
								CommonUtils.toMap("filter", query)
					);
		}
		
		if(fieldBoosterDetails == null)
		{
			return CommonUtils.toMap("query", query);
		}
		
		Map<String, Object> finalQuery = CommonUtils.toMap("query", query, "field_value_factor", fieldBoosterDetails.toQuery());
		finalQuery = CommonUtils.toMap("function_score", finalQuery);
		
		return CommonUtils.toMap("query", finalQuery);
	}
}
