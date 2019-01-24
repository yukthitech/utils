package com.yukthitech.autox.ide.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.IIdeConstants;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.context.IdeContext;
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
	private IdeContext ideContext;
	
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
	
	public void executeStepCode(String code, Project project)
	{
		ExecutionEnvironment interactiveEnv = executionEnvironmentManager.getInteractiveEnvironment(project);
		
		if(interactiveEnv == null)
		{
			final String mssg = "Starting interactive environment for project: " + project.getName();
			inProgressDialog=InProgressDialog.getInstance();
			inProgressDialog.display(mssg, new Runnable()
			{
				@Override
				public void run()
				{
					logger.debug("Starting interactive environment for project: {}", project.getName());
					
					ExecutionEnvironment newInteractiveEnv = executionEnvironmentManager.startInteractiveEnvironment(project, true);
					inProgressDialog.setSubmessage("Waiting for environment to get started...");
					
					while(!newInteractiveEnv.isReadyToInteract() && !newInteractiveEnv.isTerminated())
					{
						try
						{
							Thread.sleep(100);
						}catch(Exception ex)
						{}
					}
					
					if(!newInteractiveEnv.isTerminated())
					{
						newInteractiveEnv.sendDataToServer(new InteractiveExecuteSteps(code));
					}
				}
			});
		}
		else
		{
			interactiveEnv.sendDataToServer(new InteractiveExecuteSteps(code));
		}

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
		String selectedText = fileEditor.getSelectedText();
		
		if(selectedText == null)
		{
			selectedText = fileEditor.getCurrentElementText(IIdeConstants.ELEMENT_TYPE_STEP);
			
			if(selectedText == null)
			{
				JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "There is no selected text for execution nor current location is part of any step.");
				return;
			}
		}

		executeStepCode(selectedText, project);
	}
	
	@Action
	public synchronized void executeTestSuiteFolder() 
	{
		//find the selected files
		List<File> selectedFiles = ideContext.getSelectedFiles();
		
		if(selectedFiles == null || selectedFiles.isEmpty())
		{
			File activeFolder = ideContext.getActiveFile();
			
			if(activeFolder == null)
			{
				return;
			}
			
			selectedFiles = new ArrayList<>();
			selectedFiles.add(activeFolder);
		}
		
		//filter folders only from selected files
		List<File> filteredFolders = new ArrayList<>();
		
		for(File file : selectedFiles)
		{
			if(!file.isDirectory())
			{
				filteredFolders.add(file);
			}
		}

		if(selectedFiles.isEmpty())
		{
			return;
		}

		Project project = ideContext.getActiveProject();
		executionEnvironmentManager.executeTestSuiteFolder(project, selectedFiles);	
	}
	
	@Action
	public synchronized void executeProject() 
	{
		Project project = ideContext.getActiveProject();
		executionEnvironmentManager.executeProject(project);
	}
}
