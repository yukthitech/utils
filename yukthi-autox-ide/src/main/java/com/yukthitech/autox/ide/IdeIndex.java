package com.yukthitech.autox.ide;

import java.util.TreeSet;

import org.springframework.stereotype.Service;

@Service
public class IdeIndex
{
	private TreeSet<FileDetails> files = new TreeSet<>();
	
	public void cleanFileIndex()
	{
		files.clear();
	}
	
	public void addFile(FileDetails file)
	{
		files.add(file);
	}
	
	public TreeSet<FileDetails> getFiles()
	{
		return files;
	}
}
