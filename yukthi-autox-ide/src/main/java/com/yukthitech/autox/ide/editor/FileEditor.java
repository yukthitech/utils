package com.yukthitech.autox.ide.editor;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaHighlighter;
import org.fife.ui.rsyntaxtextarea.SquiggleUnderlineHighlightPainter;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.GutterIconInfo;
import org.fife.ui.rtextarea.IconRowHeader;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.IdeNotificationPanel;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.xmlfile.Attribute;
import com.yukthitech.autox.ide.xmlfile.Element;
import com.yukthitech.autox.ide.xmlfile.MessageType;
import com.yukthitech.autox.ide.xmlfile.XmlFile;
import com.yukthitech.autox.ide.xmlfile.XmlFileLocation;
import com.yukthitech.autox.ide.xmlfile.XmlFileMessage;
import com.yukthitech.autox.ide.xmlfile.XmlLocationType;
import com.yukthitech.autox.ide.xmlfile.XmlLoctionAnalyzer;
import com.yukthitech.autox.ide.xmlfile.XmlParseException;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class FileEditor extends RTextScrollPane
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(FileEditor.class);
	
	private static ImageIcon ERROR_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/bookmark_error.png", 16);
	
	private static ImageIcon WARN_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/bookmark_warn.png", 16);

	private Project project;

	private File file;

	private RSyntaxTextArea syntaxTextArea = new RSyntaxTextArea("");
	
	private RSyntaxTextAreaHighlighter highlighter = new RSyntaxTextAreaHighlighter();

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
	
	private List<XmlFileMessage> currentHighlights = new ArrayList<>();
	
	//private IconRowHeader
	
	public FileEditor(Project project, File file)
	{
		super(new RSyntaxTextArea());
		
		if(file.getName().toLowerCase().endsWith(".xml"))
		{
			xmlCompletionProvider = new XmlCompletionProvider(project, this);
		}
		
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
		this.syntaxTextArea.setHighlighter(highlighter);
		this.syntaxTextArea.setToolTipSupplier(this::getToolTip);
		
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

		CompletionProvider provider = getStepsProvider();
		
		if(provider != null)
		{
			AutoCompletion ac = new AutoCompletion(provider);
			// show documentation dialog box
			ac.setShowDescWindow(true);
			ac.install(syntaxTextArea);
		}
	}
	
	@PostConstruct
	private void init()
	{
		fileContentChanged();
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
		
		if(file.getName().toLowerCase().endsWith(".xml"))
		{
			//from last change time, try to parse the xml content and highlight errors if any
			IdeUtils.executeConsolidatedJob("FileEditor.parseXmlContent", this::parseXmlContent, 3000);
		}
	}
	
	private String getToolTip(RTextArea textArea, MouseEvent e)
	{
		int offset = textArea.viewToModel(e.getPoint());
		
		if(offset < 0)
		{
			return null;
		}
		
		for(XmlFileMessage mssg : this.currentHighlights)
		{
			if(!mssg.hasValidOffsets())
			{
				continue;
			}
			
			if(offset >= mssg.getStartOffset() && offset <= mssg.getEndOffset())
			{
				return mssg.getMessage();
			}
		}
		
		return null;
	}
	
	private void addMessage(XmlFileMessage message)
	{
		Gutter gutter = getGutter();
		
		try
		{
			if(message.getMessageType() == MessageType.ERROR)
			{
				gutter.addLineTrackingIcon(message.getLineNo() - 1, ERROR_ICON, message.getMessage());
				
				if(message.hasValidOffsets())
				{
					highlighter.addHighlight(message.getStartOffset(), message.getEndOffset(), new SquiggleUnderlineHighlightPainter(Color.red));
				}
			}
			else
			{
				gutter.addLineTrackingIcon(message.getLineNo() - 1, WARN_ICON, message.getMessage());
				
				if(message.hasValidOffsets())
				{
					highlighter.addHighlight(message.getStartOffset(), message.getEndOffset(), new SquiggleUnderlineHighlightPainter(Color.yellow));
				}
			}
			
			this.currentHighlights.add(message);
			
		}catch(BadLocationException ex)
		{
			throw new InvalidStateException("An error occurred while adding xml file message", ex);
		}
	}
	
	private void clearAllMessages()
	{
		getGutter().removeAllTrackingIcons();
		highlighter.removeAllHighlights();
		this.currentHighlights.clear();
	}
	
	private void parseXmlContent()
	{
		clearAllMessages();
		
		XmlFile xmlFile = null;
		
		try
		{
			xmlFile = XmlFile.parse(syntaxTextArea.getText(), -1);
		}catch(XmlParseException ex)
		{
			xmlFile = ex.getXmlFile();
			addMessage(new XmlFileMessage(MessageType.ERROR, ex.getMessage(), ex.getLineNumber(), ex.getOffset(), ex.getEndOffset()));
		}catch(Exception ex)
		{
			addMessage(new XmlFileMessage(MessageType.ERROR, "Failed to parse xml file with error: " + ex, 1));
		}
		
		if(xmlFile != null)
		{
			List<XmlFileMessage> messages = new LinkedList<>();
			xmlFile.getRootElement().populateTestFileTypes(project, messages);
			
			messages.stream().forEach(mssg -> this.addMessage(mssg));
		}
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

	private CompletionProvider getStepsProvider()
	{
		return xmlCompletionProvider;
	}

	public GutterIconInfo addLineTrackingIcon(int line, Icon icon, String tip)
	{
		return addLineTrackingIcon(line, icon, tip);
	}
}
