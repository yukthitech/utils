package com.yukthitech.autox.ide.projexplorer;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.ui.BaseTreeNode;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class FolderTreeNode extends BaseTreeNode
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(FolderTreeNode.class);

	private File folder;

	private Project project;

	public FolderTreeNode(Project project, String name, File folder)
	{
		super(IdeUtils.loadIcon("/ui/icons/folder.png", 20), name);

		this.project = project;
		this.folder = folder;
		reload(false);
	}

	private void removeNonExistingNodes()
	{
		File file = null;
		Set<String> nodesToRemove = new HashSet<>();

		for(BaseTreeNode child : super.getChildNodes())
		{
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
				nodesToRemove.add(child.getId());
			}
		}

		super.removeChildNodes(nodesToRemove);
	}

	@Override
	public synchronized void reload(boolean childReload)
	{
		logger.debug("Reloading folder: {}", folder.getPath() );
		
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

		BaseTreeNode existingNode = null;

		for(File file : files)
		{
			existingNode = super.getChild(file.getPath());

			if(existingNode != null)
			{
				if(childReload)
				{
					existingNode.reload(true);
				}
				
				continue;
			}

			if(file.isDirectory())
			{
				FolderTreeNode folderTreeNode = new FolderTreeNode(project, file.getName(), file);
				super.addChild(file.getPath(), folderTreeNode);
			}
		}
		
		for(File file : files)
		{
			existingNode = super.getChild(file.getPath());

			if(existingNode != null)
			{
				continue;
			}

			if(!file.isDirectory())
			{
				FileTreeNode fileNode = new FileTreeNode(project, file.getName(), file, null);
				super.addChild(file.getPath(), fileNode);

			}
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
