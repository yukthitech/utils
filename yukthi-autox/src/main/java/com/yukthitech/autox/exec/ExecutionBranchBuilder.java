package com.yukthitech.autox.exec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;

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
	
	public static <C extends IExecutable> ExecutionBranchBuilder<C> newBranchBuilder(AutomationContext context, String label, IExecutable executable, Collection<C> subbranches)
	{
		ExecutionBranchBuilder<C> builder = new ExecutionBranchBuilder<C>();
		builder.context = context;
		builder.executionBranch = new ExecutionBranch(label, executable);
		
		return builder;
	}

	public static <C extends IExecutable> ExecutionBranchBuilder<C> newBranchNode(AutomationContext context, String label, IExecutable executable, Collection<IStep> steps)
	{
		ExecutionBranchBuilder<C> builder = new ExecutionBranchBuilder<C>();
		builder.context = context;
		builder.executionBranch = new ExecutionBranch(label, executable);
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

	public ExecutionBranch build()
	{
		if(CollectionUtils.isNotEmpty(subbranches))
		{
			this.executionBranch.childBranches = new ArrayList<ExecutionBranch>();
			
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
				
				this.executionBranch.childBranches.add(childBranch);
			}
		}
		
		if(childBranchesRequired && CollectionUtils.isEmpty(this.executionBranch.childBranches))
		{
			return null;
		}
		
		return executionBranch;
	}
}
