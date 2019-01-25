package com.yukthitech.autox.ide.exeenv;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.monitor.ContextAttributeEventHandler;
import com.yukthitech.autox.ide.monitor.InteractiveServerReadyHandler;
import com.yukthitech.autox.ide.monitor.ReportMessageDataHandler;
import com.yukthitech.autox.monitor.MonitorClient;
import com.yukthitech.autox.monitor.MonitorLogMessage;
import com.yukthitech.autox.monitor.ienv.ContextAttributeDetails;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * @author admin
 */
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

	private boolean interactive;

	private boolean readyToInteract = false;

	private List<ContextAttributeDetails> contextAttributes = new LinkedList<>();
	
	private File reportFile;

	ExecutionEnvironment(String name, Process process, IContextListener proxyListener, int monitoringPort, File reportFolder, String initialMessage)
	{
		this.name = name;
		this.process = process;
		this.proxyListener = proxyListener;
		this.reportFolder = reportFolder;
		
		logOnConsole(initialMessage, false);

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

		IdeUtils.execute(() -> {
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
		monitorClient.addAsyncClientDataHandler(new ContextAttributeEventHandler(this));
		monitorClient.addAsyncClientDataHandler(new ReportMessageDataHandler(this));
		monitorClient.addAsyncClientDataHandler(new InteractiveServerReadyHandler(this));
	}

	private synchronized void appenConsoleHtml(String html)
	{
		consoleHtml.append(html);
		proxyListener.environmentChanged(EnvironmentEvent.newConsoleChangedEvent(this, html));
	}

	private void logOnConsole(String lineText, boolean error)
	{
		if(error)
		{
			logger.error(">>[{}] {}", name, lineText);
		}
		else
		{
			logger.debug(">>[{}] {}", name, lineText);
		}

		lineText = lineText.replace("&", "&amp;");
		lineText = lineText.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		lineText = lineText.replace("<", "&lt;");
		lineText = lineText.replace(">", "&gt;");
		lineText = lineText.replace("\n", "<br/>");

		String lineHtml = null;
		
		if(error)
		{
			lineHtml = "<div style=\"color:red;\">" + lineText + "</div>";
		}
		else
		{
			lineHtml = "<div>" + lineText + "</div>";
		}

		appenConsoleHtml(lineHtml);
	}
	
	private void readConsoleStream(InputStream input, boolean error)
	{
		InputStreamReader inReader = new InputStreamReader(input);
		final BufferedReader reader = new BufferedReader(inReader);

		String line = null;

		try
		{
			while((line = reader.readLine()) != null)
			{
				logOnConsole(line, error);
			}

			reader.close();

			int code = process.waitFor();
			
			//client can be null, if process is exited without proper starting itself
			if(monitorClient != null)
			{
				monitorClient.close();
			}

			appenConsoleHtml("<div>Process exited with code: " + code + "</div>");

			synchronized(this)
			{
				if(!terminated)
				{
					terminated = true;
					
					File repFile = new File(reportFolder, "index.html");
					
					if(repFile.exists())
					{
						this.reportFile = repFile;
					}
					
					proxyListener.environmentTerminated(this);
				}
			}
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while reading console stream", ex);
		}
	}

	public void addReportMessage(MonitorLogMessage mssg)
	{
		this.reportMessages.add(mssg);
		proxyListener.environmentChanged(EnvironmentEvent.newReportLogEvent(this, mssg));
	}
	
	public void addContextAttribute(ContextAttributeDetails ctx)
	{
		this.contextAttributes.add(ctx);
		proxyListener.environmentChanged(EnvironmentEvent.newContextAttributeEvent(this, ctx));
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

	public List<ContextAttributeDetails> getContextAttributes()
	{
		return contextAttributes;
	}

	public File getReportFolder()
	{
		return reportFolder;
	}

	void setInteractive(boolean interactive)
	{
		this.interactive = interactive;
	}

	public boolean isInteractive()
	{
		return interactive;
	}

	public void setReadyToInteract(boolean readyToInteract)
	{
		this.readyToInteract = readyToInteract;
	}

	public boolean isReadyToInteract()
	{
		return readyToInteract;
	}

	public void sendDataToServer(Serializable data)
	{
		if(!readyToInteract)
		{
			throw new InvalidStateException("This environment is not an interactive environment or not ready to interact. [Is Interactive: {}]", interactive);
		}

		monitorClient.sendDataToServer(data);
	}

	public synchronized void clearConsole()
	{
		consoleHtml.setLength(0);
	}
	
	public boolean isReportFileAvailable()
	{
		return (reportFile != null && reportFile.exists());
	}
	
	public File getReportFile()
	{
		return reportFile;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}