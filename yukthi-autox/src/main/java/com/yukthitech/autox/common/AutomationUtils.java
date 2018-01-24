package com.yukthitech.autox.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.SourceType;
import com.yukthitech.autox.ref.IReference;
import com.yukthitech.autox.resource.IResource;
import com.yukthitech.autox.resource.ResourceFactory;
import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.ConvertUtils;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Common util functions.
 * @author akiran
 */
public class AutomationUtils
{
	/**
	 * Pattern to parse java type.
	 */
	private static final Pattern TYPE_STR_PATTERN = Pattern.compile("([\\w\\.\\$]+)\\s*\\<\\s*([\\w\\.\\$\\,\\ ]+\\s*)\\>\\s*");
	
	/**
	 * Pattern used to extract type string from the ending of the source string. Note, pattern ensures 
	 * type is considered only when defined at end of source string.
	 */
	private static final Pattern TYPE_EXTRACT_PATTERN = Pattern.compile("\\#(.*)$");
	
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
		return FreeMarkerMethodManager.replaceExpressions(context, templateStr);
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
		
		if(object instanceof IReference)
		{
			IReference ref = (IReference) object;
			return (T) ref.getValue(context);
		}
		
		if(object instanceof String)
		{
			return (T) replaceExpressions(context, (String) object);
		}

		//logger.trace("Processing expressions in object: {}", object);
		
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
		Param param = null;
		
		for(Field field : fields)
		{
			if(Modifier.isStatic(field.getModifiers()))
			{
				continue;
			}
			
			//skip fields which are annotated with SkipParsing 
			if(field.getAnnotation(SkipParsing.class) != null)
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
				
				param = field.getAnnotation(Param.class);
				
				//skip field parsing if object type parameters, as they will processed as part of step execution
				if(param != null && param.sourceType() == SourceType.OBJECT)
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
	
	/**
	 * Evaluates specified free marker condition and returns the result.
	 * @param context Context to be used
	 * @param condition Condition to be evaluated
	 * @return true, if condition evaluated to be true
	 */
	public static boolean evaluateCondition(AutomationContext context, String condition)
	{
		if("true".equalsIgnoreCase(condition))
		{
			return true;
		}

		if("false".equalsIgnoreCase(condition))
		{
			return false;
		}

		String ifCondition = String.format("<#if %s>true<#else>false</#if>", condition);
		String res = null;
		
		try
		{
			res = AutomationUtils.replaceExpressions(context, ifCondition);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while evaluating condition: {}", condition, ex);
		}
		
		return "true".equals(res);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static JavaType parseJavaType(String typeStr)
	{
		typeStr = typeStr.trim();
		
		if(typeStr.indexOf("<") <= 0)
		{
			try
			{
				Class<?> type = Class.forName(typeStr);
				return TypeFactory.defaultInstance().uncheckedSimpleType(type);
			}catch(Exception ex)
			{
				throw new InvalidArgumentException("Invalid simple-type specified: {}", typeStr, ex);
			}
		}
		
		Matcher matcher = TYPE_STR_PATTERN.matcher(typeStr);
		
		if(!matcher.matches())
		{
			throw new InvalidArgumentException("Invalid type string specified: {}", typeStr);
		}
		
		String baseTypeStr = matcher.group(1);
		String paramLstStr = matcher.group(2);
		String paramTypeStr[] = paramLstStr.split("\\s*\\,\\s*");
		
		Class<?> baseType = null;
		Class<?> paramTypes[] = null;
		
		try
		{
			baseType = Class.forName(baseTypeStr);
		}catch(Exception ex)
		{
			throw new InvalidArgumentException("Invalid base-type '{}' specified in parameterized typ string: {}", baseTypeStr, typeStr, ex);
		}
		
		paramTypes = new Class<?>[paramTypeStr.length];
		
		for(int i = 0; i < paramTypes.length; i++)
		{
			try
			{
				paramTypes[i] = Class.forName(paramTypeStr[i]);
			}catch(Exception ex)
			{
				throw new InvalidArgumentException("Invalid param-type '{}' specified in parameterized typ string: {}", paramTypeStr[i], typeStr, ex);
			}
		}
		
		if(Collection.class.isAssignableFrom(baseType) && paramTypes.length == 1)
		{
			return TypeFactory.defaultInstance().constructCollectionType( (Class) baseType, paramTypes[0]);
		}
		
		if(Map.class.isAssignableFrom(baseType) && paramTypes.length == 2)
		{
			return TypeFactory.defaultInstance().constructMapType( (Class) baseType, paramTypes[0], paramTypes[1]);
		}
		
		return TypeFactory.defaultInstance().constructParametrizedType(baseType, baseType, paramTypes);
	}

	/**
	 * Parses specified source (if string) and returns the result. If not string is specified, the 
	 * same will be returned.
	 * @param exeLogger Logger for logging messages
	 * @param source source to be passed
	 * @param defaultType Default type expected as result. Can be null.
	 * @return parsed value
	 */
	public static Object parseObjectSource(AutomationContext context, ExecutionLogger exeLogger, Object source, JavaType defaultType)
	{
		if(source instanceof IReference)
		{
			return ((IReference) source).getValue(context);
		}
		
		if(!(source instanceof String))
		{
			return source;
		}
		
		String sourceStr = (String) source;
		sourceStr = sourceStr.trim();
		
		//check if string is a reference
		Matcher matcher = IAutomationConstants.REF_PATTERN.matcher(sourceStr);
		
		if(matcher.matches())
		{
			try
			{
				return PropertyUtils.getProperty(context, matcher.group(1));
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while evaluating expression {} on context", matcher.group(1), ex);
			}
		}

		//check if string is resource
		JavaType resultType = defaultType != null ? defaultType : TypeFactory.defaultInstance().uncheckedSimpleType(Object.class);
		Matcher typeExtractMatcher = TYPE_EXTRACT_PATTERN.matcher(sourceStr);
		
		if(typeExtractMatcher.find())
		{
			String typeStr = typeExtractMatcher.group(1);
			resultType = parseJavaType(typeStr);
			
			sourceStr = sourceStr.substring(0, typeExtractMatcher.start()).trim();
		}
		
		IResource resource = ResourceFactory.getResource(context, sourceStr, exeLogger, true);
		String resourceName = resource.getName();
		
		if(resourceName != null && resourceName.toLowerCase().endsWith(".xml"))
		{
			Object rootBean = null;
			
			if(resultType != null && !Object.class.equals(resultType.getRawClass()))
			{
				try
				{
					rootBean = resultType.getRawClass().newInstance();
				}catch(Exception ex)
				{
					throw new InvalidStateException("An error occurred while creating instance of {} [Raw type: {}]", resultType, resultType.getRawClass().getName(), ex);
				}
			}
			
			rootBean = XMLBeanParser.parse(resource.getInputStream(), rootBean);
			return rootBean;
		}

		try
		{
			Object value = IAutomationConstants.OBJECT_MAPPER.readValue(resource.getInputStream(), resultType);
			resource.close();
			
			return value;
		}catch(Exception ex)
		{
			throw new IllegalStateException("An exception occurred while parsing json resource: " + resource.toText(), ex);
		}
	}
	
	/**
	 * Tries to delete specified folder. In case of exception 5 times it will tried to delete the folder
	 * with 2 seconds gap.
	 * @param reportFolder
	 * @throws IOException
	 */
	public static void deleteFolder(File reportFolder) throws IOException
	{
		if(!reportFolder.exists())
		{
			return;
		}
			
		
		for(int i = 0; ; i++)
		{
			try
			{
				FileUtils.forceDelete(reportFolder);
				break;
			}catch(IOException ex)
			{
				if(i < 5)
				{
					System.err.println("Ignored Error: " + ex);
					
					try
					{
						Thread.sleep(2000);
					}catch(Exception ex1)
					{}
					
				}
				else
				{
					throw ex;
				}
			}
		}
	}
	
	/**
	 * Converts original value into specified type. 
	 * @param originalValue value to be converted
	 * @param strType type to be converted. If null, original value will be retained.
	 * @return converted value
	 */
	public static Object convert(Object originalValue, String strType)
	{
		Class<?> type = null;
		
		if(strType != null)
		{
			type = CommonUtils.getClass(strType);
		}

		if(type != null)
		{
			return ConvertUtils.convert(originalValue, type);
		}

		return originalValue;
	}
}
