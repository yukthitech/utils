package com.yukthitech.autox.ide.projpropdialog;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.ide.IdeFileUtils;
import com.yukthitech.autox.ide.model.Project;

public class ProjectPropTestSuiteFolderPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private JScrollPane scrollPane;
	private JTree tree;
	private Project project;

	private TestSuitesFolderTreeModel model;

	public ProjectPropTestSuiteFolderPanel()
	{
		setLayout(new BorderLayout(0, 0));
		add(getScrollPane());
	}

	private JScrollPane getScrollPane()
	{
		if(scrollPane == null)
		{
			scrollPane = new JScrollPane(getTree());
		}
		return scrollPane;
	}

	private JTree getTree()
	{
		if(tree == null)
		{
			tree = new JTree()
			{
				private static final long serialVersionUID = 1L;

				public boolean isPathEditable(TreePath path)
				{
					Object comp = path.getLastPathComponent();

					if(comp instanceof DefaultMutableTreeNode)
					{
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) comp;
						return !node.isRoot();
					}

					return false;
				}
			};
			tree.setBorder(new EmptyBorder(5, 5, 5, 5));

			CheckBoxTreeCellRenderer renderer = new CheckBoxTreeCellRenderer();
			tree.setCellRenderer(renderer);
			CheckBoxTreeCellEditor editor = new CheckBoxTreeCellEditor();
			setLayout(new BorderLayout(0, 0));
			tree.setCellEditor(editor);
			tree.setEditable(true);
		}

		return tree;
	}

	public void setProject(Project project)
	{
		this.project = project;
		model = new TestSuitesFolderTreeModel(new File(project.getBaseFolderPath()), project);
		
		if(CollectionUtils.isNotEmpty(project.getTestSuitesFoldersList()))
		{
			Set<File> testSuiteFolders = new HashSet<>();
			
			for(String path : project.getTestSuitesFoldersList())
			{
				try
				{
					testSuiteFolders.add(new File(project.getBaseFolderPath(), path).getCanonicalFile());
				} catch(IOException ex)
				{
					throw new IllegalStateException("An error occurred while getting cannoical path of folder: " + path, ex);
				}
			}
			
			model.setSelectedFolders(testSuiteFolders);
		}

		tree.setModel(model);
		super.setVisible(true);
	}

	public Set<String> saveChanges()
	{
		Set<String> set = new HashSet<>();
		Set<File> files = model.getSelectedFolders();

		for(File file : files)
		{
			set.add(IdeFileUtils.getRelativePath(new File(project.getBaseFolderPath()), file));
		}
		
		return set;
	}
}