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
