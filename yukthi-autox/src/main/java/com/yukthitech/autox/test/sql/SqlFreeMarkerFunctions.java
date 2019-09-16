package com.yukthitech.autox.test.sql;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;

import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.fmarker.annotaion.FreeMarkerMethod;

public class SqlFreeMarkerFunctions
{
	@FreeMarkerMethod(
			description = "Used to convert specified data into a blob object. Supported parameter types - CharSequence, byte[], Serializable, InputStream.",
			returnDescription = "Converted blob object."
			)
	public static Blob toBlob(Object val) throws SerialException, SQLException, IOException
	{
		if(val instanceof byte[])
		{
			return new SerialBlob((byte[]) val);
		}
		
		if(val instanceof InputStream)
		{
			InputStream is = (InputStream) val;
			return new SerialBlob(IOUtils.toByteArray(is));
		}
		
		if(val instanceof CharSequence)
		{
			byte b[] = val.toString().getBytes();
			return new SerialBlob(b);
		}
		
		if(val instanceof Serializable)
		{
			byte b[] = SerializationUtils.serialize((Serializable) val);
			return new SerialBlob(b);
		}
		
		throw new InvalidArgumentException("Invalid object (%s) specified for conversion to blob", val.getClass().getName());
	}

	@FreeMarkerMethod(
			description = "Used to load specified resource as blob object.",
			returnDescription = "Loaded blob object."
			)
	public static Blob resBlob(String res) throws SerialException, SQLException, IOException
	{
		InputStream is = SqlFreeMarkerFunctions.class.getResourceAsStream(res);
		return new SerialBlob(IOUtils.toByteArray(is));		
	}

	@FreeMarkerMethod(
			description = "Used to convert specified data into a clob. Supported parameter types - CharSequence, byte[], InputStream, Reader.",
			returnDescription = "Converted input stream."
			)
	public static Clob toClob(Object val) throws SerialException, SQLException, IOException
	{
		if(val instanceof byte[])
		{
			String str = new String((byte[]) val);
			return new SerialClob(str.toCharArray());
		}
		
		if(val instanceof InputStream)
		{
			InputStream is = (InputStream) val;
			return new SerialClob(IOUtils.toCharArray(is));
		}

		if(val instanceof Reader)
		{
			Reader is = (Reader) val;
			return new SerialClob(IOUtils.toCharArray(is));
		}

		if(val instanceof CharSequence)
		{
			char ch[] = ((CharSequence) val).toString().toCharArray();
			return new SerialClob(ch);
		}
		
		throw new InvalidArgumentException("Invalid object (%s) specified for conversion to clob", val.getClass().getName());
	}

	@FreeMarkerMethod(
			description = "Used to load specified resource as clob object.",
			returnDescription = "Loaded blob object."
			)
	public static Clob resClob(String res) throws SerialException, SQLException, IOException
	{
		InputStream is = SqlFreeMarkerFunctions.class.getResourceAsStream(res);
		return new SerialClob(IOUtils.toCharArray(is));		
	}
}
