package com.fw.test.persitence.entity.lob;

import com.yukthitech.persistence.ICrudRepository;

public interface IFileBlobRepository extends ICrudRepository<FileBlobEntity>
{
	public FileBlobEntity findByName(String name);
}
