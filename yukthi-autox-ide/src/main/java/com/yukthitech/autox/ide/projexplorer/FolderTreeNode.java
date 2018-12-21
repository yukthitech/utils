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

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.ui.BaseTreeNode;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class FolderTreeNode extends BaseTreeNode
{
	private static final long serialVersionUID = 1L;

	private File folder;

	private Project project;
	
	private ProjectExplorer projectExplorer;
	
	protected FolderTreeNode(ProjectExplorer projectExplorer, Icon icon, Project project, String name, File folder)
	{
		super(icon, name);

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
	}

	public File getFolder()
	{
		return folder;
	}

	public Project getProject()
	{
		return project;
	}
}
