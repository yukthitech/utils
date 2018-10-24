package com.yukthitech.autox.ide.actions;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.editor.FileEditorTabbedPane;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironmentManager;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.ui.InProgressDialog;
import com.yukthitech.autox.monitor.ienv.InteractiveExecuteSteps;

@ActionHolder
public class RunActions
{
	private static Logger logger = LogManager.getLogger(RunActions.class);
	
	public static final String NODE_TEST_SUITE = "testsuite";
	
	public static final String NODE_TEST_CASE = "testcase";
	
	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;
	
	@Autowired
	private ExecutionEnvironmentManager executionEnvironmentManager;
	
	@Autowired
	private InProgressDialog inProgressDialog;
	
	@Action
	public void runTestSuite()
	{
		FileEditor fileEditor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(fileEditor == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no active test-suite file for execution.");
			return;
		}

		Project project = fileEditor.getProject();
		String testSuite = fileEditor.getCurrentElementName(NODE_TEST_SUITE);
		
		if(testSuite == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no active test-suite found at cusrsor position.");
			return;
		}
		
		executionEnvironmentManager.executeTestSuite(project, testSuite);
	}
	
	@Action
	public void runTestCase()
	{
		FileEditor fileEditor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(fileEditor == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no active test-suite file for execution.");
			return;
		}
		
		Project project = fileEditor.getProject();
		String testCase = fileEditor.getCurrentElementName(NODE_TEST_CASE);
		
		if(testCase == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no active test-case found at cursor position.");
			return;
		}
		
		executionEnvironmentManager.executeTestCase(project, testCase);
	}
	
	@Action
	public synchronized void runSelectedSteps()
	{
		FileEditor fileEditor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(fileEditor == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no active test-suite file for execution.");
			return;
		}
		
		Project project = fileEditor.getProject();
		final String selectedText = fileEditor.getSelectedText();
		
		if(selectedText == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no selected test for execution.");
			return;
		}

		ExecutionEnvironment interactiveEnv = executionEnvironmentManager.getInteractiveEnvironment(project);
		
		if(interactiveEnv == null)
		{
			final String mssg = "Starting interactive environment for project: " + project.getName();
			
			inProgressDialog.display(mssg, new Runnable()
			{
				@Override
				public void run()
				{
					logger.debug("Starting interactive environment for project: {}", project.getName());
					
					ExecutionEnvironment newInteractiveEnv = executionEnvironmentManager.startInteractiveEnvironment(project, true);
					inProgressDialog.setSubmessage("Waiting for environment to get started...");
					
					while(!newInteractiveEnv.isReadyToInteract())
					{
						try
						{
							Thread.sleep(100);
						}catch(Exception ex)
						{}
					}
					
					newInteractiveEnv.sendDataToServer(new InteractiveExecuteSteps(selectedText));
				}
			});
		}
		else
		{
			interactiveEnv.sendDataToServer(new InteractiveExecuteSteps(selectedText));
		}
	}
	
	@Action
	public void execute() 
	{
	}
}
