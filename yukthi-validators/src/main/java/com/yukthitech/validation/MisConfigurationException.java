package com.yukthitech.validation;

public class MisConfigurationException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public MisConfigurationException(String mssgTemp, Object... args)
	{
		super(mssgTemp.formatted(args), getRootCause(args));
	}
	
	public static Throwable getRootCause(Object args[])
	{
		if(args == null || args.length == 0)
		{
			return null;
		}
		
		if(!(args[args.length - 1] instanceof Throwable))
		{
			return null;
		}
		
		return (Throwable) args[args.length - 1];
	}

}
