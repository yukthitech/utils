package com.yukthitech.autox.ide.editor;

import java.awt.Frame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.find.FindAndReplaceDialog;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionHolder;

/**
 * Actions related to editor.
 * @author akiran
 */
@ActionHolder
public class EditorActions
{
	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	private FindAndReplaceDialog findAndReplaceDialog;
	
	private void init()
	{
		if(findAndReplaceDialog != null)
		{
			return;
		}
		
		findAndReplaceDialog = new FindAndReplaceDialog( (Frame) IdeUtils.getCurrentWindow() );
		IdeUtils.autowireBean(applicationContext, findAndReplaceDialog);
	}

	@Action
	public void findAndReplace()
	{
		init();
		
		FileEditor editor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(editor == null)
		{
			return;
		}

		findAndReplaceDialog.display();
	}
}
