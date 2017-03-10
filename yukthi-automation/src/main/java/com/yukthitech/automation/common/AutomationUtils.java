package com.yukthitech.automation.common;

import java.io.File;
import java.io.FileFilter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.automation.AutomationContext;
import com.yukthitech.automation.Param;
import com.yukthitech.utils.exceptions.InvalidStateException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Common util functions.
 * @author akiran
 */
public class AutomationUtils
{
	/**
	 * Freemarker template config used to parse expressions.
	 */
	private static Configuration configuration = new Configuration();
	
	/**
	 * Object mapper for json coversions.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	static
	{
		try
		{
			configuration.setSetting("number_format", "#");
		} catch(TemplateException ex)
		{
			throw new InvalidStateException("An error occurred while init freemarker context", ex);
		}
	}

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
	 * Treats provided template as freemarker template and processes them. The result will be returned.
	 * @param context Automation context which would be used as freemarker context for processing.
	 * @param template Template in which expressions should be replaced
	 * @return Processed string
	 */
	public static String replaceExpressions(AutomationContext context, String templateStr)
	{
		try
		{
			Template template = new Template("template", templateStr, configuration);
			
			StringWriter writer = new StringWriter();
			template.process(context, writer);
			writer.flush();
			
			return writer.toString();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while processing template:\n" + templateStr, ex);
		}
	}

	/**
	 * Replaces expressions in specified step properties.
	 * @param context Context to fetch values for expressions.
	 * @param executable Step/validator or other executable in which expressions has to be replaced
	 */
	@SuppressWarnings("unchecked")
	public static void replaceExpressions(AutomationContext context, Object executable)
	{
		if(executable == null)
		{
			return;
		}
		
		//when executable is collection
		if(executable instanceof Collection)
		{
			Collection<Object> collection = (Collection<Object>) executable;
			
			if(collection.isEmpty())
			{
				return;
			}
			
			for(Object element : collection)
			{
				replaceExpressions(context, element);
			}
			
			return;
		}
			
		//when executable is map
		if(executable instanceof Map)
		{
			Map<Object, Object> map = (Map<Object, Object>) executable;
			
			if(map.isEmpty())
			{
				return;
			}
			
			for(Object element : map.values())
			{
				replaceExpressions(context, element);
			}
			
			return;
		}

		Field fields[] = executable.getClass().getDeclaredFields();
		Object fieldValue = null;
		Class<?> fieldType = null;
		
		for(Field field : fields)
		{
			try
			{
				fieldType = field.getType();
				
				if(!Collection.class.isAssignableFrom(fieldType) &&
						!Map.class.isAssignableFrom(fieldType) &&
						fieldType.getName().startsWith("java"))
				{
					continue;
				}
				
				if(fieldType.isPrimitive())
				{
					continue;
				}

				field.setAccessible(true);
				
				fieldValue = field.get(executable);
				
				//ignore null field values
				if(fieldValue == null)
				{
					continue;
				}
				
				if(fieldValue instanceof String)
				{
					fieldValue = replaceExpressions(context, (String) fieldValue);
					
					//set the result string back to field
					field.set(executable, fieldValue);
					continue;
				}
				
				replaceExpressions(context, fieldValue);
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
	
	/**
	 * Deep clones the object by converting object to json and back to object.
	 * @param object object to be cloned.
	 * @return cloned object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deepClone(T object)
	{
		try
		{
			String jsonStr = objectMapper.writeValueAsString(object);
			return (T) objectMapper.readValue(jsonStr, object.getClass());
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while deep cloning object: {}", object, ex);
		}
	}
}
