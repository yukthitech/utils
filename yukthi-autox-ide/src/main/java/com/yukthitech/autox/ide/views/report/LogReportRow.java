package com.yukthitech.autox.ide.views.report;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.yukthitech.autox.test.log.LogLevel;

public class LogReportRow implements IReportRow
{
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm:ss aa");
	
	private LogLevel logLevel;
	private String source;
	private String message;
	private Date time;

	public LogReportRow(LogLevel logLevel, String source, String message, Date time)
	{
		this.logLevel = logLevel;
		this.source = source;
		this.message = message;
		this.time = time;
	}

	public LogLevel getLogLevel()
	{
		return logLevel;
	}

	public void setLogLevel(LogLevel logLevel)
	{
		this.logLevel = logLevel;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}
	
	@Override
	public Object getValueAt(int col)
	{
		switch (col)
		{
			case 0:
			{
				String style = (logLevel == LogLevel.ERROR) ? "color:red;" : "";
				
				String str = String.format(
						"<span style='%s'>%s</span> %s [%s]", 
						style, 
						logLevel.getPaddedString(), 
						TIME_FORMAT.format(time), 
						source, 
						source);
				
				return str.replace("'", "\"");
			}
			case 1:
				String message = this.message;
				
				if(logLevel == LogLevel.ERROR)
				{
					message = "<div style='color:red;'>" + message + "</div>";
				}
				
				message = message.replace("\n", "<br/>");
				message = message.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
				message = message.replace("<code", "<div style='padding: 10px; margin: 5px 0px 5px 30px; border: 1px solid black;' ");
				message = message.replace("</code>", "</div>");
				
				return message.replace("'", "\"");
		}
		
		return "";
	}
}
