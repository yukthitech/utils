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
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import com.yukthitech.swing.tree.ILazyTreeDataProvider;
import com.yukthitech.swing.tree.cbox.CboxNodeData;
import com.yukthitech.swing.tree.cbox.ICboxTreeStateManager;
import com.yukthitech.swing.tree.cbox.SelectStatus;
import com.yukthitech.utils.exceptions.InvalidArgumentException;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Data provider which provides folder information. This allows to specify
 * 	selected folders - By default these folders get selected
 *  read-only folders - Folders whose state cannot be changed.
 *   
 * @author akranthikiran
 */
public class DirectoryDataProvider implements ILazyTreeDataProvider<CboxNodeData>, ICboxTreeStateManager
{
	/**
	 * Root folders to be expanded.
	 */
	private List<File> rootFolders = new ArrayList<File>();
	
	/**
	 * Folders which are selected by default.
	 */
	private List<File> selectedFolders = new ArrayList<File>();
	
	/**
	 * Folders whose state cannot be changed.
	 */
	private List<File> readOnlyFolders = new ArrayList<File>();
	
	public DirectoryDataProvider(File rootFolder)
	{
		this(Arrays.asList(rootFolder));
	}
	
	public DirectoryDataProvider(Collection<File> rootFolders)
	{
		if(CollectionUtils.isEmpty(rootFolders))
		{
			throw new InvalidArgumentException("No/empty root folders specified");
		}
		
		this.rootFolders = canonlize(rootFolders);
	}
	
	/**
	 * Gets the folders which are selected by default.
	 *
	 * @return the folders which are selected by default
	 */
	public List<File> getSelectedFolders()
	{
		return selectedFolders;
	}
	
	private List<File> canonlize(Collection<File> rootFolders)
	{
		List<File> res = new ArrayList<File>(rootFolders.size());
		
		for(File folder : rootFolders)
		{
			if(!folder.isDirectory())
			{
				throw new InvalidArgumentException("Invalid/non-existing folder specified: {}", folder.getPath());
			}
			
			try
			{
				res.add(folder.getCanonicalFile());
			} catch(IOException ex)
			{
				throw new InvalidStateException("Failed to cannolize folder: {}", folder, ex);
			}
		}
		
		return res;
	}
	
	/**
	 * Sets the folders which are selected by default.
	 *
	 * @param selectedFolders the new folders which are selected by default
	 */
	public void setSelectedFolders(Collection<File> selectedFolders)
	{
		this.selectedFolders = CollectionUtils.isEmpty(selectedFolders) ? null : canonlize(selectedFolders);
	}
	
	public void setReadOnlyFolders(Collection<File> readOnlyFolders)
	{
		this.readOnlyFolders = CollectionUtils.isEmpty(readOnlyFolders) ? null : canonlize(readOnlyFolders);
	}
	
	@Override
	public CboxNodeData getRootNode()
	{
		return new CboxNodeData(null, "Folders");
	}
	
	private CboxNodeData toCheckBoxNodeData(File folder, boolean root)
	{
		CboxNodeData res = root ? 
				CboxNodeData.newReadOnlyData(folder, folder.getName()): 
					new CboxNodeData(folder, folder.getName());
		
		try
		{
			if(selectedFolders != null)
			{
				for(File selFolder : selectedFolders)
				{
					if(selFolder.equals(folder))
					{
						res.setStatus(SelectStatus.SELECTED, null);
						break;
					}
					
					if(FileUtils.directoryContains(folder, selFolder))
					{
						res.setStatus(SelectStatus.PARTIALLY_SELECTED, null);
						res.childSelectionChanged(selFolder, true, null);
						break;
					}
				}
			}

			if(readOnlyFolders != null)
			{
				for(File selFolder : readOnlyFolders)
				{
					if(selFolder.equals(folder))
					{
						res.setFixedStatus(true);
						break;
					}
				}
			}
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while building node data", ex);
		}
		
		return res;
	}

	@Override
	public List<CboxNodeData> getChildNodes(CboxNodeData parent)
	{
		if(parent.isFixedStatus())
		{
			return Collections.emptyList();
		}
		
		File parentFile = (File) parent.getUserData();
		
		if(parentFile == null)
		{
			return rootFolders.stream()
					.map(folder -> toCheckBoxNodeData(folder, true))
					.collect(Collectors.toList());
		}
		
		List<CboxNodeData> childNodes = new ArrayList<CboxNodeData>();
		
		parentFile.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				if(pathname.isDirectory())
				{
					childNodes.add(toCheckBoxNodeData(pathname, false));
				}
				
				return false;
			}
		});
		
		return childNodes;
	}
	
	@Override
	public void stateChanged(Object nodeData, boolean selected)
	{
		File folder = (File) nodeData;
		
		if(selected)
		{
			this.selectedFolders.add(folder);
		}
		else
		{
			this.selectedFolders.remove(folder);
		}
	}
}
