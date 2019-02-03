package com.yukthitech.autox.ide;

import com.yukthitech.utils.fmarker.FreeMarkerEngine;

public interface IIdeConstants
{
	public String ELEMENT_TYPE_STEP = "$";
	
	public FreeMarkerEngine FREE_MARKER_ENGINE = new FreeMarkerEngine();
	
	/**
	 * Delay used during typing in file editor to reparse the content.
	 */
	public int FILE_EDITOR_PARSE_DELAY = 1000;
}
