package com.yukthitech.swing.combo;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import com.yukthitech.swing.common.SwingUtils;

public class SearchableComboBox<E> extends JComboBox<E>
{
	private static final long serialVersionUID = 1L;

	/**
	 * Data provider to search items.
	 */
	private ISearchableDataProvider<E> dataProvider;
	
	private JTextField editField;
	
	private String currentFilter;
	
	public SearchableComboBox(ISearchableDataProvider<E> dataProvider)
	{
		this.dataProvider = dataProvider;
		super.setEditable(true);
		
		super.setEditor(new BasicComboBoxEditor() 
		{
			@Override
			public void setItem(Object anObject)
			{
				SearchableComboBox.this.setSelectedItem(anObject);
				System.out.println(SearchableComboBox.this.getSelectedItem());
			}
		});
		
		editField = (JTextField) super.getEditor().getEditorComponent();
		
		editField.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				doFilter();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				doFilter();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e)
			{
			}
		});
		
		editField.addFocusListener(new FocusListener() 
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				//onFocusChange(true);
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				//onFocusChange(false);
			}
		});
		
		doFilter();
	}
	
	private void doFilter()
	{
		SwingUtils.executeUiTask(() -> 
		{
			String filterText = editField.getText();
			
			if(filterText.equals(currentFilter))
			{
				return;
			}
			
			System.out.println("Got text: " + filterText);
			
			List<E> items = dataProvider.fetchItems(filterText);
			
			setEditable(false);
			super.removeAllItems();
			
			System.out.println("Got items: " + items);
			items.forEach(item -> addItem(item));
			
			currentFilter = filterText;
			
			setEditable(true);
			setPopupVisible(true);
		});
	}
	
	private void onFocusChange(boolean focusGained)
	{
		String filterText = editField.getText();
		
		if(filterText.length() > 0)
		{
			setPopupVisible(focusGained);
		}
	}
}
