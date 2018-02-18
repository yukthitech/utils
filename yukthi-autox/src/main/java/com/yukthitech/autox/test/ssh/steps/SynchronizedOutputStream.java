package com.yukthitech.autox.test.ssh.steps;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Thread safe output stream.
 * @author akiran
 */
public class SynchronizedOutputStream extends OutputStream
{
	/**
	 * Buffered stream.
	 */
	private ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
	@Override
	public synchronized void write(int b) throws IOException
	{
		bos.write(b);
	}

	@Override
	public synchronized void write(byte[] b) throws IOException
	{
		bos.write(b);
	}

	@Override
	public synchronized void write(byte[] b, int off, int len) throws IOException
	{
		bos.write(b, off, len);
	}

	@Override
	public synchronized void flush() throws IOException
	{
		bos.flush();
	}

	/**
	 * Converts to byte array.
	 * @return converted byte array.
	 */
	public byte[] toByteArray()
	{
		return bos.toByteArray();
	}
}
