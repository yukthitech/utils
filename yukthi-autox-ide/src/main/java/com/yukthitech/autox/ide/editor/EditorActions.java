package com.yukthitech.autox.ide.editor;

import java.awt.Frame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.dialog.FindAndReplaceDialog;
import com.yukthitech.autox.ide.dialog.GotoLineDialog;
import com.yukthitech.autox.ide.dialog.OpenResourceDialog;
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
	
	private GotoLineDialog gotoLineDialog;
	
	private OpenResourceDialog openResourceDialog;
	
	private void init()
	{
		if(findAndReplaceDialog != null)
		{
			return;
		}
		
		findAndReplaceDialog = new FindAndReplaceDialog( (Frame) IdeUtils.getCurrentWindow() );
		IdeUtils.autowireBean(applicationContext, findAndReplaceDialog);
		
		gotoLineDialog = new GotoLineDialog( (Frame) IdeUtils.getCurrentWindow() );
		IdeUtils.autowireBean(applicationContext, gotoLineDialog);
		
		IdeUtils.centerOnScreen(gotoLineDialog);
		
		openResourceDialog = new OpenResourceDialog(IdeUtils.getCurrentWindow());
		IdeUtils.autowireBean(applicationContext, openResourceDialog);
		IdeUtils.centerOnScreen(openResourceDialog);
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
	
	@Action
	public void gotoLine()
	{
		init();
		FileEditor editor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(editor == null)
		{
			return;
		}

		gotoLineDialog.display(editor);
	}
	
	@Action
	public void openResource()
	{
		init();
		openResourceDialog.display();
	}
	
	@Action
	public void toUpperCase()
	{
		init();
		FileEditor editor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(editor == null)
		{
			return;
		}
		
		editor.changeCase(true);
	}
	
	@Action
	public void toLowerCase()
	{
		init();
		FileEditor editor = fileEditorTabbedPane.getCurrentFileEditor();
		
		if(editor == null)
		{
			return;
		}
		
		editor.changeCase(false);
	}
}
