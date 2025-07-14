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
package com.yukthitech.persistence.rdbms;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.persistence.ITransactionManager;
import com.yukthitech.persistence.TransactionException;
import com.yukthitech.persistence.TransactionWrapper;

public class RdbmsTransactionManager implements ITransactionManager<RdbmsTransaction>
{
	private static Logger logger = LogManager.getLogger(RdbmsTransactionManager.class);
	
	private Map<Thread, RdbmsTransaction> threadToTransaction = new HashMap<>();
	
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}
	
	public DataSource getDataSource()
	{
		return dataSource;
	}
	
	private RdbmsTransaction createTransaction() throws TransactionException
	{
		RdbmsTransaction transaction = null;
		
		try
		{
			transaction = new RdbmsTransaction(this, dataSource.getConnection());
			logger.trace("Created new transaction: {}", transaction);
		}catch(SQLException ex)
		{
			throw new TransactionException("An error occurred while opnening new DB connection", ex);
		}
		
		threadToTransaction.put(Thread.currentThread(), transaction);
		return transaction;
	}
	
	@Override
	public RdbmsTransaction newTransaction() throws TransactionException
	{
		RdbmsTransaction transaction = threadToTransaction.get(Thread.currentThread());
		
		if(transaction != null)
		{
			logger.error("A transaction is already in progress by current thread: {}", transaction);
			throw new IllegalStateException("A transaction is already in progress by current thread");
		}
		
		return createTransaction();
	}
	
	@Override
	public TransactionWrapper<RdbmsTransaction> currentTransaction() throws TransactionException
	{
		RdbmsTransaction transaction = threadToTransaction.get(Thread.currentThread());
		
		if(transaction != null)
		{
			return new TransactionWrapper<RdbmsTransaction>(transaction, true);
		}
		
		throw new IllegalStateException("No transaction is started by current thread");
	}
	
	@Override
	public TransactionWrapper<RdbmsTransaction> newOrExistingTransaction() throws TransactionException
	{
		RdbmsTransaction transaction = threadToTransaction.get(Thread.currentThread());
		
		if(transaction != null)
		{
			logger.trace("Using existing transaction: {}", transaction);
			return new TransactionWrapper<RdbmsTransaction>(transaction, true);
		}
		
		return new TransactionWrapper<RdbmsTransaction>(createTransaction(), false);
	}

	void removeTransaction()
	{
		threadToTransaction.remove(Thread.currentThread());
	}
}
