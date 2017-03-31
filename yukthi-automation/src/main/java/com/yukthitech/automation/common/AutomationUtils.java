package com.yukthitech.automation.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	private static Logger logger = LogManager.getLogger(AutomationUtils.class);
	
	/**
	 * Freemarker template config used to parse expressions.
	 */
	private static Configuration configuration = new Configuration();
	
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
	 * @param templateStr Template in which expressions should be replaced
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
	 * Gets all fields of specified type and also its ancestors.
	 * @param type Type from which fields needs to be extracted
	 * @return Fields from specified type and its ancestors
	 */
	private static List<Field> getAllInstanceFields(Class<?> type)
	{
		List<Field> fields = new ArrayList<>();
		Class<?> curClass = type;
		
		while(true)
		{
			fields.addAll(Arrays.asList(curClass.getDeclaredFields()));
			curClass = curClass.getSuperclass();
			
			if(curClass.getName().startsWith("java"))
			{
				break;
			}
		}
		
		return fields;
	}

	/**
	 * Replaces expressions in specified step properties.
	 * @param context Context to fetch values for expressions.
	 * @param object Object in which expressions has to be replaced
	 */
	@SuppressWarnings("unchecked")
	public static <T> T replaceExpressions(AutomationContext context, T object)
	{
		if(object == null)
		{
			return null;
		}
		
		Class<?> executableType = object.getClass();
		
		if(executableType.isPrimitive() || executableType.isArray())
		{
			return object;
		}
		
		if(object instanceof String)
		{
			return (T) replaceExpressions(context, (String) object);
		}

		logger.debug("Processing expressions in object: {}", object);
		
		//when executable is collection
		if(object instanceof Collection)
		{
			Collection<Object> collection = (Collection<Object>) object;
			
			if(collection.isEmpty())
			{
				return (T) collection;
			}
			
			Collection<Object> resCollection = null;
			
			try
			{
				resCollection = (Collection<Object>) executableType.newInstance();
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while parsing expressions", ex);
			}
			
			for(Object element : collection)
			{
				resCollection.add( replaceExpressions(context, element) );
			}
			
			return (T) resCollection;
		}
			
		//when executable is map
		if(object instanceof Map)
		{
			Map<Object, Object> map = (Map<Object, Object>) object;
			
			if(map.isEmpty())
			{
				return (T) map;
			}
			
			Map<Object, Object> resMap = null;
			
			try
			{
				resMap = (Map<Object, Object>) executableType.newInstance();
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while parsing expressions", ex);
			}
			
			for(Map.Entry<Object, Object> entry : map.entrySet())
			{
				resMap.put( entry.getKey(), replaceExpressions(context, entry.getValue()) ) ;
			}
			
			return (T) resMap;
		}
		
		if(executableType.getName().startsWith("java"))
		{
			return object;
		}

		List<Field> fields = getAllInstanceFields(object.getClass());
		
		Object fieldValue = null;
		Class<?> fieldType = null;
		
		for(Field field : fields)
		{
			if(Modifier.isStatic(field.getModifiers()))
			{
				continue;
			}
			
			try
			{
				fieldType = field.getType();
				
				if(fieldType.isPrimitive() || fieldType.isArray())
				{
					continue;
				}

				field.setAccessible(true);
				
				fieldValue = field.get(object);
				
				//ignore null field values
				if(fieldValue == null)
				{
					continue;
				}
				
				fieldValue = replaceExpressions(context, fieldValue);
				field.set(object, fieldValue);
				
			} catch(InvalidStateException ex)
			{
				throw ex;
			} catch(Exception ex)
			{
				throw new InvalidStateException(ex, "An error occurred while parsing expressions in field: {}.{}", 
					object.getClass().getName(), field.getName());
			}
		}
		
		return object;
	}

	/**
	 * Validates required parameters, configured by {@link Param}, are specified for target bean.
	 * @param bean bean to validate
	 */
	public static void validateRequiredParams(Object bean)
	{
		List<Field> fields = getAllInstanceFields(bean.getClass());
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
			//covert object into bytes
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			
			oos.writeObject(object);
			oos.flush();
			bos.flush();
			
			//read bytes back to object which would be deep copy
			ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bis);
			
			return (T) ois.readObject();
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while deep cloning object: {}", object, ex);
		}
	}
}
