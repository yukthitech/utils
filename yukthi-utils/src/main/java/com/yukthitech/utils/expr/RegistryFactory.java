package com.yukthitech.utils.expr;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Factory to add default operators and functions to registry.
 * @author akiran
 */
public class RegistryFactory
{
	/**
	 * Registers default operators on the specified registry.
	 * @param registry Registry to which operators to be registered
	 */
	public static void registerDefaults(ExpressionRegistry registry)
	{
		registry.addOperator("+", new SimpleOperator("+", "Addition Operator", Number.class, Number.class)
		{
			@Override
			public Object evaluate(Object leftOperand, Object rightOperand)
			{
				return ((Number) leftOperand).doubleValue() + ((Number) rightOperand).doubleValue();
			}
		});

		registry.addOperator("-", new SimpleOperator("-", "Subtraction Operator", Number.class, Number.class)
		{
			@Override
			public Object evaluate(Object leftOperand, Object rightOperand)
			{
				return ((Number) leftOperand).doubleValue() - ((Number) rightOperand).doubleValue();
			}
		});

		registry.addOperator("*", new SimpleOperator("*", "Multiplication Operator", Number.class, Number.class)
		{
			@Override
			public Object evaluate(Object leftOperand, Object rightOperand)
			{
				return ((Number) leftOperand).doubleValue() * ((Number) rightOperand).doubleValue();
			}
		});

		registry.addOperator("/", new SimpleOperator("/", "Division Operator", Number.class, Number.class)
		{
			@Override
			public Object evaluate(Object leftOperand, Object rightOperand)
			{
				return ((Number) leftOperand).doubleValue() / ((Number) rightOperand).doubleValue();
			}
		});

		registry.addOperator("%", new SimpleOperator("%", "Remainder Operator", Number.class, Number.class)
		{
			@Override
			public Object evaluate(Object leftOperand, Object rightOperand)
			{
				return ((Number) leftOperand).doubleValue() % ((Number) rightOperand).doubleValue();
			}
		});
		
		//Add Conditional Operators

		registry.addOperator(">", new SimpleOperator(">", "Greater than comparision Operator", Number.class, Boolean.class)
		{
			@Override
			public Object evaluate(Object leftOperand, Object rightOperand)
			{
				return ((Number) leftOperand).doubleValue() > ((Number) rightOperand).doubleValue();
			}
		});

		registry.addOperator(">=", new SimpleOperator(">=", "Greater or equals comparision Operator", Number.class, Boolean.class)
		{
			@Override
			public Object evaluate(Object leftOperand, Object rightOperand)
			{
				return ((Number) leftOperand).doubleValue() >= ((Number) rightOperand).doubleValue();
			}
		});

		registry.addOperator("<", new SimpleOperator("<", "Lesser than comparision Operator", Number.class, Boolean.class)
		{
			@Override
			public Object evaluate(Object leftOperand, Object rightOperand)
			{
				return ((Number) leftOperand).doubleValue() < ((Number) rightOperand).doubleValue();
			}
		});

		registry.addOperator("<=", new SimpleOperator("<=", "Lesser or equals comparision Operator", Number.class, Boolean.class)
		{
			@Override
			public Object evaluate(Object leftOperand, Object rightOperand)
			{
				return ((Number) leftOperand).doubleValue() <= ((Number) rightOperand).doubleValue();
			}
		});

		registry.addOperator("==", new SimpleOperator("==", "Equals comparision Operator", Object.class, Boolean.class)
		{
			@Override
			public Object evaluate(Object leftOperand, Object rightOperand)
			{
				if((leftOperand instanceof Number) && (rightOperand instanceof Number))
				{
					return ((Number) leftOperand).doubleValue() == ((Number) rightOperand).doubleValue();
				}
				
				return leftOperand.equals(rightOperand);
			}
		});

		registry.addOperator("!=", new SimpleOperator("!=", "Not equals comparision Operator", Object.class, Boolean.class)
		{
			@Override
			public Object evaluate(Object leftOperand, Object rightOperand)
			{
				if((leftOperand instanceof Number) && (rightOperand instanceof Number))
				{
					return ((Number) leftOperand).doubleValue() != ((Number) rightOperand).doubleValue();
				}
				
				return !leftOperand.equals(rightOperand);
			}
		});
		
		//Join operators
		registry.addOperator("&&", new SimpleOperator("&&", "AND Operator", Boolean.class, Boolean.class)
		{
			@Override
			public Object evaluate(Object leftOperand, Object rightOperand)
			{
				return ((Boolean) leftOperand).booleanValue() && ((Boolean) rightOperand).booleanValue();
			}
		});

		registry.addOperator("||", new SimpleOperator("||", "OR Operator", Boolean.class, Boolean.class)
		{
			@Override
			public Object evaluate(Object leftOperand, Object rightOperand)
			{
				return ((Boolean) leftOperand).booleanValue() || ((Boolean) rightOperand).booleanValue();
			}
		});
		
		registerFunction(registry, DefaultFunctions.class);
	}
	
	public static void registerFunction(ExpressionRegistry registry, Class<?> clazz)
	{
		Method methods[] = clazz.getMethods();
		FunctionInfo functionInfo = null;
		
		for(Method method : methods)
		{
			if(!Modifier.isStatic(method.getModifiers()))
			{
				continue;
			}
			
			functionInfo = method.getAnnotation(FunctionInfo.class);
			
			if(functionInfo == null)
			{
				continue;
			}
			
			if(void.class.equals(method.getReturnType()))
			{
				continue;
			}
			
			registry.addFunction(functionInfo.name(), new SimpleJavaFunction(functionInfo, method));
		}
	}
}
