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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintTarget;
import javax.validation.ConstraintValidator;
import javax.validation.MessageInterpolator;
import javax.validation.Payload;
import javax.validation.metadata.ConstraintDescriptor;

/**
 * @author akiran
 *
 */
public class CrossMessageInterpolatorContext implements MessageInterpolator.Context, ConstraintDescriptor<Annotation>
{
	private Object validatedValue;
	private Annotation annotation;
	private String messageTemplate;
	
	private Map<String, Object> attributes = new HashMap<>();
	
	public CrossMessageInterpolatorContext(Object validatedValue, ICrossConstraintValidator<?> validator)
	{
		this.validatedValue = validatedValue;
		this.annotation = validator.getAnnotation();
		this.messageTemplate = validator.getErrorMessage();

		Method methods[] = this.annotation.annotationType().getMethods();
		
		for(Method met : methods)
		{
			if(met.getParameterTypes().length > 0)
			{
				continue;
			}
			
			try
			{
				attributes.put(met.getName(), met.invoke(annotation));
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while fetching annotation values", ex);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.validation.MessageInterpolator.Context#getConstraintDescriptor()
	 */
	@Override
	public ConstraintDescriptor<?> getConstraintDescriptor()
	{
		return this;
	}

	/* (non-Javadoc)
	 * @see javax.validation.MessageInterpolator.Context#getValidatedValue()
	 */
	@Override
	public Object getValidatedValue()
	{
		return validatedValue;
	}

	/* (non-Javadoc)
	 * @see javax.validation.MessageInterpolator.Context#unwrap(java.lang.Class)
	 */
	@Override
	public <T1> T1 unwrap(Class<T1> type)
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.validation.metadata.ConstraintDescriptor#getAnnotation()
	 */
	@Override
	public Annotation getAnnotation()
	{
		return annotation;
	}

	/* (non-Javadoc)
	 * @see javax.validation.metadata.ConstraintDescriptor#getMessageTemplate()
	 */
	@Override
	public String getMessageTemplate()
	{
		return messageTemplate;
	}

	/* (non-Javadoc)
	 * @see javax.validation.metadata.ConstraintDescriptor#getGroups()
	 */
	@Override
	public Set<Class<?>> getGroups()
	{
		return Collections.emptySet();
	}

	/* (non-Javadoc)
	 * @see javax.validation.metadata.ConstraintDescriptor#getPayload()
	 */
	@Override
	public Set<Class<? extends Payload>> getPayload()
	{
		return Collections.emptySet();
	}

	/* (non-Javadoc)
	 * @see javax.validation.metadata.ConstraintDescriptor#getValidationAppliesTo()
	 */
	@Override
	public ConstraintTarget getValidationAppliesTo()
	{
		return ConstraintTarget.IMPLICIT;
	}

	/* (non-Javadoc)
	 * @see javax.validation.metadata.ConstraintDescriptor#getConstraintValidatorClasses()
	 */
	@Override
	public List<Class<? extends ConstraintValidator<Annotation, ?>>> getConstraintValidatorClasses()
	{
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see javax.validation.metadata.ConstraintDescriptor#getAttributes()
	 */
	@Override
	public Map<String, Object> getAttributes()
	{
		return attributes;
	}

	/* (non-Javadoc)
	 * @see javax.validation.metadata.ConstraintDescriptor#getComposingConstraints()
	 */
	@Override
	public Set<ConstraintDescriptor<?>> getComposingConstraints()
	{
		return Collections.emptySet();
	}

	/* (non-Javadoc)
	 * @see javax.validation.metadata.ConstraintDescriptor#isReportAsSingleViolation()
	 */
	@Override
	public boolean isReportAsSingleViolation()
	{
		return false;
	}

}
