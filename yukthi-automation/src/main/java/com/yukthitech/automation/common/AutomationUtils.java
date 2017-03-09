package com.yukthitech.automation.common;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Collection;
import java.util.Comparator;
import java.util.Stack;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Param;
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
		String fieldValue = null, value= null;
		String propertyExpr = null;
		
		Matcher matcher = null;
		StringBuffer buffer = new StringBuffer();
		
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
				
				fieldValue = (String) field.get(executable);
				
				//ignore null field values
				if(fieldValue == null)
				{
					continue;
				}
				
				matcher = CONTEXT_EXPR_PATTERN.matcher(fieldValue);
				buffer.setLength(0);
	
				//replace the expressions in the field value
				while(matcher.find())
				{
					propertyExpr = matcher.group(1);
					
					try
					{
						value = BeanUtils.getProperty(context, propertyExpr);
					}catch(Exception ex)
					{
						throw new InvalidStateException("An error occurred while parsing context expression '{}' in field {}.{}", 
							matcher.group(1), executable.getClass().getName(), field.getName());
					}
					
					matcher.appendReplacement(buffer, value);
				}
				
				matcher.appendTail(buffer);
				
				//set the result string back to field
				field.set(executable, buffer.toString());
			} catch(InvalidStateException ex)
			{
				throw ex;
			} catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while parsing expressions in field: {}.{}", 
					executable.getClass().getName(), field.getName());
			}
		}
	}

	/**
	 * Validates required parameters, configured by {@link Param}, are specified for target bean.
	 * @param bean bean to validate
	 */
	public static void validateRequiredParams(Object bean)
	{
		Field fields[] = bean.getClass().getDeclaredFields();
		Param param = null;
		Object value = null;
		
		try
		{
			for(Field field : fields)
			{
				param = field.getAnnotation(Param.class);
				
				//if field is not require, ignore
				if(param == null || !param.required())
				{
					continue;
				}
	
				field.setAccessible(true);
				value = field.get(bean);
				
				//validate value is provided and not blank
				if(value == null)
				{
					throw new InvalidStateException("Required param {} is not specified", field.getName());
				}
				
				if( (value instanceof String) && StringUtils.isBlank(value.toString()) )
				{
					throw new InvalidStateException("Blank value is specified for required param {}", field.getName());
				}
				
				if( (value instanceof Collection) && CollectionUtils.isEmpty((Collection<?>)value) )
				{
					throw new InvalidStateException("No value(s) specified for required param {}", field.getName());
				}
			}
		} catch(InvalidStateException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while validating bean - {}", bean, ex);
		}
	}
}
