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
package com.yukthitech.persistence.repository.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.persistence.OrderByField;
import com.yukthitech.utils.CommonUtils;

/**
 * Search query object for search methods with dynamic conditions
 * 
 * @author akiran
 */
public class SearchQuery
{
	/**
	 * Dynamic conditions to be applied
	 */
	private List<SearchCondition> conditions = new ArrayList<>();
	
	/**
	 * List of fields by which search results should be ordered
	 */
	private List<OrderByField> orderByFields = new ArrayList<>();
	
	/**
	 * Row number after which results should be fetched. Used in paging.
	 */
	private int resultsOffset = -1;
	
	/**
	 * Maximum number of rows to return. Used in paging.
	 */
	private int resultsLimit = -1;
	
	/**
	 * Additional fields to include along with standard return fields.
	 */
	private Set<String> additionalEntityFields;
	
	/**
	 * Search result fields to be excluded.
	 */
	private Set<String> excludeFields;
	
	/**
	 * Used when current search query is used as subquery.
	 */
	private Class<?> subentityType;
	
	/**
	 * Instantiates a new search query.
	 */
	public SearchQuery()
	{}
	
	/**
	 * Instantiates a new search query.
	 *
	 * @param conditions the conditions
	 */
	public SearchQuery(SearchCondition... conditions)
	{
		for(SearchCondition cond : conditions)
		{
			this.addCondition(cond);
		}
	}

	/**
	 * Builder method to build search-subquery. 
	 * @param entityType Entity on which this subquery has to be executed
	 * @param resultField single result field of this subquery
	 * @param conditions conditions to be applied in subquery
	 * @return new search subquery
	 */
	public static SearchQuery subquery(Class<?> entityType, String resultField, SearchCondition... conditions)
	{
		SearchQuery searchQuery = new SearchQuery(conditions);
		searchQuery.subentityType = entityType;
		searchQuery.additionalEntityFields = CommonUtils.toSet(resultField);
		
		return searchQuery;
	}
	
	/**
	 * Adds value to {@link #conditions conditions}
	 *
	 * @param condition
	 *            condition to be added
	 *            
	 * @return returns curent query to add more conditions
	 */
	public SearchQuery addCondition(SearchCondition condition)
	{
		if(condition == null)
		{
			throw new NullPointerException("Condition can not be null.");
		}
		
		conditions.add(condition);
		return this;
	}
	
	/**
	 * Sets the dynamic conditions to be applied.
	 *
	 * @param conditions the new dynamic conditions to be applied
	 */
	public void setConditions(List<SearchCondition> conditions)
	{
		this.conditions = conditions;
	}

	/**
	 * Gets the dynamic conditions to be applied.
	 *
	 * @return the dynamic conditions to be applied
	 */
	public List<SearchCondition> getConditions()
	{
		return conditions;
	}
	
	/**
	 * Gets the list of fields by which search results should be ordered.
	 *
	 * @return the list of fields by which search results should be ordered
	 */
	public List<OrderByField> getOrderByFields()
	{
		return orderByFields;
	}

	/**
	 * Sets the list of fields by which search results should be ordered.
	 *
	 * @param orderByFields the new list of fields by which search results should be ordered
	 */
	public void setOrderByFields(List<OrderByField> orderByFields)
	{
		this.orderByFields = orderByFields;
	}

	/**
	 * Gets the row number after which results should be fetched. Used in paging.
	 *
	 * @return the row number after which results should be fetched
	 */
	public int getResultsOffset()
	{
		return resultsOffset;
	}

	/**
	 * Sets the row number after which results should be fetched. Used in paging.
	 *
	 * @param resultsOffset the new row number after which results should be fetched
	 */
	public void setResultsOffset(int resultsOffset)
	{
		this.resultsOffset = resultsOffset;
	}

	/**
	 * Gets the maximum number of rows to return. Used in paging.
	 *
	 * @return the maximum number of rows to return
	 */
	public int getResultsLimit()
	{
		return resultsLimit;
	}

	/**
	 * Sets the maximum number of rows to return. Used in paging.
	 *
	 * @param resultsLimit the new maximum number of rows to return
	 */
	public void setResultsLimit(int resultsLimit)
	{
		this.resultsLimit = resultsLimit;
	}
	
	/**
	 * Gets the additional fields to include along with standard return fields.
	 *
	 * @return the additional fields to include along with standard return fields
	 */
	public Set<String> getAdditionalEntityFields()
	{
		return additionalEntityFields;
	}

	/**
	 * Sets the additional fields to include along with standard return fields.
	 *
	 * @param additionalEntityFields the new additional fields to include along with standard return fields
	 */
	public SearchQuery setAdditionalEntityFields(Set<String> additionalEntityFields)
	{
		this.additionalEntityFields = additionalEntityFields;
		return this;
	}
	
	/**
	 * Adds specified additional entity field to the search query result fields.
	 * @param field Field to be added.
	 */
	public void addAdditionEntityField(String field)
	{
		if(this.additionalEntityFields == null)
		{
			this.additionalEntityFields = new HashSet<>();
		}
		
		this.additionalEntityFields.add(field);
	}

	/**
	 * Gets the search result fields to be excluded.
	 *
	 * @return the search result fields to be excluded
	 */
	public Set<String> getExcludeFields()
	{
		return excludeFields;
	}

	/**
	 * Sets the search result fields to be excluded.
	 *
	 * @param excludeFields the new search result fields to be excluded
	 */
	public SearchQuery setExcludeFields(Set<String> excludeFields)
	{
		this.excludeFields = excludeFields;
		return this;
	}
	
	/**
	 * Adds specified exceluded field to the search query.
	 * @param field Field to be excluded.
	 */
	public void addExcludedField(String field)
	{
		if(this.excludeFields == null)
		{
			this.excludeFields = new HashSet<>();
		}
		
		this.excludeFields.add(field);
	}
	
	/**
	 * Gets the used when current search query is used as subquery.
	 *
	 * @return the used when current search query is used as subquery
	 */
	public Class<?> getSubentityType()
	{
		return subentityType;
	}

	/**
	 * Sets the used when current search query is used as subquery.
	 *
	 * @param subentityType the new used when current search query is used as subquery
	 */
	public void setSubentityType(Class<?> subentityType)
	{
		this.subentityType = subentityType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Conditions: ").append(conditions);
		builder.append(", ").append("Order-by Fields: ").append(orderByFields);
		builder.append(", ").append("Results Offset: ").append(resultsOffset);
		builder.append(", ").append("Results Limit: ").append(resultsLimit);
		
		if(CollectionUtils.isNotEmpty(additionalEntityFields))
		{
			builder.append(", ").append("Additional Fields: ").append(additionalEntityFields);
		}
		
		if(CollectionUtils.isNotEmpty(excludeFields))
		{
			builder.append(", ").append("Excluded Fields: ").append(excludeFields);
		}
		
		

		builder.append("]");
		return builder.toString();
	}

}
