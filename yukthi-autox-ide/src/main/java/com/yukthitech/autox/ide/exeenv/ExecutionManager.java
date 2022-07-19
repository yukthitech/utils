package com.yukthitech.autox.ide.exeenv;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.swing.Icon;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.layout.UiIdElementsManager;
import com.yukthitech.autox.ide.model.IdeState;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.proj.ProjectManager;
import com.yukthitech.autox.ide.services.IdeEventHandler;
import com.yukthitech.autox.ide.services.IdeStartedEvent;
import com.yukthitech.swing.DropDownButton;
import com.yukthitech.swing.DropDownItem;
import com.yukthitech.swing.common.SwingUtils;

/**
 * Service to manage run configurations.
 * @author akiran
 */
@Service
public class ExecutionManager
{
	private static final String ATTR_RUN_CONFIGS = "runConfigurations";
	
	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private ExecutionEnvironmentManager executionEnvironmentManager;
	
	@Autowired
	private ProjectManager projectManager;
	
	private Map<ExecutionType, Icon> typeIconMap = new HashMap<>();
	
	//private Map<String, RunConfig> runConfigurations = new HashMap<>();

	@PostConstruct
	private void init()
	{
		ideContext.addContextListener(new IContextListener()
		{
			@Override
			public void saveState(IdeState state)
			{
				onSaveState(state);
			}
			
			@Override
			public void projectRemoved(Project project)
			{
				onProjectRemove(project);
			}
		});
	}
	
	private synchronized Icon getIcon(ExecutionType exeType)
	{
		Icon icon = typeIconMap.get(exeType);
		
		if(icon != null)
		{
			return icon;
		}
		
		icon = SwingUtils.loadIconWithoutBorder("/ui/run-icons/" + exeType.name() + ".svg", 20);
		typeIconMap.put(exeType, icon);
		
		return icon;
	}
	
	public ExecutionEnvironment execute(ExecutionType executionType, Project project, String name)
	{
		RunConfig newConfig = new RunConfig(executionType, project.getName(), name);
		
		//RunConfig existingConfig = runConfigurations.get(newConfig.getUniqueId());
		//TODO: here existing runconfig settings should be used for execution 
		ExecutionEnvironment env = executionEnvironmentManager.execute(executionType, project, name);
		newConfig.setName(env.getName());
		
		onNewExecution(newConfig);
		return env;
	}
	
	public ExecutionEnvironment getInteractiveEnvironment(Project project)
	{
		return executionEnvironmentManager.getInteractiveEnvironment(project);
	}
	
	private void onNewExecution(RunConfig runConfig)
	{
		DropDownButton runButton = (DropDownButton) UiIdElementsManager.getElement("runList");
		DropDownItem item = runButton.moveToTop(runConfig);
		
		if(item != null)
		{
			return;
		}
		
		item = new DropDownItem(runConfig.getName(), getIcon(runConfig.getExecutionType()));
		item.setUserData(runConfig);
		item.addActionListener(this::onRunConfigItemClick);
		
		runButton.addItem(item);
	}
	
	private void onRunConfigItemClick(ActionEvent e)
	{
		IdeUtils.execute(() -> 
		{
			DropDownItem dropDownItem = (DropDownItem) e.getSource();
			RunConfig runConfig = (RunConfig) dropDownItem.getUserData();
			
			Project project = projectManager.getProject(runConfig.getProjectName());
			
			if(project == null)
			{
				return;
			}
			
			execute(runConfig.getExecutionType(), project, runConfig.getExecutableName());
		}, 1);
	}
	
	private void onProjectRemove(Project project)
	{
		DropDownButton runButton = (DropDownButton) UiIdElementsManager.getElement("runList");
		List<DropDownItem> items = runButton.getItems();
		List<DropDownItem> itemsToRemove = new ArrayList<DropDownItem>();
		String projName = project.getName();
		
		for(DropDownItem item : items)
		{
			RunConfig config = (RunConfig) item.getUserData();
			
			if(config.getProjectName().equals(projName))
			{
				itemsToRemove.add(item);
			}
		}
		
		itemsToRemove.forEach(item -> runButton.removeItem(item));
	}
	
	private void onSaveState(IdeState state)
	{
		DropDownButton runButton = (DropDownButton) UiIdElementsManager.getElement("runList");
		List<DropDownItem> items = runButton.getItems();
		
		List<RunConfig> configs = new ArrayList<RunConfig>();
		
		for(DropDownItem item : items)
		{
			RunConfig config = (RunConfig) item.getUserData();
			configs.add(config);
		}

		state.setAtribute(ATTR_RUN_CONFIGS, configs);
	}

	@SuppressWarnings("unchecked")
	@IdeEventHandler
	public void onStartup(IdeStartedEvent event)
	{
		IdeState state = event.getIdeState();
		List<RunConfig> configs = (List<RunConfig>) state.getAttribute(ATTR_RUN_CONFIGS);
		
		if(CollectionUtils.isEmpty(configs))
		{
			return;
		}
		
		//reverse the configs, so that latest stays on top
		configs = new ArrayList<RunConfig>(configs);
		Collections.reverse(configs);
		
		//add to drop down
		DropDownButton runButton = (DropDownButton) UiIdElementsManager.getElement("runList");
		
		for(RunConfig config : configs)
		{
			DropDownItem item = new DropDownItem(config.getName(), getIcon(config.getExecutionType()));

			item.setUserData(config);
			item.addActionListener(this::onRunConfigItemClick);
			
			runButton.addItem(item);
		}
	}
}
