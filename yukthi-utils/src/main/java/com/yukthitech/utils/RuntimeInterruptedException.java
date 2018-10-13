package com.yukthitech.utils;

/**
 * Runtime wrapper of interrupted exception.
 * @author akiran
 */
public class RuntimeInterruptedException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public RuntimeInterruptedException(InterruptedException ex)
	{
		super(ex);
	}
}
