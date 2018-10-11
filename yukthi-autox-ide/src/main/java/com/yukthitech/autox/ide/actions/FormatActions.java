package com.yukthitech.autox.ide.actions;

import java.io.File;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.editor.FileEditorTabbedPane;
import com.yukthitech.autox.ide.format.XmlFormatter;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;

@ActionHolder
public class FormatActions
{
	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;

	@Action
	public void formatCode()
	{
		FileEditor fileEditor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(fileEditor == null)
		{
			JOptionPane.showMessageDialog(IdeUtils.getCurrentWindow(), "No active file editor found for formatting.");
			return;
		}
		
		String currentText = fileEditor.getTextArea().getText();
		currentText = formatFile(fileEditor.getFile(), currentText);
		
		if(currentText == null)
		{
			return;
		}
		
		fileEditor.getTextArea().setText(currentText);
	}
	
	private String formatFile(File file, String content)
	{
		if(file.getName().toLowerCase().endsWith(".xml"))
		{
			return XmlFormatter.formatXml(content);
		}
		
		return content;
	}
	
}
