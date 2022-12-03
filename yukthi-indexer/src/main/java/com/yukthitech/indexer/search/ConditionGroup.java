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
 * The Class ConditionGroup.
 */
public class ConditionGroup implements IConditionTreeNode
{
	/**
	 * Root condition of this group.
	 */
	private IConditionTreeNode condition;
	
	/**
	 * Next condition in the link.
	 */
	private NextCondition nextCondition;

	/**
	 * Instantiates a new condition group.
	 *
	 * @param condition the condition
	 * @param nextCondition the next condition
	 */
	public ConditionGroup(IConditionTreeNode condition, NextCondition nextCondition)
	{
		this.condition = condition;
		this.nextCondition = nextCondition;
	}

	/**
	 * Gets the root condition of this group.
	 *
	 * @return the root condition of this group
	 */
	public IConditionTreeNode getCondition()
	{
		return condition;
	}

	/**
	 * Sets the root condition of this group.
	 *
	 * @param condition the new root condition of this group
	 */
	void setCondition(Condition condition)
	{
		this.condition = condition;
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
