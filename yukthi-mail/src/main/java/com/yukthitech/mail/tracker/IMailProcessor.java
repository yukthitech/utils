package com.yukthitech.mail.tracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.mail.Folder;
import javax.mail.Message;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Abstraction of mail processors.
 * @author akiran
 */
public interface IMailProcessor
{
	/**
	 * Called to process mails. 
	 * @param context Context for mail processing.
	 * @param mailMessage Mail to be processed.
	 * @return true if processed
	 */
	public boolean process(IMailTrackerContext context, ReceivedMailMessage mailMessage);
	
	public default String getUniqueMessageId(Folder folder, Message message)
	{
		try
		{
			/*
			if(folder instanceof UIDFolder)
			{
				UIDFolder uidFolder = (UIDFolder) folder;
				return "" + uidFolder.getUID(message);
			}
			*/
			
			Date recvDate = message.getReceivedDate();
			recvDate = (recvDate == null) ? message.getSentDate() : recvDate;
			
			return message.getFrom()[0].toString() + "-" + recvDate.getTime() + "-" + message.getSubject();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while building unique message id", ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	public default Set<String> getProcessedMailIds()
	{
		File file = new File(".mails.cache");
		
		if(!file.exists())
		{
			return Collections.emptySet();
		}
		
		try
		{
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			Set<String> ids = (Set<String>) ois.readObject();
			ois.close();
			fis.close();
			
			return ids;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while reading mail cache", ex);
		}
	}

	public default void setProcessedMailIds(Set<String> ids)
	{
		File file = new File(".mails.cache");
		
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeObject(ids);
			oos.flush();
			
			oos.close();
			fos.close();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while saving to mail cache", ex);
		}
	}
}
