package com.yukthitech.autox.ide.actions;

import java.io.File;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rtextarea.RTextArea;
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
	private static Logger logger = LogManager.getLogger(FormatActions.class);
	
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

		RTextArea textArea = fileEditor.getTextArea();
		String currentText = textArea.getText();
		currentText = formatFile(fileEditor.getFile(), currentText);
		
		if(currentText == null)
		{
			return;
		}
		
		int caretPos = textArea.getCaretPosition(); 
		fileEditor.getTextArea().replaceRange(currentText, 0, textArea.getText().length());
		
		try
		{
			textArea.setCaretPosition(caretPos);
		}catch(Exception ex)
		{
			logger.info("Failed to set caret position post formatting. Ignoring the error: " + ex);
		}
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
