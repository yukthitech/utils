package com.yukthitech.autox.ide.actions.search;

import java.io.File;

public class FileSearchQuery
{
	/**
	 * String to be searched.
	 */
	private String searchString;
	
	/**
	 * Folders in which search should be done.
	 */
	private File searchFolders[];
	
	/**
	 * Whether search string should be search in case sensitive way. 
	 */
	private boolean caseSensitive;
	
	/**
	 * Whether search string should be used as regular expression.
	 */
	private boolean regularExpression;
	
	/**
	 * File name pattern.
	 */
	private String fileNamePattern;
	
}
