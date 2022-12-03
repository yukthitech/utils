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
package com.yukthitech.indexer.search;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;

/**
 * Builds the condition tree based on input object using {@link SearchQuery} and {@link SearchGroupCondition} annotations.
 * @author akiran
 */
public class SearchConditionTreeBuilder
{
	/**
	 * Builds the condition tree for specified query object.
	 * @param query query object to use for building
	 * @return condition tree representing query object
	 */
	public IConditionTreeNode buildConditionTree(Object query) throws Exception
	{
		JoinOperator defJoinOp = JoinOperator.AND;
		SearchQuery searchQuery = query.getClass().getAnnotation(SearchQuery.class);
		
		if(searchQuery != null && searchQuery.defaultJoinOp() != JoinOperator.DEFAULT)
		{
			defJoinOp = searchQuery.defaultJoinOp();
		}
		
		
		return buildConditionTree(query, "", defJoinOp); 
	}
	
	private IConditionTreeNode buildConditionTree(Object query, String prefix, JoinOperator defJoinOp) throws Exception
	{
		Class<?> type = query.getClass();
		Field fields[] = type.getDeclaredFields();
		SearchCondition searchCondition = null;
		IConditionTreeNode root = null;
		IConditionTreeNode condition = null;
		IConditionTreeNode newCondition = null;
		SearchGroupCondition groupCondition = null;
		JoinOperator joinOp = null;
		
		for(Field fld : fields)
		{
			searchCondition = fld.getAnnotation(SearchCondition.class);
			groupCondition = fld.getAnnotation(SearchGroupCondition.class);
			
			if(searchCondition == null && groupCondition == null)
			{
				continue;
			}
			
			fld.setAccessible(true);
			Object value = fld.get(query);
			
			if(value == null)
			{
				continue;
			}
			
			if(groupCondition == null)
			{
				String name = fld.getName();
				
				if(StringUtils.isNoneBlank(searchCondition.field()))
				{
					name = searchCondition.field();
				}
				
				newCondition = new Condition(name, searchCondition.matchOp(), value);
				joinOp = searchCondition.joinWith();
			}
			else
			{
				String parentName = StringUtils.isNotBlank(groupCondition.parentField()) ? groupCondition.parentField() + "." : null;
				JoinOperator defGrpJoinOp = 
						groupCondition.defaultJoinOp() == JoinOperator.DEFAULT ? JoinOperator.AND : groupCondition.defaultJoinOp();
				
				newCondition = buildConditionTree(value, parentName, defGrpJoinOp);
				
				newCondition = new ConditionGroup(newCondition, null);
				joinOp = groupCondition.joinWith();
			}
			
			if(root == null)
			{
				root = newCondition;
				condition = newCondition;
				continue;
			}
			
			if(joinOp == JoinOperator.DEFAULT)
			{
				joinOp = defJoinOp;
			}
			
			condition.setNextCondition(new NextCondition(joinOp, newCondition));
		}
		
		return root;
	}
}
