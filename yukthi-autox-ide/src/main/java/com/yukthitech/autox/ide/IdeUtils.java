package com.yukthitech.autox.ide;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;

import org.apache.commons.lang3.SerializationUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Common util methods used across the ide project.
 * @author akiran
 */
public class IdeUtils
{
	/**
	 * Used to serialize and deserialize the objects.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private static final int BORDER_SIZE = 8;
	private static final int HALF_BORDER_SIZE = BORDER_SIZE / 2;
	
	/**
	 * Size of file icon.
	 */
	private static final int FILE_ICON_SIZE = 20;
	
	/**
	 * Extension to icon cache map.
	 */
	private static Map<String, ImageIcon> extensionToIcon = new HashMap<>();
	
	/**
	 * Thread pools for scheduled jobs.
	 */
	private static ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(10);
	
	/**
	 * Saves the specified object into specified file.
	 * @param object object to persist
	 * @param file file to persist
	 */
	public static void save(Object object, File file)
	{
		try
		{
			objectMapper.writeValue(file, object);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while saving object [File: {}, Object: {}]", file.getPath(), object, ex);
		}
	}
	
	/**
	 * Loads the object of specified type from specified file.
	 * @param file file to load
	 * @param type type of object to be loaded
	 * @return loaded object
	 */
	public static <T> T load(File file, Class<T> type)
	{
		if(!file.exists())
		{
			return null;
		}
		
		try
		{
			return objectMapper.readValue(file, type);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading object [File: {}, Type: {}]", file.getPath(), type.getName(), ex);
		}
	}
	
	public static void serialize(Serializable object, File file)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
			SerializationUtils.serialize(object, fos);
			fos.close();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while serializing data to file: {}", file.getPath(), ex);
		}
	}
	
	/**
	 * Deserializes the data from specified file.
	 * @param file File to deserialize
	 * @return
	 */
	public static Object deserialize(File file)
	{
		if(!file.exists())
		{
			return null;
		}
		
		try
		{
			FileInputStream fis = new FileInputStream(file);
			Object readObj = SerializationUtils.deserialize(fis);
			fis.close();
			
			return readObj;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading object [File: {}]", file.getPath(), ex);
		}
	}
	
	/**
	 * Loads specified resource as an icon with specified size.
	 * @param resource
	 * @param size
	 * @return
	 */
	public static ImageIcon loadIcon(String resource, int size)
	{
		ImageIcon icon = new ImageIcon(IdeUtils.class.getResource(resource));
		BufferedImage img = new BufferedImage(size + BORDER_SIZE, size + BORDER_SIZE, BufferedImage.TYPE_INT_ARGB);
		img.getGraphics().drawImage(icon.getImage(), HALF_BORDER_SIZE, HALF_BORDER_SIZE, size, size, null);
		
		return new ImageIcon(img);
	}
	
	private static ImageIcon getEmptyFileIcon()
	{
		ImageIcon imageIcon = extensionToIcon.get("");
		
		if(imageIcon != null)
		{
			return imageIcon;
		}
			
		int fullSize = FILE_ICON_SIZE + BORDER_SIZE;

		BufferedImage img = new BufferedImage(FILE_ICON_SIZE + BORDER_SIZE, FILE_ICON_SIZE + BORDER_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		
		g.setColor(Color.white);
		g.fillRect(0, 0, fullSize, fullSize);
		
		g.setColor(Color.black);
		g.drawRect(HALF_BORDER_SIZE, HALF_BORDER_SIZE, FILE_ICON_SIZE - HALF_BORDER_SIZE - 1, FILE_ICON_SIZE - HALF_BORDER_SIZE - 1);
		
		imageIcon = new ImageIcon(img);
		
		extensionToIcon.put("", imageIcon);
		return imageIcon;
	}
	
	public static ImageIcon getFileIcon(File file)
	{
		ImageIcon emptyFileIcon = getEmptyFileIcon();
		
		String extension = file.getName().toUpperCase();
		int dotIdx = extension.lastIndexOf(".");
		
		if(dotIdx <= 0)
		{
			return emptyFileIcon;
		}
		
		extension = extension.substring(dotIdx + 1, dotIdx + 2);
		
		ImageIcon fileIcon = extensionToIcon.get(extension);
		
		if(fileIcon != null)
		{
			return fileIcon;
		}
		
		BufferedImage img = new BufferedImage(FILE_ICON_SIZE, FILE_ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		
		g.drawImage(emptyFileIcon.getImage(), 0, 0, null);
		g.setColor(Color.blue);
		/*
		g.fillRect(0, 12, FILE_ICON_SIZE, 18);
		
		g.setColor(Color.white);
		*/
		g.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
		g.drawString(extension, FILE_ICON_SIZE / 2 - 2, 17);
		
		fileIcon = new ImageIcon(img);
		extensionToIcon.put(extension, fileIcon);
		
		return fileIcon;
	}
	
	public static Window getCurrentWindow()
	{
		return KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
	}
	
	/**
	 * Executes specified runnable task after specified delay.
	 * @param runnable
	 * @param delay
	 */
	public static void execute(Runnable runnable, long delay)
	{
		threadPool.schedule(runnable, delay, TimeUnit.MILLISECONDS);
	}
}
