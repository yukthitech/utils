package com.yukthitech.autox.ide.projpropdialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.ui.*;

public class ProjectPropertiesTestSuiteFolders extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JScrollPane scrollPane;
	private JTree tree;
	private Project project;

	private TestSuitesFolderTreeModel model;
	public ProjectPropertiesTestSuiteFolders() {
		setLayout(new BorderLayout(0, 0));
		add(getScrollPane());
		add(getTree());
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
		}
		return scrollPane;
	}

	private JTree getTree() {
		if (tree == null) {
			tree = new JTree() {
				public boolean isPathEditable(TreePath path) {
					Object comp = path.getLastPathComponent();
					if (comp instanceof DefaultMutableTreeNode) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) comp;
						Object userObject = node.getUserObject();
						if (userObject instanceof NodeData) {
							return true;
						}
					}
					return false;
				}
			};
			CheckBoxTreeCellRenderer renderer = new CheckBoxTreeCellRenderer();
			tree.setCellRenderer(renderer);
			CheckBoxTreeCellEditor editor = new CheckBoxTreeCellEditor();
			setLayout(new BorderLayout(0, 0));
			tree.setCellEditor(editor);
			tree.setEditable(true);
		}
		return tree;
	}

	public void setProject(Project project) {
		this.project = project;
		model = new TestSuitesFolderTreeModel(new File(project.getBaseFolderPath()));
		model.setSelectedFolders(project.getTestSuitesFoldersList());
//		model.setSelectedFolders(project.getTestSuitesFoldersList());
		tree.setModel(model);
		super.setVisible(true);
	}

	public void reload(File file) {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(file.getName()), child;
		File[] files = null;
		try {
			files = file.getCanonicalFile().listFiles();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (File f : files) {
			if (f.isDirectory()) {
				NodeData data = new NodeData(f.getName());
				child = new DefaultMutableTreeNode(data);
				rootNode.add(child);
				reload(f);

			}
		}

		tree.setModel(new DefaultTreeModel(rootNode));
	}
	public Set<String> saveChanges()
	{
		Set<String> set= new HashSet<>();
		Set<File> files = model.getSelectedFolders();
		for(File file:files)
		{
			set.add(file.getAbsolutePath());
		}
		return set;
		
	}
}