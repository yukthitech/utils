package com.yukthitech.autox.ide.exeenv;

import com.yukthitech.autox.monitor.MonitorLogMessage;

/**
 * Used to send environment status change information.
 * @author akiran
 */
public class EnvironmentEvent
{
	/**
	 * Environment in which change occurred.
	 */
	private ExecutionEnvironment environment;
	
	/**
	 * Event type.
	 */
	private EnvironmentEventType eventType;
	
	/**
	 * Available for {@link EnvironmentEventType#CONSOLE_CHANGED} event.
	 */
	private String newMessage;
	
	/**
	 * Available for {@link EnvironmentEventType#REPORT_LOG_ADDED} event. 
	 */
	private MonitorLogMessage newReportLog;

	/**
	 * Instantiates a new environment event.
	 *
	 * @param environment the environment
	 * @param eventType the event type
	 */
	private EnvironmentEvent(ExecutionEnvironment environment, EnvironmentEventType eventType)
	{
		this.environment = environment;
		this.eventType = eventType;
	}
	
	/**
	 * New console changed event.
	 *
	 * @param env the env
	 * @param newMssg the new mssg
	 * @return the environment event
	 */
	public static EnvironmentEvent newConsoleChangedEvent(ExecutionEnvironment env, String newMssg)
	{
		EnvironmentEvent event = new EnvironmentEvent(env, EnvironmentEventType.CONSOLE_CHANGED);
		event.newMessage = newMssg;
		
		return event;
	}
	
	/**
	 * New report log event.
	 *
	 * @param env the env
	 * @param mssg the mssg
	 * @return the environment event
	 */
	public static EnvironmentEvent newReportLogEvent(ExecutionEnvironment env, MonitorLogMessage mssg)
	{
		EnvironmentEvent event = new EnvironmentEvent(env, EnvironmentEventType.REPORT_LOG_ADDED);
		event.newReportLog = mssg;
		
		return event;
	}

	/**
	 * Gets the environment in which change occurred.
	 *
	 * @return the environment in which change occurred
	 */
	public ExecutionEnvironment getEnvironment()
	{
		return environment;
	}

	/**
	 * Gets the event type.
	 *
	 * @return the event type
	 */
	public EnvironmentEventType getEventType()
	{
		return eventType;
	}

	/**
	 * Gets the available for {@link EnvironmentEventType#CONSOLE_CHANGED} event.
	 *
	 * @return the available for {@link EnvironmentEventType#CONSOLE_CHANGED} event
	 */
	public String getNewMessage()
	{
		return newMessage;
	}

	/**
	 * Gets the available for {@link EnvironmentEventType#REPORT_LOG_ADDED} event.
	 *
	 * @return the available for {@link EnvironmentEventType#REPORT_LOG_ADDED} event
	 */
	public MonitorLogMessage getNewReportLog()
	{
		return newReportLog;
	}
}
