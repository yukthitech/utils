package com.yukthitech.autox.ide.monitor;

import java.io.Serializable;

import com.yukthitech.autox.debug.client.IClientDataHandler;
import com.yukthitech.autox.debug.common.ContextAttributeDetails;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;

public class ContextAttributeEventHandler implements IClientDataHandler
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
