package com.yukthitech.test.persitence.entity.lob;

import com.yukthitech.persistence.ICrudRepository;

public interface IFileClobRepository extends ICrudRepository<FileClobEntity>
{
	public FileClobEntity findByName(String name);
}
