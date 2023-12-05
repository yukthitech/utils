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

import java.awt.Component;
import java.util.function.Function;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

public class SimpleListCellRenderer<E> extends DefaultListCellRenderer
{
	private static final long serialVersionUID = 1L;
	
	private Function<E, Icon> iconProvider;
	
	private Function<E, String> labelProvider;

	public SimpleListCellRenderer(Function<E, Icon> iconProvider, Function<E, String> labelProvider)
	{
		this.iconProvider = iconProvider;
		this.labelProvider = labelProvider;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	
		if(iconProvider != null)
		{
			label.setIcon(iconProvider.apply((E) value));
		}
		
		if(labelProvider != null)
		{
			label.setText(labelProvider.apply((E) value));
		}
		
		return label;
	}
}
