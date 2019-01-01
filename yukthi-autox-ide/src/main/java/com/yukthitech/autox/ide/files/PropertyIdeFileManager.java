package com.yukthitech.autox.ide.files;

import java.io.File;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.AbstractIdeFileManager;
import com.yukthitech.autox.ide.model.Project;

/**
 * Ide file manager for property files.
 * @author akiran
 */
@Service
public class PropertyIdeFileManager extends AbstractIdeFileManager
{
	@Override
	public boolean isSuppored(Project project, File file)
	{
		return file.getName().toLowerCase().endsWith(".properties");
	}

	@Override
	public String getSyntaxEditingStyle(String extension)
	{
		return RSyntaxTextArea.SYNTAX_STYLE_PROPERTIES_FILE;
	}
}
