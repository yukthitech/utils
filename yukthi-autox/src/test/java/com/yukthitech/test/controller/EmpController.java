package com.yukthitech.test.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping("/emp")
public class EmpController
{
	private Map<Integer, Employee> empMap = new HashMap<>();
	
	private Map<Integer, File> fileMap = new HashMap<>();
	
	public EmpController()
	{
		System.out.println("=======> Initializing emp controller..");
	}
	
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	public Employee getEmplyoee(@PathVariable("id") int id, HttpServletResponse response)
	{
		if(!empMap.containsKey(id))
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
		
		return empMap.get(id);
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public SaveResult saveEmployee(@RequestBody Employee emp)
	{
		emp.setId(empMap.size() + 1);
		empMap.put(emp.getId(), emp);
		
		return new SaveResult("" + emp.getId());
	}
	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public void removeEmplyoee(@PathVariable("id") int id)
	{
		empMap.remove(id);
	}

	@RequestMapping(value = "/saveWithFile", method = RequestMethod.POST)
	public SaveResult saveWithFile(@RequestPart("details") Employee emp, MultipartHttpServletRequest servletRequest) throws Exception
	{
		emp.setId(empMap.size() + 1);
		empMap.put(emp.getId(), emp);

		MultipartFile file = servletRequest.getFile("file");
		File tempFile = File.createTempFile("emp", ".dat");
		
		FileOutputStream fos = new FileOutputStream(tempFile);
		IOUtils.copy(file.getInputStream(), fos);
		
		fos.flush();
		fos.close();
		
		fileMap.put(emp.getId(), tempFile);
		return new SaveResult("" + emp.getId());
	}
	
	@RequestMapping(value = "/getFile/{id}", method = RequestMethod.GET)
	public void getFileById(@PathVariable("id") Integer fileId, HttpServletResponse response) throws Exception
	{
		File file = fileMap.get(fileId);
		
		OutputStream os = response.getOutputStream();
		FileInputStream fis = new FileInputStream(file);
		IOUtils.copy(fis, os);
		
		os.flush();
		os.close();
		fis.close();
	}
	
}
