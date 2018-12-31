package com.yukthitech.autox.ide.projexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;

import com.yukthitech.autox.ide.IdeFileUtils;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.ui.BaseTreeNode;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class FolderTreeNode extends BaseTreeNode
{
	private static final long serialVersionUID = 1L;

	private File folder;

	private Project project;
	
	protected ProjectExplorer projectExplorer;
	
	protected FolderTreeNode(ProjectExplorer projectExplorer, Icon icon, Project project, String name, File folder)
	{
		super(projectExplorer.getProjectTreeModel(), icon, name);

		this.project = project;
		this.folder = folder;
		this.projectExplorer = projectExplorer;
		
		reload(false);
	}

	public FolderTreeNode(ProjectExplorer projectExplorer, Project project, String name, File folder)
	{
		this(projectExplorer, IdeUtils.loadIcon("/ui/icons/folder.png", 20), project, name, folder);
	}

	protected void removeNonExistingNodes()
	{
		Set<String> nodesToRemove = new HashSet<>();

		for(BaseTreeNode child : super.getChildNodes())
		{
			if(checkForRemoval(child))
			{
				nodesToRemove.add(child.getId());
			}
		}

		if(nodesToRemove != null)
		{
			super.removeChildNodes(nodesToRemove);

		}
	}
	
	protected boolean checkForRemoval(BaseTreeNode child)
	{
		File file = null;
		
		if(child instanceof FolderTreeNode)
		{
			file = ((FolderTreeNode) child).folder;
		}
		else
		{
			file = ((FileTreeNode) child).getFile();
		}

		if(!file.exists())
		{
			return true;
		}
		
		return false;
	}

	@Override
	public synchronized void reload(boolean childReload)
	{
		removeNonExistingNodes();

		File[] files = null;

		try
		{
			files = folder.getCanonicalFile().listFiles();
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while fetching cannoical children of folder: {}", folder.getPath(), ex);
		}

		if(files == null)
		{
			return;
		}

		List<File> list = new ArrayList<>(Arrays.asList(files));
		
		Collections.sort(list, new Comparator<File>()
		{
			@Override
			public int compare(File o1, File o2)
			{
				if(o1.isDirectory() != o2.isDirectory())
				{
					return o1.isDirectory() ? -1 : 1;
				}

				String name1 = o1.getName();
				String name2 = o2.getName();
				return name1.compareTo(name2);
			}

		});

		BaseTreeNode existingNode = null;
		int index = 0;

		for(File file : list)
		{
			if(file.getPath().startsWith("."))
			{
				continue;
			}
			
			existingNode = super.getChild(file.getPath());

			if(existingNode != null)
			{
				if(childReload)
				{
					existingNode.reload(true);
				}

				index++;
				continue;
			}

			if(file.isDirectory())
			{
				FolderTreeNode folderTreeNode = new FolderTreeNode(projectExplorer, project, file.getName(), file);
				super.insert(file.getPath(), folderTreeNode, index);
			}
			else if(file.isFile())
			{
				FileTreeNode fileNode = new FileTreeNode(projectExplorer, project, file.getName(), file, null);
				super.insert(file.getPath(), fileNode, index);
			}

			index++;
		}
		
		super.checkErrorStatus();
	}

	public File getFolder()
	{
		return folder;
	}

	public Project getProject()
	{
		return project;
	}
	
	public FileTreeNode getFileNode(File file)
	{
		String relativePath = IdeFileUtils.getRelativePath(folder, file);
		
		if(relativePath == null || relativePath.length() == 0)
		{
			return null;
		}
		
		String path[] = relativePath.split("\\" + File.separator);
		return getFileNode(file, path, 0);
	}
	
	public FileTreeNode getFileNode(File file, String path[], int index)
	{
		int count = super.getChildCount();
		
		if(count <= 0)
		{
			return null;
		}

		//from second level
		if(index > 0)
		{
			//before proceeding further, current folder matches with parent in path
			if(!folder.getName().equals(path[index - 1]))
			{
				return null;
			}
		}
		
		boolean immediateChild = ( index == (path.length - 1) );
		BaseTreeNode node = null;
		
		for(int i = 0; i < count; i++)
		{
			node = (BaseTreeNode) super.getChildAt(i);
			
			if(node instanceof FolderTreeNode)
			{
				if(immediateChild)
				{
					continue;
				}
				
				FolderTreeNode folderNode = (FolderTreeNode) node;
				FileTreeNode fileNode = folderNode.getFileNode(file, path, index + 1);
				
				if(fileNode != null)
				{
					return fileNode;
				}
			}
			
			if(!immediateChild)
			{
				continue;
			}
			
			if(node instanceof FileTreeNode)
			{
				FileTreeNode fileNode = (FileTreeNode) node;
				
				if(fileNode.getFile().getName().equals(path[index]))
				{
					return fileNode;
				}
			}
		}
		
		return null;
	}
}
