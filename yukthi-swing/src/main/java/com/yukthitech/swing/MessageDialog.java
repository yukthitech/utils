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
package com.yukthitech.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.yukthitech.utils.ObjectWrapper;

/**
 * Used to display message box with dynamic actions. And the user action button is displayed.
 * 
 * @author akranthikiran
 */
public class MessageDialog extends EscapableDialog
{
	private static final long serialVersionUID = 1L;
	
	private static MessageDialog instance = new MessageDialog();

	private final JPanel contentPanel = new JPanel();
	private final JPanel buttonPanel = new JPanel();
	private final JButton btnNewButton = new JButton("Cancel");
	private final JLabel mssgLbl = new JLabel("New label");

	/**
	 * Create the dialog.
	 */
	public MessageDialog()
	{
		super.setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 529, 171);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(null);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		
		GridBagConstraints gbc_mssgLbl = new GridBagConstraints();
		gbc_mssgLbl.insets = new Insets(10, 5, 15, 3);
		gbc_mssgLbl.anchor = GridBagConstraints.NORTHWEST;
		gbc_mssgLbl.gridx = 0;
		gbc_mssgLbl.gridy = 0;
		contentPanel.add(mssgLbl, gbc_mssgLbl);
		FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		buttonPanel.setBorder(new EmptyBorder(0, 10, 3, 2));
		
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		buttonPanel.add(btnNewButton);
	}
	
	private static void centerOnScreen(Component c)
	{
		final int width = c.getWidth();
		final int height = c.getHeight();
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width / 2) - (width / 2);
		int y = (screenSize.height / 2) - (height / 2);

		c.setLocation(x, y);
	}

	
	public static String display(String title, String mssg, List<String> actions)
	{
		instance.setTitle(title);
		instance.mssgLbl.setText(mssg);
		
		instance.buttonPanel.removeAll();
		
		ObjectWrapper<String> res = new ObjectWrapper<>();
		
		actions.forEach(act -> 
		{
			JButton button = new JButton(act);
			instance.buttonPanel.add(button);
			
			button.addActionListener(e -> 
			{
				res.setValue(act);
				instance.setVisible(false);
			});
		});
		
		instance.pack();
		centerOnScreen(instance);
		instance.setVisible(true);
		return res.getValue();
	}
	
	public static void main(String[] args)
	{
		String res = display("test", "This is my first message?", Arrays.asList("Yes", "No", "Save All", "Cancel"));
		System.out.println(res);
	}
}
