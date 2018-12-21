package com.yukthitech.autox.ide;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdeFileManagerFactory
{
	/**
	 * Managers to have file based ide management.
	 */
	@Autowired
	private List<IIdeFileManager> ideFileManagers;

	public IIdeFileManager getFileManager(File file)
	{
		for(IIdeFileManager manager : this.ideFileManagers)
		{
			if(manager.isSuppored(file))
			{
				return manager;
			}
		}
		
		return null;
	}
}
