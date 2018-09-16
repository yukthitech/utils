package com.yukthitech.autox.ide.editor;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.context.IdeContext;

public class FileEditor extends RTextScrollPane 
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(FileEditor.class);
	
	private File file;
	
	private RSyntaxTextArea syntaxTextArea = new RSyntaxTextArea("");
	
	@Autowired
	private IdeContext ideContext;
	
	public FileEditor(File file) 
	{
		super(new RSyntaxTextArea());
		this.syntaxTextArea = (RSyntaxTextArea) super.getViewport().getView();
		super.setLineNumbersEnabled(true);
		
		this.file = file;
		
		setSyntaxStyle();
		syntaxTextArea.setCodeFoldingEnabled(true);
		
		try
		{
			syntaxTextArea.setText(FileUtils.readFileToString(file));
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		syntaxTextArea.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				fileContentChanged();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				fileContentChanged();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				fileContentChanged();
			}
		});
	}
	
	private void setSyntaxStyle()
	{
		int extIdx = file.getName().lastIndexOf(".");
		String extension = null;
		
		if(extIdx > 0 && extIdx < file.getName().length() - 1)
		{
			extension = file.getName().substring(extIdx + 1).toLowerCase();
		}
		
		if("properties".equals(extension))
		{
			syntaxTextArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_PROPERTIES_FILE);
		}
		else if("xml".equals(extension))
		{
			syntaxTextArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_XML);
		}
		else if("json".equals(extension))
		{
			syntaxTextArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_JSON);
		}
	}
	
	public void setFile(File file)
	{
		this.file = file;
		setSyntaxStyle();
	}
	
	public void saveFile()
	{
		try
		{
			FileUtils.write(file, syntaxTextArea.getText());
			ideContext.getProxy().fileSaved(file);
		}catch(Exception ex)
		{
			logger.debug("An error occurred while saving file: " + file.getPath(), ex);
			JOptionPane.showMessageDialog(this, "An error occurred while saving file: " + file.getName() + "\nError: " + ex.getMessage());
		}
	}
	
	private void fileContentChanged()
	{
		ideContext.getProxy().fileChanged(file);
	}
	
	public int getCaretPosition()
	{
		return syntaxTextArea.getCaretPosition();
	}
	
	public void setCaretPosition(int position)
	{
		syntaxTextArea.setCaretPosition(position);
	}
}
