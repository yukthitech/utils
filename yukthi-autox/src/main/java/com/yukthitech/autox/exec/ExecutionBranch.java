package com.yukthitech.autox.exec;

import java.util.ArrayList;
import java.util.List;

import com.yukthitech.autox.IStep;
import com.yukthitech.autox.test.IDataProvider;
import com.yukthitech.autox.test.TestCaseData;
import com.yukthitech.autox.test.TestCaseResult;

/**
 * A unit of execution on the stack.
 * @author akranthikiran
 */
public class ExecutionBranch
{
	/**
	 * Label to be used in stack display.
	 */
	String label;
	
	String description;
	
	Object executable;
	
	private List<ExecutionBranch> childBranches;
	
	ExecutionBranch setup;
	
	ExecutionBranch cleanup;
	
	ExecutionBranch beforeChild;
	
	ExecutionBranch afterChild;

	ExecutionBranch dataSetup;
	
	ExecutionBranch dataCleanup;

	List<IStep> childSteps;
	
	IDataProvider dataProvider;
	
	TestCaseResult result;
	
	int failedChildCount;
	
	TestCaseData testCaseData;
	
	ExecutionBranch(String label, String description, Object executable)
	{
		this.label = label;
		this.description = description;
		this.executable = executable;
	}
	
	void incrementFailedChildCount()
	{
		failedChildCount++;
	}

	public String getLabel()
	{
		return label;
	}
	
	public String getDescription()
	{
		return description;
	}

	public Object getExecutable()
	{
		return executable;
	}
	
	public void addChildBranch(ExecutionBranch branch)
	{
		if(childBranches == null)
		{
			childBranches = new ArrayList<ExecutionBranch>();
		}
		
		childBranches.add(branch);
	}

	public List<ExecutionBranch> getChildBranches()
	{
		return childBranches;
	}
	
	public ExecutionBranch getSetup()
	{
		return setup;
	}

	public ExecutionBranch getCleanup()
	{
		return cleanup;
	}

	public List<IStep> getChildSteps()
	{
		return childSteps;
	}

	public IDataProvider getDataProvider()
	{
		return dataProvider;
	}
	
	private void toText(StringBuilder builder, String indent, String indentIncrement)
	{
		builder.append(indent).append(label).append("\n");
		
		String childIndent = indent.concat(indentIncrement);
		
		if(setup != null)
		{
			builder.append(childIndent);
			
			if(executable != null)
			{
				 builder.append("[").append(executable.getClass().getSimpleName()).append("] ");
			}
			
			builder.append(label).append("[").append(setup.label).append("]\n");
		}
		
		if(childBranches != null)
		{
			for(ExecutionBranch child : childBranches)
			{
				child.toText(builder, childIndent, indentIncrement);
			}
		}

		if(cleanup != null)
		{
			builder.append(childIndent).append(label).append("[").append(cleanup.label).append("]\n");
		}
	}
	
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		toText(builder, "", "\t");
		return builder.toString();
	}
}
