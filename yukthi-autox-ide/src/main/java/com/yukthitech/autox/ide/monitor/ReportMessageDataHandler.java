package com.yukthitech.autox.ide.monitor;

import java.io.Serializable;

import com.yukthitech.autox.debug.client.IClientDataHandler;
import com.yukthitech.autox.debug.common.MonitorLogServerMssg;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;

public class ReportMessageDataHandler implements IClientDataHandler
{
	/**
	 * Environment whose events are being listened.
	 */
	private ExecutionEnvironment environment;
	
	public ReportMessageDataHandler(ExecutionEnvironment environment)
	{
		this.environment = environment;
	}

	@Override
	public void processData(Serializable data)
	{
		if(!(data instanceof MonitorLogServerMssg))
		{
			return;
		}
		
		MonitorLogServerMssg mssg = (MonitorLogServerMssg) data;
		environment.addReportMessage(mssg);
	}
}
