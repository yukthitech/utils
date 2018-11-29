package com.yukthitech.autox.ide.editor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Field;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.GutterIconInfo;
import org.fife.ui.rtextarea.IconRowHeader;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.doc.DocGenerator;
import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.doc.StepInfo;
import com.yukthitech.autox.ide.IdeNotificationPanel;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.xmlfile.Attribute;
import com.yukthitech.autox.ide.xmlfile.Element;
import com.yukthitech.autox.ide.xmlfile.XmlFile;
import com.yukthitech.autox.ide.xmlfile.XmlFileLocation;
import com.yukthitech.autox.ide.xmlfile.XmlLocationType;
import com.yukthitech.autox.ide.xmlfile.XmlLoctionAnalyzer;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class FileEditor extends RTextScrollPane
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger(FileEditor.class);

	private Project project;

	private File file;

	private RSyntaxTextArea syntaxTextArea = new RSyntaxTextArea("");

	private IconRowHeader iconArea;

	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private IdeNotificationPanel ideNotificationPanel;
	
	/**
	 * Last parsed content line number.
	 */
	private XmlFile lastParsedContent;
	
	private XmlCompletionProvider xmlCompletionProvider;
	
	public FileEditor(Project project, File file)
	{
		super(new RSyntaxTextArea());
		xmlCompletionProvider = new XmlCompletionProvider(project, this);
		
		getGutter().setBookmarkIcon(IdeUtils.loadIcon("/ui/icons/bullet.png", 7));
		getGutter().setBookmarkingEnabled(true);

		try
		{
			Field iconAreaFld = getGutter().getClass().getDeclaredField("iconArea");
			iconAreaFld.setAccessible(true);
			iconArea = (IconRowHeader) iconAreaFld.get(getGutter());
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while fetching icon area", ex);
		}
		
		iconArea.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(SwingUtilities.isRightMouseButton(e))
				{
					GutterPopup popup = new GutterPopup();
					iconArea.add(popup);
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		this.project = project;
		this.syntaxTextArea = (RSyntaxTextArea) super.getViewport().getView();
		super.setLineNumbersEnabled(true);

		this.file = file;

		setSyntaxStyle();
		syntaxTextArea.setCodeFoldingEnabled(true);

		try
		{
			syntaxTextArea.setText(FileUtils.readFileToString(file));
		} catch(Exception ex)
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

		syntaxTextArea.getInputMap().put(KeyStroke.getKeyStroke("ctrl ENTER"), "dummy");

		CompletionProvider provider = getStepsProvider1();
		AutoCompletion ac = new AutoCompletion(provider);
		// show documentation dialog box
		ac.setShowDescWindow(true);
		ac.install(syntaxTextArea);
	}
	
	public void insertStepCode(String code)
	{
		int pos = syntaxTextArea.getCaretPosition();
		syntaxTextArea.insert(code, pos);
	}
	
	public XmlFileLocation getXmlFileLocation()
	{
		try
		{
			XmlFileLocation loc = XmlLoctionAnalyzer.getLocation(syntaxTextArea.getText(), syntaxTextArea.getCaretPosition());
			return loc;
		}catch(Exception ex)
		{
			ideNotificationPanel.displayWarning("Failed to parse xml till current location. Error: " + ex.getMessage());
			return null;
		}
	}
	
	public boolean isStepInsertablePosition()
	{
		XmlFileLocation loc = getXmlFileLocation();
		
		if(loc == null)
		{
			return false;
		}
		
		return (loc.getType() == XmlLocationType.CHILD_ELEMENT);
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
			if(!file.canWrite())
			{
				int res = JOptionPane.showConfirmDialog(IdeUtils.getCurrentWindow(), "Current file '" + file.getName() + "' is read-only file. Do you still want to overwite the file?", "Save File", JOptionPane.YES_NO_OPTION);

				if(res == JOptionPane.NO_OPTION)
				{
					return;
				}

				file.setWritable(true);
			}

			FileUtils.write(file, syntaxTextArea.getText());
			ideContext.getProxy().fileSaved(file);
		} catch(Exception ex)
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

	private XmlFile getXmlFile()
	{
		if(!file.getName().toLowerCase().endsWith(".xml"))
		{
			return null;
		}

		try
		{
			XmlFile xmlFile = XmlFile.parse(syntaxTextArea.getText(), -1);
			return xmlFile;
		} catch(Exception ex)
		{
			logger.trace("Failed to parse xml file: " + file.getName() + " Error: " + ex);
			return null;
		}
	}

	public String getCurrentElementName(String nodeName)
	{
		XmlFile xmlFile = getXmlFile();

		if(xmlFile == null)
		{
			return null;
		}

		int curLineNo = syntaxTextArea.getCaretLineNumber();
		Element testSuiteElement = xmlFile.getElement(nodeName, curLineNo);

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

	public String getSelectedText()
	{
		String selectedText = syntaxTextArea.getSelectedText();

		if(StringUtils.isNotBlank(selectedText))
		{
			return selectedText;
		}

		return null;
	}

	public File getFile()
	{
		return file;
	}

	private CompletionProvider getStepsProvider1()
	{
		return xmlCompletionProvider;
	}
	
	private CompletionProvider getStepsProvider()
	{
		DefaultCompletionProvider stepProvider = new DefaultCompletionProvider();
		String[] basepackage = { "com.yukthitech" };
		DocInformation docInformation = null;

		try
		{
			docInformation = DocGenerator.buildDocInformation(basepackage);
		} catch(Exception e)
		{
			throw new IllegalStateException("An error occured while loading document Information", e);
		}

		for(StepInfo step : docInformation.getSteps())
		{
			stepProvider.addCompletion((new BasicCompletion(stepProvider, step.getName(), "short discription", step.getDescription())));
		}
		return stepProvider;

	}

	public GutterIconInfo addLineTrackingIcon(int line, Icon icon, String tip)
	{
		return addLineTrackingIcon(line, icon, tip);
	}
}
