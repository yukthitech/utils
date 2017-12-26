package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.yukthitech.autox.ide.model.IdeState;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.awt.event.ActionEvent;

public class SettingsDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private final JLabel lblAppConfigurationFile = new JLabel("App Configuration File: ");
	private final JTextField fldConfigFile = new JTextField();
	private final JLabel lblCommandLineArgs = new JLabel("Command Line Args: ");
	private final JTextField fldCmdLineArgs = new JTextField();

	private IdeState ideState;
	
	/**
	 * Create the dialog.
	 */
	public SettingsDialog(Frame frame)
	{
		super(frame, ModalityType.APPLICATION_MODAL);
		
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setTitle("Settings");
		fldCmdLineArgs.setColumns(10);
		fldConfigFile.setColumns(10);
		setBounds(100, 100, 450, 155);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		
		GridBagConstraints gbc_lblAppConfigurationFile = new GridBagConstraints();
		gbc_lblAppConfigurationFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblAppConfigurationFile.anchor = GridBagConstraints.EAST;
		gbc_lblAppConfigurationFile.gridx = 0;
		gbc_lblAppConfigurationFile.gridy = 0;
		contentPanel.add(lblAppConfigurationFile, gbc_lblAppConfigurationFile);
		
		GridBagConstraints gbc_fldConfigFile = new GridBagConstraints();
		gbc_fldConfigFile.insets = new Insets(0, 0, 5, 0);
		gbc_fldConfigFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_fldConfigFile.gridx = 1;
		gbc_fldConfigFile.gridy = 0;
		contentPanel.add(fldConfigFile, gbc_fldConfigFile);
		
		GridBagConstraints gbc_lblCommandLineArgs = new GridBagConstraints();
		gbc_lblCommandLineArgs.anchor = GridBagConstraints.EAST;
		gbc_lblCommandLineArgs.insets = new Insets(0, 0, 0, 5);
		gbc_lblCommandLineArgs.gridx = 0;
		gbc_lblCommandLineArgs.gridy = 1;
		contentPanel.add(lblCommandLineArgs, gbc_lblCommandLineArgs);
		
		GridBagConstraints gbc_fldCmdLineArgs = new GridBagConstraints();
		gbc_fldCmdLineArgs.fill = GridBagConstraints.HORIZONTAL;
		gbc_fldCmdLineArgs.gridx = 1;
		gbc_fldCmdLineArgs.gridy = 1;
		contentPanel.add(fldCmdLineArgs, gbc_fldCmdLineArgs);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Apply");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) 
					{
						persisteState();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
	
	public void display(IdeState ideState)
	{
		this.ideState = ideState;
		
		fldConfigFile.setText(ideState.getApplicationConfigFile());
		
		if(ideState.getCommandLineArguments() != null)
		{
			fldCmdLineArgs.setText(
				Arrays.asList(ideState.getCommandLineArguments())
					.stream()
					.collect(Collectors.joining(" "))
			);
		}
		
		super.setVisible(true);
	}

	private void persisteState()
	{
		String configFile = fldConfigFile.getText();
		String cmdLineArgs = fldCmdLineArgs.getText();
		
		IdeState ideState = new IdeState();
		
		try
		{
			ideState.setApplicationConfigFile(configFile);
			
			if(cmdLineArgs.trim().length() > 0)
			{
				ideState.setCommandLineArguments(cmdLineArgs.split("\\s+"));
			}
			
			ideState.validate();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, ex.getMessage());
			return;
		}
		
		this.ideState.setApplicationConfigFile(configFile);
		this.ideState.setCommandLineArguments(ideState.getCommandLineArguments());
		
		try
		{
			this.ideState.save();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "An error occurred while persisting ide-state. Error: " + ex);
			return;
		}
		
		super.setVisible(false);
	}
}
