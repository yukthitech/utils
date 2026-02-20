package com.yukthitech.transform.template;

import static com.yukthitech.transform.ITransformConstants.OBJECT_MAPPER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.transform.Conversions;
import com.yukthitech.transform.IContentLoader;
import com.yukthitech.transform.TransformException;
import com.yukthitech.transform.template.TransformTemplate.Expression;
import com.yukthitech.transform.template.TransformTemplate.ForEachLoop;
import com.yukthitech.transform.template.TransformTemplate.Include;
import com.yukthitech.transform.template.TransformTemplate.Resource;
import com.yukthitech.transform.template.TransformTemplate.Switch;
import com.yukthitech.transform.template.TransformTemplate.SwitchCase;
import com.yukthitech.transform.template.TransformTemplate.TransformList;
import com.yukthitech.transform.template.TransformTemplate.TransformObject;
import com.yukthitech.transform.template.TransformTemplate.TransformObjectField;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class JsonTemplateFactory implements ITemplateFactory
{
	public static Set<String> SUB_ATTR = Set.of(
			"@params",
			"@expressions",
			"@resParams",
			"@for-each-condition",
			"@case"
		);
	
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
	 * Key used to specify switch statement with multiple cases.
	 */
	private static final String KEY_SWITCH = "@switch";
	
	/**
	 * Key used in switch case to specify condition.
	 */
	private static final String KEY_CASE = "@case";
	
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
    
    public void reset()
    {
    	includeCache.clear();
    }

    public TransformTemplate parseTemplate(String templateContent) 
    {
        Object jsonObj = null;
        
        try
        {
            jsonObj = OBJECT_MAPPER.readValue(templateContent, Object.class);
        } catch(Exception ex)
        {
            throw new InvalidStateException("An error occurred while parsing json template.", ex);
        }

        Object root = parseObject(jsonObj, "");

        return new TransformTemplate(JsonGenerator.class, root);
    }
    
    private void parseTemplateTo(String templateContent, TransformTemplate template)
    {
        Object jsonObj = null;
        
        try
        {
            jsonObj = OBJECT_MAPPER.readValue(templateContent, Object.class);
        } catch(Exception ex)
        {
            throw new InvalidStateException("An error occurred while parsing json template.", ex);
        }

        Object root = parseObject(jsonObj, "");
        template.setRoot(root);
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
                object = TransformUtils.parseExpression((String) object, path, false);
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
            if(KEY_CONDITION.equals(entry.getKey()))
            {
                transformObject.setCondition(entry.getValue().toString());
                continue;
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

            if(KEY_SWITCH.equals(entry.getKey()))
            {
            	Object switchValue = entry.getValue();
            	
            	if(!(switchValue instanceof List))
            	{
            		throw new TransformException(path + ">" + entry.getKey(), 
            			"@switch must have a list value, but found: {}", 
            			(switchValue != null ? switchValue.getClass().getName() : "null"));
            	}
            	
            	@SuppressWarnings("unchecked")
            	List<Object> switchCases = (List<Object>) switchValue;
            	
            	// Validation: At least one case is mandatory
            	if(switchCases.isEmpty())
            	{
            		throw new TransformException(path + ">" + entry.getKey(), 
            			"@switch must have at least one case");
            	}
            	
            	List<SwitchCase> parsedCases = new ArrayList<>();
            	boolean defaultCaseFound = false;
            	
            	for(int i = 0; i < switchCases.size(); i++)
            	{
            		Object caseObj = switchCases.get(i);
            		
            		if(!(caseObj instanceof Map))
            		{
            			throw new TransformException(path + ">" + entry.getKey() + "[" + i + "]", 
            				"Switch case must be a map object, but found: {}", 
            				(caseObj != null ? caseObj.getClass().getName() : "null"));
            		}
            		
            		@SuppressWarnings("unchecked")
            		Map<String, Object> caseMap = (Map<String, Object>) caseObj;
            		
            		String condition = null;
            		Object value = null;
            		
            		// Check for @case
            		if(caseMap.containsKey(KEY_CASE))
            		{
            			condition = caseMap.get(KEY_CASE).toString();
            		}
            		
            		// Validation: Default case (no condition) should be at the end only
            		if(condition == null)
            		{
            			if(defaultCaseFound)
            			{
            				throw new TransformException(path + ">" + entry.getKey() + "[" + i + "]", 
            					"Multiple default cases (cases without @case) found in @switch. Only one default case is allowed and it must be at the end");
            			}
            			
            			// Check if this is not the last case
            			if(i < switchCases.size() - 1)
            			{
            				throw new TransformException(path + ">" + entry.getKey() + "[" + i + "]", 
            					"Default case (case without @case) must be the last case in @switch");
            			}
            			
            			defaultCaseFound = true;
            		}
            		
            		// Check for @value - @value is mandatory
            		if(caseMap.containsKey(KEY_VALUE))
            		{
            			value = parseObject(caseMap.get(KEY_VALUE), path + ">" + entry.getKey() + "[" + i + "]>@value");
            		}
                    else
            		{
            			throw new TransformException(path + ">" + entry.getKey() + "[" + i + "]", 
            				"Switch case must have @value specified");
            		}
            		
            		parsedCases.add(new SwitchCase(condition, value));
            	}
            	
            	transformObject.setSwitchStatement(new Switch(parsedCases));
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
                transformObject.setTransformExpression(TransformUtils.parseExpression((String) entry.getValue(), path + ">" + entry.getKey(), false));
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
            
            // Check if the entry represents a set attr entry
            Matcher setMatcher = SET_PATTERN.matcher(entry.getKey());

            if(setMatcher.matches())
            {
            	String attributeName = setMatcher.group(1);

                transformObject.addField(new TransformObjectField(
                        entry.getKey(), 
                        null,
                        attributeName, 
                        parseObject(entry.getValue(), path + ">" + entry.getKey()),
                        false
                    ));
                continue;
            }

            // check if this is replaced entry

            if(REPLACE_PATTERN.matcher(entry.getKey()).matches())
            {
                transformObject.addField(new TransformObjectField(
                        entry.getKey(), 
                        null,
                        null, 
                        parseObject(entry.getValue(), path + ">" + entry.getKey()),
                        true
                    ));
                continue;
            }

            //check if key itself represents an expression
            Expression keyExpression = TransformUtils.parseExpression(entry.getKey(), path + ">" + entry.getKey(), true);
            
            // if key starts with @, those are special keys
            if(entry.getKey().startsWith("@") && keyExpression == null)
            {
            	if(SUB_ATTR.contains(entry.getKey()))
            	{
            		continue;
            	}
            	
                throw new InvalidStateException("Invalid transform attribute '{}' specified at path: {}", entry.getKey(), path);
            }

            transformObject.addField(new TransformObjectField(
                entry.getKey(), 
                keyExpression,
                null, 
                parseObject(entry.getValue(), path + ">" + entry.getKey()),
                false
            ));
        }
        
        return transformObject;
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
		subTemplate = new TransformTemplate(JsonGenerator.class, null);
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
