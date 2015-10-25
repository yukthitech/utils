/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.validation.cross;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yukthi.validation.annotations.FutureOrToday;
import com.yukthi.validation.annotations.GreaterThan;
import com.yukthi.validation.annotations.LessThan;
import com.yukthi.validation.annotations.LessThanEquals;
import com.yukthi.validation.annotations.MandatoryOption;
import com.yukthi.validation.annotations.MatchWith;
import com.yukthi.validation.annotations.Required;
import com.yukthi.validation.beans.GEBean;
import com.yukthi.validation.beans.GTBean;
import com.yukthi.validation.beans.LEBean;
import com.yukthi.validation.beans.LTBean;
import com.yukthi.validation.beans.MandatoryOptionBean;
import com.yukthi.validation.beans.MatchWithBean;
import com.yukthi.validation.beans.RequiredBean;
import com.yukthi.validation.beans.SimpleBean;
import com.yukthi.validators.GreaterThanEqualsValidator;

/**
 * Test cases for validators
 * @author akiran
 */
public class TValidators
{
	private static Logger logger = LogManager.getLogger(TValidators.class);
	
	private Validator validator;
	
	@BeforeClass
	public void setup()
	{
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}
	
	/**
	 * Invokes validations on specified "bean" and in case violation occurs
	 * ensures validation message contains "expectedMssg" 
	 * @param bean
	 * @param expectedMssg
	 * @return
	 */
	private boolean validate(Object bean, String expectedMssg)
	{
		logger.debug("Validating bean - " + bean);
		
		Set<ConstraintViolation<Object>> violations = validator.validate(bean);
		
		//loop through violation constraints
		for(ConstraintViolation<Object> violation : violations)
		{
			logger.debug("\tGot violation as - " + violation.getMessage());
			
			if(expectedMssg != null)
			{
				Assert.assertTrue(violation.getMessage().contains(expectedMssg), String.format("Expected '%s' but found - %s", expectedMssg, violation.getMessage()));
			}
		}
		
		return violations.isEmpty();
	}
	
	/**
	 * Creates a date object by adding "days" to current date
	 * @param days
	 * @return
	 */
	private Date getDate(int days)
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DATE, days);
		
		return calendar.getTime();
	}
	
	/**
	 * Validates {@link GreaterThanEqualsValidator} functionality
	 */
	@Test
	public void testGreaterThanOrEqualsValidator()
	{
		//number based validation
		Assert.assertFalse( validate(new GEBean(3, 10), "field2") );
		Assert.assertTrue( validate(new GEBean(10, 3), null) );
		Assert.assertTrue( validate(new GEBean(3, 3), null) );

		//date based validation
		Assert.assertFalse( validate(new GEBean(getDate(3), getDate(5)), "field4") );
		Assert.assertTrue( validate(new GEBean(getDate(5), getDate(3)), null) );
		Assert.assertTrue( validate(new GEBean(getDate(5), getDate(5)), null) );
	}

	/**
	 * Validates {@link FutureOrToday} functionality
	 */
	@Test
	public void testFutureOrTodayValidator()
	{
		//future or today validations
		Assert.assertTrue( validate(new GEBean(getDate(3), null), null) );
		Assert.assertTrue( validate(new GEBean(getDate(0), null), null) );
		Assert.assertFalse( validate(new GEBean(getDate(-1), null), null) );
	}

	/**
	 * Validate {@link GreaterThan} constraint functionality
	 */
	@Test
	public void testGreaterThanValidator()
	{
		//number based validation
		Assert.assertFalse( validate(new GTBean(3, 10), "field2") );
		Assert.assertTrue( validate(new GTBean(10, 3), null) );
		Assert.assertFalse( validate(new GTBean(3, 3), "field2") );

		//date based validation
		Assert.assertFalse( validate(new GTBean(getDate(3), getDate(5)), "field4") );
		Assert.assertTrue( validate(new GTBean(getDate(5), getDate(3)), null) );
		Assert.assertFalse( validate(new GTBean(getDate(5), getDate(5)), "field4") );
	}

	/**
	 * Validate {@link LessThan} constraint functionality
	 */
	@Test
	public void testLessThanValidator()
	{
		//number based validation
		Assert.assertFalse( validate(new LTBean(10, 3), "field2") );
		Assert.assertTrue( validate(new LTBean(3, 10), null) );
		Assert.assertFalse( validate(new LTBean(3, 3), "field2") );

		//date based validation
		Assert.assertFalse( validate(new LTBean(getDate(5), getDate(3)), "field4") );
		Assert.assertTrue( validate(new LTBean(getDate(3), getDate(5)), null) );
		Assert.assertFalse( validate(new LTBean(getDate(5), getDate(5)), "field4") );
	}

	/**
	 * Validates {@link LessThanEquals} functionality
	 */
	@Test
	public void testLessThanOrEqualsValidator()
	{
		//number based validation
		Assert.assertFalse( validate(new LEBean(10, 3), "field2") );
		Assert.assertTrue( validate(new LEBean(3, 10), null) );
		Assert.assertTrue( validate(new LEBean(3, 3), null) );

		//date based validation
		Assert.assertFalse( validate(new LEBean(getDate(5), getDate(3)), "field4") );
		Assert.assertTrue( validate(new LEBean(getDate(3), getDate(5)), null) );
		Assert.assertTrue( validate(new LEBean(getDate(5), getDate(5)), null) );
	}

	/**
	 * Validates {@link MandatoryOption} functionality
	 */
	@Test
	public void testMandatoryOptionValidator()
	{
		//number based validation
		Assert.assertFalse( validate(new MandatoryOptionBean(null, null, null, getDate(3)), null) );
		Assert.assertTrue( validate(new MandatoryOptionBean(10, null, null, getDate(3)), null) );
		Assert.assertTrue( validate(new MandatoryOptionBean(null, 2.3f, null, getDate(3)), null) );
		Assert.assertTrue( validate(new MandatoryOptionBean(null, null, getDate(-2), getDate(3)), null) );
		Assert.assertTrue( validate(new MandatoryOptionBean(10, 2.3f, getDate(3), getDate(3)), null) );
	}

	/**
	 * Validates {@link MatchWith} functionality
	 */
	@Test
	public void testMatchWithValidator()
	{
		//number based validation
		Assert.assertTrue( validate(new MatchWithBean(null, null, null, null), null) );
		Assert.assertTrue( validate(new MatchWithBean(10, 10, null, null), null) );
		Assert.assertTrue( validate(new MatchWithBean(null, null, getDate(3), getDate(3)), null) );
		Assert.assertTrue( validate(new MatchWithBean(10, 10, getDate(3), getDate(3)), null) );

		Assert.assertFalse( validate(new MatchWithBean(10, 20, getDate(3), getDate(3)), "field2") );
		Assert.assertFalse( validate(new MatchWithBean(10, 10, getDate(4), getDate(3)), "field4") );
		Assert.assertFalse( validate(new MatchWithBean(10, null, getDate(4), getDate(4)), "field2") );
		Assert.assertFalse( validate(new MatchWithBean(10, 10, getDate(4), null), "field4") );
	}

	/**
	 * Tests simple field level validations
	 */
	@Test
	public void testSimpleValidations()
	{
		//max len validation
		Assert.assertFalse( validate(new SimpleBean("123456", null, null), null), "5" );
		Assert.assertTrue( validate(new SimpleBean("123", null, null), null), null );
		
		//min len validation
		Assert.assertFalse( validate(new SimpleBean(null, "12", null), null), "3" );
		Assert.assertTrue( validate(new SimpleBean(null, "123456789", null), null), null );
		
		//mispattern field
		Assert.assertFalse( validate(new SimpleBean(null, null, "sds34"), null), null );
		Assert.assertFalse( validate(new SimpleBean(null, null, "34433"), null), null );
		Assert.assertFalse( validate(new SimpleBean(null, null, "344dfdf"), null), null );
		Assert.assertTrue( validate(new SimpleBean(null, null, "AD43"), null), null );
		Assert.assertTrue( validate(new SimpleBean(null, null, "AD"), null), null );

		//empty validation
		Assert.assertFalse( validate(new SimpleBean("", Arrays.asList("123", "34")), null), null );
		Assert.assertFalse( validate(new SimpleBean("  ", Arrays.asList("123", "34")), null), null );
		Assert.assertFalse( validate(new SimpleBean(" df ", null), null), null );
		Assert.assertFalse( validate(new SimpleBean(" df ", new ArrayList<>()), null), null );
		Assert.assertTrue( validate(new SimpleBean(" df ", Arrays.asList("34")), null), null );
		
		//past or current date
		Assert.assertTrue( validate(new SimpleBean(getDate(0)), null), null );
		Assert.assertTrue( validate(new SimpleBean(getDate(-5)), null), null );
		Assert.assertFalse( validate(new SimpleBean(getDate(5)), null), null );
	}
	
	/**
	 * Tests {@link Required} constraint
	 */
	@Test
	public void testRequiredValidator()
	{
		Assert.assertTrue( validate(new RequiredBean("123456", 20), null), null );
		Assert.assertFalse( validate(new RequiredBean("  ", 20), null), null );
		Assert.assertFalse( validate(new RequiredBean(null, 20), null), null );
		Assert.assertFalse( validate(new RequiredBean("ew", 0), null), null );
	}	
}
