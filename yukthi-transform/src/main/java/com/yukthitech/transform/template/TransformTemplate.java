package com.yukthitech.transform.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yukthitech.transform.ITransformConstants;
import com.yukthitech.utils.fmarker.FreeMarkerTemplate;

/**
 * Represents a transform template based on which new objects can be created
 * based on the input context object.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransformTemplate implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static abstract class TransformElement implements Serializable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Location where this element is defined.
		 */
		private Location location;

		public TransformElement(Location location)
		{
			this.location = location;
		}

		public Location getLocation()
		{
			return location;
		}

		public String toString()
		{
			return ITransformConstants.toPrettyJson(this);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Resource extends TransformElement
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Path to the resource to be included.
		 */
		private String path;

		/**
		 * Content of the resource.
		 */
		private String content;

		/**
		 * If true, expressions in the resource will not be evaluated.
		 */
		private boolean expressionsDisabled;

		/**
		 * Parameters to be passed to the resource.
		 */
		private Object resParams;

		/**
		 * Expression to be evaluated to transform the resource.
		 */
		private String transformExpression;

		public Resource(Location location, String path, String content, boolean expressionsDisabled, Object resParams, String transformExpression)
		{
			super(location);
			this.path = path;
			this.content = content;
			this.expressionsDisabled = expressionsDisabled;
			this.resParams = resParams;
			this.transformExpression = transformExpression;
		}

		public String getPath()
		{
			return path;
		}

		public String getContent()
		{
			return content;
		}

		public boolean isExpressionsDisabled()
		{
			return expressionsDisabled;
		}

		public Object getResParams()
		{
			return resParams;
		}

		public String getTransformExpression()
		{
			return transformExpression;
		}

		@Override
		public String toString()
		{
			return ITransformConstants.toPrettyJson(this);
		}
	}

	/**
	 * Represents a for-each loop in the tree.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ForEachLoop extends TransformElement
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Expression to be evaluated to get the list of values to loop through.
		 */
		private Object listExpression;

		/**
		 * Variable name to be used to access the current value in the loop.
		 */
		private String loopVariable;

		/**
		 * Condition to be evaluated to true for the object to be included in
		 * the result.
		 */
		private FreeMarkerTemplate condition;
		
		/**
		 * Name expression to be used for this for-each. Populated only in
		 * case map entry loops.
		 */
		private Expression nameExpression;

		public ForEachLoop(Location location, Object listExpression, String loopVariable, FreeMarkerTemplate condition)
		{
			super(location);
			this.listExpression = listExpression;
			this.loopVariable = loopVariable;
			this.condition = condition;
		}
		
		public void setNameExpression(Expression nameExpression)
		{
			this.nameExpression = nameExpression;
		}
		
		public Expression getNameExpression()
		{
			return nameExpression;
		}

		public Object getListExpression()
		{
			return listExpression;
		}

		public String getLoopVariable()
		{
			return loopVariable;
		}

		public FreeMarkerTemplate getCondition()
		{
			return condition;
		}

		@Override
		public String toString()
		{
			return ITransformConstants.toPrettyJson(this);
		}
	}

	public static enum ExpressionType
	{
		FMARKER, XPATH, XPATH_MULTI, JSON_PATH, JSON_PATH_MULTI,

		TEMPLATE,

		/**
		 * Static string.
		 */
		STRING
	}

	/**
	 * Represents an expression in the tree. Expression can be used as a key or
	 * a value as well.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Expression extends TransformElement
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Type of the expression.
		 */
		private ExpressionType type;

		/**
		 * Expression to be evaluated.
		 */
		private String expression;
		
		/**
		 * Parsed expression object.
		 */
		private Object parsedExpression;

		public Expression(Location location, ExpressionType type, String expression, Object parsedExpression)
		{
			super(location);
			this.type = type;
			this.expression = expression;
			this.parsedExpression = parsedExpression;
		}

		public ExpressionType getType()
		{
			return type;
		}

		public String getExpression()
		{
			return expression;
		}
		
		public Object getParsedExpression()
		{
			return parsedExpression;
		}

		@Override
		public String toString()
		{
			return ITransformConstants.toPrettyJson(this);
		}
	}

	public static enum FieldType
	{
		ATTRIBUTE,

		NODE,

		TEXT_CONTENT
	}

	/**
	 * Represents a field of an object.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class TransformObjectField extends TransformElement
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Name of the field. Note: This can be an expression which will be
		 * evaluated to get the final field name in case of loop objects.
		 */
		private String name;

		/**
		 * Expression to be evaluated to get the final field name mainly used in
		 * case of loop objects.
		 */
		private Expression nameExpression;

		/**
		 * If specified, the current entry value will be set on the context with
		 * this attribute name.
		 */
		private String attributeName;

		/**
		 * Value of the field. This can be a simple value or a complex object.
		 */
		private Object value;

		/**
		 * If true, in place of current entry in parent, the entries of this
		 * object will be included.
		 */
		private boolean replaceEntry;

		/**
		 * Type of field. Used by xml transformation only.
		 */
		private FieldType type;

		public TransformObjectField(Location location, String name, Expression nameExpression, String attributeName, Object value, boolean replaceEntry)
		{
			super(location);
			
			this.name = name;
			this.nameExpression = nameExpression;
			this.attributeName = attributeName;
			this.value = value;
			this.replaceEntry = replaceEntry;
		}

		public String getName()
		{
			return name;
		}

		public Expression getNameExpression()
		{
			return nameExpression;
		}

		public String getAttributeName()
		{
			return attributeName;
		}

		public Object getValue()
		{
			return value;
		}

		public boolean isReplaceEntry()
		{
			return replaceEntry;
		}

		TransformObjectField setType(FieldType type)
		{
			this.type = type;
			return this;
		}

		public FieldType getType()
		{
			return type;
		}

		@Override
		public String toString()
		{
			return ITransformConstants.toPrettyJson(this);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Include extends TransformElement
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Path to the resource to be included.
		 */
		private String path;

		/**
		 * Content of the resource. Note: Ignored in json to avoid never-ending
		 * recursion.
		 */
		@JsonIgnore
		private TransformTemplate content;

		/**
		 * Parameters to be passed to the include.
		 */
		private TransformObject params;

		public Include(Location location, String path, TransformTemplate content, TransformObject params)
		{
			super(location);
			this.path = path;
			this.content = content;
			this.params = params;
		}

		public String getPath()
		{
			return path;
		}

		public TransformTemplate getContent()
		{
			return content;
		}

		public TransformObject getParams()
		{
			return params;
		}
	}

	/**
	 * Represents a switch statement with multiple cases.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Switch extends TransformElement
	{
		private static final long serialVersionUID = 1L;

		/**
		 * List of case objects. Each case has an optional condition and a
		 * value.
		 */
		private List<SwitchCase> cases = new ArrayList<>();

		public Switch(Location location, List<SwitchCase> cases)
		{
			super(location);
			this.cases = cases;
		}

		public List<SwitchCase> getCases()
		{
			return cases;
		}

		@Override
		public String toString()
		{
			return ITransformConstants.toPrettyJson(this);
		}
	}

	/**
	 * Represents a single case in a switch statement.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class SwitchCase extends TransformElement
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Condition to be evaluated. If null, this is the default case.
		 */
		private FreeMarkerTemplate condition;

		/**
		 * Value to return if condition is true.
		 */
		private Object value;

		public SwitchCase(Location location, FreeMarkerTemplate condition, Object value)
		{
			super(location);
			this.condition = condition;
			this.value = value;
		}

		public FreeMarkerTemplate getCondition()
		{
			return condition;
		}

		public Object getValue()
		{
			return value;
		}

		@Override
		public String toString()
		{
			return ITransformConstants.toPrettyJson(this);
		}
	}

	/**
	 * Represents an object template in the tree.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class TransformObject extends TransformElement
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Name of node responsible for creation of this object. Used in xml
		 * (not in json).
		 */
		private String name;

		/**
		 * Condition to be evaluated to true for the object to be included in
		 * the result.
		 */
		private FreeMarkerTemplate condition;

		/**
		 * Value to be returned if the condition is evaluated to false.
		 */
		private Object falseValue;

		/**
		 * Value to be returned if the condition is evaluated to true ( instead
		 * of returning full object itself, this value will be returned).
		 */
		private Object value;
		
		/**
		 * Default value to be used, in case value results in exception. This is a means
		 * of evaluating expressions in safe manner with default value. 
		 */
		private Object safeValue;

		/**
		 * Resource to be included in place of this object.
		 */
		private Resource resource;

		/**
		 * For-each loop to a copy of this transform object will be evaluated
		 * for each value in the list.
		 * 
		 * If present, a copy of this transform object will be evaluated for
		 * each value in the list returned by the expression.
		 */
		private ForEachLoop forEachLoop;

		/**
		 * Expression to be evaluated to transform the result of this object.
		 */
		private Expression transformExpression;

		/**
		 * Include to be included in place of this object.
		 */
		private Include include;

		/**
		 * Switch statement to evaluate multiple conditions and return matching
		 * value.
		 */
		private Switch switchStatement;

		/**
		 * Fields to be set on the object.
		 */
		private List<TransformObjectField> fields = new ArrayList<>();
		
		/**
		 * Flag indicating if this is dummy object, which needs to be ignored 
		 * post processing.
		 */
		private boolean dummy = false;

		public TransformObject(Location location)
		{
			super(location);
		}

		public TransformObject(Location location, String name)
		{
			super(location);
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

		public FreeMarkerTemplate getCondition()
		{
			return condition;
		}

		TransformObject setCondition(FreeMarkerTemplate condition)
		{
			this.condition = condition;
			return this;
		}

		public Object getFalseValue()
		{
			return falseValue;
		}

		TransformObject setFalseValue(Object falseValue)
		{
			this.falseValue = falseValue;
			return this;
		}

		public Object getValue()
		{
			return value;
		}

		TransformObject setValue(Object value)
		{
			this.value = value;
			return this;
		}
		
		public Object getSafeValue()
		{
			return safeValue;
		}
		
		TransformObject setSafeValue(Object safeValue)
		{
			this.safeValue = safeValue;
			return this;
		}

		public Resource getResource()
		{
			return resource;		}

		TransformObject setResource(Resource resource)
		{
			this.resource = resource;
			return this;
		}

		public ForEachLoop getForEachLoop()
		{
			return forEachLoop;
		}

		TransformObject setForEachLoop(ForEachLoop forEachLoop)
		{
			this.forEachLoop = forEachLoop;
			return this;
		}

		public Expression getTransformExpression()
		{
			return transformExpression;
		}

		TransformObject setTransformExpression(Expression transformExpression)
		{
			this.transformExpression = transformExpression;
			return this;
		}

		public Include getInclude()
		{
			return include;
		}

		TransformObject setInclude(Include include)
		{
			this.include = include;
			return this;
		}

		public Switch getSwitchStatement()
		{
			return switchStatement;
		}

		TransformObject setSwitchStatement(Switch switchStatement)
		{
			this.switchStatement = switchStatement;
			return this;
		}

		public List<TransformObjectField> getFields()
		{
			return fields;
		}

		TransformObject addField(TransformObjectField field)
		{
			this.fields.add(field);
			return this;
		}
		
		TransformObject setDummy(boolean dummy)
		{
			this.dummy = dummy;
			return this;
		}
		
		public boolean isDummy()
		{
			return dummy;
		}

		@Override
		public String toString()
		{
			return ITransformConstants.toPrettyJson(this);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class TransformList extends TransformElement
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Condition to be evaluated to true for the list to be included in the
		 * result.
		 */
		private FreeMarkerTemplate condition;

		/**
		 * List of objects to be included in the result.
		 */
		private List<Object> objects;

		public TransformList(Location location, FreeMarkerTemplate condition, List<Object> objects)
		{
			super(location);
			this.condition = condition;
			this.objects = objects;
		}

		public FreeMarkerTemplate getCondition()
		{
			return condition;
		}

		public List<Object> getObjects()
		{
			return objects;
		}

		@Override
		public String toString()
		{
			return ITransformConstants.toPrettyJson(this);
		}
	}

	/**
	 * Name of the template.
	 */
	private String name;

	/**
	 * Root object of the template.
	 */
	private Object root;

	/**
	 * Composer type to be used to compose final object.
	 */
	private Class<? extends IGenerator> generatorType;
	
	private Location location;

	public TransformTemplate(String name, Class<? extends IGenerator> generatorType, Object root, Location location)
	{
		this.name = name;
		this.generatorType = generatorType;
		this.root = root;
		this.location = location;
	}

	public String getName()
	{
		return name;
	}

	void setRoot(Object root)
	{
		this.root = root;
	}

	public Object getRoot()
	{
		return root;
	}

	public Class<? extends IGenerator> getGeneratorType()
	{
		return generatorType;
	}
	
	public Location getLocation()
	{
		return location;
	}

	@Override
	public String toString()
	{
		return ITransformConstants.toPrettyJson(this);
	}
}
