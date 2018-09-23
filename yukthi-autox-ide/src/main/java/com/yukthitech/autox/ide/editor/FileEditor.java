package com.yukthitech.autox.ide.editor;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.xmlfile.Attribute;
import com.yukthitech.autox.ide.xmlfile.Element;
import com.yukthitech.autox.ide.xmlfile.XmlFile;

public class FileEditor extends RTextScrollPane 
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(FileEditor.class);
	
	private Project project;
	
	private File file;
	
	private RSyntaxTextArea syntaxTextArea = new RSyntaxTextArea("");
	
	@Autowired
	private IdeContext ideContext;
	
	public FileEditor(Project project, File file) 
	{
		super(new RSyntaxTextArea());
		
		this.project = project;
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
		
		syntaxTextArea.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				handleKeyRelease(e);
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
	
	public Project getProject()
	{
		return project;
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
	
	private void handleKeyRelease(KeyEvent e)
	{
		Document doc = syntaxTextArea.getDocument();
		int caretPos = syntaxTextArea.getCaretPosition();
		
	}
	
	private XmlFile getXmlFile()
	{
		if(!file.getName().toLowerCase().endsWith(".xml"))
		{
			return null;
		}
		
		try
		{
			XmlFile xmlFile = XmlFile.parse(syntaxTextArea.getText());
			return xmlFile;
		}catch(Exception ex)
		{
			logger.trace("Failed to parse xml file: " + file.getName() + " Error: " + ex);
			return null;
		}
	}
	
	public String getCurrentTestSuite()
	{
		XmlFile xmlFile = getXmlFile();
		
		if(xmlFile == null)
		{
			return null;
		}
		
		int curLineNo = syntaxTextArea.getCaretLineNumber();
		Element testSuiteElement = xmlFile.getElement("testsuite", curLineNo);
		
		if(testSuiteElement == null)
		{
			return null;
		}
		
		Attribute attr = testSuiteElement.getAttribute("name");
		
		if(attr == null || StringUtils.isBlank(attr.getValue()))
		{
			return null;
		}
		
		return attr.getValue();
	}
}
