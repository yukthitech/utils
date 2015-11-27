package com.fw.test.persitence.entity;

import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.repository.annotations.Condition;
import com.yukthi.persistence.repository.annotations.Field;
import com.yukthi.persistence.repository.annotations.UpdateFunction;
import com.yukthi.persistence.repository.annotations.UpdateOperator;

public interface IEntityWUpdateOpRepo extends ICrudRepository<EntityWithUpdateOperator>
{
	@UpdateFunction
	public boolean incrementAge(@Field(value = "age", updateOp = UpdateOperator.ADD) int ageAdd, @Condition("id") long id);

	@UpdateFunction
	public boolean decrementAge(@Field(value = "age", updateOp = UpdateOperator.SUBTRACT) int ageAdd, @Condition("id") long id);
}
