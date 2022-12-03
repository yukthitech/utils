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
 * Can be used to process records as and when records are being fetched from DB. This instance can 
 * be passed {@link IDataStore#executeFinder(com.yukthitech.persistence.query.FinderQuery, EntityDetails, IFinderRecordProcessor)}
 * @author akiran
 */
public interface IFinderRecordProcessor
{
	/**
	 * Represents action to be taken on the record
	 * @author akiran
	 */
	public enum Action
	{
		/**
		 * Ignore current record and proceed to next one.
		 */
		IGNORE, 
		
		/**
		 * Process the record and proceed to next one.
		 */
		PROCESS, 
		
		/**
		 * Ignore current record and stop processing following records.
		 */
		STOP;
	}
	
	/**
	 * Called for every record being processed.
	 * @param recordNo Current record number. This starts with zero.
	 * @param record Record fetched
	 * @return Action to be taken on current and future records.
	 */
	public Action process(long recordNo, Record record);
}
