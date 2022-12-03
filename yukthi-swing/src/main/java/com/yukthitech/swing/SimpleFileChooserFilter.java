/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.swing;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

/**
 * Simple file chooser filter for specified extensions.
 */
public class SimpleFileChooserFilter extends FileFilter
{
	/**
	 * Label for this filter.
	 */
	private String label;
	
	/**
	 * Extensions supported by this filter.
	 */
	private Set<String> extensions = new HashSet<String>();
	
	public SimpleFileChooserFilter(String label, String ext1, String... otherExtensions)
	{
		this.label = label;
		extensions.add(ext1.toLowerCase());
		
		if(otherExtensions.length > 0)
		{
			extensions.addAll(Arrays.asList(otherExtensions));
		}
	}
	
	@Override
	public String getDescription()
	{
		return label;
	}

	@Override
	public boolean accept(File f)
	{
		String ext = FilenameUtils.getExtension(f.getName());
		return this.extensions.contains(ext);
	}
}
