package com.yukthitech.autox.exec;

import com.yukthitech.autox.IStep;
import com.yukthitech.autox.test.AutoxException;

public class ExecutionFlowFailed extends AutoxException
{
	private static final long serialVersionUID = 1L;

	public ExecutionFlowFailed(IStep sourceStep, String message, Object... args)
	{
		super(sourceStep, message, args);
	}
}
