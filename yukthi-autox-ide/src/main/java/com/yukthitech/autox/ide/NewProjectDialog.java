package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.ide.model.Project;

public class NewProjectDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger(NewProjectDialog.class);
	
	private final JPanel contentPanel = new JPanel();
	private JTextField appConfigPath;
	private JTextField appPropertyPath;
	private JTextField testSuiteFolderPath;
	private JTextField baseFolderPath;
	private JTextField projectName;
	private JFileChooser fileChooser= new JFileChooser();
	private Project project = new Project();

	/**
	 * Create the dialog.
	 */
	public NewProjectDialog(Window window)
	{
		super(window, ModalityType.APPLICATION_MODAL);
		
		setBounds(100, 100, 450, 252);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 64, 12, 257, 45, 0 };
		gbl_contentPanel.rowHeights = new int[] { 22, 20, 23, 23, 23, 23, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);

		JLabel lblNewProject = new JLabel("New Project");
		lblNewProject.setFont(new Font("Tahoma", Font.BOLD, 18));
		GridBagConstraints gbc_lblNewProject = new GridBagConstraints();
		gbc_lblNewProject.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblNewProject.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewProject.gridwidth = 3;
		gbc_lblNewProject.gridx = 0;
		gbc_lblNewProject.gridy = 0;
		contentPanel.add(lblNewProject, gbc_lblNewProject);

		JLabel lblName = new JLabel("Name");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.WEST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 1;
		contentPanel.add(lblName, gbc_lblName);

		projectName = new JTextField(project.getName());
		projectName.setColumns(10);
		GridBagConstraints gbc_projectName = new GridBagConstraints();
		gbc_projectName.anchor = GridBagConstraints.NORTH;
		gbc_projectName.fill = GridBagConstraints.HORIZONTAL;
		gbc_projectName.insets = new Insets(0, 0, 5, 0);
		gbc_projectName.gridwidth = 2;
		gbc_projectName.gridx = 2;
		gbc_projectName.gridy = 1;
		contentPanel.add(projectName, gbc_projectName);

		JLabel lblBaseFolder = new JLabel("Base folder");
		GridBagConstraints gbc_lblBaseFolder = new GridBagConstraints();
		gbc_lblBaseFolder.anchor = GridBagConstraints.WEST;
		gbc_lblBaseFolder.insets = new Insets(0, 0, 5, 5);
		gbc_lblBaseFolder.gridx = 0;
		gbc_lblBaseFolder.gridy = 2;
		contentPanel.add(lblBaseFolder, gbc_lblBaseFolder);

		JButton btnBaseFolder = new JButton("...");
		btnBaseFolder.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onBaseFolderSelect();
			}
		});

		baseFolderPath = new JTextField();
		GridBagConstraints gbc_baseFolderPath = new GridBagConstraints();
		gbc_baseFolderPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_baseFolderPath.insets = new Insets(0, 0, 5, 5);
		gbc_baseFolderPath.gridx = 2;
		gbc_baseFolderPath.gridy = 2;
		contentPanel.add(baseFolderPath, gbc_baseFolderPath);
		baseFolderPath.setColumns(10);
		GridBagConstraints gbc_btnBaseFolder = new GridBagConstraints();
		gbc_btnBaseFolder.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnBaseFolder.insets = new Insets(0, 0, 5, 0);
		gbc_btnBaseFolder.gridx = 3;
		gbc_btnBaseFolder.gridy = 2;
		contentPanel.add(btnBaseFolder, gbc_btnBaseFolder);

		JButton btnAppConfig = new JButton("...");
		btnAppConfig.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				
				fileChooser.setDialogTitle("Select Base folder");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setAcceptAllFileFilterUsed(false);
				if(fileChooser.showOpenDialog(btnAppConfig) == JFileChooser.APPROVE_OPTION)
				{
					appConfigPath.setText(project.getAppConfigFilePath());
				}
			}
		});

		JLabel lblAppConfigFile = new JLabel("App config file");
		GridBagConstraints gbc_lblAppConfigFile = new GridBagConstraints();
		gbc_lblAppConfigFile.anchor = GridBagConstraints.WEST;
		gbc_lblAppConfigFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblAppConfigFile.gridwidth = 2;
		gbc_lblAppConfigFile.gridx = 0;
		gbc_lblAppConfigFile.gridy = 3;
		contentPanel.add(lblAppConfigFile, gbc_lblAppConfigFile);

		appConfigPath = new JTextField(project.getAppConfigFilePath());
		appConfigPath.setColumns(10);
		GridBagConstraints gbc_appConfigPath = new GridBagConstraints();
		gbc_appConfigPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_appConfigPath.insets = new Insets(0, 0, 5, 5);
		gbc_appConfigPath.gridx = 2;
		gbc_appConfigPath.gridy = 3;
		contentPanel.add(appConfigPath, gbc_appConfigPath);
		GridBagConstraints gbc_btnAppConfig = new GridBagConstraints();
		gbc_btnAppConfig.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnAppConfig.insets = new Insets(0, 0, 5, 0);
		gbc_btnAppConfig.gridx = 3;
		gbc_btnAppConfig.gridy = 3;
		contentPanel.add(btnAppConfig, gbc_btnAppConfig);

		JButton btnAppProperty = new JButton("...");
		btnAppProperty.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				fileChooser.setDialogTitle("Select Base folder");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setAcceptAllFileFilterUsed(false);
				if(fileChooser.showOpenDialog(btnAppProperty) == JFileChooser.APPROVE_OPTION)
				{
					appPropertyPath.setText("./app.properties");
				}
			}
		});

		JLabel lblAppProperty = new JLabel("App property");
		GridBagConstraints gbc_lblAppProperty = new GridBagConstraints();
		gbc_lblAppProperty.anchor = GridBagConstraints.WEST;
		gbc_lblAppProperty.insets = new Insets(0, 0, 5, 5);
		gbc_lblAppProperty.gridx = 0;
		gbc_lblAppProperty.gridy = 4;
		contentPanel.add(lblAppProperty, gbc_lblAppProperty);

		appPropertyPath = new JTextField(project.getAppPropertyFilePath());
		appPropertyPath.setColumns(10);
		GridBagConstraints gbc_appPropertyPath = new GridBagConstraints();
		gbc_appPropertyPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_appPropertyPath.insets = new Insets(0, 0, 5, 5);
		gbc_appPropertyPath.gridx = 2;
		gbc_appPropertyPath.gridy = 4;
		contentPanel.add(appPropertyPath, gbc_appPropertyPath);
		GridBagConstraints gbc_btnAppProperty = new GridBagConstraints();
		gbc_btnAppProperty.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnAppProperty.insets = new Insets(0, 0, 5, 0);
		gbc_btnAppProperty.gridx = 3;
		gbc_btnAppProperty.gridy = 4;
		contentPanel.add(btnAppProperty, gbc_btnAppProperty);

		JLabel lblNewLabel = new JLabel("Test suite folder");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridwidth = 2;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 5;
		contentPanel.add(lblNewLabel, gbc_lblNewLabel);

		JButton btnTestSuite = new JButton("...");
		btnTestSuite.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				fileChooser.setDialogTitle("Select Base folder");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setAcceptAllFileFilterUsed(false);
				if(fileChooser.showOpenDialog(btnTestSuite) == JFileChooser.APPROVE_OPTION)
				{
					testSuiteFolderPath.setText("./testsuites");
				}
			}
		});

		testSuiteFolderPath = new JTextField(project.getTestsuiteFolderPath());
		testSuiteFolderPath.setColumns(10);
		GridBagConstraints gbc_testSuiteFolderPath = new GridBagConstraints();
		gbc_testSuiteFolderPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_testSuiteFolderPath.insets = new Insets(0, 0, 0, 5);
		gbc_testSuiteFolderPath.gridx = 2;
		gbc_testSuiteFolderPath.gridy = 5;
		contentPanel.add(testSuiteFolderPath, gbc_testSuiteFolderPath);
		GridBagConstraints gbc_btnTestSuite = new GridBagConstraints();
		gbc_btnTestSuite.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnTestSuite.gridx = 3;
		gbc_btnTestSuite.gridy = 5;
		contentPanel.add(btnTestSuite, gbc_btnTestSuite);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Create");
				okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						onCreateProject();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	private void onCreateProject()
	{
		if(StringUtils.isBlank(projectName.getText()))
		{
			JOptionPane.showMessageDialog(NewProjectDialog.this, "Project name can not be empty");
			return;
		}

		if(StringUtils.isBlank(baseFolderPath.getText()))
		{
			JOptionPane.showMessageDialog(NewProjectDialog.this, "Base folder path can not be empty");
			return;
		}
		
		if(StringUtils.isBlank(appConfigPath.getText()))
		{
			JOptionPane.showMessageDialog(NewProjectDialog.this, "appCofig file name must be given");
			return;
		}
		else
		{
			String str=appConfigPath.getText().toLowerCase();
		
			if(!str.endsWith(".xml"))
			{
				JOptionPane.showMessageDialog(NewProjectDialog.this, " file extension must be .xml");
				return;
			}
		}
		
		if(StringUtils.isBlank(appPropertyPath.getText()))
		{
			JOptionPane.showMessageDialog(NewProjectDialog.this, "appProperties file name must be given");
			return;
		}
		else
		{
			String str=appPropertyPath.getText().toLowerCase();
			if(!str.endsWith(".properties"))
			{
				JOptionPane.showMessageDialog(NewProjectDialog.this, "file extension must be .properties");
				return;
			}
		}
		
		if(StringUtils.isBlank(testSuiteFolderPath.getText()))
		{
			JOptionPane.showMessageDialog(NewProjectDialog.this, "testSuite folder name must be given");
			return;
		}
		
		project.setName(projectName.getText());
		project.setProjectFilePath(new File(baseFolderPath.getText(), Project.PROJECT_FILE_NAME).getPath());
		project.setAppConfigFilePath(appConfigPath.getText());
		project.setAppPropertyFilePath(appPropertyPath.getText());
		project.setTestsuiteFolderPath(testSuiteFolderPath.getText());
		
		try
		{
			project.createProject();
			logger.debug("Created new project with name '{}' at path: {}", project.getName(), project.getBaseFolderPath());
			
			this.setVisible(false);
		} catch(IOException e1)
		{
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(NewProjectDialog.this, e1.toString());
		}
	}
	
	private void onBaseFolderSelect()
	{
		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select Base folder");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		int res = fileChooser.showOpenDialog(this);
		
		if(res == JFileChooser.APPROVE_OPTION)
		{
			File baseFolderFile = fileChooser.getSelectedFile();
			String projectName = this.projectName.getText();
			
			if(!baseFolderFile.getName().equals(projectName))
			{
				baseFolderFile = new File(baseFolderFile, projectName);
			}
		
			baseFolderPath.setText(baseFolderFile.getPath());
		}
	}

	public Project display()
	{
		project = new Project();
		super.setVisible(true);
		
		return project;
	}
}
