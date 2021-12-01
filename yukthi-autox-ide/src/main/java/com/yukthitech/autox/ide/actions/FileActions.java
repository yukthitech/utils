package com.yukthitech.autox.ide.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.FileDetails;
import com.yukthitech.autox.ide.IdeFileUtils;
import com.yukthitech.autox.ide.IdeIndex;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.editor.FileEditorTabbedPane;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.projexplorer.ProjectExplorer;
import com.yukthitech.autox.ide.projexplorer.ProjectTreeNode;
import com.yukthitech.autox.ide.ui.BaseTreeNode;
import com.yukthitech.utils.exceptions.InvalidStateException;

@ActionHolder
public class FileActions
{
	private static Logger logger = LogManager.getLogger(FileActions.class);

	private static final String TEST_FILE_TEMPLATE = "testFileTemplate";

	private static Map<String, String> templates = new HashMap<>();

	private Transferable fileTransferable;
	
	private File moveFile=null;
	

	static
	{
		try
		{
			templates.put(TEST_FILE_TEMPLATE, 
					IOUtils.toString(FileActions.class.getResourceAsStream("/templates/test-file-template.xml"), Charset.defaultCharset()));
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading templates", ex);
		}
	}

	@Autowired
	private IdeContext ideContext;

	@Autowired
	private ProjectExplorer projectExplorer;

	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;

	@Autowired
	private IdeIndex ideIndex;
	
	@Autowired
	private ProjectActions projectActions;

	@Action
	public void newFolder()
	{
		File activeFolder = ideContext.getActiveFile();
		String newFolder = JOptionPane.showInputDialog("Please provide new folder name?");

		if(newFolder == null)
		{
			return;
		}

		newFolder = newFolder.replace("\\", File.separator);
		newFolder = newFolder.replace("/", File.separator);

		File finalFolder = new File(activeFolder, newFolder);

		if(finalFolder.exists())
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "Specified folder already exists: \n" + finalFolder.getPath());
			return;
		}

		try
		{
			FileUtils.forceMkdir(finalFolder);
		} catch(Exception ex)
		{
			logger.error("Failed to create folder: " + finalFolder.getPath(), ex);
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "Failed to create folder: \n" + finalFolder.getPath() + "\nError: " + ex.getMessage());
		}

		projectExplorer.reloadActiveNode();
	}

	private File createFile(String templateName)
	{
		File activeFolder = ideContext.getActiveFile();
		String newFile = JOptionPane.showInputDialog("Please provide new test-file name?");

		if(newFile == null)
		{
			return null;
		}

		newFile = newFile.replace("\\", File.separator);
		newFile = newFile.replace("/", File.separator);

		File finalFile = new File(activeFolder, newFile);

		if(finalFile.exists())
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "Specified file already exists: \n" + finalFile.getPath());
			return null;
		}

		if(!finalFile.getParentFile().exists())
		{
			try
			{
				FileUtils.forceMkdir(finalFile.getParentFile());
			} catch(Exception ex)
			{
				logger.error("Failed to create folder: " + finalFile.getPath(), ex);
				JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "Failed to create folder: \n" + finalFile.getPath() + "\nError: " + ex.getMessage());
			}
		}

		if(templateName != null)
		{
			try
			{
				FileUtils.write(finalFile, templates.get(templateName), Charset.defaultCharset());
			} catch(Exception ex)
			{
				logger.error("Failed to create file: " + finalFile.getPath(), ex);
				JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "Failed to create file: \n" + finalFile.getPath() + "\nError: " + ex.getMessage());
			}
		}
		else
		{
			try
			{
				finalFile.createNewFile();
			} catch(Exception ex)
			{
				logger.error("Failed to create file: " + finalFile.getPath(), ex);
				JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "Failed to create file: \n" + finalFile.getPath() + "\nError: " + ex.getMessage());
			}
		}

		return finalFile;
	}

	@Action
	public void newTestFile() throws IOException
	{
		File file = createFile(TEST_FILE_TEMPLATE);
		projectExplorer.reloadActiveNode();

		if(file != null)
		{
			ideContext.setActiveDetails(ideContext.getActiveProject(), file);
			fileEditorTabbedPane.openFile();
		}
	}

	@Action
	public void newFile() throws IOException
	{
		File file = createFile(null);
		projectExplorer.reloadActiveNode();

		if(file != null)
		{
			ideContext.setActiveDetails(ideContext.getActiveProject(), file);
			fileEditorTabbedPane.openFile();
		}
	}

	public void deleteFolder(File activeFolder)
	{

		try
		{
			FileUtils.forceDelete(activeFolder);
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while deleting folder: {}", activeFolder.getPath(), ex);
		}

		projectExplorer.reloadActiveNodeParent();
		fileEditorTabbedPane.filePathChanged(activeFolder, activeFolder);

	}

	private void deleteActiveFolder(File activeFolder)
	{
		int res = JOptionPane.showConfirmDialog(IdeUtils.getCurrentWindow(), 
				"Are you sure you want to delete folder '" + activeFolder.getName() 
					+ "' and it's sub folders and files?", 
				"Delete Folder", 
				JOptionPane.YES_NO_OPTION);

		if(res == JOptionPane.NO_OPTION)
		{
			return;
		}

		try
		{
			FileUtils.forceDelete(activeFolder);
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while deleting folder: {}", activeFolder.getPath(), ex);
		}

		projectExplorer.reloadActiveNodeParent();
		fileEditorTabbedPane.filePathChanged(activeFolder, activeFolder);
	}
	
	public void deleteFile(File activeFile)
	{
		try
		{
			FileUtils.forceDelete(activeFile);
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while deleting file: {}", activeFile.getPath(), ex);
		}

		projectExplorer.reloadActiveNodeParent();
		fileEditorTabbedPane.filePathChanged(activeFile, activeFile);
	}
	
	private void deleteActiveFile(File activeFile)
	{
		int res = JOptionPane.showConfirmDialog(IdeUtils.getCurrentWindow(), 
				"Are you sure you want to delete file '" + activeFile.getName() + "' ?",
				"Delete File",
				JOptionPane.YES_NO_OPTION
				);
		
		if(res == JOptionPane.NO_OPTION)
		{
			return;
		}
		
		deleteFile(activeFile);
	}

	@Action
	public void deleteFile()
	{
		BaseTreeNode baseNode = projectExplorer.getActiveNode();
		
		if(baseNode instanceof ProjectTreeNode)
		{
			projectActions.deleteProject();
			return;
		}
		
		File activeFile = ideContext.getActiveFile();

		if(activeFile == null)
		{
			return;
		}

		if(activeFile.isDirectory())
		{
			deleteActiveFolder(activeFile);
		}
		else
		{
			deleteActiveFile(activeFile);
		}
	}

	private void renameFile(File activeFile)
	{
		String newName = JOptionPane.showInputDialog("Please provide new name for file?", activeFile.getName());

		if(newName == null || newName.equals(activeFile.getName()))
		{
			return;
		}

		File newNameFile = new File(activeFile.getParentFile(), newName);
		activeFile.renameTo(newNameFile);

		projectExplorer.reloadActiveNodeParent();
		fileEditorTabbedPane.filePathChanged(activeFile, newNameFile);
	}

	private void renameFolder(File activeFolder)
	{
		String newName = JOptionPane.showInputDialog("Please provide new name for folder?", activeFolder.getName());

		if(newName == null || newName.equals(activeFolder.getName()))
		{
			return;
		}

		File newNameFile = new File(activeFolder.getParentFile(), newName);
		activeFolder.renameTo(newNameFile);

		projectExplorer.reloadActiveNodeParent();
		fileEditorTabbedPane.filePathChanged(activeFolder, newNameFile);
	}

	@Action
	public void renameFile()
	{
		File activeFile = ideContext.getActiveFile();

		if(activeFile == null)
		{
			return;
		}

		if(activeFile.isDirectory())
		{
			renameFolder(activeFile);
		}
		else
		{
			renameFile(activeFile);
		}
	}
	
	@Action
	public void cutFile() throws UnsupportedFlavorException, IOException {
		File file = ideContext.getActiveFile();
		copyFile(file);
		moveFile=file;
	}
	public void copyFile(File activeFile) throws UnsupportedFlavorException, IOException
	{
		moveFile=null;
		ArrayList<File> listOfFiles = new ArrayList<File>();
		listOfFiles.add(activeFile);
		fileTransferable = new TransferableFiles(listOfFiles);
		Clipboard clip = (Clipboard) Toolkit.getDefaultToolkit().getSystemClipboard();
		clip.setContents(fileTransferable, null);
	}

	@Action
	public void copyFile() throws UnsupportedFlavorException, IOException
	{
		File activeFile = ideContext.getActiveFile();
		copyFile(activeFile);
	}

	public void pasteFile(File activeFolder, List<File> list) throws IOException, InterruptedException
	{
		for(File srcFile : list)
		{
			String fname = srcFile.getName();
			File destFile = new File(activeFolder.getAbsolutePath() + File.separator + fname);

			if(destFile.exists())
			{
				if(srcFile.isDirectory() != destFile.isDirectory())
				{
					if(srcFile.isDirectory())
					{
						JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), 
								String.format("A file (not folder) with same name as source folder already exist '%s'. Skipping the copy of this folder.", srcFile.getName())
						);
					}
					else
					{
						JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), 
								String.format("A folder (not file) with same name as source file already exist '%s'. Skipping the copy of this file.", srcFile.getName())
						);
					}
					
					continue;
				}
				
				int res = 0;
				
				if(!destFile.isDirectory())
				{
					res = JOptionPane.showConfirmDialog(IdeUtils.getCurrentWindow(), 
							String.format("A file with name '%s' already exist. Do you want to overwrite?", destFile.getName()), 
							"Overwrite", 
							JOptionPane.YES_NO_CANCEL_OPTION);
				}
				else
				{
					res = JOptionPane.showConfirmDialog(IdeUtils.getCurrentWindow(), 
							String.format("A folder with name '%s' already exist. The subfolder and files will be merged. "
									+ "\nExisting file(s) will be overwritten."
									+ "\nDo you want to continue?", destFile.getName()), 
							"Overwrite", 
							JOptionPane.YES_NO_CANCEL_OPTION);
				}

				if(res == JOptionPane.CANCEL_OPTION)
				{
					return;
				}
				if(res == JOptionPane.NO_OPTION)
				{
					continue;
				}
			}
			
			if(srcFile.isDirectory())
			{
				FileUtils.copyDirectory(srcFile, destFile);
			}
			else
			{
				FileUtils.copyFile(srcFile, destFile);
			}
		}
		if(moveFile!=null) {
			FileUtils.forceDelete(moveFile);
		}

		projectExplorer.reloadActiveNode();
	}

	public void pasteFile(File activeFolder) throws UnsupportedFlavorException, IOException, InterruptedException
	{
		Clipboard clip = (Clipboard) Toolkit.getDefaultToolkit().getSystemClipboard();
		
		if(!clip.isDataFlavorAvailable(DataFlavor.javaFileListFlavor))
		{
			return;
		}
		
		@SuppressWarnings("unchecked")
		List<File> list = (List<File>) clip.getData(DataFlavor.javaFileListFlavor);
		pasteFile(activeFolder, list);
	}

	@Action
	public void pasteFile() throws UnsupportedFlavorException, IOException, InterruptedException
	{
		File activeFolder = ideContext.getActiveFile();
		pasteFile(activeFolder);
	}

	@Action
	public void refreshFolder()
	{
		projectExplorer.reloadActiveNode();
	}
	
	public void gotoFile(Project project, String file, int lineNo, boolean limitToTestSuiteFolder)
	{
		FileDetails selectedFile = null;
		
		for(FileDetails fileDet : ideIndex.getFiles())
		{
			if(fileDet.getProject() != project || !fileDet.getFile().getName().equals(file))
			{
				continue;
			}
			
			//if limitToTestSuiteFolder flag is true, consider current only if it is part of test suite folder 
			if(limitToTestSuiteFolder)
			{
				boolean tsFolderFile = false;
				
				for(String tsFolder : project.getTestSuitesFoldersList())
				{
					File folder = new File(project.getBaseFolderPath(), tsFolder);
					
					if(IdeFileUtils.getRelativePath(folder, fileDet.getFile()) != null)
					{
						tsFolderFile = true;
						break;
					}
				}
				
				if(!tsFolderFile)
				{
					continue;
				}
			}

			selectedFile = fileDet;
			break;
		}
		
		if(selectedFile == null)
		{
			return;
		}

		FileEditor editor = fileEditorTabbedPane.openProjectFile(project, selectedFile.getFile());
		
		if(lineNo < 0 || editor == null)
		{
			return;
		}
		
		editor.gotoLine(lineNo);
	}
}
