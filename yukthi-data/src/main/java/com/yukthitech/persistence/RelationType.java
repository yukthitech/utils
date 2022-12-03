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

/**
 * Enumeration of entity relation types.
 * @author akiran
 */
public enum RelationType
{
	ONE_TO_ONE(false, false),
	MANY_TO_ONE(false, true),
	ONE_TO_MANY(true, false),
	MANY_TO_MANY(true, true);
	
	/**
	 * Is the field where relation is defined is expected to be collection
	 */
	private boolean collectionExpected;
	
	/**
	 * Is the mapped field (from target entity) is expected to be collection
	 */
	private boolean collectionTargetExpected;

	private RelationType(boolean collectionExpected, boolean collectionTargetExpected)
	{
		this.collectionExpected = collectionExpected;
		this.collectionTargetExpected = collectionTargetExpected;
	}

	/**
	 * @return the {@link #collectionExpected collectionExpected}
	 */
	public boolean isCollectionExpected()
	{
		return collectionExpected;
	}

	/**
	 * @return the {@link #collectionTargetExpected collectionTargetExpected}
	 */
	public boolean isCollectionTargetExpected()
	{
		return collectionTargetExpected;
	}
	
	
}
