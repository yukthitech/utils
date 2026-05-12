package com.yukthitech.transform.template;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yukthitech.transform.ITransformConstants;
import com.yukthitech.utils.fmarker.FreeMarkerTemplate;

/**
 * Represents a transform template based on which new objects can be created
 * based on the input context object.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransformTemplate implements ITemplateObject
{
	private static final long serialVersionUID = 1L;

	public static abstract class TransformElement implements ITemplateObject
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

		@Override
		public void compile(TemplateCompileContext context)
		{
			// overridden by nodes that have compiled artifacts or children to compile
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
		private ITemplateObject listExpression;

		/**
		 * Variable name to be used to access the current value in the loop.
		 */
		private String loopVariable;

		/**
		 * FreeMarker condition source text for the loop body (not serializable artifact).
		 */
		private String conditionExpression;

		/**
		 * Condition to be evaluated to true for the object to be included in
		 * the result.
		 */
		private transient FreeMarkerTemplate condition;
		
		/**
		 * Name expression to be used for this for-each. Populated only in
		 * case map entry loops.
		 */
		private Expression nameExpression;

		public ForEachLoop(Location location, ITemplateObject listExpression, String loopVariable, String conditionExpression)
		{
			super(location);
			this.listExpression = listExpression;
			this.loopVariable = loopVariable;
			this.conditionExpression = conditionExpression;
		}

		@Override
		public void compile(TemplateCompileContext context)
		{
			if(StringUtils.isNotBlank(conditionExpression))
			{
				this.condition = context.getFreeMarkerEngine().buildConditionTemplate("for-each-condition", conditionExpression);
			}
			if(listExpression != null)
			{
				listExpression.compile(context);
			}
			if(nameExpression != null)
			{
				nameExpression.compile(context);
			}
		}

		public String getConditionExpression()
		{
			return conditionExpression;
		}
		
		public void setNameExpression(Expression nameExpression)
		{
			this.nameExpression = nameExpression;
		}
		
		public Expression getNameExpression()
		{
			return nameExpression;
		}

		public ITemplateObject getListExpression()
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
		 * Parsed expression object (FreeMarker template, XPath compiled form, JsonPath, etc.).
		 */
		private transient Object parsedExpression;

		public Expression(Location location, ExpressionType type, String expression, Object parsedExpression)
		{
			super(location);
			this.type = type;
			this.expression = expression;
			this.parsedExpression = parsedExpression;
		}

		void setParsedExpression(Object parsedExpression)
		{
			this.parsedExpression = parsedExpression;
		}

		@Override
		public void compile(TemplateCompileContext context)
		{
			ExpressionUtils.compileParsedExpression(context.getFreeMarkerEngine(), this);
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
		private ITemplateObject value;

		/**
		 * If true, in place of current entry in parent, the entries of this
		 * object will be included.
		 */
		private boolean replaceEntry;

		/**
		 * Type of field. Used by xml transformation only.
		 */
		private FieldType type;

		public TransformObjectField(Location location, String name, Expression nameExpression, String attributeName, ITemplateObject value, boolean replaceEntry)
		{
			super(location);
			
			this.name = name;
			this.nameExpression = nameExpression;
			this.attributeName = attributeName;
			this.value = value;
			this.replaceEntry = replaceEntry;
		}

		@Override
		public void compile(TemplateCompileContext context)
		{
			if(nameExpression != null)
			{
				nameExpression.compile(context);
			}
			if(value != null)
			{
				value.compile(context);
			}
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

		public ITemplateObject getValue()
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

		@Override
		public void compile(TemplateCompileContext context)
		{
			if(params != null)
			{
				params.compile(context);
			}
			if(content != null)
			{
				content.compile(context);
			}
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
		public void compile(TemplateCompileContext context)
		{
			for(SwitchCase switchCase : cases)
			{
				switchCase.compile(context);
			}
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
		 * FreeMarker condition source text. Null for the default switch branch.
		 */
		private String conditionExpression;

		/**
		 * Condition to be evaluated. If null, this is the default case.
		 */
		private transient FreeMarkerTemplate condition;

		/**
		 * Value to return if condition is true.
		 */
		private ITemplateObject value;

		public SwitchCase(Location location, String conditionExpression, ITemplateObject value)
		{
			super(location);
			this.conditionExpression = conditionExpression;
			this.value = value;
		}

		@Override
		public void compile(TemplateCompileContext context)
		{
			if(StringUtils.isNotBlank(conditionExpression))
			{
				this.condition = context.getFreeMarkerEngine().buildConditionTemplate("switch-condition", conditionExpression);
			}
			if(value != null)
			{
				value.compile(context);
			}
		}

		public String getConditionExpression()
		{
			return conditionExpression;
		}

		public FreeMarkerTemplate getCondition()
		{
			return condition;
		}

		public ITemplateObject getValue()
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
		 * FreeMarker condition source for object inclusion.
		 */
		private String conditionExpression;

		/**
		 * Condition to be evaluated to true for the object to be included in
		 * the result.
		 */
		private transient FreeMarkerTemplate condition;

		/**
		 * Value to be returned if the condition is evaluated to false.
		 */
		private ITemplateObject falseValue;

		/**
		 * Value to be returned if the condition is evaluated to true ( instead
		 * of returning full object itself, this value will be returned).
		 */
		private ITemplateObject value;
		
		/**
		 * Default value to be used, in case value results in exception. This is a means
		 * of evaluating expressions in safe manner with default value. 
		 */
		private ITemplateObject safeValue;

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

		public String getConditionExpression()
		{
			return conditionExpression;
		}

		TransformObject setConditionExpression(String conditionExpression)
		{
			this.conditionExpression = conditionExpression;
			return this;
		}

		public FreeMarkerTemplate getCondition()
		{
			return condition;
		}

		@Override
		public void compile(TemplateCompileContext context)
		{
			if(!context.beginTransformObject(this))
			{
				return;
			}
			try
			{
				if(StringUtils.isNotBlank(conditionExpression))
				{
					this.condition = context.getFreeMarkerEngine().buildConditionTemplate("transform-condition", conditionExpression);
				}
				if(forEachLoop != null)
				{
					forEachLoop.compile(context);
				}
				if(transformExpression != null)
				{
					transformExpression.compile(context);
				}
				if(include != null)
				{
					include.compile(context);
				}
				if(switchStatement != null)
				{
					switchStatement.compile(context);
				}
				for(TransformObjectField field : fields)
				{
					field.compile(context);
				}
				if(falseValue != null)
				{
					falseValue.compile(context);
				}
				if(value != null)
				{
					value.compile(context);
				}
				if(safeValue != null)
				{
					safeValue.compile(context);
				}
			} finally
			{
				context.endTransformObject(this);
			}
		}

		public ITemplateObject getFalseValue()
		{
			return falseValue;
		}

		TransformObject setFalseValue(ITemplateObject falseValue)
		{
			this.falseValue = falseValue;
			return this;
		}

		public ITemplateObject getValue()
		{
			return value;
		}

		TransformObject setValue(ITemplateObject value)
		{
			this.value = value;
			return this;
		}
		
		public ITemplateObject getSafeValue()
		{
			return safeValue;
		}
		
		TransformObject setSafeValue(ITemplateObject safeValue)
		{
			this.safeValue = safeValue;
			return this;
		}

		public Resource getResource()
		{
			return resource;
		}

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
		 * FreeMarker condition source for list inclusion (first list element {@code @condition:...}).
		 */
		private String conditionExpression;

		/**
		 * Condition to be evaluated to true for the list to be included in the
		 * result.
		 */
		private transient FreeMarkerTemplate condition;

		/**
		 * List of objects to be included in the result.
		 */
		private List<ITemplateObject> objects;

		public TransformList(Location location, String conditionExpression, List<ITemplateObject> objects)
		{
			super(location);
			this.conditionExpression = conditionExpression;
			this.objects = objects;
		}

		@Override
		public void compile(TemplateCompileContext context)
		{
			if(StringUtils.isNotBlank(conditionExpression))
			{
				this.condition = context.getFreeMarkerEngine().buildConditionTemplate("transform-list-condition", conditionExpression);
			}
			for(ITemplateObject object : objects)
			{
				if(object != null)
				{
					object.compile(context);
				}
			}
		}

		public String getConditionExpression()
		{
			return conditionExpression;
		}

		public FreeMarkerTemplate getCondition()
		{
			return condition;
		}

		public List<ITemplateObject> getObjects()
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
	private ITemplateObject root;

	/**
	 * Composer type to be used to compose final object.
	 */
	private Class<? extends IGenerator> generatorType;
	
	private Location location;

	public TransformTemplate(String name, Class<? extends IGenerator> generatorType, ITemplateObject root, Location location)
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

	void setRoot(ITemplateObject root)
	{
		this.root = root;
	}

	public ITemplateObject getRoot()
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
	public void compile(TemplateCompileContext context)
	{
		if(root == null)
		{
			return;
		}
		if(!context.beginTemplate(this))
		{
			return;
		}
		try
		{
			root.compile(context);
		} finally
		{
			context.endTemplate(this);
		}
	}
	
	@Override
	public String toString()
	{
		return ITransformConstants.toPrettyJson(this);
	}
}
