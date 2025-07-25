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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransactionWrapper<T extends ITransaction> implements ITransaction
{
	private static Logger logger = LogManager.getLogger(TransactionWrapper.class);
	
	private T transaction;
	private boolean existingTransaction;
	
	private boolean committed = false;
	
	public TransactionWrapper(T transaction, boolean existingTransaction)
	{
		this.transaction = transaction;
		this.existingTransaction = existingTransaction;
	}

	@Override
	public void commit() throws TransactionException
	{
		committed = true;
		
		if(existingTransaction)
		{
			logger.trace("Commit is called on existing transaction. Ignoring commit call: {}", this);
			return;
		}
		
		logger.trace("Commiting transaction: {}", this);
		transaction.commit();
	}

	@Override
	public void rollback() throws TransactionException
	{
		logger.debug("Rolling back transaction: {}", this);
		transaction.rollback();
	}

	@Override
	public void close() throws TransactionException
	{
		if(existingTransaction)
		{
			if(committed)
			{
				logger.trace("Close is called on committed existing transaction. Ignoring commit: {}", this);
				return;
			}
			
			logger.debug("Without calling commit(), close is called on existing transaction. Ignoring close: {}", this);
			return;
		}

		logger.trace("Closing transaction: {}", this);
		transaction.close();
	}
	
	public boolean isExistingTransaction()
	{
		return existingTransaction;
	}

	public T getTransaction()
	{
		return transaction;
	}
	
	@Override
	public boolean isClosed()
	{
		return transaction.isClosed();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Transaction: ").append(transaction);
		builder.append(",").append("Existing: ").append(existingTransaction);

		builder.append("]");
		return builder.toString();
	}
}
