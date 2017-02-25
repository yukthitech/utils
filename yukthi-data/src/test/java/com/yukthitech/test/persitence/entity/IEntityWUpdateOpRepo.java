package com.fw.test.persitence.entity;

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
