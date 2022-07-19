package com.yukthitech.autox.ide.exeenv;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.layout.ConsoleLinePattern;
import com.yukthitech.autox.ide.layout.UiLayout;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.monitor.ContextAttributeEventHandler;
import com.yukthitech.autox.ide.monitor.InteractiveServerReadyHandler;
import com.yukthitech.autox.ide.monitor.ReportMessageDataHandler;
import com.yukthitech.autox.monitor.IMessageCallback;
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
	
	private ExecutionType executionType;

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

	private LinkedHashMap<String, ContextAttributeDetails> contextAttributes = new LinkedHashMap<>();
	
	private File reportFile;
	
	private Project project;
	
	private UiLayout uiLayout;
	
	private String extraArgs[];

	ExecutionEnvironment(ExecutionType executionType, Project project, String name, Process process, IContextListener proxyListener, int monitoringPort, File reportFolder, 
			String initialMessage, UiLayout uiLayout, String extraArgs[])
	{
		this.executionType = executionType;
		this.project = project;
		this.name = name;
		this.process = process;
		this.proxyListener = proxyListener;
		this.reportFolder = reportFolder;
		this.uiLayout = uiLayout;
		this.extraArgs = extraArgs;
		
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
	
	public String[] getExtraArgs()
	{
		return extraArgs;
	}
	
	public ExecutionType getExecutionType()
	{
		return executionType;
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
			lineHtml = String.format("<div style=\"color:red;\">%s</div>", lineText);
		}
		else
		{
			String color = null;
			
			for(ConsoleLinePattern linePattern : uiLayout.getConsoleLinePatterns())
			{
				if(linePattern.getPattern().matcher(lineText).find())
				{
					color = linePattern.getColor();
					break;
				}
			}
			
			if(color == null)
			{
				lineHtml = "<div>" + lineText + "</div>";
			}
			else
			{
				lineHtml = String.format("<div style=\"color:%s;\">%s</div>", color, lineText);
			}
		}

		appenConsoleHtml(lineHtml);
	}
	
	private void readConsoleStream(InputStream input, boolean error)
	{
		//input can be null, when error is redirected to output stream
		if(input == null)
		{
			return;
		}
		
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
		this.contextAttributes.put(ctx.getName(), ctx);
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

	public Collection<ContextAttributeDetails> getContextAttributes()
	{
		return contextAttributes.values();
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
		this.sendDataToServer(data, null);
	}

	public void sendDataToServer(Serializable data, IMessageCallback callback)
	{
		if(!readyToInteract)
		{
			throw new InvalidStateException("This environment is not an interactive environment or not ready to interact. [Is Interactive: {}]", interactive);
		}

		monitorClient.sendDataToServer(data, callback);
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
	
	public Project getProject()
	{
		return project;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
