package com.yukthitech.autox.ide.projexplorer;

import java.io.File;
import java.util.function.BiConsumer;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.ui.BaseTreeNode;

public class FileTreeNode extends BaseTreeNode 
{
	private static final long serialVersionUID = 1L;
	
	private File file;
	
	private long lastModifiedOn;
	
	private Project project;
	
	private BiConsumer<Project, File> fileReloadOp;

	public FileTreeNode(Project project, String name, File file, BiConsumer<Project, File> fileReloadOp)
	{
		if(project == null)
		{
			throw new NullPointerException("Project can not be null");
		}
		
		if(file == null)
		{
			throw new NullPointerException("File can not be null.");
		}
		
		this.project = project;
		this.fileReloadOp = fileReloadOp;
		
		super.setLabel(name);
		super.setIcon(IdeUtils.getFileIcon(file));
		this.file=file;
		
		reload(false);
	}
	
	@Override
	public synchronized void reload(boolean childReload)
	{
		if(fileReloadOp == null)
		{
			return;
		}
		
		long newLastModified = file.lastModified();
		
		if(lastModifiedOn == newLastModified)
		{
			return;
		}
		
		fileReloadOp.accept(project, file);
		this.lastModifiedOn = newLastModified;
	}
	
	public Project getProject()
	{
		return project;
	}
	
	public File getFile()
	{
		return file;
	}
}
