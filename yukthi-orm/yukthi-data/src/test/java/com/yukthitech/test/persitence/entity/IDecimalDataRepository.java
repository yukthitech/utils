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

import java.math.BigDecimal;
import java.util.List;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Operator;

public interface IDecimalDataRepository extends ICrudRepository<DecimalDataEntity>
{
	public List<DecimalDataEntity> fetchByAmountGreaterThan(@Condition(value = "amount", op = Operator.GT) BigDecimal amount);
	
	public List<DecimalDataEntity> fetchByTaxRateLessThan(@Condition(value = "taxRate", op = Operator.LT) BigDecimal taxRate);
	
	public DecimalDataEntity findByName(@Condition("name") String name);
}
