package com.fw.test.persitence.entity.lob;

import com.yukthi.persistence.ICrudRepository;

public interface IObjBlobRepository extends ICrudRepository<ObjBlobEntity>
{
	public ObjBlobEntity findByName(String name);
}
