/*
 * The MIT License (MIT)
 * Copyright (c) 2016 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.persistence.rdbms;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.PersistenceException;

import com.yukthi.persistence.EntityDetailsFactory;
import com.yukthi.persistence.ForeignConstraintDetails;
import com.yukthi.persistence.ForeignConstraintViolationException;
import com.yukthi.persistence.UniqueConstraintDetails;
import com.yukthi.persistence.UniqueConstraintViolationException;

/**
 * Handler to convert vendor specific constraint exceptions into generic exceptions
 * @author akiran
 */
public class SqlExceptionHandler
{
	/**
	 * Pattern to find word in exception message
	 */
	public static Pattern WORD_PATTERN = Pattern.compile("(\\w+)");
	
	/**
	 * Parses sql exception message for constraint name. If found, error specific exceptions will be thrown 
	 * with constraint messages which are more user friendly
	 * @param ex
	 * @param entityDetailsFactory
	 */
	public static void handleException(String defaultMessage, Exception ex, EntityDetailsFactory entityDetailsFactory, boolean duringDelete)
	{
		if(!(ex instanceof SQLException))
		{
			throw new PersistenceException(defaultMessage, ex);
		}
		
		String message = ex.getMessage();
		
		//if message is null
		if(message == null || message.trim().length() == 0)
		{
			throw new PersistenceException(defaultMessage, ex);
		}
		
		//fetch the constraint name from the message
		Matcher matcher = WORD_PATTERN.matcher(message);
		String word = null;
		Object constraint = null;
		
		while(matcher.find())
		{
			word = matcher.group(1);
			constraint = entityDetailsFactory.getConstraint(word);
			
			if(constraint != null)
			{
				break;
			}
		}
		
		//if no constraint could be found in the message
		if(constraint == null)
		{
			throw new PersistenceException(defaultMessage, ex);
		}
		
		//if found constraint is unique constraint exception
		if(constraint instanceof UniqueConstraintDetails)
		{
			UniqueConstraintDetails uniqueConstraintDetails = (UniqueConstraintDetails)constraint;
			
			message = uniqueConstraintDetails.getMessage();
			
			if(message == null)
			{
				message = "Unique constraint violated - " + word;
			}
			
			throw new UniqueConstraintViolationException(uniqueConstraintDetails.getEntityDetails().getEntityType(), 
					uniqueConstraintDetails.getFields().toArray(new String[0]), 
					uniqueConstraintDetails.getConstraintName(), message, ex);
		}
		//if exception is foreign constraint exception
		else
		{
			ForeignConstraintDetails foreignConstraintDetails = (ForeignConstraintDetails)constraint;
			
			message = duringDelete ? foreignConstraintDetails.getDeleteMessage() : foreignConstraintDetails.getMessage();
			
			if(message == null)
			{
				message = "Foreign constraint violated - " + word;
			}
			
			throw new ForeignConstraintViolationException(foreignConstraintDetails.getOwnerEntityDetails().getEntityType(), 
					foreignConstraintDetails.getConstraintName(), message, ex);
		}
	}
}
