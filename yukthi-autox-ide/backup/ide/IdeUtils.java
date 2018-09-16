package com.yukthitech.autox.ide;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.yukthitech.utils.exceptions.InvalidStateException;

public class IdeUtils
{
	private static ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(5);
	
	private static final DataFlavor RTF_FLAVOR;
	
	static
	{
		try
		{
			RTF_FLAVOR = new DataFlavor("text/rtf");
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while initializing rtf flavor", ex);
		}
	}
	
	public static void invokeAfter(int millis, Runnable runnable)
	{
		threadPool.schedule(runnable, millis, TimeUnit.MILLISECONDS);
	}
	
	public static String getRtfText(RSyntaxTextArea fld)
	{
		try
		{
			fld.selectAll();
			fld.copyAsRtf();
			
			ByteArrayInputStream bis = (ByteArrayInputStream) Toolkit.getDefaultToolkit().getSystemClipboard().getData(RTF_FLAVOR);
			String rtfText = IOUtils.toString(bis);
	
			return rtfText;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while extracting rtf text");
		}
	}
}
