package com.yukthitech.autox.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.AbstractLocationBased;
import com.yukthitech.autox.IStep;
import com.yukthitech.autox.IStepContainer;
import com.yukthitech.autox.common.SkipParsing;
import com.yukthitech.ccg.xml.IParentAware;
import com.yukthitech.ccg.xml.util.ValidateException;
import com.yukthitech.ccg.xml.util.Validateable;

/**
 * Represents list of steps that needs to be executed before executing testing unit.
 */
public class Setup extends AbstractLocationBased implements IStepContainer, Validateable, IEntryPoint, IParentAware
{
	/**
	 * Name for logger and other purposes.
	 */
	public static final String NAME = "setup";
	
	/**
	 * Steps for the test case.
	 */
	private List<IStep> steps = new ArrayList<>();
	
	/**
	 * Parent of this element.
	 */
	@SkipParsing
	private Object parent;
	
	@Override
	public void setParent(Object parent)
	{
		this.parent = parent;
	}
	
	@Override
	public String toText()
	{
		return parent + ".<setup>";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yukthitech.ui.automation.IStepContainer#addStep(com.yukthitech.ui.automation.
	 * IStep)
	 */
	@Override
	public void addStep(IStep step)
	{
		if(steps == null)
		{
			steps = new ArrayList<IStep>();
		}

		steps.add(step);
	}

	/**
	 * Gets the steps for the test case.
	 *
	 * @return the steps for the test case
	 */
	public List<IStep> getSteps()
	{
		return steps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.ccg.core.Validateable#validate()
	 */
	@Override
	public void validate() throws ValidateException
	{
		if(CollectionUtils.isEmpty(steps))
		{
			throw new ValidateException("No steps provided for setup");
		}
	}

	@Override
	public String toString()
	{
		String extra = String.format(" [Location: %s:%s]", super.getLocation(), super.getLineNumber());
		return super.toString() + extra;
	}
}
