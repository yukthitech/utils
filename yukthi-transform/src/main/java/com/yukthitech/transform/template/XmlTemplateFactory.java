package com.yukthitech.transform.template;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import com.yukthitech.ccg.xml.XMLBeanParser;
import com.yukthitech.transform.IContentLoader;
import com.yukthitech.transform.ITransformConstants;
import com.yukthitech.transform.TransformFmarkerMethods;
import com.yukthitech.transform.template.TransformTemplate.Expression;
import com.yukthitech.transform.template.TransformTemplate.FieldType;
import com.yukthitech.transform.template.TransformTemplate.ForEachLoop;
import com.yukthitech.transform.template.TransformTemplate.Include;
import com.yukthitech.transform.template.TransformTemplate.Resource;
import com.yukthitech.transform.template.TransformTemplate.Switch;
import com.yukthitech.transform.template.TransformTemplate.SwitchCase;
import com.yukthitech.transform.template.TransformTemplate.TransformElement;
import com.yukthitech.transform.template.TransformTemplate.TransformList;
import com.yukthitech.transform.template.TransformTemplate.TransformObject;
import com.yukthitech.transform.template.TransformTemplate.TransformObjectField;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;
import com.yukthitech.utils.fmarker.FreeMarkerTemplate;

public class XmlTemplateFactory implements ITemplateFactory
{
	public static Set<String> SUB_ATTR = Set.of(
			"name",
			"loopVar",
			"forEachCondition",
			"value",
			"condition"
		);

    /**
	 * Key used to specify condition for an object/map inclusion.
	 */
	private static final String KEY_CONDITION = "condition";
	
	/**
	 * Name expression attr name to override name.
	 */
	private static final String NAME_EXPRESSION = "name";

	/**
	 * Key used to specify value for the enclosing map. Useful when condition has to be specified for simple attribute.
	 */
	private static final String KEY_VALUE = "value";
	
	/**
	 * Key used to specify value for the enclosing map when condition fails. Useful when condition has to be specified for simple attribute.
	 */
	private static final String KEY_FALSE_VALUE = "falseValue";

	/**
	 * Attr used to define repetition.
	 */
	private static final String FOR_EACH_EXPR = "forEach";

	/**
	 * Attr used to define loop variable for repetition.
	 */
	private static final String FOR_EACH_LOOP_VAR = "loopVar";

	/**
	 * Loop condition to exclude objects being generated.
	 */
	private static final String KEY_FOR_EACH_CONDITION = "forEachCondition";
	
	/**
	 * Set node name.
	 */
	public static final String SET = "set";

	/**
	 * Replace node name.
	 */
	public static final String REPLACE = "replace";
	
	/**
	 * Switch node name.
	 */
	public static final String SWITCH = "switch";
	
	/**
	 * Case node name.
	 */
	public static final String CASE = "case";

	/**
	 * In a map if this key is specified with expression, along with @value/@falseValue then the result will be result of this expression.
	 * Current value will be available in this expressions as thisValue.
	 */
	private static final String TRANSFORM = "transform";

	/**
	 * Use to load resource value.
	 */
	public static final String RES = "resource";

	/**
	 * Use to specify that expressions in resource being loaded should be processed or not.
	 * By default this will be true.
	 */
	private static final String RES_PARAM_EXPR = "expressions";

	/**
	 * Use to include resource template.
	 */
	public static final String INCLUDE_RES = "includeResource";

	/**
	 * Use to include file template.
	 */
	public static final String INCLUDE_FILE = "includeFile";
	
	/**
	 * Params that can be passed during inclusion. These params can be accessed in expressions using "params" as key.
	 */
	public static final String PARAMS = "params";

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

    public XmlTemplateFactory()
    {
    	this(DEFAULT_CONTENT_LOADER, new FreeMarkerEngine());
    }
    
    public XmlTemplateFactory(IContentLoader contentLoader)
    {
    	this(DEFAULT_CONTENT_LOADER, new FreeMarkerEngine());
    }
    
	public XmlTemplateFactory(IContentLoader contentLoader, FreeMarkerEngine freeMarkerEngine)
	{
    	if(contentLoader == null)
    	{
    		contentLoader = DEFAULT_CONTENT_LOADER;
    	}
    	
        this.contentLoader = contentLoader;
		this.setFreeMarkerEngine(freeMarkerEngine);
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

    public TransformTemplate parseTemplate(String name, String templateContent) 
    {
    	XmlTemplateParserHandler parserHandler = new XmlTemplateParserHandler(name);
    	parserHandler.setExpressionEnabled(false);
    	
    	XmlDynamicBean dynBean = (XmlDynamicBean) XMLBeanParser.parse(
    			new ByteArrayInputStream(templateContent.getBytes()), 
    			parserHandler);

    	TransformElement root = (TransformElement) parseObject(dynBean, dynBean.getLocation());

        return new TransformTemplate(name, XmlGenerator.class, root, root.getLocation());
    }
    
    private void parseTemplateTo(String templateContent, TransformTemplate template)
    {
    	XmlTemplateParserHandler parserHandler = new XmlTemplateParserHandler(template.getName());
    	parserHandler.setExpressionEnabled(false);

    	XmlDynamicBean dynBean = (XmlDynamicBean) XMLBeanParser.parse(
    			new ByteArrayInputStream(templateContent.getBytes()), 
    			parserHandler);

        Object root = parseObject(dynBean, dynBean.getLocation());
        template.setRoot(root);
    }
    
    @SuppressWarnings("unchecked")
    private Object parseObject(Object object, Location location)
    {
		try
		{
			if(object instanceof List)
			{
				object = parseList((List<XmlDynamicBean>) object, location);
			}
			else if(object instanceof XmlDynamicBean)
			{
				object = parseDynBean((XmlDynamicBean) object);
			}
			else if(object instanceof String)
			{
                object = ExpressionUtils.parseExpression(this.freeMarkerEngine, (String) object, location, false);
			}
		} catch(TemplateParseException ex)
		{
			throw ex;
		} catch(Exception ex)
		{
			throw new TemplateParseException(location, "An unhandled error occurred", ex);
		}
		
		return object;
    }

    private Object parseList(List<XmlDynamicBean> list, Location location)
    {
        boolean firstValue = true;
        String condition = null;
        List<Object> objects = new ArrayList<>();
        
        for(XmlDynamicBean object : list)
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

            objects.add(parseObject(object, object.getLocation()));
        }
        
        FreeMarkerTemplate conditionTemp = (condition == null) ? null : freeMarkerEngine.buildConditionTemplate("transform-list-condition", condition);
        return new TransformList(location, conditionTemp, objects);
    }

    private String checkForListCondition(Object object)
    {
        if(!(object instanceof String))
        {
            return null;
        }
        
        Matcher matcher = ExpressionUtils.EXPR_PATTERN.matcher((String) object);
        
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

    private boolean processReserveText(String name, String value, Location location, TransformObject transformObject)
    {
        if(KEY_CONDITION.equals(name))
        {
        	FreeMarkerTemplate conditionTemp = freeMarkerEngine.buildConditionTemplate("transform-condition", value);
            transformObject.setCondition(conditionTemp);
            return true;
        }

        if(KEY_FALSE_VALUE.equals(name))
        {
            Object falseValue = parseObject(value, location);
            transformObject.setFalseValue(falseValue);
            return true;
        }

        if(KEY_VALUE.equals(name))
        {
            Object valueObj = parseObject(value, location);
            transformObject.setValue(valueObj);
            return true;
        }

        if(TRANSFORM.equals(name)
            && StringUtils.isNotBlank(value))
        {
            transformObject.setTransformExpression(ExpressionUtils.parseExpression(this.freeMarkerEngine, value, location, false));
            return true;
        }
        
        // Attributes which are handled in special way, ignore those reserve attr
        if(SUB_ATTR.contains(name))
        {
            return true;
        }

        return false;
    }
    
    private void processReserveAttributes(XmlDynamicBean dynBean, TransformObject transformObject)
    {
        Map<String, String> reserveAttr = dynBean.getReserveAttributes();

        for(Map.Entry<String, String> entry : reserveAttr.entrySet())
        {
            if(FOR_EACH_EXPR.equals(entry.getKey()))
            {
                String loopVariable = reserveAttr.get(FOR_EACH_LOOP_VAR);
                Object listExpression = entry.getValue();
                String loopCondition = reserveAttr.get(KEY_FOR_EACH_CONDITION);
                
                String nameExpression = reserveAttr.get(NAME_EXPRESSION);
                
                if(listExpression instanceof String)
                {
                	Expression expression = ExpressionUtils.parseValueExpression(this.freeMarkerEngine, (String) listExpression, 
                			dynBean.getLocation());
                	
                	listExpression = expression;
                }
                
                FreeMarkerTemplate conditionTemplate = loopCondition  == null ? null : freeMarkerEngine.buildConditionTemplate("for-each-condition", loopCondition);
                
                ForEachLoop forEachLoop = new ForEachLoop(dynBean.getLocation(), listExpression, loopVariable, conditionTemplate);

                // Note: unlike in json, same element with same name can be repeated in xml (so name expression in for-loop is not mandatory)
                Expression nameExpressionObj = (nameExpression == null) ? null : ExpressionUtils.parseExpression(freeMarkerEngine, nameExpression, 
                		dynBean.getLocation(), false);
                forEachLoop.setNameExpression(nameExpressionObj);

                transformObject.setForEachLoop(forEachLoop);
                continue;
            }
    
            if(processReserveText(entry.getKey(), entry.getValue(), dynBean.getLocation(), transformObject))
            {
                continue;
            }
        
            throw new TemplateParseException(dynBean.getLocation(), "Invalid transform attribute '{}' specified", entry.getKey());
        }
    }

    private void processReserveNode(XmlDynamicBean parentBean, XmlDynamicBean reservedBean, TransformObject transformObject)
    {
        if(reservedBean.isTextNode())
        {
            if(processReserveText(reservedBean.getName(), reservedBean.getTextContent(), reservedBean.getLocation(), transformObject))
            {
                return;
            }
        }

        if(INCLUDE_RES.equals(reservedBean.getName()) || INCLUDE_FILE.equals(reservedBean.getName()))
        {
            transformObject.setInclude(parseInclude(reservedBean));
            return;
        }
		
        if(RES.equals(reservedBean.getName()))
        {
            transformObject.setResource(parseResource(reservedBean));
            return;
        }
        
        if(SWITCH.equals(reservedBean.getName()))
        {
        	// Switch should have child nodes as cases
        	List<XmlDynamicBean> caseNodes = reservedBean.getNodes();
        	
        	// Validation: At least one case is mandatory
        	if(caseNodes.isEmpty())
        	{
        		throw new TemplateParseException(reservedBean.getLocation(), 
        			"t:switch must have at least one t:case element");
        	}
        	
        	List<SwitchCase> parsedCases = new ArrayList<>();
        	boolean defaultCaseFound = false;
        	
        	for(int i = 0; i < caseNodes.size(); i++)
        	{
        		XmlDynamicBean caseNode = caseNodes.get(i);
        		
        		// Validation: Case nodes must be named "case"
        		if(!CASE.equals(caseNode.getName()))
        		{
        			throw new TemplateParseException(caseNode.getLocation(), 
        				"Switch case element must be named 't:case', but found: {}", caseNode.getName());
        		}
        		
        		Object condition = caseNode.getReserveValue(KEY_CONDITION);
        		
        		if(condition instanceof XmlDynamicBean)
        		{
        			condition = ((XmlDynamicBean) condition).getTextContent();
        			
        			if(condition == null)
        			{
        				throw new TemplateParseException(caseNode.getLocation(), 
            					"Encountered non-text condition node.");
        			}
        		}

                // Validation: Default case (no condition) should be at the end only
        		if(condition == null)
        		{
        			if(defaultCaseFound)
        			{
        				throw new TemplateParseException(caseNode.getLocation(), 
        					"Multiple default cases (cases without t:condition) found in t:switch. Only one default case is allowed and it must be at the end");
        			}
        			
        			// Check if this is not the last case
        			if(i < caseNodes.size() - 1)
        			{
        				throw new TemplateParseException(caseNode.getLocation(), 
        					"Default case (case without t:condition) must be the last case in t:switch");
        			}
        			
        			defaultCaseFound = true;
        		}
                else if(!(condition instanceof String))
                {
                    throw new TemplateParseException(caseNode.getLocation(), 
                        "Switch case condition must be a string, but found: {}", condition.getClass().getName());
                }
        		
        		Object value = caseNode.getReserveValue(KEY_VALUE);
        		
        		// Check for t:value attribute - t:value is mandatory
        		if(value != null)
        		{
        			value = parseObject(value, 
        				caseNode.getLocation());
        		}
                else
        		{
        			throw new TemplateParseException(caseNode.getLocation(), 
        				"Switch case must have t:value attribute specified");
        		}
        		
        		FreeMarkerTemplate conditionTemp = (condition == null) ? null : freeMarkerEngine.buildConditionTemplate("switch-condition", (String) condition);
        		parsedCases.add(new SwitchCase(reservedBean.getLocation(), conditionTemp, value));
        	}
        	
        	transformObject.setSwitchStatement(new Switch(reservedBean.getLocation(), parsedCases));
        	return;
        }
        
        // Check if the entry represents a set attr entry
        if(SET.equals(reservedBean.getName()))
        {
        	String attributeName = reservedBean.getAttributes().get("name");
        	
        	// if set is being done with text body
        	if(reservedBean.getTextContent() != null)
        	{
        		Object expression =  ExpressionUtils.parseExpression(this.freeMarkerEngine, reservedBean.getTextContent(), reservedBean.getLocation(), false);
        		
                transformObject.addField(new TransformObjectField(
                		reservedBean.getLocation(),
                		reservedBean.getName(), 
                        null,
                        attributeName, 
                        expression,
                        false
                    ).setType(FieldType.TEXT_CONTENT));
                return;
        	}

        	// if set is being done with sub xml content
            transformObject.addField(new TransformObjectField(
            		reservedBean.getLocation(),
            		reservedBean.getName(), 
                    null,
                    attributeName, 
                    parseObject(reservedBean, reservedBean.getLocation()),
                    false
                ));
            return;
        }

        // check if this is replaced entry
        if(REPLACE.equals(reservedBean.getName()))
        {
            transformObject.addField(new TransformObjectField(
            		reservedBean.getLocation(),
            		reservedBean.getName(), 
                    null,
                    null, 
                    parseObject(reservedBean, reservedBean.getLocation()),
                    true
                ));
            return;
        }
    }

    private void processAttributes(XmlDynamicBean dynBean, TransformObject transformObject)
    {
        Map<String, String> attrMap = dynBean.getAttributes();

        for(Map.Entry<String, String> entry : attrMap.entrySet())
        {
            transformObject.addField(new TransformObjectField(
            	dynBean.getLocation(),
                entry.getKey(), 
                null,
                null, 
                parseObject(entry.getValue(), dynBean.getLocation()),
                false
            ).setType(FieldType.ATTRIBUTE));
        }
    }

    private void processNode(XmlDynamicBean parentBean, XmlDynamicBean child, TransformObject transformObject)
    {
		Expression keyExpression = null;
		String keyExpressionStr = child.getReserveAttributes().get(NAME_EXPRESSION);
		
        if(StringUtils.isNotBlank(keyExpressionStr))
        {
            keyExpression = ExpressionUtils.parseExpression(this.freeMarkerEngine, keyExpressionStr, child.getLocation(), true);
        }
        
        transformObject.addField(new TransformObjectField(
        		child.getLocation(),
        		child.getName(), 
                keyExpression,
                null, 
                parseObject(child, child.getLocation()),
                false
            ).setType(FieldType.NODE));
    }

    private Object parseDynBean(XmlDynamicBean dynBean)
    {
        TransformObject transformObject = new TransformObject(dynBean.getLocation(), dynBean.getName());
        
        processReserveAttributes(dynBean, transformObject);
        processAttributes(dynBean, transformObject);
        
        for(XmlDynamicBean child : dynBean.getNodes())
        {
        	if(child.isReserved())
        	{
        		processReserveNode(dynBean, child, transformObject);
        		continue;
        	}
        	
        	processNode(dynBean, child, transformObject);
        }
        
        if(StringUtils.isNotBlank(dynBean.getTextContent()))
        {
            transformObject.addField(new TransformObjectField(
            		dynBean.getLocation(),
                    ITransformConstants.FIELD_TEXT_CONTENT, 
                    null,
                    null, 
                    parseObject(dynBean.getTextContent().trim(), dynBean.getLocation()),
                    false
                ).setType(FieldType.TEXT_CONTENT));
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
		
		try
		{
			// first template is kept on cache (before parsing) to avoid
			//   never ending recursion in case of recursive templates
			subTemplate = new TransformTemplate(path, XmlGenerator.class, null, location);
			includeCache.put(key, subTemplate);
			
			parseTemplateTo(content, subTemplate);
			
			return subTemplate;
		}catch(Exception ex)
		{
			throw new TemplateParseException(location, "Failed to parse xml {}: {}", 
					isResource ? "resource" : "file", 
					path, ex); 
		}
    }

	private Include parseInclude(XmlDynamicBean dynamicBean)
    {
        String includePath = dynamicBean.getAttributes().get("path");
        
        XmlDynamicBean paramsNode = (XmlDynamicBean) dynamicBean.getReserveNode(PARAMS);
		TransformObject paramsObj = null;

		if(paramsNode != null)
		{
			paramsObj = (TransformObject) parseObject(paramsNode, dynamicBean.getLocation());
		}
        
        boolean isResource = INCLUDE_RES.equals(dynamicBean.getName());
		
		TransformTemplate subTemplate = loadAndParse(dynamicBean.getLocation(), includePath, isResource);
        return new Include(dynamicBean.getLocation(), includePath, subTemplate, paramsObj);
    }

	private Resource parseResource(XmlDynamicBean dynamicBean)
    {
        String resPath = dynamicBean.getAttributes().get("path");
        String content = null;

        try
        {
            content = contentLoader.loadResource(resPath);
        }catch(Exception ex)
        {
            throw new TemplateParseException(dynamicBean.getLocation(), "Failed to load resource: {}", resPath, ex);			
        }

        boolean disableExpressions = "false".equalsIgnoreCase("" + dynamicBean.getAttributes().get(RES_PARAM_EXPR));
        String transformExpression = dynamicBean.getAttributes().get(TRANSFORM);

        XmlDynamicBean paramsNode = (XmlDynamicBean) dynamicBean.getReserveNode(PARAMS);
		Object paramsObj = null;

		if(paramsNode != null)
		{
			paramsObj = paramsNode.toSimpleMap();
		}

        return new Resource(
                dynamicBean.getLocation(),
                resPath, 
                content, 
                disableExpressions, 
                paramsObj, 
                transformExpression);
    }
}
