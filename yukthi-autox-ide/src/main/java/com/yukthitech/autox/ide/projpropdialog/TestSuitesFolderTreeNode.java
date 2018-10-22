package com.yukthitech.autox.ide.projpropdialog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.ui.BaseTreeNode;

public class TestSuitesFolderTreeNode extends DefaultMutableTreeNode
{
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

	public void getSelectedFolders(Set<File> folders)
	{
		Object userObject = super.getUserObject();
		if(userObject instanceof NodeData)
		{
			boolean selected = ((NodeData) userObject).isChecked();
			if(selected)
			{
				folders.add(baseFolder);
			}
		}

		// loop through the children
		int count = super.getChildCount();

		for(int i = 0; i < count; i++)
		{
			TestSuitesFolderTreeNode child = (TestSuitesFolderTreeNode) super.getChildAt(i);
			// call getSelectedFolders on each child
			child.getSelectedFolders(folders);
		}
	}

	public void reload(boolean reloadChild)
	{
		File[] files = null;
		try
		{
			files = baseFolder.getCanonicalFile().listFiles();

		} catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				// super.addChild(file.getPath(), folderTreeNode);
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
