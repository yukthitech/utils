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

/**
 * Represents a condition to be executed for search.
 * @author akiran
 */
public class Condition implements IConditionTreeNode
{
	/**
	 * Field on which condition is defined.
	 */
	private String field;
	
	/**
	 * Match operator to be used.
	 */
	private MatchOperator matchOp;
	
	/**
	 * Value to be used.
	 */
	private Object value;
	
	/**
	 * Next condition in the link.
	 */
	private NextCondition nextCondition;

	/**
	 * Instantiates a new condition.
	 *
	 * @param joinOperator the join operator
	 * @param field the field
	 * @param matchOp the match op
	 * @param value the value
	 */
	public Condition(String field, MatchOperator matchOp, Object value)
	{
		this.field = field;
		this.matchOp = matchOp;
		this.value = value;
	}

	/**
	 * Gets the field on which condition is defined.
	 *
	 * @return the field on which condition is defined
	 */
	public String getField()
	{
		return field;
	}

	/**
	 * Gets the match operator to be used.
	 *
	 * @return the match operator to be used
	 */
	public MatchOperator getMatchOp()
	{
		return matchOp;
	}

	/**
	 * Gets the value to be used.
	 *
	 * @return the value to be used
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Sets the next condition in the link.
	 *
	 * @param nextCondition the new next condition in the link
	 */
	@Override
	public void setNextCondition(NextCondition nextCondition)
	{
		this.nextCondition = nextCondition;
	}
	
	/**
	 * Gets the next condition in the link.
	 *
	 * @return the next condition in the link
	 */
	@Override
	public NextCondition getNextCondition()
	{
		return nextCondition;
	}
}
