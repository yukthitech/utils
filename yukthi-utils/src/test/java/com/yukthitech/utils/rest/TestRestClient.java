package com.yukthitech.utils.rest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yukthitech.utils.CommonUtils;

/**
 * Test cases for RestClient.
 * @author akranthikiran
 */
public class TestRestClient
{
	private static Logger logger = LogManager.getLogger(TestRestClient.class);
	
	/**
	 * A simple pojo for testing.
	 */
	public static class Post
	{
		private int userId;
		private int id;
		private String title;
		private String body;

		public int getUserId()
		{
			return userId;
		}

		public void setUserId(int userId)
		{
			this.userId = userId;
		}

		public int getId()
		{
			return id;
		}

		public void setId(int id)
		{
			this.id = id;
		}

		public String getTitle()
		{
			return title;
		}

		public void setTitle(String title)
		{
			this.title = title;
		}

		public String getBody()
		{
			return body;
		}

		public void setBody(String body)
		{
			this.body = body;
		}
		
		@Override
		public String toString()
		{
			return ToStringBuilder.reflectionToString(this);
		}
	}
	
	private static File tempFolder;
	
	private static MockServer mockServer;

	private RestClient localClient = new RestClient("http://localhost:9999/entity");

	private RestClient jsonPlaceholderClient = new RestClient("https://jsonplaceholder.typicode.com");
	
	private RestClient httpBinClient = new RestClient("https://httpbin.org");
	
	@BeforeClass
	public static void setup() throws Exception
	{
		tempFolder = new File("target/temp");
		
		if(tempFolder.exists())
		{
			FileUtils.deleteDirectory(tempFolder);
		}
		
		tempFolder.mkdirs();
		
		mockServer = new MockServer(9999);
		mockServer.start();
	}
	
	@AfterClass
	public static void cleanup() throws Exception
	{
		mockServer.stop();
	}

	/**
	 * An end to end flow where an object is Posted using POST. Read using GET. 
	 * Updated using PUT. Reconfirm the updates using GET. And finally use DELETE 
	 * to delte the object and use GET to ensure it is deleted.
	 */
	@Test
	public void testEndToEndFlow()
	{
		logger.debug("Testing end to end flow..");
		
		//Create post object
		Post post = new Post();
		post.setUserId(1);
		post.setTitle("Test Title");
		post.setBody("Test Body");
		
		//invoke post request to create new post
		PostRestRequest postRequest = new PostRestRequest("/posts");
		postRequest.setJsonBody(post);
		
		RestResult<Post> result = localClient.invokeJsonRequest(postRequest, Post.class);
		Assert.assertTrue(result.isSuccess());
		Assert.assertEquals(result.getStatusCode(), 201);
		
		Post createdPost = result.getValue();
		Assert.assertTrue(createdPost.getId() > 0);
		Assert.assertEquals(createdPost.getTitle(), "Test Title");
		Assert.assertEquals(createdPost.getBody(), "Test Body");
		
		//Read the created post and validate
		GetRestRequest getRequest = new GetRestRequest("/posts/{id}");
		getRequest.addPathVariable("id", Integer.toString(createdPost.getId()));
		
		result = localClient.invokeJsonRequest(getRequest, Post.class);
		Assert.assertTrue(result.isSuccess());
		Assert.assertEquals(result.getStatusCode(), 200);
		
		Post readPost = result.getValue();
		Assert.assertEquals(readPost.getId(), createdPost.getId());
		Assert.assertEquals(readPost.getTitle(), "Test Title");
		Assert.assertEquals(readPost.getBody(), "Test Body");

		//update the post
		PutRestRequest putRequest = new PutRestRequest("/posts/{id}");
		putRequest.addPathVariable("id", Integer.toString(createdPost.getId()));
		putRequest.setJsonBody( CommonUtils.toMap("title", "New Title", "body", "New Body") );
		
		result = localClient.invokeJsonRequest(putRequest, Post.class);
		Assert.assertTrue(result.isSuccess());
		Assert.assertEquals(result.getStatusCode(), 200);

		Post updatedPost = result.getValue();
		Assert.assertEquals(updatedPost.getId(), createdPost.getId());
		Assert.assertEquals(updatedPost.getTitle(), "New Title");
		Assert.assertEquals(updatedPost.getBody(), "New Body");

		//re-read the post and confirm updates
		result = localClient.invokeJsonRequest(getRequest, Post.class);
		Assert.assertTrue(result.isSuccess());
		Assert.assertEquals(result.getStatusCode(), 200);
		
		readPost = result.getValue();
		Assert.assertEquals(readPost.getId(), createdPost.getId());
		Assert.assertEquals(readPost.getTitle(), "New Title");
		Assert.assertEquals(readPost.getBody(), "New Body");
		
		//delete the post
		DeleteRestRequest deleteRequest = new DeleteRestRequest("/posts/{id}");
		deleteRequest.addPathVariable("id", Integer.toString(createdPost.getId()));
		
		RestResult<String> deleteResult = localClient.invokeRequest(deleteRequest);
		Assert.assertTrue(deleteResult.isSuccess());
		Assert.assertEquals(deleteResult.getStatusCode(), 204);
		
		//re-read the post and confirm it is deleted
		result = localClient.invokeJsonRequest(getRequest, Post.class);
		Assert.assertFalse(result.isSuccess());
		Assert.assertEquals(result.getStatusCode(), 404);
	}
	
	/**
	 * Tests the GET request with url parameters.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetWithUrlParams()
	{
		GetRestRequest getRequest = new GetRestRequest("/posts");
		getRequest.addParam("userId", "1");
		
		RestResult<List<Post>> result = jsonPlaceholderClient.invokeJsonRequestForList(getRequest, List.class, Post.class);
		Assert.assertTrue(result.isSuccess());
		Assert.assertEquals(result.getStatusCode(), 200);
		
		List<Post> posts = result.getValue();
		Assert.assertFalse(posts.isEmpty());
		
		posts.forEach(post -> Assert.assertEquals(post.getUserId(), 1));
	}
	
	/**
	 * Tests post request with form data.
	 */
	@Test
	public void testPostWithForm()
	{
		PostRestRequest request = new PostRestRequest("/post");
		request.addFormField("param1", "value1");
		request.addJsonFormField("user", CommonUtils.toMap("name", "testUser", "id", 123));
		
		RestResult<String> result = httpBinClient.invokeRequest(request);
		Assert.assertTrue(result.isSuccess());
		
		String response = result.getValue();
		Assert.assertTrue(response.contains("\"param1\": \"value1\""));
		Assert.assertTrue(response.contains("\"user\": \"{\\\"name\\\":\\\"testUser\\\",\\\"id\\\":123}\""));
	}
	
	/**
	 * Tests post request with attachments.
	 * @throws IOException
	 */
	@Test
	public void testPostWithAttachments() throws IOException
	{
		File file1 = new File(tempFolder, "test1.txt");
		FileUtils.write(file1, "some data 1", StandardCharsets.UTF_8);

		File file2 = new File(tempFolder, "test2.txt");
		FileUtils.write(file2, "some data 2", StandardCharsets.UTF_8);

		PostRestRequest request = new PostRestRequest("/post");
		
		request.addAttachment("file1", file1, "text/plain");
		request.addAttachment("file2", file2, "text/plain");
		
		RestResult<String> result = httpBinClient.invokeRequest(request);
		Assert.assertTrue(result.isSuccess());
		
		String response = result.getValue();
		Assert.assertTrue(response.contains("some data 1"));
		Assert.assertTrue(response.contains("some data 2"));
	}
	
	/**
	 * Tests multipart request.
	 */
	@Test
	public void testMultipart()
	{
		PostRestRequest request = new PostRestRequest("/post");
		
		request.addTextPart("textPart", "text value");
		request.addJsonPart("jsonPart", CommonUtils.toMap("name", "testUser", "id", 123));
		request.addBinaryPart("binaryPart", "test", "binary data".getBytes(), "text/plain");
		
		RestResult<String> result = httpBinClient.invokeRequest(request);
		Assert.assertTrue(result.isSuccess());
		
		String response = result.getValue();
		Assert.assertTrue(response.contains("text value"));
		Assert.assertTrue(response.contains("\"{\\\"name\\\":\\\"testUser\\\",\\\"id\\\":123}\""));
		Assert.assertTrue(response.contains("binary data"));
	}
	
	/**
	 * Tests different response handling mechanisms.
	 */
	@Test
	public void testResponseHandling()
	{
		//test simple string response
		GetRestRequest getRequest = new GetRestRequest("/get");
		RestResult<String> strResult = httpBinClient.invokeRequest(getRequest);
		Assert.assertTrue(strResult.isSuccess());
		Assert.assertTrue(strResult.getValue().contains("\"url\": \"https://httpbin.org/get\""));
		
		//test json to map conversion
		getRequest = new GetRestRequest("/json");
		ObjectMapper mapper = new ObjectMapper();
		JavaType mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
		
		RestResult<Map<String, Object>> mapResult = httpBinClient.invokeJsonRequest(getRequest, mapType);
		Assert.assertTrue(mapResult.isSuccess());
		
		Map<String, Object> map = mapResult.getValue();
		Assert.assertTrue(map.containsKey("slideshow"));
		
		//test custom response handling
		getRequest = new GetRestRequest("/image/png");
		
		RestResult<byte[]> byteResult = httpBinClient.invokeRequest(getRequest, (response) -> 
		{
			int statusCode = response.getCode();
			byte[] data = null;
			
			if(statusCode >= 200 && statusCode <= 299)
			{
				HttpEntity entity = response.getEntity();
				
				try
				{
					data = entity != null? EntityUtils.toByteArray(entity): null;
				}catch(Exception ex)
				{
					data = null;
				}
			}
			
			return new RestResult<byte[]>(data, statusCode, response);
		});
		
		Assert.assertTrue(byteResult.isSuccess());
		Assert.assertTrue(byteResult.getValue().length > 0);
	}
	
	/**
	 * Tests the failure status codes.
	 */
	@Test
	public void testFailureStatusCodes()
	{
		GetRestRequest getRequest = new GetRestRequest("/status/404");
		RestResult<String> result = httpBinClient.invokeRequest(getRequest);
		
		Assert.assertFalse(result.isSuccess());
		Assert.assertEquals(result.getStatusCode(), 404);
	}
}