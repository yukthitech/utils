package com.yukthitech.autox.ide.actions;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.ProjectExplorer;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.editor.FileEditorTabbedPane;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironmentManager;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;
import com.yukthitech.autox.ide.model.Project;

@ActionHolder
public class RunActions
{
	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private ProjectExplorer projectExplorer;
	
	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;
	
	@Autowired
	private ExecutionEnvironmentManager executionEnvironmentManager;
	
	@Action
	public void runTestSuite()
	{
		FileEditor fileEditor = fileEditorTabbedPane.getCurrentFileEditor();
		Project project = fileEditor.getProject();
		
		if(project == null || fileEditor == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no active test-suite file for execution.");
			return;
		}
		
		String testSuite = fileEditor.getCurrentTestSuite();
		
		if(testSuite == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no active test-suite file for execution.");
			return;
		}
		
		executionEnvironmentManager.executeTestSuite(project, testSuite);
	}
	
	@Action
	public void runTestCase()
	{
	}
	
	@Action
	public void runSelectedStep()
	{
	}
}
