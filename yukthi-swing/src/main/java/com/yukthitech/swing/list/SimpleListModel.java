/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.swing.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

public class SimpleListModel<E> extends AbstractListModel<E>
{
	private static final long serialVersionUID = 1L;

	private List<E> elements = new ArrayList<>();

	public SimpleListModel()
	{}

	public SimpleListModel(List<E> elements)
	{
		if(elements != null)
		{
			this.elements.addAll(elements);
		}
	}

	public int getSize()
	{
		return elements.size();
	}

	public E getElementAt(int index)
	{
		return elements.get(index);
	}

	public int size()
	{
		return elements.size();
	}

	public boolean isEmpty()
	{
		return elements.isEmpty();
	}

	public List<E> elements()
	{
		return Collections.unmodifiableList(elements);
	}

	public boolean contains(Object elem)
	{
		return elements.contains(elem);
	}

	public int indexOf(Object elem)
	{
		return elements.indexOf(elem);
	}

	public int lastIndexOf(Object elem)
	{
		return elements.lastIndexOf(elem);
	}

	public void setElementAt(E element, int index)
	{
		elements.set(index, element);
		fireContentsChanged(this, index, index);
	}

	public void removeElementAt(int index)
	{
		elements.remove(index);
		fireIntervalRemoved(this, index, index);
	}

	public void insertElementAt(E element, int index)
	{
		elements.add(index, element);
		fireIntervalAdded(this, index, index);
	}

	public void addElement(E element)
	{
		int index = elements.size();
		elements.add(element);
		fireIntervalAdded(this, index, index);
	}
	
	public void setElements(List<E> elements)
	{
		clear();
		
		if(elements != null && !elements.isEmpty())
		{
			this.elements.addAll(elements);
			fireIntervalAdded(this, 0, elements.size());
		}
	}

	public boolean removeElement(Object obj)
	{
		int index = indexOf(obj);
		boolean rv = elements.remove(obj);
		
		if(index >= 0)
		{
			fireIntervalRemoved(this, index, index);
		}
		
		return rv;
	}

	public void clear()
	{
		int index1 = elements.size() - 1;
		elements.clear();
		
		if(index1 >= 0)
		{
			fireIntervalRemoved(this, 0, index1);
		}
	}

}
