package com.yukthitech.test.persitence.entity;

import java.util.Date;
import java.util.List;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Operator;

public interface IDateDataRepository extends ICrudRepository<DateDataEntity>
{
	public List<DateDataEntity> fetchDataAfter(@Condition(value = "date", op = Operator.GT) Date afterDate);
	
	public List<DateDataEntity> fetchDataBefore(@Condition(value = "dateWithTime", op = Operator.LT) Date beforeDate);
}
