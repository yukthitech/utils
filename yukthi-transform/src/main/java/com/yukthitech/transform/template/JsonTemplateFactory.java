package com.yukthitech.transform.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.transform.IContentLoader;
import com.yukthitech.transform.TransformFmarkerMethods;
import com.yukthitech.transform.template.JsonWithLocationParser.JsonElementWithLocation;
import com.yukthitech.transform.template.JsonWithLocationParser.ListWithLocation;
import com.yukthitech.transform.template.JsonWithLocationParser.MapWithLocation;
import com.yukthitech.transform.template.JsonWithLocationParser.ValueWithLocation;
import com.yukthitech.transform.template.TransformTemplate.Expression;
import com.yukthitech.transform.template.TransformTemplate.ForEachLoop;
import com.yukthitech.transform.template.TransformTemplate.Include;
import com.yukthitech.transform.template.TransformTemplate.Resource;
import com.yukthitech.transform.template.TransformTemplate.Switch;
import com.yukthitech.transform.template.TransformTemplate.SwitchCase;
import com.yukthitech.transform.template.TransformTemplate.TransformElement;
import com.yukthitech.transform.template.TransformTemplate.TransformList;
import com.yukthitech.transform.template.TransformTemplate.TransformObject;
import com.yukthitech.transform.template.TransformTemplate.TransformObjectField;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;
import com.yukthitech.utils.fmarker.FreeMarkerTemplate;

public class JsonTemplateFactory implements ITemplateFactory
{
	public static Set<String> SUB_ATTR = Set.of(
			"@params",
			"@expressions",
			"@resParams",
			"@for-each-condition",
			"@case",
			"@name",
			"@safe"
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
	 * Key used to specify safe value to be used, in case value results in exception. This is a means
	 * of evaluating expressions in safe manner with default value. 
	 */
	private static final String KEY_SAFE_VALUE = "@safe";
	
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
	 * Used in cases where parent-key has to be modified based on this expression.
	 */
	private static final String KEY_NAME = "@name";
	
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
    
	/**
	 * Free marker engine for expression processing.
	 */
	private FreeMarkerEngine freeMarkerEngine;

	/**
	 * Configuration for the template factory.
	 */
	private TemplateFactoryConfiguration templateFactoryConfiguration = new TemplateFactoryConfiguration();

    public JsonTemplateFactory()
    {
    	this(DEFAULT_CONTENT_LOADER, new FreeMarkerEngine());
    }
    
    public JsonTemplateFactory(FreeMarkerEngine freeMarkerEngine)
    {
    	this(DEFAULT_CONTENT_LOADER, freeMarkerEngine);
    }
    
    public JsonTemplateFactory(IContentLoader contentLoader)
    {
    	this(DEFAULT_CONTENT_LOADER, new FreeMarkerEngine());
    }
    
	public JsonTemplateFactory(IContentLoader contentLoader, FreeMarkerEngine freeMarkerEngine)
	{
    	if(contentLoader == null)
    	{
    		contentLoader = DEFAULT_CONTENT_LOADER;
    	}
    	
        this.contentLoader = contentLoader;
		this.setFreeMarkerEngine(freeMarkerEngine);
	}

	public void setTemplateFactoryConfiguration(TemplateFactoryConfiguration templateFactoryConfiguration)
	{
		if(templateFactoryConfiguration == null)
		{
			throw new NullPointerException("Template factory configuration cannot be set to null.");
		}

		this.templateFactoryConfiguration = templateFactoryConfiguration;
	}

	/**
	 * Sets the free marker engine for expression processing.
	 *
	 * @param freeMarkerEngine
	 *            the new free marker engine for expression processing
	 */
	public void setFreeMarkerEngine(FreeMarkerEngine freeMarkerEngine)
	{
		if(freeMarkerEngine == null)
		{
			throw new NullPointerException("Free marker engine cannot be set to null.");
		}
		
		this.freeMarkerEngine = freeMarkerEngine;
		this.freeMarkerEngine.loadClass(TransformFmarkerMethods.class);
	}

	public void reset()
    {
    	includeCache.clear();
    }

    public TransformTemplate parseTemplate(String name, String templateContent) 
    {
		templateFactoryConfiguration.pushCurrentInstance();

		try
		{
			JsonWithLocationParser parser = new JsonWithLocationParser(name);
			Object jsonObj = null;
			
			try
			{
				jsonObj = parser.parse(templateContent);
			} catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while parsing json template.", ex);
			}

			TransformElement root = (TransformElement) parseObject((JsonElementWithLocation) jsonObj, null);

			return new TransformTemplate(name, JsonGenerator.class, root, root.getLocation());
		} finally
		{
			templateFactoryConfiguration.popCurrentInstance();
		}
	}
    
    private void parseTemplateTo(String templateContent, TransformTemplate template)
    {
		templateFactoryConfiguration.pushCurrentInstance();

		try
		{
			JsonWithLocationParser parser = new JsonWithLocationParser(template.getName());
			Object jsonObj = null;
			
			try
			{
				jsonObj = parser.parse(templateContent);
			} catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while parsing json template.", ex);
			}

			Object root = parseObject((JsonElementWithLocation) jsonObj, null);
			template.setRoot(root);
		} finally
		{
			templateFactoryConfiguration.popCurrentInstance();
		}
    }
    
    private Object parseObject(JsonElementWithLocation object, String parentKey)
    {
		try
		{
			if(object instanceof ListWithLocation)
			{
				return parseList((ListWithLocation) object);
			}

			if(object instanceof MapWithLocation)
			{
				return parseMap((MapWithLocation) object, parentKey);
			}

			ValueWithLocation valueWithLocation = (ValueWithLocation) object;

			if(valueWithLocation.getValue() instanceof String)
			{
                return ExpressionUtils.parseExpression(this.freeMarkerEngine, (String) valueWithLocation.getValue(), 
					valueWithLocation.getLocation(), false);
			}

			return valueWithLocation.getValue();
		} catch(TemplateParseException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new TemplateParseException(object.getLocation(), "An unhandled error occurred", ex);
		}
    }

    private TransformList parseList(ListWithLocation list)
    {
        boolean firstValue = true;
        String condition = null;
        List<Object> objects = new ArrayList<>();
        
        for(JsonElementWithLocation object : list)
        {
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

            objects.add(parseObject(object, null));
        }
        
        FreeMarkerTemplate conditionTemp = (condition == null) ? null : freeMarkerEngine.buildConditionTemplate("transform-list-condition", condition);
        return new TransformList(list.getLocation(), conditionTemp, objects);
    }

    private String checkForListCondition(JsonElementWithLocation object)
    {
        if(!(object.getValue() instanceof String))
        {
            return null;
        }
        
        Matcher matcher = ExpressionUtils.EXPR_PATTERN.matcher((String) object.getValue());
        
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

    private TransformObject parseMap(MapWithLocation map, String parentKey)
    {
        TransformObject transformObject = new TransformObject(map.getLocation());

        for(Map.Entry<String, JsonElementWithLocation> entry : map.entrySet())
        {
            if(KEY_CONDITION.equals(entry.getKey()))
            {
            	FreeMarkerTemplate conditionTemp = freeMarkerEngine.buildConditionTemplate("transform-condition", map.getString(KEY_CONDITION));
                transformObject.setCondition(conditionTemp);
                continue;
            }

            Matcher forEachMatcher = FOR_EACH_PATTERN.matcher(entry.getKey());

            if(forEachMatcher.matches())
            {
                String loopVariable = forEachMatcher.group(1);
                JsonElementWithLocation listExpression = (JsonElementWithLocation) entry.getValue();
                String loopCondition = map.getString(KEY_FOR_EACH_CONDITION);
                
                String nameExpression = map.getString(KEY_NAME);
                Location nameExprLocation = StringUtils.isNotBlank(nameExpression) ? map.get(KEY_NAME).getLocation() : null;
                
                // For map based for-loop, if name expression is not defined, use parent key as name-expression
                if(parentKey != null && StringUtils.isBlank(nameExpression))
                {
                	nameExpression = parentKey;
                	nameExprLocation = map.getLocation();
                }
                
                Object listExpressionValue = listExpression.getValue();
                
                if(listExpressionValue instanceof String)
                {
                	Expression expression = ExpressionUtils.parseValueExpression(this.freeMarkerEngine, (String) listExpressionValue, 
                			listExpression.getLocation());
                	
                	listExpressionValue = expression;
                }
                
                FreeMarkerTemplate conditionTemplate = StringUtils.isBlank(loopCondition) ? null : freeMarkerEngine.buildConditionTemplate("for-each-condition", loopCondition);
                
                ForEachLoop forEachLoop = new ForEachLoop(listExpression.getLocation(), listExpressionValue, 
    					loopVariable, conditionTemplate);

                Expression nameExpressionObj = (nameExpression == null) ? null : ExpressionUtils.parseExpression(freeMarkerEngine, nameExpression, 
                		nameExprLocation, false);
                forEachLoop.setNameExpression(nameExpressionObj);
                
                transformObject.setForEachLoop(forEachLoop);
                continue;
            }

            if(KEY_SWITCH.equals(entry.getKey()))
            {
            	JsonElementWithLocation switchValue = entry.getValue();
            	
            	if(!(switchValue instanceof ListWithLocation))
            	{
            		throw new TemplateParseException(
						switchValue.getLocation(), "For '@switch' value must be a list.");
            	}

            	ListWithLocation switchCases = (ListWithLocation) switchValue.getValue();
            	List<SwitchCase> parsedCases = new ArrayList<>();
            	boolean defaultCaseFound = false;
            	
            	for(int i = 0; i < switchCases.size(); i++)
            	{
            		JsonElementWithLocation caseObj = switchCases.get(i);
            		
            		if(!(caseObj instanceof MapWithLocation))
            		{
            			throw new TemplateParseException(caseObj.getLocation(), "Switch case must be a map object.");
            		}
            		
            		MapWithLocation caseMap = (MapWithLocation) caseObj.getValue();
            		
            		String condition = null;
            		Object value = null;
            		
            		// Check for @case
            		if(caseMap.containsKey(KEY_CASE))
            		{
            			condition = caseMap.getString(KEY_CASE);
            		}
            		
            		// Validation: Default case (no condition) should be at the end only
            		if(condition == null)
            		{
            			if(defaultCaseFound)
            			{
            				throw new TemplateParseException(caseObj.getLocation(), 
								"Multiple default cases (cases without @case) found in @switch. Only one default case is allowed and it must be at the end");
            			}
            			
            			// Check if this is not the last case
            			if(i < switchCases.size() - 1)
            			{
            				throw new TemplateParseException(caseObj.getLocation(), 
            					"Default case (case without @case) must be the last case in @switch");
            			}
            			
            			defaultCaseFound = true;
            		}
            		
            		// Check for @value - @value is mandatory
            		if(caseMap.containsKey(KEY_VALUE))
            		{
            			value = parseObject(caseMap.get(KEY_VALUE), null);
            		}
                    else
            		{
            			throw new TemplateParseException(caseObj.getLocation(), 
            				"Switch case must have @value specified");
            		}
            		
            		FreeMarkerTemplate conditionTemp = (condition == null) ? null : freeMarkerEngine.buildConditionTemplate("switch-condition", condition);
            		parsedCases.add(new SwitchCase(caseObj.getLocation(), conditionTemp, value));
            	}
            	
            	transformObject.setSwitchStatement(new Switch(switchValue.getLocation(), parsedCases));
                continue;
            }

            if(KEY_FALSE_VALUE.equals(entry.getKey()))
            {
            	Object falseValue = parseObject(entry.getValue(), null);
                transformObject.setFalseValue(falseValue);
                continue;
            }

            if(KEY_VALUE.equals(entry.getKey()))
            {
            	Object valueObj = parseObject(entry.getValue(), null);
                transformObject.setValue(valueObj);
                
                if(map.containsKey(KEY_SAFE_VALUE))
                {
                	Object safeValueObj = parseObject(map.get(KEY_SAFE_VALUE), null);
                    transformObject.setSafeValue(safeValueObj);
                }
                
                continue;
            }

            if(TRANSFORM.equals(entry.getKey()))
            {
				JsonElementWithLocation transformValue = (JsonElementWithLocation) entry.getValue();
				
                transformObject.setTransformExpression(ExpressionUtils.parseExpression(
                	this.freeMarkerEngine, 
					map.getString(TRANSFORM),
					transformValue.getLocation(),
					false));
                continue;
            }

            if(RES.equals(entry.getKey()))
            {
                String resPath = map.getString(RES);
                String content = null;

                try
                {
                    content = contentLoader.loadResource(resPath);
                }catch(Exception ex)
                {
                    throw new TemplateParseException(entry.getValue().getLocation(), 
						"Failed to load resource: {}", resPath, ex);			
                }

                boolean disableExpressions = "false".equalsIgnoreCase("" + map.get(RES_PARAM_EXPR));
                Object resParams = map.get(RES_PARAM_PARAMS).getValue();

                String transformExpression = map.getString(TRANSFORM);

                transformObject.setResource(new Resource(
					entry.getValue().getLocation(),
                    resPath, 
                    content, 
                    disableExpressions, 
                    resParams, 
                    transformExpression));
                continue;
            }

            if(INCLUDE_RES.equals(entry.getKey()) || INCLUDE_FILE.equals(entry.getKey()))
            {
                transformObject.setInclude(parseInclude(map, entry.getKey()));
                continue;
            }
            
            // Check if the entry represents a set attr entry
            Matcher setMatcher = SET_PATTERN.matcher(entry.getKey());

            if(setMatcher.matches())
            {
            	String attributeName = setMatcher.group(1);

                transformObject.addField(new TransformObjectField(
                        entry.getValue().getLocation(),
                        entry.getKey(), 
                        null,
                        attributeName, 
                        parseObject(entry.getValue(), null),
                        false
                    ));
                continue;
            }

            // check if this is replaced entry

            if(REPLACE_PATTERN.matcher(entry.getKey()).matches())
            {
                transformObject.addField(new TransformObjectField(
                        entry.getValue().getLocation(),
                        entry.getKey(), 
                        null,
                        null, 
                        parseObject(entry.getValue(), null),
                        true
                    ));
                continue;
            }

            //check if key itself represents an expression
            Expression keyExpression = ExpressionUtils.parseExpression(this.freeMarkerEngine, entry.getKey(), entry.getValue().getLocation(), true);
            
            // if key starts with @, those are special keys
            if(entry.getKey().startsWith("@") && keyExpression == null)
            {
            	if(SUB_ATTR.contains(entry.getKey()))
            	{
            		continue;
            	}
            	
                throw new TemplateParseException(entry.getValue().getLocation(), 
					"Invalid transform attribute '{}' specified", entry.getKey());
            }

            transformObject.addField(new TransformObjectField(
                entry.getValue().getLocation(),
                entry.getKey(), 
                keyExpression,
                null, 
                parseObject(entry.getValue(), entry.getKey()),
                false
            ));
        }
        
        return transformObject;
    }

    private TransformTemplate loadAndParse(Location location, String path, boolean isResource)
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
				throw new TemplateParseException(location, "Failed to include resource: {}", path, ex);
			}
			else
			{
				throw new TemplateParseException(location, "Failed to include file: {}", path, ex);
			}
		}
		
		// first template is kept on cache (before parsing) to avoid
		//   never ending recursion in case of recursive templates
		subTemplate = new TransformTemplate(path, JsonGenerator.class, null, location);
		includeCache.put(key, subTemplate);
		
		parseTemplateTo(content, subTemplate);
		
		return subTemplate;
    }

    private Include parseInclude(MapWithLocation map, String key)
    {
        String includePath = map.getString(key);
        
		JsonElementWithLocation params = map.get(PARAMS);
		
		if(params != null && !(params instanceof MapWithLocation))
		{
			throw new TemplateParseException(map.get(PARAMS).getLocation(), 
				"Invalid params specified for include tag. params should be of type Map");
		}
		
		TransformObject paramsObj = null;

		if(params != null)
		{
			paramsObj = (TransformObject) parseObject(params, null);
		}
        
        boolean isResource = INCLUDE_RES.equals(key);
		
		TransformTemplate subTemplate = loadAndParse(map.getLocation(), includePath, isResource);
        return new Include(map.getLocation(), includePath, subTemplate, paramsObj);
    }
}
