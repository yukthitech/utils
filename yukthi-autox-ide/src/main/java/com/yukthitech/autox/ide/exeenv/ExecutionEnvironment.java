package com.yukthitech.autox.ide.exeenv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class ExecutionEnvironment
{
	private String name;
	
	private Process process;
	
	private StringBuilder consoleHtml = new StringBuilder();
	
	private IContextListener proxyListener;
	
	private boolean terminated = false;
	
	ExecutionEnvironment(String name, Process process, IContextListener proxyListener)
	{
		this.name = name;
		this.process = process;
		this.proxyListener = proxyListener;
		
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
				line = line.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
				
				if(error)
				{
					lineHtml = "<div style=\"color:red;\">" + line + "</div>";
				}
				else
				{
					lineHtml = "<div>" + line + "</div>";
				}
				
				proxyListener.environmentConsoleChanged(this, lineHtml);
			}
			
			reader.close();
			
			process.waitFor();
			
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
	
	@Override
	public String toString()
	{
		return name;
	}
}
