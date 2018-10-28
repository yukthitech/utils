package com.yukthitech.autox.ide.projpropdialog;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import com.yukthitech.autox.ide.IdeFileUtils;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.ui.BaseTreeNode;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class TestSuitesFolderTreeNode extends DefaultMutableTreeNode
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	File baseFolder;

	public File getFolder()
	{
		return baseFolder;
	}

	public TestSuitesFolderTreeNode(String name, File basefolder)
	{
		// TODO Auto-generated constructor stub
		super(name);
		this.baseFolder = basefolder;
		reload(false);
	}

	public TestSuitesFolderTreeNode(NodeData data, File basefolder)
	{
		super(data);
		this.baseFolder = basefolder;
		reload(false);
	}

	public void getSelectedFolders(Set<File> folders, Project project, File selectedParent)
	{
		Object userObject = super.getUserObject();
		boolean selected = false;
		
		if(userObject instanceof NodeData)
		{
			selected = ((NodeData) userObject).isChecked();
		
			if(selected)
			{
				if(selectedParent != null)
				{
					File projBaseFolder = new File(project.getBaseFolderPath());
					String curRelativePath = IdeFileUtils.getRelativePath(projBaseFolder, baseFolder);
					String parentRelativePath = IdeFileUtils.getRelativePath(projBaseFolder, selectedParent);
					
					throw new InvalidStateException("Multiple folders are selected from same hierarchy. [Parent: {}, Child: {}]", parentRelativePath, curRelativePath);
				}
				
				folders.add(baseFolder);
			}
		}

		// loop through the children
		int count = super.getChildCount();

		for(int i = 0; i < count; i++)
		{
			TestSuitesFolderTreeNode child = (TestSuitesFolderTreeNode) super.getChildAt(i);
			// call getSelectedFolders on each child
			child.getSelectedFolders(folders, project, selected ? baseFolder : selectedParent);
		}
	}

	public void reload(boolean reloadChild)
	{
		File[] files = null;
		
		try
		{
			files = baseFolder.getCanonicalFile().listFiles();
		} catch(IOException ex)
		{
			throw new InvalidStateException("An error occurred while loading cannoical file paths", ex);
		}
		
		if(files == null)
		{
			return;
		}

		BaseTreeNode existingNode = null;

		for(File file : files)
		{
			// existingNode = super.getChild(file.getPath());

			if(existingNode != null)
			{
				if(reloadChild)
				{
					existingNode.reload(true);
				}

				continue;
			}

			if(file.isDirectory())
			{
				TestSuitesFolderTreeNode folderTreeNode = new TestSuitesFolderTreeNode(new NodeData(file.getName()), file);
				super.add(folderTreeNode);
			}
		}

	}

	public void setSelectedFolder()
	{
		// TODO Auto-generated method stub
		Object object = super.getUserObject();
		((NodeData) object).setChecked(true);
	}

}
