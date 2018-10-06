package com.yukthitech.autox.ide.context;

import java.io.File;

import com.yukthitech.autox.ide.exeenv.EnvironmentEvent;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;
import com.yukthitech.autox.ide.model.IdeState;

/**
 * Listener to listen to the events of the ide and take approp actions.
 * @author akiran
 */
public interface IContextListener
{
	/**
	 * Invoked when file content is changed.
	 * @param file
	 */
	public default void fileChanged(File file)
	{}
	
	/**
	 * Invoked when file content is saved to disk.
	 * @param file
	 */
	public default void fileSaved(File file)
	{}
	
	/**
	 * Called before closing the ide. The data set on state will be persisted
	 * and during next ide start, the same state will be sent via {@link #loadState(IdeState)}
	 * @param state object on which target object needs to be set.
	 */
	public default void saveState(IdeState state)
	{}
	
	/**
	 * Invoked when ide is started by passing previously persisted state.
	 * @param state state to be loaded
	 */
	public default void loadState(IdeState state)
	{}
	
	/**
	 * Invoked when new environment is started.
	 * @param environment
	 */
	public default void newEnvironmentStarted(ExecutionEnvironment environment)
	{}
	
	/**
	 * Invoked when environment is changed.
	 * @param activeEnvironment new active environment. Can be null.
	 */
	public default void activeEnvironmentChanged(ExecutionEnvironment activeEnvironment)
	{}
	
	/**
	 * Invoked when environment is changed.
	 * @param event Event indicating the change and value of change.
	 */
	public default void environmentChanged(EnvironmentEvent event)
	{}
	
	/**
	 * Invoked when environment is terminated.
	 * @param environment
	 */
	public default void environmentTerminated(ExecutionEnvironment environment)
	{}
}
