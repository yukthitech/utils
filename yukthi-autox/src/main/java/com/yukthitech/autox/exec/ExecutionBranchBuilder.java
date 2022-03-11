package com.yukthitech.autox.exec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.test.Cleanup;
import com.yukthitech.autox.test.IDataProvider;
import com.yukthitech.autox.test.Setup;

/**
 * A unit of execution on the stack.
 * @author akranthikiran
 */
public class ExecutionBranchBuilder<C extends IExecutable>
{
	private AutomationContext context;
	
	private ExecutionBranch executionBranch;
	
	private List<C> subbranches;
	
	private Predicate<C> filter;
	
	private boolean childBranchesRequired = false;
	
	public static <C extends IExecutable> ExecutionBranchBuilder<C> newBranchBuilder(AutomationContext context, String label, String desc, IExecutable executable, Collection<C> subbranches)
	{
		ExecutionBranchBuilder<C> builder = new ExecutionBranchBuilder<C>();
		builder.context = context;
		builder.executionBranch = new ExecutionBranch(label, desc, executable);
		
		if(subbranches != null)
		{
			builder.subbranches = new ArrayList<C>(subbranches);
		}
		
		return builder;
	}

	public static <C extends IExecutable> ExecutionBranchBuilder<C> newBranchNode(AutomationContext context, String label, String desc, IExecutable executable, Collection<IStep> steps)
	{
		ExecutionBranchBuilder<C> builder = new ExecutionBranchBuilder<C>();
		builder.context = context;
		builder.executionBranch = new ExecutionBranch(label, desc, executable);
		builder.executionBranch.childSteps = new ArrayList<IStep>(steps);
		
		return builder;
	}

	public ExecutionBranchBuilder<C> setup(Setup setup)
	{
		if(setup == null)
		{
			return this;
		}
		
		executionBranch.setup =  setup.buildExecutionBranch(context);
		return this;
	}

	public ExecutionBranchBuilder<C> cleanup(Cleanup cleanup)
	{
		if(cleanup == null)
		{
			return this;
		}
		
		executionBranch.cleanup =  cleanup.buildExecutionBranch(context);
		return this;
	}
	
	public ExecutionBranchBuilder<C> dataSetup(Setup setup)
	{
		if(setup == null)
		{
			return this;
		}
		
		executionBranch.dataSetup =  setup.buildExecutionBranch(context);
		return this;
	}

	public ExecutionBranchBuilder<C> dataCleanup(Cleanup cleanup)
	{
		if(cleanup == null)
		{
			return this;
		}
		
		executionBranch.dataCleanup =  cleanup.buildExecutionBranch(context);
		return this;
	}

	public ExecutionBranchBuilder<C> beforeChild(Setup setup)
	{
		if(setup == null)
		{
			return this;
		}
		
		executionBranch.beforeChild =  setup.buildExecutionBranch(context);
		return this;
	}

	public ExecutionBranchBuilder<C> afterChild(Cleanup cleanup)
	{
		if(cleanup == null)
		{
			return this;
		}
		
		executionBranch.afterChild =  cleanup.buildExecutionBranch(context);
		return this;
	}

	public ExecutionBranchBuilder<C> childFilter(Predicate<C> filter)
	{
		this.filter = filter;
		return this;
	}
	
	public ExecutionBranchBuilder<C> dataProvider(IDataProvider provider)
	{
		if(provider == null)
		{
			return this;
		}
		
		executionBranch.dataProvider = provider;
		return this;
	}
	
	/**
	 * This is to be called on builder when at least one child branch 
	 * should exist for current builder to build.
	 * For example: a test-suite should not be built without single testcase.
	 * @return
	 */
	public ExecutionBranchBuilder<C> childBranchesRequired()
	{
		this.childBranchesRequired = true;
		return this;
	}

	public ExecutionBranchBuilder<C> dependencies(String dependencies)
	{
		if(StringUtils.isBlank(dependencies))
		{
			return this;
		}
		
		List<String> depLst = Arrays.asList(dependencies.trim().split("\\s*,\\s*"));
		this.executionBranch.dependencies = depLst;
		
		return this;
	}

	public ExecutionBranch build()
	{
		if(CollectionUtils.isNotEmpty(subbranches))
		{
			for(C c : subbranches)
			{
				if(filter != null && !filter.test(c))
				{
					continue;
				}
				
				ExecutionBranch childBranch = c.buildExecutionBranch(context);
				
				if(childBranch == null)
				{
					continue;
				}
				
				this.executionBranch.addChildBranch(childBranch);
			}
		}
		
		if(childBranchesRequired && CollectionUtils.isEmpty(this.executionBranch.getChildBranches()))
		{
			return null;
		}
		
		return executionBranch;
	}
}
