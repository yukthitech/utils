package com.fw.test.persitence;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fw.test.persitence.entity.IProjectRepository;
import com.fw.test.persitence.entity.Project;
import com.fw.test.persitence.entity.ProjectSearchResult;
import com.yukthi.persistence.repository.RepositoryFactory;
import com.yukthi.persistence.repository.annotations.Operator;
import com.yukthi.persistence.repository.search.SearchCondition;
import com.yukthi.persistence.repository.search.SearchQuery;
import com.yukthi.utils.CommonUtils;

/**
 * Test cases to test basic CRUD functionality
 * @author akiran
 */
public class TExtendedEntity extends TestSuiteBase
{
	private Long projIdWithFields;
	private Long projIdWithoutFields;
	
	@Override
	protected void initFactoryBeforeClass(RepositoryFactory factory)
	{
		IProjectRepository repo = factory.getRepository(IProjectRepository.class);

		repo.save(new Project("test1", CommonUtils.toMap("field1", 10, "field3", 100)));
		repo.save(new Project("test3", CommonUtils.toMap("field1", 30, "field3", 300)));
		
		Project proj = new Project("test2", CommonUtils.toMap("field1", 20, "field3", 200, "field2", 2000));
		repo.save(proj);
		
		projIdWithFields = proj.getId();
		
		//create project without fields
		proj = new Project("test4", null);
		repo.save(proj);
		
		projIdWithoutFields = proj.getId();
	}

	@Override
	protected void cleanFactoryAfterClass(RepositoryFactory factory)
	{
		//cleanup the emp table
		factory.dropRepository(Project.class);
	}
	
	@Test(dataProvider = "repositoryFactories")
	public void testAsEntityFields(RepositoryFactory factory)
	{
		IProjectRepository repo = factory.getRepository(IProjectRepository.class);
		
		Project proj = repo.findFullById(projIdWithFields, CommonUtils.toSet("field1"));
		Assert.assertEquals(proj.getName(), "test2");
		Assert.assertEquals(proj.getExtensions(), CommonUtils.toMap("field1", "20"));
		
		proj = repo.findFullById(projIdWithFields, CommonUtils.toSet("field1", "field2", "field3"));
		Assert.assertEquals(proj.getName(), "test2");
		Assert.assertEquals(proj.getExtensions(), CommonUtils.toMap("field1", "20", "field2", "2000", "field3", "200"));
		
		//check for project whter there are no fields
		proj = repo.findFullById(projIdWithoutFields, CommonUtils.toSet("field1", "field2", "field3"));
		Assert.assertEquals(proj.getName(), "test4");
		Assert.assertNull(proj.getExtensions());
	}

	@Test(dataProvider = "repositoryFactories")
	public void testAsResultFields(RepositoryFactory factory)
	{
		IProjectRepository repo = factory.getRepository(IProjectRepository.class);

		//test without extension fields
		List<ProjectSearchResult> results = repo.find(new SearchQuery(new SearchCondition("name", Operator.EQ, "test1")));
		
		Assert.assertEquals(results.size(), 1);
		Assert.assertEquals(results.get(0).getName(), "test1");
		Assert.assertEquals(results.get(0).getExtField3(), "100");
		Assert.assertTrue(results.get(0).getExtensionFields().isEmpty());

		//test with extension fields
		results = repo.find(new SearchQuery(
					new SearchCondition("name", Operator.EQ, "test2")
				).setAdditionalEntityFields(CommonUtils.toSet("extensions.field2", "extensions.field3"))
		);
		
		Assert.assertEquals(results.size(), 1);
		Assert.assertEquals(results.get(0).getName(), "test2");
		Assert.assertEquals(results.get(0).getExtField3(), "200");
		Assert.assertEquals(results.get(0).getExtensionFields().size(), 2);
		Assert.assertEquals(results.get(0).getExtensionFields().get("extensions.field2"), "2000");
		Assert.assertEquals(results.get(0).getExtensionFields().get("extensions.field3"), "200");
	}
	
	@Test(dataProvider = "repositoryFactories")
	public void testExtensionInCondition(RepositoryFactory factory)
	{
		IProjectRepository repo = factory.getRepository(IProjectRepository.class);

		//test without extension fields
		List<ProjectSearchResult> results = repo.find(new SearchQuery(new SearchCondition("extensions.field1", Operator.EQ, "10")));
		
		Assert.assertEquals(results.size(), 1);
		Assert.assertEquals(results.get(0).getName(), "test1");
		Assert.assertEquals(results.get(0).getExtField3(), "100");
	}
	
	@Test(dataProvider = "repositoryFactories")
	public void testUpdate(RepositoryFactory factory)
	{
		IProjectRepository repo = factory.getRepository(IProjectRepository.class);

		Project proj = new Project("updateTest1", CommonUtils.toMap("field3", 10, "field4", "20", "field5", "30"));
		repo.save(proj);
		
		ProjectSearchResult projFromDb = repo.findByName("updateTest1");
		Assert.assertEquals(projFromDb.getName(), "updateTest1");
		Assert.assertEquals(projFromDb.getExtField3(), "10");
		Assert.assertEquals(projFromDb.getExtField4(), "20");
		
		//perform update
		proj.setExtensions(CommonUtils.toMap("field3", 100, "field4", "200"));
		repo.update(proj);
		
		projFromDb = repo.findByName("updateTest1");
		
		Assert.assertEquals(projFromDb.getName(), "updateTest1");
		Assert.assertEquals(projFromDb.getExtField3(), "100");
		Assert.assertEquals(projFromDb.getExtField4(), "200");
		Assert.assertEquals(projFromDb.getExtField5(), "30");
	}
	
	@Test(dataProvider = "repositoryFactories")
	public void testDelete(RepositoryFactory factory)
	{
		IProjectRepository repo = factory.getRepository(IProjectRepository.class);

		Project proj = new Project("delTest1", CommonUtils.toMap("field3", 10, "field4", "20", "field5", "30"));
		repo.save(proj);
		
		Assert.assertTrue(repo.deleteById(proj.getId()));
	}
}
