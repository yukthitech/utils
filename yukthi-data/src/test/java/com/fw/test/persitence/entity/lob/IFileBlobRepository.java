package com.fw.test.persitence.entity.lob;

import com.yukthi.persistence.ICrudRepository;

public interface IFileBlobRepository extends ICrudRepository<FileBlobEntity>
{
	public FileBlobEntity findByName(String name);
}
