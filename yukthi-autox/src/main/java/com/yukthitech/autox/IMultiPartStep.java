package com.yukthitech.autox;

/**
 * To be implemented by steps, which are externally extended. Example: if-else, try-catch.
 * @author akiran
 */
public interface IMultiPartStep
{
	public void addChildStep(IStep step);
}
