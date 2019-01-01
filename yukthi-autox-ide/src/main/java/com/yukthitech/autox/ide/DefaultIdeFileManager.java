package com.yukthitech.autox.ide;

import java.io.File;

import org.fife.ui.autocomplete.CompletionProvider;

import com.yukthitech.autox.ide.editor.FileEditor;
import com.yukthitech.autox.ide.model.Project;

/**
 * Default ide file manager that will be used with files which dont have file manager configured.
 * @author akiran
 */
public class DefaultIdeFileManager implements IIdeFileManager
{
	@Override
	public boolean isSuppored(Project project, File file)
	{
		return false;
	}

	@Override
	public String getSyntaxEditingStyle(String extension)
	{
		return null;
	}

	@Override
	public CompletionProvider getCompletionProvider(FileEditor fileEditor)
	{
		return null;
	}

	@Override
	public Object parseFile(Project project, File file, FileParseCollector collector)
	{
		return null;
	}

	@Override
	public Object parseContent(Project project, String name, String content, FileParseCollector collector)
	{
		return null;
	}

	@Override
	public String getToolTip(FileEditor fileEditor, Object parsedFile, int offset)
	{
		return null;
	}

	@Override
	public String getActiveElement(FileEditor fileEditor, String nodeType)
	{
		return null;
	}
}
