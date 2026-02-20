package com.yukthitech.transform;

import com.yukthitech.transform.template.TransformUtils;
import com.yukthitech.transform.template.TransformTemplate.Expression;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

/**
 * Context to maintain service instances which are needed during
 * dynamic expression evaluation.
 */
public class InternalExpressionContext
{
	private static final ThreadLocal<InternalExpressionContext> instance = new ThreadLocal<>();
	
	private FreeMarkerEngine freeMarkerEngine;
	private ITransformContext context;
	private TransformState transformState;
	
	public static InternalExpressionContext getInstance()
	{
		return instance.get();
	}

	public static void push(FreeMarkerEngine freeMarkerEngine, ITransformContext context, TransformState transformState)
	{
		InternalExpressionContext newContext = new InternalExpressionContext();
		newContext.freeMarkerEngine = freeMarkerEngine;
		newContext.context = context;
		newContext.transformState = transformState;
		instance.set(newContext);
	}

	public static void pop()
	{
		instance.remove();
	}

	public Object evaluateExpression(String expressionStr)
	{
		Expression expression = TransformUtils.parseExpression(expressionStr, 
			transformState.getPath() + " (eval-expr)", false);
		return TransformUtils.processExpression(freeMarkerEngine, expression, context, transformState);
	}
}
