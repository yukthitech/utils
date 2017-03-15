package com.fw.test.persitence.entity.lob;

import java.util.List;

import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.repository.annotations.Field;

public interface IObjBlobRepository extends ICrudRepository<ObjBlobEntity>
{
	public ObjBlobEntity findByName(String name);
	
	@Field("values")
	public List<String> findValuesByName(String name);
}
