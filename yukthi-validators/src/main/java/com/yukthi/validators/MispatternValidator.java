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
package com.yukthi.validators;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.yukthi.validation.annotations.Mispattern;

/**
 * Validator for {@link Mispattern} constraint
 * @author akiran
 */
public class MispatternValidator implements ConstraintValidator<Mispattern, String> 
{
	/**
	 * Patterns to be compared
	 */
	private Pattern patterns[];
	
	@Override
	public void initialize(Mispattern constraintAnnotation)
	{
		String regexp[] = constraintAnnotation.regexp();
		
		//compile provided expression
		patterns = new Pattern[regexp.length];
		
		for(int i = 0; i < regexp.length; i++)
		{
			patterns[i] = Pattern.compile(regexp[i]);
		}
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{
		//if value is null
		if(value == null)
		{
			return true;
		}
		
		//if any of the specified pattern matches fail validation
		for(Pattern pattern : this.patterns)
		{
			if(pattern.matcher(value).matches())
			{
				return false;
			}
		}
		
		return true;
	}

}
