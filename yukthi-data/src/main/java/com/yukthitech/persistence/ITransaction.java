package com.yukthitech.persistence;

public interface ITransaction extends AutoCloseable
{
	public void commit() throws TransactionException;
	
	public void rollback() throws TransactionException;
	
	public void close() throws TransactionException;
	
	/**
	 * Fetches whether this transaction is closed or not.
	 * @return true if transaction is already closed.
	 */
	public boolean isClosed();
}
