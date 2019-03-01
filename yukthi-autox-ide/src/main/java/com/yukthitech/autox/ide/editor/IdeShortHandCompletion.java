package com.yukthitech.autox.ide.editor;

import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;

import com.yukthitech.autox.ide.IdeUtils;

/**
 * Extension of short hand completion along with cursor position in inserted text.
 * @author akiran
 */
public class IdeShortHandCompletion extends ShorthandCompletion
{
	private static String CUR_POS_TEXT = "###CUR###";
	
	/**
	 * Left side movement of the cursor required from the end of inserted text.
	 */
	private int cursorLeftMovement = -1;
	
	public IdeShortHandCompletion(CompletionProvider provider, String inputText, String replacementText, String shortDesc, String summary)
	{
		super(provider, inputText, getFinalText(replacementText), shortDesc, summary);
		
		int pos = replacementText.indexOf(CUR_POS_TEXT);
		
		if(pos < 0)
		{
			return;
		}
		
		this.cursorLeftMovement = replacementText.length() - pos - CUR_POS_TEXT.length();
	}
	
	/**
	 * Gets the left side movement of the cursor required from the end of inserted text.
	 *
	 * @return the left side movement of the cursor required from the end of inserted text
	 */
	public int getCursorLeftMovement()
	{
		return cursorLeftMovement;
	}
	
	private static String getFinalText(String actualText)
	{
		actualText = IdeUtils.removeCarriageReturns(actualText);
		return actualText.replace(CUR_POS_TEXT, "");
	}
}
