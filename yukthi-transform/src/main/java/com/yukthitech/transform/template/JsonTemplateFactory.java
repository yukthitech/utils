package com.yukthitech.transform.template;

import static com.yukthitech.transform.ITransformConstants.OBJECT_MAPPER;
import static com.yukthitech.transform.template.TransformTemplate.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.transform.Conversions;
import com.yukthitech.transform.IContentLoader;
import com.yukthitech.transform.ITransformConstants;
import com.yukthitech.transform.TransformException;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class JsonTemplateFactory implements ITemplateFactory
{
    	/**
	 * Key used to specify condition for an object/map inclusion.
	 */
	private static final String KEY_CONDITION = "@condition";
	
	/**
	 * Key used to specify value for the enclosing map. Useful when condition has to be specified for simple attribute.
	 */
	private static final String KEY_VALUE = "@value";
	
	/**
	 * Key used to specify value for the enclosing map when condition fails. Useful when condition has to be specified for simple attribute.
	 */
	private static final String KEY_FALSE_VALUE = "@falseValue";

	/**
	 * Pattern used by keys to define repetition.
	 */
	private static final Pattern FOR_EACH_PATTERN = Pattern.compile("^\\@for\\-each\\((\\w+)\\)$");
	
	/**
	 * Loop condition to exclude objects being generated.
	 */
	private static final String KEY_FOR_EACH_CONDITION = "@for-each-condition";
	
	/**
	 * Pattern used by keys to set complex object (post processing) on context.
	 */
	private static final Pattern SET_PATTERN = Pattern.compile("^\\@set\\((\\w+)\\)$");
	
	
	/**
	 * Used to replace current map entry, with entries with entries of value map (of current entry). Mainly
	 * expected to be used with @includeResource or @includeFile. 
	 * 
	 * Note: Though param string is supported, the param itself is not in use. It is added to support multiple replacements
	 * in single map (in simple terms as key differentiators).
	 */
	private static final Pattern REPLACE_PATTERN = Pattern.compile("^\\@replace\\((\\w+)\\)$");

    
	/**
	 * In a map if this key is specified with expression, along with @value/@falseValue then the result will be result of this expression.
	 * Current value will be available in this expressions as thisValue.
	 */
	private static final String TRANSFORM = "@transform";

	/**
	 * Use to load resource value.
	 */
	private static final String RES = "@resource";

	/**
	 * Use to specify that expressions in resource being loaded should be processed or not.
	 * By default this will be true.
	 */
	private static final String RES_PARAM_EXPR = "@expressions";

	/**
	 * If specified, will be added to current context as 'resParams', which can be accessed within expressions of resource.
	 */
	private static final String RES_PARAM_PARAMS = "@resParams";

	/**
	 * Use to include resource template.
	 */
	private static final String INCLUDE_RES = "@includeResource";

	/**
	 * Use to include file template.
	 */
	private static final String INCLUDE_FILE = "@includeFile";
	
	/**
	 * Params that can be passed during inclusion. These params can be accessed in expressions using "params" as key.
	 */
	private static final String PARAMS = "@params";

	/**
	 * Expression used by value string which has to be replaced with resultant value.
	 */
	public static final Pattern EXPR_PATTERN = Pattern.compile("^\\@([\\w\\-]+)\\s*\\:\\s*(.*)$");
	
	private static final IContentLoader DEFAULT_CONTENT_LOADER = new IContentLoader() {};

    private IContentLoader contentLoader = DEFAULT_CONTENT_LOADER;
    
    /**
     * A cache of resource/file path to loaded template. This is maintained
     * to make template loading efficient (avoided repeated parsing) also needed
     * for recursive templates.
     */
    private Map<String, TransformTemplate> includeCache = new HashMap<>();

    public JsonTemplateFactory()
    {
    	this(DEFAULT_CONTENT_LOADER);
    }
    
    public JsonTemplateFactory(IContentLoader contentLoader)
    {
    	if(contentLoader == null)
    	{
    		contentLoader = DEFAULT_CONTENT_LOADER;
    	}
    	
        this.contentLoader = contentLoader;
    }

    public TransformTemplate pareseTemplate(String jsonContent) 
    {
        Object jsonObj = null;
        
        try
        {
            jsonObj = OBJECT_MAPPER.readValue(jsonContent, Object.class);
        } catch(Exception ex)
        {
            throw new InvalidStateException("An error occurred while parsing json template.", ex);
        }

        Object root = parseObject(jsonObj, "");

        return new TransformTemplate(this, root);
    }
    
    private void parseTemplateTo(String jsonContent, TransformTemplate template)
    {
        Object jsonObj = null;
        
        try
        {
            jsonObj = OBJECT_MAPPER.readValue(jsonContent, Object.class);
        } catch(Exception ex)
        {
            throw new InvalidStateException("An error occurred while parsing json template.", ex);
        }

        Object root = parseObject(jsonObj, "");
        template.setRoot(root);
    }
    
    public String formatObject(Object object)
    {
    	try
    	{
    		return OBJECT_MAPPER.writeValueAsString(object);
        } catch(Exception ex)
        {
            throw new InvalidStateException("An error occurred while writing json value.", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Object parseObject(Object object, String path)
    {
		try
		{
			if(object instanceof List)
			{
				object = parseList((List<Object>) object, path);
			}
			else if(object instanceof Map)
			{
				object = parseMap((Map<String, Object>) object, path);
			}
			else if(object instanceof String)
			{
                object = parseExpression((String) object, path, false);
			}
		} catch(TransformException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new TransformException(path, "An unhandled error occurred", ex);
		}
		
		return object;
    }

    private Object parseList(List<Object> list, String path)
    {
        boolean firstValue = true;
        String condition = null;
        List<Object> objects = new ArrayList<>();
        int index = -1;
        
        for(Object object : list)
        {
        	index++;
        	
            if(firstValue)
            {
                condition = checkForListCondition(object);
                firstValue = false;

                // if first value is a condition, skip it
                if(condition != null)
                {
                    continue;
                }
            }

            objects.add(parseObject(object, path + "[" + index + "]"));
        }
        
        return new TransformList(path, condition, objects);
    }

    private String checkForListCondition(Object object)
    {
        if(!(object instanceof String))
        {
            return null;
        }
        
        Matcher matcher = Conversions.EXPR_PATTERN.matcher((String) object);
        
        if(!matcher.matches())
        {
            return null;
        }
        
        String exprType = matcher.group(1);
        
        if(!exprType.equals("condition"))
        {
            return null;
        }
        
        return matcher.group(2);
    }

    private Object parseMap(Map<String, Object> map, String path)
    {
        TransformObject transformObject = new TransformObject(path);

        for(Map.Entry<String, Object> entry : map.entrySet())
        {
            String condition = (String) map.get(KEY_CONDITION);

            if(StringUtils.isNotBlank(condition))
            {
                transformObject.setCondition(condition);
            }

            Matcher forEachMatcher = FOR_EACH_PATTERN.matcher(entry.getKey());

            if(forEachMatcher.matches())
            {
                String loopVariable = forEachMatcher.group(1);
                Object listExpression = entry.getValue();
                String loopCondition = (String) map.get(KEY_FOR_EACH_CONDITION);

                transformObject.setForEachLoop(new ForEachLoop(listExpression, loopVariable, loopCondition));
                continue;
            }

            if(KEY_FALSE_VALUE.equals(entry.getKey()))
            {
            	Object falseValue = parseObject(entry.getValue(), path + ">" + entry.getKey());
                transformObject.setFalseValue(falseValue);
                continue;
            }

            if(KEY_VALUE.equals(entry.getKey()))
            {
            	Object valueObj = parseObject(entry.getValue(), path + ">" + entry.getKey());
                transformObject.setValue(valueObj);
                continue;
            }

            if(TRANSFORM.equals(entry.getKey()) && (entry.getValue() instanceof String)
                && StringUtils.isNotBlank((String) entry.getValue()))
            {
                transformObject.setTransformExpression(parseExpression((String) entry.getValue(), path + ">" + entry.getKey(), false));
                continue;
            }

            if(RES.equals(entry.getKey()))
            {
                String resPath = entry.getValue().toString();
                String content = null;

                try
                {
                    content = contentLoader.loadResource(resPath);
                }catch(Exception ex)
                {
                    throw new TransformException(path, "Failed to load resource: {}", entry.getValue(), ex);			
                }

                boolean disableExpressions = "false".equalsIgnoreCase("" + map.get(RES_PARAM_EXPR));
                Object resParams = map.get(RES_PARAM_PARAMS);

                String transformExpression = (String) map.get(TRANSFORM);

                transformObject.setResource(new Resource(
                    resPath, 
                    content, 
                    disableExpressions, 
                    resParams, 
                    transformExpression));
                continue;
            }

            if(INCLUDE_RES.equals(entry.getKey()) || INCLUDE_FILE.equals(entry.getKey()))
            {
                transformObject.setInclude(parseInclude(map, entry.getKey(), path + ">" + entry.getKey()));
                continue;
            }
            
            boolean parsedEntry = false;
            
            // Check if the entry represents a set attr entry
            Matcher setMatcher = SET_PATTERN.matcher(entry.getKey());
            String attributeName = null;

            if(setMatcher.matches())
            {
                attributeName = setMatcher.group(1);
                parsedEntry = true;
            }

            // check if this is replaced entry
            boolean replaceEntry = REPLACE_PATTERN.matcher(entry.getKey()).matches();
            parsedEntry = (parsedEntry || replaceEntry);
            
            //check if key itself represents an expression
            Expression keyExpression = parseExpression(entry.getKey(), path + ">" + entry.getKey(), true);
            
            // if key starts with @, those are special keys and should be ignored
            if(entry.getKey().startsWith("@") && keyExpression == null && !parsedEntry)
            {
                continue;
            }

            transformObject.addField(new TransforObjectField(
                entry.getKey(), 
                keyExpression,
                attributeName, 
                parseObject(entry.getValue(), path + ">" + entry.getKey()),
                replaceEntry
            ));
        }
        
        return transformObject;
    }

    private Expression parseExpression(String expression, String path, boolean nullByDefault)
    {
        Matcher matcher = EXPR_PATTERN.matcher(expression);
        
        if(!matcher.matches())
        {
        	if(expression.contains("${") || expression.contains("<#"))
        	{
        		return new Expression(ExpressionType.TEMPLATE, expression);
        	}
        	
            return nullByDefault ? null : new Expression(ExpressionType.STRING, expression);
        }

        String exprType = matcher.group(1);
        String expr = matcher.group(2);

        if(ITransformConstants.EXPR_TYPE_FMARKER.equals(exprType))
        {
            return new Expression(ExpressionType.FMARKER, expr);
        }
        else if(ITransformConstants.EXPR_TYPE_XPATH.equals(exprType))
        {
            return new Expression(ExpressionType.XPATH, expr);
        }
        else if(ITransformConstants.EXPR_TYPE_XPATH_MULTI.equals(exprType))
        {
            return new Expression(ExpressionType.XPATH_MULTI, expr);
        }

        throw new TransformException(path, "Invalid expression type specified '%s' in expression: %s", exprType, expression);
    }
    
    private TransformTemplate loadAndParse(String path, boolean isResource)
    {
    	String keyPrefix = isResource ? "res:" : "file:";
    	String key = keyPrefix + path;
    	
    	TransformTemplate subTemplate = includeCache.get(key);
    	
    	if(subTemplate != null)
    	{
    		return subTemplate;
    	}
    	
    	String content = null;
    	
		try
		{
			if(isResource)
			{
				content = contentLoader.loadResource(path);
			}
			else
			{
				content = contentLoader.loadFile(path);
			}
			
		}catch(Exception ex)
		{
			if(isResource)
			{
				throw new TransformException(path, "Failed to include resource: {}", path, ex);
			}
			else
			{
				throw new TransformException(path, "Failed to include file: {}", path, ex);
			}
		}
		
		// first template is kept on cache (before parsing) to avoid
		//   never ending recursion in case of recursive templates
		subTemplate = new TransformTemplate(this, null);
		includeCache.put(key, subTemplate);
		
		parseTemplateTo(content, subTemplate);
		
		return subTemplate;
    }

    private Include parseInclude(Map<String, Object> map, String key, String path)
    {
        String includePath = map.get(key).toString();
        
		Object params = map.get(PARAMS);
		
		if(params != null && !(params instanceof Map))
		{
			throw new TransformException(path, "Invalid params specified for include tag. params should be of type Map");
		}
		
		TransformObject paramsObj = null;

		if(params != null)
		{
			paramsObj = (TransformObject) parseObject(params, path + ">" + PARAMS);
		}
        
        boolean isResource = INCLUDE_RES.equals(key);
		
		TransformTemplate subTemplate = loadAndParse(includePath, isResource);
        return new Include(includePath, subTemplate, paramsObj);
    }
}
