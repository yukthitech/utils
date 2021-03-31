/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthitech.test.persitence;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.test.persitence.entity.lob.FileBlobEntity;
import com.yukthitech.test.persitence.entity.lob.FileClobEntity;
import com.yukthitech.test.persitence.entity.lob.IFileBlobRepository;
import com.yukthitech.test.persitence.entity.lob.IFileClobRepository;
import com.yukthitech.test.persitence.entity.lob.IObjBlobRepository;
import com.yukthitech.test.persitence.entity.lob.ObjBlobEntity;

/**
 * @author akiran
 *
 */
public class TBlobData extends TestSuiteBase
{
	private static Logger logger = LogManager.getLogger(TBlobData.class);

	@Override
	protected void initFactoryBeforeClass(RepositoryFactory factory)
	{
		IFileBlobRepository repo = factory.getRepository(IFileBlobRepository.class);
		repo.deleteAll();
	}

	@Override
	protected void cleanFactoryAfterClass(RepositoryFactory factory)
	{
		//cleanup the emp table
		factory.dropRepository(ObjBlobEntity.class);
		factory.dropRepository(FileBlobEntity.class);
		factory.dropRepository(FileClobEntity.class);
	}
	
	/**
	 * Tests object save and retrieve in blob
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testObjectBlobs(RepositoryFactory factory) throws Exception
	{
		List<String> values = new ArrayList<>(Arrays.asList("one", "two", "three"));
		
		ObjBlobEntity entity = new ObjBlobEntity(0, "Test", values);
		IObjBlobRepository repo = factory.getRepository(IObjBlobRepository.class);
		
		boolean res = repo.save(entity);
		
		Assert.assertEquals(res, true);
		
		ObjBlobEntity entityFromDb = repo.findByName("Test");
		Assert.assertEquals(entityFromDb.getValues(), values);
	}

	/**
	 * Tests file content storage in blob fields
	 * @param factory
	 * @throws Exception
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testFileBlobs(RepositoryFactory factory) throws Exception
	{
		//create temp file with some content
		StringBuilder content = new StringBuilder();
		
		for(int i = 0; i < 10000; i++)
		{
			content.append("Some temp content line - " + i).append("\n");
		}
		
		File tempFile = File.createTempFile("test", ".dat");
		FileUtils.writeStringToFile(tempFile, content.toString());
		logger.debug("Create temp file - {}", tempFile.getPath());
		
		
		//create entity to persist file content in blob
		FileBlobEntity entity = new FileBlobEntity(0, "TestFile1", tempFile);
		IFileBlobRepository repo = factory.getRepository(IFileBlobRepository.class);
		
		boolean res = repo.save(entity);
		
		Assert.assertEquals(res, true);
		
		FileBlobEntity entityFromDb = repo.findByName("TestFile1");
		File entityFile = entityFromDb.getFile();
		
		logger.debug("File obtained from db - {}", tempFile.getPath());
		
		//ensure actual file and db files are different
		Assert.assertNotEquals(tempFile.getName(), entityFile.getName());
		
		//ensure content from entity is good
		Assert.assertEquals(FileUtils.readFileToString(entityFile), content.toString());
		Assert.assertEquals(entity.getName(), "TestFile1");
	}

	/**
	 * Tests file content storage in clob fields
	 * @param factory
	 * @throws Exception
	 */
	@Test(dataProvider = "repositoryFactories")
	public void testFileClobs(RepositoryFactory factory) throws Exception
	{
		//create temp file with some content
		StringBuilder content = new StringBuilder();
		
		for(int i = 0; i < 10000; i++)
		{
			content.append("Some temp content line - " + i).append("\n");
		}
		
		File tempFile = File.createTempFile("test", ".dat");
		FileUtils.writeStringToFile(tempFile, content.toString());
		logger.debug("Create temp file - {}", tempFile.getPath());
		
		
		//create entity to persist file content in blob
		FileClobEntity entity = new FileClobEntity(0, "TestFile1", tempFile);
		IFileClobRepository repo = factory.getRepository(IFileClobRepository.class);
		
		boolean res = repo.save(entity);
		
		Assert.assertEquals(res, true);
		
		FileClobEntity entityFromDb = repo.findByName("TestFile1");
		File entityFile = entityFromDb.getFile();
		
		logger.debug("File obtained from db - {}", tempFile.getPath());
		
		//ensure actual file and db files are different
		Assert.assertNotEquals(tempFile.getName(), entityFile.getName());
		
		//ensure content from entity is good
		Assert.assertEquals(FileUtils.readFileToString(entityFile), content.toString());
		Assert.assertEquals(entity.getName(), "TestFile1");
		
		///////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////
		///Test for update
		//create temp file with some content
		content.setLength(0);
		
		for(int i = 0; i < 10000; i++)
		{
			content.append("Some temp content line - " + i).append("\n");
		}

		tempFile = File.createTempFile("test", ".dat");
		FileUtils.writeStringToFile(tempFile, content.toString());
		logger.debug("Create temp file for update - {}", tempFile.getPath());

		//perform update operation
		entity = new FileClobEntity(entity.getId(), "TestFile1", tempFile);
		
		res = repo.update(entity);
		Assert.assertEquals(res, true);
		
		entityFromDb = repo.findByName("TestFile1");
		entityFile = entityFromDb.getFile();
		
		logger.debug("File obtained from db - {}", tempFile.getPath());
		
		//ensure actual file and db files are different
		Assert.assertNotEquals(tempFile.getName(), entityFile.getName());
		
		//ensure content from entity is good
		Assert.assertEquals(FileUtils.readFileToString(entityFile), content.toString());
		Assert.assertEquals(entity.getName(), "TestFile1");
	}

	@Test(dataProvider = "repositoryFactories")
	public void testFinderWithBlobCollection(RepositoryFactory factory) throws Exception
	{
		List<String> values = new ArrayList<>(Arrays.asList("one", "two", "three"));
		
		ObjBlobEntity entity = new ObjBlobEntity(0, "Test_finder", values);
		IObjBlobRepository repo = factory.getRepository(IObjBlobRepository.class);
		
		boolean res = repo.save(entity);
		
		Assert.assertEquals(res, true);
		
		List<String> valuesFromDb = repo.findValuesByName("Test_finder");
		Assert.assertEquals(valuesFromDb, values);
	}
}
