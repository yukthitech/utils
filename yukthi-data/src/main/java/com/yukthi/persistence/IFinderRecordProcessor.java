/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.persistence;

/**
 * Can be used to process records as and when records are being fetched from DB. This instance can 
 * be passed {@link IDataStore#executeFinder(com.yukthi.persistence.query.FinderQuery, EntityDetails, IFinderRecordProcessor)}
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
	 * @param recordNo Current record number
	 * @param record Record fetched
	 * @return Action to be taken on current and future records.
	 */
	public Action process(long recordNo, Record record);
}
