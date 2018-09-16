package com.yukthitech.autox.ide.layout;

import java.awt.event.InputEvent;

import javax.swing.KeyStroke;

import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Short key for menu action.
 * @author akiran
 */
public class ShortKey implements Validateable
{
	/**
	 * true if control should be down.
	 */
	public boolean ctrl;
	
	/**
	 * true if alt should be down.
	 */
	private boolean alt;
	
	/**
	 * true if shift should be down.
	 */
	private boolean shift;
	
	/**
	 * Character to be used.
	 */
	private char ch;

	/**
	 * Gets the true if control should be down.
	 *
	 * @return the true if control should be down
	 */
	public boolean isCtrl()
	{
		return ctrl;
	}

	/**
	 * Sets the true if control should be down.
	 *
	 * @param ctrl the new true if control should be down
	 */
	public void setCtrl(boolean ctrl)
	{
		this.ctrl = ctrl;
	}

	/**
	 * Gets the true if alt should be down.
	 *
	 * @return the true if alt should be down
	 */
	public boolean isAlt()
	{
		return alt;
	}

	/**
	 * Sets the true if alt should be down.
	 *
	 * @param alt the new true if alt should be down
	 */
	public void setAlt(boolean alt)
	{
		this.alt = alt;
	}

	/**
	 * Gets the true if shift should be down.
	 *
	 * @return the true if shift should be down
	 */
	public boolean isShift()
	{
		return shift;
	}

	/**
	 * Sets the true if shift should be down.
	 *
	 * @param shift the new true if shift should be down
	 */
	public void setShift(boolean shift)
	{
		this.shift = shift;
	}

	/**
	 * Gets the character to be used.
	 *
	 * @return the character to be used
	 */
	public char getCh()
	{
		return ch;
	}

	/**
	 * Sets the character to be used.
	 *
	 * @param ch the new character to be used
	 */
	public void setCh(char ch)
	{
		this.ch = ch;
	}
	
	@Override
	public void validate() throws ValidateException
	{
		if(!ctrl && !alt)
		{
			throw new ValidateException("Both CTRL and ALT are not set. One of this key is mandatory.");
		}
		
		if(ch <= 0)
		{
			throw new ValidateException("No character key specified.");
		}
	}
	
	/**
	 * Converts current object to stroke.
	 * @return
	 */
	public KeyStroke toKeyStroke()
	{
		int modifiers = 0;
		
		if(ctrl)
		{
			modifiers |= InputEvent.CTRL_DOWN_MASK;
		}
		
		if(alt)
		{
			modifiers |= InputEvent.ALT_DOWN_MASK;
		}
		
		if(shift)
		{
			modifiers |= InputEvent.SHIFT_DOWN_MASK;
		}
		
		KeyStroke stroke = KeyStroke.getKeyStroke(ch, modifiers);
		return stroke;
	}
}
