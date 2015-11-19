package com.fw.test.persitence.entity.lob;

import com.yukthi.persistence.ICrudRepository;

public interface IFileClobRepository extends ICrudRepository<FileClobEntity>
{
	public FileClobEntity findByName(String name);
}
