package com.yukthitech.test.persitence.entity;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.UpdateFunction;
import com.yukthitech.persistence.repository.annotations.UpdateOperator;

public interface IEntityWVerRepo extends ICrudRepository<EntityWithVersion>
{
	@UpdateFunction
	public boolean incrementAge(@Field(value = "age", updateOp = UpdateOperator.ADD) int ageAdd, @Condition("id") long id);

	@UpdateFunction
	public boolean decrementAge(@Field(value = "age", updateOp = UpdateOperator.SUBTRACT) int ageAdd, @Condition("id") long id);
}
