package com.yukthitech.autox.ide.editor;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;

/**
 * Extension of rsyntax text area completion provider with on insert callback method.
 * @author akiran
 */
public interface IIdeCompletionProvider extends CompletionProvider
{
	/**
	 * Will be invoked when auto completion is selected and corresponding text is inserted.
	 * @param completion
	 */
	public void onAutoCompleteInsert(Completion completion);
}
