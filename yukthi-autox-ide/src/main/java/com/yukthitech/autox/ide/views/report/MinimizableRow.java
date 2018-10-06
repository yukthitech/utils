package com.yukthitech.autox.ide.views.report;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class MinimizableRow<T>
{
	private boolean minimized;
	
	private List<T> children = new LinkedList<>();

	public boolean isMinimized()
	{
		return minimized;
	}

	public void setMinimized(boolean minimized)
	{
		this.minimized = minimized;
	}
	
	public void flipMinimizedStatus()
	{
		this.minimized = !minimized;
	}
	
	public void addChild(T child)
	{
		children.add(child);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void populateChildRows(Consumer<Object> consumer)
	{
		consumer.accept(this);
		
		if(minimized)
		{
			return;
		}
		
		for(Object child : children)
		{
			if(child instanceof MinimizableRow)
			{
				((MinimizableRow) child).populateChildRows(consumer);
			}
			else
			{
				consumer.accept(child);
			}
		}
	}
}
