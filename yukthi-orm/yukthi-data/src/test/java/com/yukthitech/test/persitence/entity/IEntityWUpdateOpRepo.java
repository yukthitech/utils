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
package com.yukthitech.test.persitence.entity;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.annotations.OrderBy;
import com.yukthitech.persistence.repository.annotations.OrderByField;
import com.yukthitech.persistence.repository.annotations.OrderByType;
import com.yukthitech.persistence.repository.annotations.UpdateFunction;
import com.yukthitech.persistence.repository.annotations.UpdateOperator;

public interface IEntityWUpdateOpRepo extends ICrudRepository<EntityWithUpdateOperator>
{
	@UpdateFunction
	public boolean incrementAge(@Field(value = "age", updateOp = UpdateOperator.ADD) int ageAdd, @Condition("id") long id);

	@UpdateFunction
	public boolean decrementAge(@Field(value = "age", updateOp = UpdateOperator.SUBTRACT) int ageAdd, @Condition("id") long id);
	
	@UpdateFunction
	@OrderBy(fields = @OrderByField(name = "id", type = OrderByType.DESC))
	public int updateAges(@Condition(value = "age", op = Operator.GE) int minAge, @Field(value = "age", updateOp = UpdateOperator.ADD) int val);
}
