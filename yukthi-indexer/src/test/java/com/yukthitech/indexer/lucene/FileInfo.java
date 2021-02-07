package com.yukthitech.indexer.lucene;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.yukthitech.indexer.IndexField;
import com.yukthitech.indexer.IndexType;

public class FileInfo
{
	@IndexField
	private String fileName;
	
	@IndexField
	private List<String> nameParts;
	
	@IndexField(value =  IndexType.ANALYZED)
	private String content;
	
	private long size;
	
	public FileInfo()
	{}
	
	public FileInfo(File file) throws Exception
	{
		this.fileName = file.getName();
		populateParts();
		
		this.content = FileUtils.readFileToString(file);
		this.size = file.length();
	}
	
	private void populateParts()
	{
		Pattern CAP_PATTERN = Pattern.compile("[A-Z\\W]");
		Matcher matcher = CAP_PATTERN.matcher(this.fileName);
		List<String> tokens = new ArrayList<String>();
		StringBuffer buffer = new StringBuffer();
		
		while(matcher.find())
		{
			matcher.appendReplacement(buffer, "");
			
			if(buffer.length() > 0)
			{
				tokens.add(buffer.toString());
				buffer.setLength(0);
			}
			
			buffer.append(matcher.group().toLowerCase());
		}
		
		matcher.appendTail(buffer);
		
		if(buffer.length() > 0)
		{
			tokens.add(buffer.toString());
		}
		
		this.nameParts = tokens;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public List<String> getNameParts()
	{
		return nameParts;
	}

	public void setNameParts(List<String> nameParts)
	{
		this.nameParts = nameParts;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public long getSize()
	{
		return size;
	}

	public void setSize(long size)
	{
		this.size = size;
	}
}
