package com.yukthitech.transform;

import com.yukthitech.transform.event.ITransformListener;
import com.yukthitech.transform.template.ExpressionUtils;
import com.yukthitech.transform.template.TransformTemplate.Expression;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

/**
 * Context to maintain service instances which are needed during
 * dynamic expression evaluation like (eval method).
 */
public class InternalExpressionContext
{
	private static final ThreadLocal<InternalExpressionContext> instance = new ThreadLocal<>();
	
	private FreeMarkerEngine freeMarkerEngine;
	private ITransformContext context;
	private TransformState transformState;
	private ITransformListener listener;
	
	public static InternalExpressionContext getInstance()
	{
		return instance.get();
	}

	public static void push(FreeMarkerEngine freeMarkerEngine, ITransformContext context, 
		TransformState transformState, ITransformListener listener)
	{
		InternalExpressionContext newContext = new InternalExpressionContext();
		newContext.freeMarkerEngine = freeMarkerEngine;
		newContext.context = context;
		newContext.transformState = transformState;
		newContext.listener = listener;
		instance.set(newContext);
	}

	public static void pop()
	{
		instance.remove();
	}

	public Object evaluateExpression(String expressionStr)
	{
		Expression expression = ExpressionUtils.parseExpression(freeMarkerEngine, expressionStr, 
			transformState.getLocation(), false);
		return ExpressionUtils.processExpression(freeMarkerEngine, expression, context, listener);
	}
}
