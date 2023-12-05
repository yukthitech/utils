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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import com.yukthitech.swing.common.SwingUtils;

public class ProgressDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	public class ProgressUpdater
	{
		public void setProgress(String text, int value)
		{
			progressMssgLbl.setText(text);
			progressBar.setValue(value);
			
			try
			{
				Thread.sleep(10);
			}catch(Exception ex)
			{}
		}
	}
	
	private final JPanel contentPanel = new JPanel();
	private final JLabel progressMssgLbl = new JLabel("Progress message....");
	private final JProgressBar progressBar = new JProgressBar();

	public ProgressDialog()
	{
		setAlwaysOnTop(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 450, 107);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		
		GridBagConstraints gbc_progressMssgLbl = new GridBagConstraints();
		gbc_progressMssgLbl.anchor = GridBagConstraints.WEST;
		gbc_progressMssgLbl.insets = new Insets(0, 0, 5, 0);
		gbc_progressMssgLbl.gridx = 0;
		gbc_progressMssgLbl.gridy = 0;
		contentPanel.add(progressMssgLbl, gbc_progressMssgLbl);
		
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 1;
		progressBar.setStringPainted(true);
		progressBar.setValue(30);
		contentPanel.add(progressBar, gbc_progressBar);
	}

	public void display(String title, String initMssg, Consumer<ProgressUpdater> executable)
	{
		Thread progressThread = new Thread("Progress Executable")
		{
			public void run()
			{
				executable.accept(new ProgressUpdater());
				ProgressDialog.this.setVisible(false);
			}
		};
		
		progressBar.setValue(0);
		super.setTitle(title);
		progressMssgLbl.setText(initMssg);
		SwingUtils.centerOnScreen(this);
		
		progressThread.start();
		
		setVisible(true);
	}
}
