package com.yukthitech.autox.ide.monitor;

import java.io.Serializable;

import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;
import com.yukthitech.autox.monitor.IAsyncClientDataHandler;
import com.yukthitech.autox.monitor.MonitorLogMessage;

public class ReportMessageDataHandler implements IAsyncClientDataHandler
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
		if(!(data instanceof MonitorLogMessage))
		{
			return;
		}
		
		MonitorLogMessage mssg = (MonitorLogMessage) data;
		environment.addReportMessage(mssg);
	}
}
