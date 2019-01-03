package com.yukthitech.autox.ide.editor;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
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
import org.fife.ui.rtextarea.IconRowHeader;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.FileParseCollector;
import com.yukthitech.autox.ide.IIdeFileManager;
import com.yukthitech.autox.ide.IdeFileManagerFactory;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.find.FindCommand;
import com.yukthitech.autox.ide.find.FindOperation;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.xmlfile.MessageType;
import com.yukthitech.autox.ide.xmlfile.XmlFileLocation;
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
	private IdeFileManagerFactory ideFileManagerFactory;
	
	/**
	 * File manager for current file.
	 */
	private IIdeFileManager currentFileManager;
	
	private List<FileParseMessage> currentHighlights = new ArrayList<>();
	
	/**
	 * Content parsed in last iteration.
	 */
	private Object parsedFileContent;
	
	public FileEditor(Project project, File file)
	{
		super(new RSyntaxTextArea());
		
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
	}
	
	@PostConstruct
	private void init()
	{
		this.currentFileManager = ideFileManagerFactory.getFileManager(project, file);
		
		if(this.currentFileManager != null)
		{
			CompletionProvider provider = currentFileManager.getCompletionProvider(this);
			
			if(provider != null)
			{
				AutoCompletion ac = new AutoCompletion(provider);
				// show documentation dialog box
				ac.setShowDescWindow(true);
				ac.install(syntaxTextArea);
			}
		}

		setSyntaxStyle();
		syntaxTextArea.setCodeFoldingEnabled(true);

		fileContentChanged();
	}
	
	public void insertStepCode(String code)
	{
		int pos = syntaxTextArea.getCaretPosition();
		syntaxTextArea.insert(code, pos);
	}
	
	public XmlFileLocation getXmlFileLocation()
	{
		return currentFileManager.getXmlFileLocation(this);
	}
	
	public boolean isStepInsertablePosition()
	{
		return currentFileManager.isStepInsertablePosition(this);
	}

	private void setSyntaxStyle()
	{
		int extIdx = file.getName().lastIndexOf(".");
		String extension = null;

		if(extIdx > 0 && extIdx < file.getName().length() - 1)
		{
			extension = file.getName().substring(extIdx + 1).toLowerCase();
		}
		
		String style = currentFileManager.getSyntaxEditingStyle(extension);
		syntaxTextArea.setSyntaxEditingStyle(style);
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
		
		//from last change time, try to parse the content and highlight regions if any
		IdeUtils.executeConsolidatedJob("FileEditor.parseFileContent." + file.getName(), this::parseFileContent, 2000);
	}
	
	private String getToolTip(RTextArea textArea, MouseEvent e)
	{
		int offset = textArea.viewToModel(e.getPoint());
		
		if(offset < 0)
		{
			return null;
		}
		
		for(FileParseMessage mssg : this.currentHighlights)
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
		
		return currentFileManager.getToolTip(this, parsedFileContent, offset);
	}
	
	private void addMessage(FileParseMessage message)
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
	
	private void parseFileContent()
	{
		if(!this.currentHighlights.isEmpty())
		{
			clearAllMessages();
		}
		
		FileParseCollector collector = new FileParseCollector();
		parsedFileContent = currentFileManager.parseContent(project, file.getName(), syntaxTextArea.getText(), collector);
		
		collector.getMessages().stream().forEach(mssg -> this.addMessage(mssg));
	}

	public int getCaretPosition()
	{
		return syntaxTextArea.getCaretPosition();
	}

	public void setCaretPosition(int position)
	{
		syntaxTextArea.setCaretPosition(position);
	}
	
	public String getContent()
	{
		return syntaxTextArea.getText();
	}
	
	public int getCurrentLineNumber()
	{
		return syntaxTextArea.getCaretLineNumber();
	}

	public String getCurrentElementName(String nodeType)
	{
		return currentFileManager.getActiveElement(this, nodeType);
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
	
	private int getCaretPositionForFind(FindCommand command)
	{
		int curPos = syntaxTextArea.getCaretPosition();
		
		if(syntaxTextArea.getSelectedText() != null && syntaxTextArea.getSelectedText().length() > 0)
		{
			curPos = command.isReverseDirection() ? syntaxTextArea.getSelectionStart() : syntaxTextArea.getSelectionEnd();
		}
		
		return curPos;
	}
	
	private int[] find(FindCommand command, int startPos, int curPos, boolean wrapped, String fullText)
	{
		int idx = 0;
		
		if(command.isRegularExpression())
		{
			Pattern pattern = Pattern.compile(command.getSearchString());
			Matcher matcher = pattern.matcher(fullText);
			
			if(command.isReverseDirection())
			{
				int region[] = null;
				
				while(matcher.find())
				{
					if(matcher.end() > curPos)
					{
						break;
					}
					
					region = new int[] {matcher.start(), matcher.end()};
				}
				
				if(wrapped && region[0] < startPos)
				{
					return null;
				}
				
				return region;
			}
			else
			{
				if(!matcher.find(curPos))
				{
					return null;
				}
				
				if(wrapped && matcher.end() > startPos)
				{
					return null;
				}
				
				return new int[] {matcher.start(), matcher.end()};
			}
		}
		
		fullText = command.isCaseSensitive() ? fullText : fullText.toLowerCase();
		
		String searchStr = command.getSearchString();
		searchStr = command.isCaseSensitive() ? searchStr : searchStr.toLowerCase();
		
		if(command.isReverseDirection())
		{
			if(curPos > 0)
			{
				curPos = curPos - 1;
				idx = fullText.lastIndexOf(searchStr, curPos);
			}
			else
			{
				idx = -1;
			}
		}
		else
		{
			idx = fullText.indexOf(searchStr, curPos);
		}

		if(idx < 0)
		{
			if(command.isWrapSearch())
			{
				if(wrapped)
				{
					return null;
				}
				
				int resetPos = command.isReverseDirection() ? fullText.length() : 0;
				return find(command, startPos, resetPos, true, fullText);
			}
			
			return null;
		}

		return new int[] {idx, idx + command.getSearchString().length()};
	}
	
	/**
	 * Finds the string that needs to be used as replacement string.
	 * @param command command in use
	 * @param content current content
	 * @param range range being replaced
	 * @return string to be used for replacement.
	 */
	private String findReplaceString(FindCommand command, String content, int range[])
	{
		if(!command.isRegularExpression())
		{
			return command.getReplaceWith();
		}
		
		//extract the string that needs to be replaced
		String targetStr = content.substring(range[0], range[1]);
		
		//in obtained string replace the pattern, this will ensure $ expressions of regex is respected
		String repStr = targetStr.replaceAll(command.getSearchString(), command.getReplaceWith());
		
		return repStr;
	}
	
	public String executeFindOperation(FindCommand command, FindOperation op)
	{
		String fullText = syntaxTextArea.getText();
		int startPos = getCaretPositionForFind(command);
		
		switch(op)
		{
			case FIND:
			{
				int range[] = find(command, startPos, getCaretPositionForFind(command), false, fullText);
				
				if(range == null)
				{
					return "Search string not found.";
				}
				
				syntaxTextArea.select(range[0], range[1]);
				break;
			}
			case REPLACE:
			{
				if(syntaxTextArea.getSelectedText().length() <= 0)
				{
					return "No text is selected to replace";
				}
				
				int range[] = new int[] {syntaxTextArea.getSelectionStart(), syntaxTextArea.getSelectionEnd()};
				
				//If pattern find replace string
				
				syntaxTextArea.replaceRange(
						findReplaceString(command, fullText, range), 
						range[0], range[1]);
				syntaxTextArea.setCaretPosition(range[0] + command.getReplaceWith().length());
				break;
			}
			case REPLACE_AND_FIND:
			{
				if(syntaxTextArea.getSelectedText().length() <= 0)
				{
					return "No text is selected to replace";
				}
				
				int range[] = new int[] {syntaxTextArea.getSelectionStart(), syntaxTextArea.getSelectionEnd()};
				
				syntaxTextArea.replaceRange(command.getReplaceWith(), range[0], range[1]);
				syntaxTextArea.setCaretPosition(range[0] + command.getReplaceWith().length());
				
				range = find(command, startPos, getCaretPositionForFind(command), false, fullText);
				
				if(range == null)
				{
					return "Search string not found.";
				}
				
				syntaxTextArea.select(range[0], range[1]);
				break;
			}
			default:
			{
				int count = 0;
				
				while(true)
				{
					int range[] = find(command, startPos, getCaretPositionForFind(command), false, fullText);
					
					if(range == null)
					{
						break;
					}
					
					count++;
					syntaxTextArea.replaceRange(command.getReplaceWith(), range[0], range[1]);
					syntaxTextArea.setCaretPosition(range[0] + command.getReplaceWith().length());
					
					fullText = syntaxTextArea.getText();
				}
				
				return count + " occurrences are replaced.";
			}
		}
		
		return "";
	}
}
