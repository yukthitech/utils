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
package com.yukthitech.persistence.repository.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to bind result field to entity field in read queries. And also used to 
 * bind the field to update in update queries.
 * 
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface Field
{
	/**
	 * Name of the entity field
	 * @return name of the entity field
	 */
	public String value();
	
	/**
	 * Update operator to be used on target entity field value in conjunction with current field value.
	 * By default {@link UpdateOperator#NONE} is used, which would simply set current field value to the
	 * target entity field.
	 * @return Update operator to be used
	 */
	public UpdateOperator updateOp() default UpdateOperator.NONE;

	/**
	 * Specifies the type of relation update operation to be performed when this field represents a collection/relation.
	 * By default, no relation update is performed.
	 * @return Relation update operation type
	 */
	public RelationUpdateType relationUpdate() default RelationUpdateType.NONE;
}
