package com.yukthitech.automation.common;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Comparator;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Common util functions.
 * @author akiran
 */
public class AutomationUtils
{
	/**
	 * Pattern used to replace expressions in step properties.
	 */
	private static Pattern CONTEXT_EXPR_PATTERN = Pattern.compile("\\{\\{(.+)\\}\\}");

	/**
	 * Loads the xml files from specified folder. Returned set will be ordered by their relative paths.
	 * @param folder folder to be loaded.
	 * @return loaded xml files ordered by relative path.
	 */
	public static TreeSet<File> loadXmlFiles(File folder)
	{
		final URI rootPath = folder.toURI();
		
		TreeSet<File> xmlFiles = new TreeSet<>(new Comparator<File>()
		{
			@Override
			public int compare(File o1, File o2)
			{
				String path1 = rootPath.relativize(o1.toURI()).getPath();
				String path2 = rootPath.relativize(o2.toURI()).getPath();
				
				return path1.compareTo(path2);
			}
		});
		
		Stack<File> folders = new Stack<>();
		folders.push(folder);

		// filter to filter xml files and add sub folder to stack
		FileFilter fileFiler = new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				if(pathname.isDirectory())
				{
					folders.push(pathname);
				}
				else if(pathname.getName().toLowerCase().endsWith(".xml"))
				{
					xmlFiles.add(pathname);
					return false;
				}

				return false;
			}
		};

		// loop till scanning is completed on test folder and its sub folders
		while(!folders.isEmpty())
		{
			folders.pop().listFiles(fileFiler);
		}

		return xmlFiles;
	}

	/**
	 * Replaces expressions in specified step properties.
	 * @param context Context to fetch values for expressions.
	 * @param executable Step/validator or other executable in which expressions has to be replaced
	 */
	public static void replaceExpressions(AutomationContext context, Object executable)
	{
		Field fields[] = executable.getClass().getDeclaredFields();
		String value = null;
		String propertyExpr = null;
		
		Matcher matcher = null;
		StringBuffer buffer = new StringBuffer();
		
		Map<String, Object> contextAttr = context.getAttributeMap();
		
		for(Field field : fields)
		{
			//ignore non string fields
			if(!String.class.equals(field.getType()))
			{
				continue;
			}

			try
			{
				field.setAccessible(true);
				
				value = (String) field.get(executable);
				
				//ignore null field values
				if(value == null)
				{
					continue;
				}
				
				matcher = CONTEXT_EXPR_PATTERN.matcher(value);
				buffer.setLength(0);
	
				//replace the expressions in the field value
				while(matcher.find())
				{
					propertyExpr = matcher.group(1);
					
					matcher.appendReplacement(buffer, BeanUtils.getProperty(contextAttr, propertyExpr));
				}
				
				matcher.appendTail(buffer);
				
				//set the result string back to field
				field.set(executable, buffer.toString());
			} catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while parsing expressions in field '{}' in class - {}", 
					field.getName(), executable.getClass().getName());
			}
		}
	}
}
