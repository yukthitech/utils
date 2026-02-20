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
import com.yukthitech.transform.Conversions;
import com.yukthitech.transform.IContentLoader;
import com.yukthitech.transform.TransformException;
import com.yukthitech.transform.template.TransformTemplate.Expression;
import com.yukthitech.transform.template.TransformTemplate.FieldType;
import com.yukthitech.transform.template.TransformTemplate.ForEachLoop;
import com.yukthitech.transform.template.TransformTemplate.Include;
import com.yukthitech.transform.template.TransformTemplate.Resource;
import com.yukthitech.transform.template.TransformTemplate.Switch;
import com.yukthitech.transform.template.TransformTemplate.SwitchCase;
import com.yukthitech.transform.template.TransformTemplate.TransformList;
import com.yukthitech.transform.template.TransformTemplate.TransformObject;
import com.yukthitech.transform.template.TransformTemplate.TransformObjectField;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class XmlTemplateFactory implements ITemplateFactory
{
	public static Set<String> SUB_ATTR = Set.of(
			"name",
			"loopVar",
			"forEachCondition",
			"value",
			"condition"
		);

	private static final String PATH_SEP = "/";

    private static final String RES_PATH_SEP = "/t:";

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
    
    private XmlTemplateParserHandler parserHandler = new XmlTemplateParserHandler();
    
    /**
     * A cache of resource/file path to loaded template. This is maintained
     * to make template loading efficient (avoided repeated parsing) also needed
     * for recursive templates.
     */
    private Map<String, TransformTemplate> includeCache = new HashMap<>();

    public XmlTemplateFactory()
    {
    	this(DEFAULT_CONTENT_LOADER);
    	
    	parserHandler.setExpressionEnabled(false);
    }
    
    public XmlTemplateFactory(IContentLoader contentLoader)
    {
    	if(contentLoader == null)
    	{
    		contentLoader = DEFAULT_CONTENT_LOADER;
    	}
    	
        this.contentLoader = contentLoader;
    }

    public TransformTemplate parseTemplate(String templateContent) 
    {
    	XmlDynamicBean dynBean = (XmlDynamicBean) XMLBeanParser.parse(
    			new ByteArrayInputStream(templateContent.getBytes()), 
    			parserHandler);

        Object root = parseObject(dynBean, "");

        return new TransformTemplate(XmlGenerator.class, root);
    }
    
    private void parseTemplateTo(String templateContent, TransformTemplate template)
    {
    	XmlDynamicBean dynBean = (XmlDynamicBean) XMLBeanParser.parse(
    			new ByteArrayInputStream(templateContent.getBytes()), 
    			parserHandler);

        Object root = parseObject(dynBean, "/");
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
			else if(object instanceof XmlDynamicBean)
			{
				object = parseDynBean((XmlDynamicBean) object, path);
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

    private boolean processReserveText(String name, String value, String path, TransformObject transformObject)
    {
        if(KEY_CONDITION.equals(name))
        {
            transformObject.setCondition(value);
            return true;
        }

        if(KEY_FALSE_VALUE.equals(name))
        {
            Object falseValue = parseObject(value, path + RES_PATH_SEP + name);
            transformObject.setFalseValue(falseValue);
            return true;
        }

        if(KEY_VALUE.equals(name))
        {
            Object valueObj = parseObject(value, path + RES_PATH_SEP + name);
            transformObject.setValue(valueObj);
            return true;
        }

        if(TRANSFORM.equals(name)
            && StringUtils.isNotBlank(value))
        {
            transformObject.setTransformExpression(TransformUtils.parseExpression(value, path + RES_PATH_SEP + name, false));
            return true;
        }
        
        // Attributes which are handled in special way, ignore those reserve attr
        if(SUB_ATTR.contains(name))
        {
            return true;
        }

        return false;
    }
    
    private void processReserveAttributes(XmlDynamicBean dynBean, String path, TransformObject transformObject)
    {
        Map<String, String> reserveAttr = dynBean.getReserveAttributes();

        for(Map.Entry<String, String> entry : reserveAttr.entrySet())
        {
            if(FOR_EACH_EXPR.equals(entry.getKey()))
            {
                String loopVariable = reserveAttr.get(FOR_EACH_LOOP_VAR);
                Object listExpression = entry.getValue();
                String loopCondition = reserveAttr.get(KEY_FOR_EACH_CONDITION);
    
                transformObject.setForEachLoop(new ForEachLoop(listExpression, loopVariable, loopCondition));
                continue;
            }
    
            if(processReserveText(entry.getKey(), entry.getValue(), path, transformObject))
            {
                continue;
            }
        
            throw new InvalidStateException("Invalid transform attribute '{}' specified at path: {}", entry.getKey(), path);
        }
    }

    private void processReserveNode(XmlDynamicBean parentBean, XmlDynamicBean reservedBean, String path, TransformObject transformObject)
    {
        if(reservedBean.isTextNode())
        {
            if(processReserveText(reservedBean.getName(), reservedBean.getTextContent(), path, transformObject))
            {
                return;
            }
        }

        if(INCLUDE_RES.equals(reservedBean.getName()) || INCLUDE_FILE.equals(reservedBean.getName()))
        {
            transformObject.setInclude(parseInclude(reservedBean, path + PATH_SEP + reservedBean.getName()));
            return;
        }
		
        if(RES.equals(reservedBean.getName()))
        {
            transformObject.setResource(parseResource(reservedBean, path + PATH_SEP + reservedBean.getName()));
            return;
        }
        
        if(SWITCH.equals(reservedBean.getName()))
        {
        	// Switch should have child nodes as cases
        	List<XmlDynamicBean> caseNodes = reservedBean.getNodes();
        	
        	// Validation: At least one case is mandatory
        	if(caseNodes.isEmpty())
        	{
        		throw new TransformException(path + PATH_SEP + SWITCH, 
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
        			throw new TransformException(path + PATH_SEP + SWITCH + "[" + i + "]", 
        				"Switch case element must be named 't:case', but found: {}", caseNode.getName());
        		}
        		
        		Object condition = caseNode.getReserveValue(KEY_CONDITION);
        		
        		if(condition instanceof XmlDynamicBean)
        		{
        			condition = ((XmlDynamicBean) condition).getTextContent();
        			
        			if(condition == null)
        			{
        				throw new TransformException(path + PATH_SEP + SWITCH + "[" + i + "]", 
            					"Encountered non-text condition node.");
        			}
        		}

                // Validation: Default case (no condition) should be at the end only
        		if(condition == null)
        		{
        			if(defaultCaseFound)
        			{
        				throw new TransformException(path + PATH_SEP + SWITCH + "[" + i + "]", 
        					"Multiple default cases (cases without t:condition) found in t:switch. Only one default case is allowed and it must be at the end");
        			}
        			
        			// Check if this is not the last case
        			if(i < caseNodes.size() - 1)
        			{
        				throw new TransformException(path + PATH_SEP + SWITCH + "[" + i + "]", 
        					"Default case (case without t:condition) must be the last case in t:switch");
        			}
        			
        			defaultCaseFound = true;
        		}
                else if(!(condition instanceof String))
                {
                    throw new TransformException(path + PATH_SEP + SWITCH + "[" + i + "]", 
                        "Switch case condition must be a string, but found: {}", condition.getClass().getName());
                }
        		
        		Object value = caseNode.getReserveValue(KEY_VALUE);
        		
        		// Check for t:value attribute - t:value is mandatory
        		if(value != null)
        		{
        			value = parseObject(value, 
        				path + PATH_SEP + SWITCH + "[" + i + "]" + RES_PATH_SEP + KEY_VALUE);
        		}
                else
        		{
        			throw new TransformException(path + PATH_SEP + SWITCH + "[" + i + "]", 
        				"Switch case must have t:value attribute specified");
        		}
        		
        		parsedCases.add(new SwitchCase((String) condition, value));
        	}
        	
        	transformObject.setSwitchStatement(new Switch(parsedCases));
        	return;
        }
        
        // Check if the entry represents a set attr entry
        if(SET.equals(reservedBean.getName()))
        {
        	String attributeName = reservedBean.getAttributes().get("name");
        	
        	// if set is being done with text body
        	if(reservedBean.getTextContent() != null)
        	{
        		Object expression =  TransformUtils.parseExpression(reservedBean.getTextContent(), path + ">" + reservedBean.getName() + "[" + attributeName + "]", false);
        		
                transformObject.addField(new TransformObjectField(
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
            		reservedBean.getName(), 
                    null,
                    attributeName, 
                    parseObject(reservedBean, path + ">" + reservedBean.getName() + "[" + attributeName + "]"),
                    false
                ));
            return;
        }

        // check if this is replaced entry
        if(REPLACE.equals(reservedBean.getName()))
        {
            transformObject.addField(new TransformObjectField(
            		reservedBean.getName(), 
                    null,
                    null, 
                    parseObject(reservedBean, path + ">" + reservedBean.getName()),
                    true
                ));
            return;
        }
    }

    private void processAttributes(XmlDynamicBean dynBean, String path, TransformObject transformObject)
    {
        Map<String, String> attrMap = dynBean.getAttributes();

        for(Map.Entry<String, String> entry : attrMap.entrySet())
        {
            transformObject.addField(new TransformObjectField(
                entry.getKey(), 
                null,
                null, 
                parseObject(entry.getValue(), path + PATH_SEP + entry.getKey()),
                false
            ).setType(FieldType.ATTRIBUTE));
        }
    }

    private void processNode(XmlDynamicBean parentBean, XmlDynamicBean child, String path, TransformObject transformObject)
    {
		Expression keyExpression = null;
		String keyExpressionStr = child.getReserveAttributes().get(NAME_EXPRESSION);
		
        if(StringUtils.isNotBlank(keyExpressionStr))
        {
            keyExpression = TransformUtils.parseExpression(keyExpressionStr, path + RES_PATH_SEP + NAME_EXPRESSION, true);
        }
        
        transformObject.addField(new TransformObjectField(
        		child.getName(), 
                keyExpression,
                null, 
                parseObject(child, path + PATH_SEP + child.getName()),
                false
            ).setType(FieldType.NODE));
    }

    private Object parseDynBean(XmlDynamicBean dynBean, String path)
    {
        TransformObject transformObject = new TransformObject(dynBean.getName(), path);
        
        processReserveAttributes(dynBean, path, transformObject);
        processAttributes(dynBean, path, transformObject);
        
        for(XmlDynamicBean child : dynBean.getNodes())
        {
        	if(child.isReserved())
        	{
        		processReserveNode(dynBean, child, path, transformObject);
        		continue;
        	}
        	
        	processNode(dynBean, child, path, transformObject);
        }
        
        if(StringUtils.isNotBlank(dynBean.getTextContent()))
        {
            transformObject.addField(new TransformObjectField(
                    "@textContent", 
                    null,
                    null, 
                    parseObject(dynBean.getTextContent().trim(), path + PATH_SEP + "@textContent"),
                    false
                ).setType(FieldType.TEXT_CONTENT));
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
		
		try
		{
			// first template is kept on cache (before parsing) to avoid
			//   never ending recursion in case of recursive templates
			subTemplate = new TransformTemplate(XmlGenerator.class, null);
			includeCache.put(key, subTemplate);
			
			parseTemplateTo(content, subTemplate);
			
			return subTemplate;
		}catch(Exception ex)
		{
			throw new TransformException(path, "Failed to parse xml {}: {}", 
					isResource ? "resource" : "file", 
					path, ex); 
		}
    }

	private Include parseInclude(XmlDynamicBean dynamicBean, String path)
    {
        String includePath = dynamicBean.getAttributes().get("path");
        
        XmlDynamicBean paramsNode = (XmlDynamicBean) dynamicBean.getReserveNode(PARAMS);
		TransformObject paramsObj = null;

		if(paramsNode != null)
		{
			paramsObj = (TransformObject) parseObject(paramsNode, path + PATH_SEP + PARAMS);
		}
        
        boolean isResource = INCLUDE_RES.equals(dynamicBean.getName());
		
		TransformTemplate subTemplate = loadAndParse(includePath, isResource);
        return new Include(includePath, subTemplate, paramsObj);
    }

	private Resource parseResource(XmlDynamicBean dynamicBean, String path)
    {
        String resPath = dynamicBean.getAttributes().get("path");
        String content = null;

        try
        {
            content = contentLoader.loadResource(resPath);
        }catch(Exception ex)
        {
            throw new TransformException(path, "Failed to load resource: {}", resPath, ex);			
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
                resPath, 
                content, 
                disableExpressions, 
                paramsObj, 
                transformExpression);
    }
}
