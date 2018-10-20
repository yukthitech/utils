package com.yukthitech.autox.ide.projpropdialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultTreeModel;

public class TestSuitesFolderTreeModel extends DefaultTreeModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TestSuitesFolderTreeNode rootNode;

	public TestSuitesFolderTreeModel(File file) {
		// TODO Auto-generated constructor stub
		super(new TestSuitesFolderTreeNode(file.getName(), file));
		rootNode = (TestSuitesFolderTreeNode) super.getRoot();
		// reload(file);

	}

	public Set<File> getSelectedFolders() {
		Set<File> res = new HashSet<File>();
		rootNode.getSelectedFolders(res);
		return res;
	}

	public void setSelectedFolders(Set<String> testSuitesFoldersList) {
		// TODO Auto-generated method stub
		traverse(rootNode, testSuitesFoldersList);
	}

	public void traverse(TestSuitesFolderTreeNode node, Set<String> testSuitesFolderList) {
		if (testSuitesFolderList != null) {
			if (testSuitesFolderList.contains(node.getFolder().getAbsolutePath())) {
				node.setSelectedFolder();
			}
			for (int i = 0; i < node.getChildCount(); i++) {
				traverse((TestSuitesFolderTreeNode) node.getChildAt(i), testSuitesFolderList);
			}
		}
	}

}
