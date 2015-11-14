package com.yukthi.persistence;

public interface ITransactionManager<T extends ITransaction>
{
	public T newTransaction() throws TransactionException;
	
	public TransactionWrapper<T> currentTransaction() throws TransactionException;
	
	public TransactionWrapper<T> newOrExistingTransaction() throws TransactionException;
}
