package com.yukthitech.autox.ide.exeenv;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.monitor.ReportMessageDataHandler;
import com.yukthitech.autox.monitor.MonitorClient;
import com.yukthitech.autox.monitor.MonitorLogMessage;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class ExecutionEnvironment
{
	private static Logger logger = LogManager.getLogger(ExecutionEnvironment.class);
	
	private String name;
	
	private Process process;
	
	private StringBuilder consoleHtml = new StringBuilder();
	
	private IContextListener proxyListener;
	
	private boolean terminated = false;
	
	private MonitorClient monitorClient;
	
	private List<MonitorLogMessage> reportMessages = new LinkedList<>();
	
	private File reportFolder;
	
	ExecutionEnvironment(String name, Process process, IContextListener proxyListener, int monitoringPort, File reportFolder)
	{
		this.name = name;
		this.process = process;
		this.proxyListener = proxyListener;
		this.reportFolder = reportFolder;
		
		Thread outputThread = new Thread() 
		{
			public void run()
			{
				readConsoleStream(process.getInputStream(), false);
			}
		};
		
		outputThread.start();
		
		Thread errThread = new Thread() 
		{
			public void run()
			{
				readConsoleStream(process.getErrorStream(), true);
			}
		};
		
		errThread.start();
		
		IdeUtils.execute(() -> 
		{
			monitorClient = MonitorClient.startClient("localhost", monitoringPort);
			addListeners();
		}, 1);
	}
	
	public void terminate()
	{
		if(!process.isAlive())
		{
			return;
		}
		
		process.destroyForcibly();
	}
	
	private void addListeners()
	{
		ReportMessageDataHandler logHandler = new ReportMessageDataHandler(this);
		monitorClient.addAsyncClientDataHandler(logHandler);
	}
	
	private synchronized void appenConsoleHtml(String html)
	{
		consoleHtml.append(html);
		proxyListener.environmentChanged(EnvironmentEvent.newConsoleChangedEvent(this, html));
	}
	
	private void readConsoleStream(InputStream input, boolean error)
	{
		InputStreamReader inReader = new InputStreamReader(input);
		final BufferedReader reader = new BufferedReader(inReader);
		
		String line  = null, lineHtml = null;

		try
		{
			while((line = reader.readLine()) != null)
			{
				if(error)
				{
					logger.error(">>[{}] {}", name, line);
				}
				else
				{
					logger.debug(">>[{}] {}", name, line);
				}
				
				line = line.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
				
				if(error)
				{
					lineHtml = "<div style=\"color:red;\">" + line + "</div>";
				}
				else
				{
					lineHtml = "<div>" + line + "</div>";
				}
				
				appenConsoleHtml(lineHtml);
			}
			
			reader.close();
			
			int code = process.waitFor();
			monitorClient.close();
			
			appenConsoleHtml("<div>Process exited with code: " + code + "</div>");
			
			synchronized(this)
			{
				if(!terminated)
				{
					terminated = true;
					proxyListener.environmentTerminated(this);
				}
			}
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while reading console stream", ex);
		}
	}
	
	public void addReportMessage(MonitorLogMessage mssg)
	{
		this.reportMessages.add(mssg);
		proxyListener.environmentChanged(EnvironmentEvent.newReportLogEvent(this, mssg));
	}
	
	public String getName()
	{
		return name;
	}
	
	public StringBuilder getConsoleHtml()
	{
		return consoleHtml;
	}
	
	public void stop()
	{
		process.destroyForcibly();
	}
	
	public boolean isTerminated()
	{
		return terminated;
	}
	
	public List<MonitorLogMessage> getReportMessages()
	{
		return reportMessages;
	}
	
	public File getReportFolder()
	{
		return reportFolder;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
