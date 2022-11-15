package com.yukthitech.test.beans;

import java.util.List;

import com.yukthitech.autox.exec.report.ExecutionLogData;

public class TestLogData
{
	public static class ImgMessage extends  ExecutionLogData.Message
	{
		private static final long serialVersionUID = 1L;
		
		private String imageFileName;

		public String getImageFileName()
		{
			return imageFileName;
		}

		public void setImageFileName(String imageFileName)
		{
			this.imageFileName = imageFileName;
		}
	}
	
	private ExecutionLogData.Header header;
	
	private List<ImgMessage> messages;
	
	private ExecutionLogData.Footer footer;

	public ExecutionLogData.Header getHeader()
	{
		return header;
	}

	public void setHeader(ExecutionLogData.Header header)
	{
		this.header = header;
	}

	public List<ImgMessage> getMessages()
	{
		return messages;
	}

	public void setMessages(List<ImgMessage> messages)
	{
		this.messages = messages;
	}

	public ExecutionLogData.Footer getFooter()
	{
		return footer;
	}

	public void setFooter(ExecutionLogData.Footer footer)
	{
		this.footer = footer;
	}
}
