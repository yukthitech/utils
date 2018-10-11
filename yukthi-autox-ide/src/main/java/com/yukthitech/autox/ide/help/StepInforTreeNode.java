package com.yukthitech.autox.ide.help;

import javax.swing.tree.DefaultMutableTreeNode;

import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.doc.StepInfo;

public class StepInforTreeNode extends DefaultMutableTreeNode
{
	private static final long serialVersionUID = 1L;
	
	private StepInfo stepInfo;

	public StepInforTreeNode(StepInfo step, DocInformation docInformation)
	{
		super(step.getName());
		this.stepInfo = step;
	}

	public StepInfo getStepInfo()
	{
		return stepInfo;
	}

}
