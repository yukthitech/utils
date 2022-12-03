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
package com.yukthitech.validation.cross;

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
