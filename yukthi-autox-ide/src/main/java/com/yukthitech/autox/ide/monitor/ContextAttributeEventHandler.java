package com.yukthitech.autox.ide.monitor;

import java.io.Serializable;

import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;
import com.yukthitech.autox.monitor.IAsyncClientDataHandler;
import com.yukthitech.autox.monitor.ienv.ContextAttributeDetails;

public class ContextAttributeEventHandler implements IAsyncClientDataHandler
{
	private ExecutionEnvironment environment;

	public ContextAttributeEventHandler(ExecutionEnvironment environment)
	{
		this.environment = environment;
		
	}

	@Override
	public void processData(Serializable data)
	{
		if(!(data instanceof ContextAttributeDetails))
		{
			return;
		}
		
		ContextAttributeDetails ctx = (ContextAttributeDetails) data;
		environment.addContextAttribute(ctx);
	}

}
