package com.yukthi.persistence.repository.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.yukthi.persistence.OrderByField;

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
	 * Adds value to {@link #conditions conditions}
	 *
	 * @param condition
	 *            condition to be added
	 *            
	 * @return returns curent query to add more conditions
	 */
	public SearchQuery addCondition(SearchCondition condition)
	{
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
