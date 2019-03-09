package com.yukthitech.ccg.xml;

/**
 * Parent aware beans will be injected with its parent bean when loading.
 * @author akiran
 */
public interface IParentAware
{
	/**
	 * Sets the parent of current bean.
	 * @param parent
	 */
	public void setParent(Object parent);
}
