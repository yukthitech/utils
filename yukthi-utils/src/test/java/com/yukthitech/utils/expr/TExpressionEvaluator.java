/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.utils.expr;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.utils.CommonUtils;

/**
 * Test cases for expression evaluator
 * @author akiran
 */
public class TExpressionEvaluator
{
	private static Logger logger = LogManager.getLogger(TExpressionEvaluator.class);
	
	private ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();
	
	/**
	 * Parses specified expression and ensure specified expected tokens and result
	 * tokens are same.
	 * @param expression Expression to be parsed
	 * @param expectedTokens Expected tokens
	 */
	private void validateParse(String expression, String... expectedTokens)
	{
		List<Token> tokens = expressionEvaluator.tokenize(expression);
		
		logger.debug("Expression {} is parsed as - {}", expression, tokens);
		Assert.assertEquals( tokens, Token.tokens(expectedTokens), "For expression '" + expression + "' got tokens as - " + tokens);
	}
	
	/**
	 * Tests expression parsing logic.
	 */
	@Test
	public void testParseIntoTokens()
	{
		validateParse("3", "3");
		validateParse("var", "var");
		validateParse("3 *  4", "3", "*", "4");
		validateParse(" a.b + (3 + 3.4) * pop", "a.b", "+", "(", "3", "+", "3.4", ")", "*", "pop");
		validateParse(" a.b + IF(b > 3, 'aff', 'dd\"f')", "a.b", "+", "IF", "(", "b", ">", "3", ",", "aff",  ",", "dd\"f", ")");
		validateParse("a + 'd\\'f' + \"dfdf\\\"dfdf\" ", "a", "+", "d'f", "+", "dfdf\"dfdf");
	}
	
	/**
	 * Tests parsing into expression is proper.
	 */
	@Test
	public void testParseIntoExpression()
	{
		Assert.assertEquals("" + expressionEvaluator.parse("3 * 4"), "[3.0 * 4.0]");
		Assert.assertEquals("" + expressionEvaluator.parse("3 * 4 + 6"), "[[3.0 * 4.0] + 6.0]");
		Assert.assertEquals("" + expressionEvaluator.parse("a + (bc - 5)"), "[@a + [@bc - 5.0]]");
		Assert.assertEquals("" + expressionEvaluator.parse("abc(2  * 3,  5+6, 5.7) % 45"), "[abc([2.0 * 3.0], [5.0 + 6.0], 5.7) % 45.0]");
		Assert.assertEquals("" + expressionEvaluator.parse("abc(a(1), b(2*3), c(3), d(4+5) * 5)"), "abc(a(1.0), b([2.0 * 3.0]), c(3.0), [d([4.0 + 5.0]) * 5.0])");

		Assert.assertEquals("" + expressionEvaluator.parse("abc(a(1+(2*3)), b(), c((3*4)))"), "abc(a([1.0 + [2.0 * 3.0]]), b(), c([3.0 * 4.0]))");
	}
	
	/**
	 * Tests variable extraction is proper.
	 */
	@Test
	public void testVariableCollection()
	{
		Set<String> variables = expressionEvaluator.parse("a * b.x * c.x.y  ").getVariableNames();
		Assert.assertEquals(variables, CommonUtils.toSet("a", "b.x", "c.x.y"));
		
		variables = expressionEvaluator.parse("a * (b * MIN( c.f.g + 3.4)) + MAX(r.3 + 23345)").getVariableNames();
		Assert.assertEquals(variables, CommonUtils.toSet("a", "b", "c.f.g", "r.3"));
	}
	
	/**
	 * Tests expression type is evaluated properly.
	 */
	@Test
	public void testExpressionType()
	{
		ExpressionRegistry registry = new ExpressionRegistry();
		RegistryFactory.registerDefaults(registry);
		
		IVariableTypeProvider variableTypeProvider = new IVariableTypeProvider()
		{
			public Class<?> getVariableType(String name)
			{
				if(name.endsWith("Int"))
				{
					return Integer.class;
				}
				
				if(name.endsWith("Double"))
				{
					return Double.class;
				}

				return String.class;
			}
		};
		
		Class<?> expressionType = expressionEvaluator.parse("IF((3*4) > 10, 'val1', 'val2')").getExpressionType(variableTypeProvider, registry);
		Assert.assertEquals(expressionType, String.class);
		
		expressionType = expressionEvaluator.parse("(3* vInt) > 10").getExpressionType(variableTypeProvider, registry);
		Assert.assertEquals(expressionType, Boolean.class);

		expressionType = expressionEvaluator.parse("MAX(3,5, 6, 7.4, 2, 0) * kDouble / 20").getExpressionType(variableTypeProvider, registry);
		Assert.assertEquals(expressionType, Number.class);
		
		try
		{
			expressionEvaluator.parse("IS_BLANK(3.4)").getExpressionType(variableTypeProvider, registry);
			Assert.fail("Exception is not thrown.");
		}catch(Exception ex)
		{
			Assert.assertTrue(ex.getMessage().contains("String"));
		}

		try
		{
			expressionEvaluator.parse("AVG()").getExpressionType(variableTypeProvider, registry);
			Assert.fail("Exception is not thrown.");
		}catch(Exception ex)
		{
			Assert.assertTrue(ex.getMessage().contains("insufficient"));
		}
	}
	
	@Test
	public void testEvaluation()
	{
		ExpressionRegistry registry = new ExpressionRegistry();
		RegistryFactory.registerDefaults(registry);

		IVariableValueProvider variableValueProvider = new IVariableValueProvider()
		{
			@Override
			public Object getVariableValue(String name)
			{
				if("i".equals(name) || "j".equals(name))
				{
					return 10;
				}
				
				if("a".equals(name) || "b".equals(name))
				{
					return 20;
				}
				
				if(name.startsWith("str"))
				{
					return name;
				}

				return null;
			}
		};
		
		//test simple expressions
		Assert.assertEquals(expressionEvaluator.parse("3.0").evaluate(variableValueProvider, registry), (Double)3.0);
		Assert.assertEquals(expressionEvaluator.parse("a").evaluate(variableValueProvider, registry), 20);
		Assert.assertNotNull(expressionEvaluator.parse("RANDOM()").evaluate(variableValueProvider, registry));
		
		//test arithmetic operators
		Assert.assertEquals(expressionEvaluator.parse("3 * 4").evaluate(variableValueProvider, registry), (Double)12.0);
		Assert.assertEquals(expressionEvaluator.parse("(a * 2) + i").evaluate(variableValueProvider, registry), (Double)50.0);
		Assert.assertEquals(expressionEvaluator.parse("(a * b) / i").evaluate(variableValueProvider, registry), (Double)40.0);
		Assert.assertEquals(expressionEvaluator.parse("(a + 5) % i").evaluate(variableValueProvider, registry), (Double)5.0);
		
		//test comparison operators
		Assert.assertEquals(expressionEvaluator.parse("(a > i)").evaluate(variableValueProvider, registry), (Boolean)true);
		Assert.assertEquals(expressionEvaluator.parse("(a >= i)").evaluate(variableValueProvider, registry), (Boolean)true);
		Assert.assertEquals(expressionEvaluator.parse("(i < j)").evaluate(variableValueProvider, registry), (Boolean)false);
		Assert.assertEquals(expressionEvaluator.parse("(i <= j)").evaluate(variableValueProvider, registry), (Boolean)true);
		Assert.assertEquals(expressionEvaluator.parse("(i == j)").evaluate(variableValueProvider, registry), (Boolean)true);
		Assert.assertEquals(expressionEvaluator.parse("(i != a)").evaluate(variableValueProvider, registry), (Boolean)true);
		
		//test and and or operators
		Assert.assertEquals(expressionEvaluator.parse("(a > i) && (i > 20)").evaluate(variableValueProvider, registry), (Boolean)false);
		Assert.assertEquals(expressionEvaluator.parse("(a > i) || (i > 20)").evaluate(variableValueProvider, registry), (Boolean)true);
		
		//test functions
		Assert.assertEquals(expressionEvaluator.parse("IF(a > i, 30, 40.5)").evaluate(variableValueProvider, registry), (Double)30.0);
		Assert.assertEquals(expressionEvaluator.parse("IF(a < i, 30, 40.5)").evaluate(variableValueProvider, registry), (Double)40.5);
		
		Assert.assertEquals(expressionEvaluator.parse("IF(NOT(a < i), 30, 40.5)").evaluate(variableValueProvider, registry), (Double)30.0);
		Assert.assertEquals(expressionEvaluator.parse("NVL(xyz, 10, 20)").evaluate(variableValueProvider, registry), (Double)10.0);
		Assert.assertEquals(expressionEvaluator.parse("IS_BLANK(str4)").evaluate(variableValueProvider, registry), (Boolean)false);
		Assert.assertEquals(expressionEvaluator.parse("IS_BLANK(xyz)").evaluate(variableValueProvider, registry), (Boolean)true);
		Assert.assertEquals(expressionEvaluator.parse("IS_BLANK(' ')").evaluate(variableValueProvider, registry), (Boolean)true);
		
		Assert.assertEquals(expressionEvaluator.parse("MAX(3, 5.6, 1, 2)").evaluate(variableValueProvider, registry), (Double)5.6);
		Assert.assertEquals(expressionEvaluator.parse("MAX(2)").evaluate(variableValueProvider, registry), (Double)2.0);

		Assert.assertEquals(expressionEvaluator.parse("MIN(3, 5.6, 1, 2)").evaluate(variableValueProvider, registry), (Double)1.0);
		Assert.assertEquals(expressionEvaluator.parse("MIN(2)").evaluate(variableValueProvider, registry), (Double)2.0);

		Assert.assertEquals(expressionEvaluator.parse("AVG(4, 5, 6)").evaluate(variableValueProvider, registry), (Double)5.0);
		Assert.assertEquals(expressionEvaluator.parse("AVG(2)").evaluate(variableValueProvider, registry), (Double)2.0);

		Assert.assertEquals(expressionEvaluator.parse("SUM(4, 5, 6)").evaluate(variableValueProvider, registry), (Double)15.0);
		Assert.assertEquals(expressionEvaluator.parse("SUM(2)").evaluate(variableValueProvider, registry), (Double)2.0);
	}
}
