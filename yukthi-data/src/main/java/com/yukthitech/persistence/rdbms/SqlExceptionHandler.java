/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.persistence.rdbms;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.PersistenceException;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.persistence.EntityDetailsFactory;
import com.yukthitech.persistence.ForeignConstraintDetails;
import com.yukthitech.persistence.ForeignConstraintViolationException;
import com.yukthitech.persistence.UniqueConstraintDetails;
import com.yukthitech.persistence.UniqueConstraintViolationException;

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
	public static void handleException(String defaultMessage, Exception ex, EntityDetailsFactory entityDetailsFactory, boolean duringDelete, List<Pattern> constraintErrorPatterns)
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
		
		Object constraint = null;
		String constraintName = null;
		
		if(CollectionUtils.isEmpty(constraintErrorPatterns))
		{
			//fetch the constraint name from the message
			Matcher matcher = WORD_PATTERN.matcher(message);
			String word = null;
			
			while(matcher.find())
			{
				word = matcher.group(1);
				constraint = entityDetailsFactory.getConstraint(word);
				
				if(constraint != null)
				{
					constraintName = word;
					break;
				}
			}
		}
		else
		{
			for(Pattern constraintErrorPattern : constraintErrorPatterns)
			{
				Matcher matcher = constraintErrorPattern.matcher(message);
				
				if(matcher.find())
				{
					constraintName = matcher.group(RdbmsConfiguration.PATTERN_GRP_CONST_ERR_NAME);
					constraint = entityDetailsFactory.getConstraint(constraintName);
					
					if(constraint != null)
					{
						break;
					}
				}
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
				String fields = uniqueConstraintDetails.getFields().toString();
				
				message = "Duplicate value specified for field(s) - " + fields;
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
				message = "Foreign constraint violated - " + constraintName;
			}
			
			throw new ForeignConstraintViolationException(foreignConstraintDetails.getOwnerEntityDetails().getEntityType(), 
					foreignConstraintDetails.getConstraintName(), message, ex);
		}
	}
}
