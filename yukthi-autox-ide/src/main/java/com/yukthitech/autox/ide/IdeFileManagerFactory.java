package com.yukthitech.autox.ide;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.model.Project;

@Service
public class IdeFileManagerFactory
{
	/**
	 * Managers to have file based ide management.
	 */
	@Autowired
	private List<IIdeFileManager> ideFileManagers;
	
	/**
	 * File manager to be used for files which are not matching with any configured
	 * file managers.
	 */
	private DefaultIdeFileManager defaultIdeFileManager = new DefaultIdeFileManager();

	public IIdeFileManager getFileManager(Project project, File file)
	{
		for(IIdeFileManager manager : this.ideFileManagers)
		{
			if(manager.isSuppored(project, file))
			{
				return manager;
			}
		}
		
		return defaultIdeFileManager;
	}
}
