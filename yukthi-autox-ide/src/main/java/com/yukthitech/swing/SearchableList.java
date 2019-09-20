package com.yukthitech.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Function;

import javax.swing.JList;

public class SearchableList<E> extends JList<E>
{
	private static final long serialVersionUID = 1L;
	
	private static final long ONE_SEC = 1000;
	
	private class SearchListener extends KeyAdapter
	{
		@Override
		public void keyReleased(KeyEvent e)
		{
			long curTime = System.currentTimeMillis();
			
			long diff = (curTime - lastKeyTime);
			lastKeyTime = curTime;
			
			if(diff > ONE_SEC)
			{
				searchString.setLength(0);
			}
			
			searchString.append(e.getKeyChar());
			searchAndSelect();
		}
	}
	
	/**
	 * Current search string.
	 */
	private StringBuilder searchString = new StringBuilder();
	
	/**
	 * Time when last key was pressed.
	 */
	private long lastKeyTime = 0;
	
	/**
	 * Function to be used for filtering. Should accept search string as input
	 * and should return index to be selected.
	 */
	private Function<String, E> searchFunc;
	
	public SearchableList(Function<String, E> searchFunc)
	{
		this.searchFunc = searchFunc;
		super.addKeyListener(new SearchListener());
	}
	
	private void searchAndSelect()
	{
		String srchStr = searchString.toString();
		E selection = searchFunc.apply(srchStr);
		
		if(selection != null)
		{
			super.setSelectedValue(selection, true);
		}
	}

}
