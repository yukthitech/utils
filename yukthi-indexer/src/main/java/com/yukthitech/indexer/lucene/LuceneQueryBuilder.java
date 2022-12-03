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
package com.yukthitech.indexer.lucene;

import java.util.Collection;

import com.yukthitech.indexer.search.Condition;
import com.yukthitech.indexer.search.ConditionGroup;
import com.yukthitech.indexer.search.IConditionTreeNode;
import com.yukthitech.indexer.search.JoinOperator;
import com.yukthitech.indexer.search.MatchOperator;
import com.yukthitech.indexer.search.SearchConditionTreeBuilder;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Builds the lucene query string from query object.
 * @author akiran
 */
public class LuceneQueryBuilder
{
	/**
	 * Used to extract condition tree from query object.
	 */
	private SearchConditionTreeBuilder conditionTreeBuilder = new SearchConditionTreeBuilder();
	
	/**
	 * Builds lucene query string from query object.
	 * @param queryObj
	 * @param defaultJoin
	 * @return
	 */
	public String buildQuery(Object queryObj)
	{
		IConditionTreeNode root = null;
		
		//build the condition tree
		try
		{
			root = conditionTreeBuilder.buildConditionTree(queryObj);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while building conditions from input query", ex);
		}
		
		if(root == null)
		{
			throw new InvalidStateException("No conditions found on input query");
		}
		
		//build the final Lucene query
		StringBuilder query = new StringBuilder();
		addCondition(root, query);

		return query.toString();
	}
	
	/**
	 * Adds specified condition node to the query.
	 * @param conditionNode
	 * @param query
	 */
	private void addCondition(IConditionTreeNode conditionNode, StringBuilder query)
	{
		if(conditionNode instanceof ConditionGroup)
		{
			query.append("(");
			
			ConditionGroup group = (ConditionGroup) conditionNode;
			addCondition(group.getCondition(), query);
			
			query.append(")");
			return;
		}
		
		Condition condition = (Condition) conditionNode;
		JoinOperator joinOp = null;
		
		while(true)
		{
			query.append(condition.getField()).append(": ");
			addValues(condition.getValue(), query, condition.getMatchOp());
			
			//if no more conditions are there, break the loop
			if(condition.getNextCondition() == null)
			{
				break;
			}
			
			//find the join operator to be used
			joinOp = condition.getNextCondition().getJoinOperator();
			query.append(" ").append(joinOp.name()).append(" ");
			addCondition(condition.getNextCondition().getCondition(), query);
		}
	}
	
	/**
	 * Adds the condition value(s) to the query.
	 * @param value
	 * @param query
	 * @param matchOp
	 */
	@SuppressWarnings("unchecked")
	private void addValues(Object value, StringBuilder query, MatchOperator matchOp)
	{
		//if value is collection, add space separated values 
		if(value instanceof Collection)
		{
			Collection<Object> col = (Collection<Object>) value;
			
			for(Object obj : col)
			{
				addStringValue(obj.toString(), query);
				query.append(" ");
			}
		}
		//if non collection add single string value
		else
		{
			String prefix = "";
			
			//in case of single value use specified match operator if any
			if(matchOp == MatchOperator.MUST_EXIST)
			{
				prefix = "+";
			}
			else if(matchOp == MatchOperator.MUST_NOT_EXIST)
			{
				prefix = "-";
			}
			
			addStringValue(prefix + value.toString().toLowerCase(), query);
		}
	}
	
	/**
	 * Adds the specified condition string value to query. 
	 * @param strValue
	 * @param query
	 */
	private void addStringValue(String strValue, StringBuilder query)
	{
		query.append(strValue);
	}
}
