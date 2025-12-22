package com.yukthitech.transform.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yukthitech.transform.ITransformConstants;

/**
 * Represents a transform template based on which new objects 
 * can be created based on the input context object.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransformTemplate implements Serializable
{
	private static final long serialVersionUID = 1L;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Resource implements Serializable
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

		public Resource(String path, 
				String content,
				boolean expressionsDisabled, 
				Object resParams,
				String transformExpression) {
			this.path = path;
			this.content = content;
			this.expressionsDisabled = expressionsDisabled;
			this.resParams = resParams;
			this.transformExpression = transformExpression;
		}

		public String getPath() {
			return path;
		}

		public String getContent() {	
			return content;
		}

		public boolean isExpressionsDisabled() {
			return expressionsDisabled;
		}

		public Object getResParams() {
			return resParams;
		}

		public String getTransformExpression() {
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
	public static class ForEachLoop implements Serializable
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
		 * Condition to be evaluated to true for the object to be included in the result.
		 */
		private String condition;

		public ForEachLoop(Object listExpression, String loopVariable, String condition) {
			this.listExpression = listExpression;
			this.loopVariable = loopVariable;
			this.condition = condition;
		}

		public Object getListExpression() {
			return listExpression;
		}

		public String getLoopVariable() {
			return loopVariable;
		}

		public String getCondition() {
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
		FMARKER,
		XPATH,
		XPATH_MULTI,
		
		TEMPLATE,
		
		/**
		 * Static string.
		 */
		STRING
	}

	/**
	 * Represents an expression in the tree.
	 * Expression can be used as a key or a value as well.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Expression implements Serializable
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

		public Expression(ExpressionType type, String expression) {
			this.type = type;
			this.expression = expression;
		}

		public ExpressionType getType() {
			return type;
		}

		public String getExpression() {
			return expression;
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
	public static class TransformObjectField implements Serializable
	{
		private static final long serialVersionUID = 1L;
		
		/**
		 * Name of the field.
		 * Note: This can be an expression which will be evaluated to get the final field name
		 * in case of loop objects.
		 */
		private String name;

		/**
		 * Expression to be evaluated to get the final field name
		 * mainly used in case of loop objects.
		 */
		private Expression nameExpression;

		/**
		 * If specified, the current entry value will be set on the context with this attribute name.
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

		public TransformObjectField(String name, Expression nameExpression, String attributeName, Object value, boolean replaceEntry) {
			this.name = name;
			this.nameExpression = nameExpression;
			this.attributeName = attributeName;
			this.value = value;
			this.replaceEntry = replaceEntry;
		}

		public String getName() {
			return name;
		}

		public Expression getNameExpression() {
			return nameExpression;
		}

		public String getAttributeName() {
			return attributeName;
		}

		public Object getValue() {
			return value;
		}

		public boolean isReplaceEntry() {
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
	public static class Include implements Serializable
	{
		private static final long serialVersionUID = 1L;
		
		/**
		 * Path to the resource to be included.
		 */
		private String path;

		/**
		 * Content of the resource.
		 * Note: Ignored in json to avoid never-ending recursion.
		 */
		@JsonIgnore
		private TransformTemplate content;

		/**
		 * Parameters to be passed to the include.
		 */
		private TransformObject params;

		public Include(String path, TransformTemplate content, TransformObject params) {
			this.path = path;
			this.content = content;
			this.params = params;
		}

		public String getPath() {
			return path;
		}
		
		public TransformTemplate getContent() {
			return content;
		}

		public TransformObject getParams() {
			return params;
		}
	}

	/**
	 * Represents an object template in the tree.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class TransformObject implements Serializable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Name of node responsible for creation of this object.
		 * Used in xml (not in json).
		 */
		private String name;
		
		/**
		 * Path to the object.
		 */
		private String path;
		
		/**
		 * Condition to be evaluated to true for the object to be included in the result.
		 */
		private String condition;
		
		/**
		 * Value to be returned if the condition is evaluated to false.
		 */
		private Object falseValue;

		/**
		 * Value to be returned if the condition is evaluated to true (
		 * instead of returning full object itself, this value will be returned).
		 */
		private Object value;

		/**
		 * Resource to be included in place of this object.
		 */
		private Resource resource;

		/**
		 * For-each loop to a copy of this transform object will be evaluated
		 * for each value in the list.
		 * 
		 * If present, a copy of this transform object will be evaluated for each value in the list returned by the expression.
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
		 * Fields to be set on the object.
		 */
		private List<TransformObjectField> fields = new ArrayList<>();

		public TransformObject(String path) {
			this.path = path;
		}

		public TransformObject(String name, String path) {
			this.name = name;
			this.path = path;
		}
		
		public String getName()
		{
			return name;
		}

		public String getPath() {
			return path;
		}

		public String getCondition() {
			return condition;
		}

		TransformObject setCondition(String condition) {
			this.condition = condition;
			return this;
		}
		
		public Object getFalseValue() {
			return falseValue;
		}

		TransformObject setFalseValue(Object falseValue) {
			this.falseValue = falseValue;
			return this;
		}
		
		public Object getValue() {
			return value;
		}

		TransformObject setValue(Object value) {
			this.value = value;
			return this;
		}
		
		public Resource getResource() {
			return resource;
		}

		TransformObject setResource(Resource resource) {
			this.resource = resource;
			return this;
		}
		
		public ForEachLoop getForEachLoop() {
			return forEachLoop;
		}

		TransformObject setForEachLoop(ForEachLoop forEachLoop) {
			this.forEachLoop = forEachLoop;
			return this;
		}
		
		public Expression getTransformExpression() {
			return transformExpression;
		}

		TransformObject setTransformExpression(Expression transformExpression) {
			this.transformExpression = transformExpression;
			return this;
		}

		public Include getInclude() {
			return include;
		}

		TransformObject setInclude(Include include) {
			this.include = include;
			return this;
		}

		public List<TransformObjectField> getFields() {
			return fields;
		}

		TransformObject addField(TransformObjectField field) {
			this.fields.add(field);
			return this;
		}

		@Override
		public String toString()
		{
			return ITransformConstants.toPrettyJson(this);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class TransformList implements Serializable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Path to the list.
		 */
		private String path;

		/**
		 * Condition to be evaluated to true for the list to be included in the result.
		 */
		private String condition;
		
		/**
		 * List of objects to be included in the result.
		 */
		private List<Object> objects;

		public TransformList(String path, String condition, List<Object> objects) {
			this.path = path;
			this.condition = condition;
			this.objects = objects;
		}

		public String getPath() {
			return path;
		}

		public String getCondition() {
			return condition;
		}
		
		public List<Object> getObjects() {
			return objects;
		}

		@Override
		public String toString()
		{
			return ITransformConstants.toPrettyJson(this);
		}
	}
	
	/**
	 * Root object of the template.
	 */
	private Object root;
	
	/**
	 * Composer type to be used to compose final object.
	 */
	private Class<? extends IGenerator> generatorType;

	public TransformTemplate(Class<? extends IGenerator> generatorType, Object root) 
	{
		this.generatorType = generatorType;
		this.root = root;
	}
	
	void setRoot(Object root)
	{
		this.root = root;
	}

	public Object getRoot() {
		return root;
	}

	public Class<? extends IGenerator> getGeneratorType()
	{
		return generatorType;
	}

	@Override
	public String toString()
	{
		return ITransformConstants.toPrettyJson(this);
	}
}
