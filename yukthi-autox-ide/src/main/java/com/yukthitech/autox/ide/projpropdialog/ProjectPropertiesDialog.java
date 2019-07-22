package com.yukthitech.autox.ide.projpropdialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class ProjectPropertiesDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private Project project;

	private ProjectPropTestSuiteFolderPanel projectPropertiesTestSuiteFolders;
	private ProjectPropertiesClassPath projectPropertiesClassPath;

	@Autowired
	private IdeContext ideContext;
	
	/**
	 * Create the dialog.
	 */
	public ProjectPropertiesDialog(Window window)
	{
		super(window, ModalityType.APPLICATION_MODAL);

		setBounds(100, 100, 550, 400);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			contentPanel.add(tabbedPane);
			{
				projectPropertiesTestSuiteFolders = new ProjectPropTestSuiteFolderPanel();
				tabbedPane.addTab("TestSuites Folders", null, projectPropertiesTestSuiteFolders, null);
			}
			{
				projectPropertiesClassPath = new ProjectPropertiesClassPath();
				tabbedPane.addTab("ClassPath", null, projectPropertiesClassPath, null);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Apply & close");
				okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						saveProjectProperties();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Close");
				cancelButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	@Override
	protected JRootPane createRootPane()
	{
		JRootPane rootPane = new JRootPane();
		KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");

		Action actionListener = new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent actionEvent)
			{
				setVisible(false);
			}
		};
		InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, "ESCAPE");
		rootPane.getActionMap().put("ESCAPE", actionListener);

		return rootPane;
	}

	public Project display(IdeContext ideContext)
	{
		this.project = null;
		this.project = ideContext.getActiveProject();
		projectPropertiesClassPath.setProject(project);
		projectPropertiesTestSuiteFolders.setProject(project);
		setTitle(project.getName() + " Properties");

		IdeUtils.centerOnScreen(this);
		super.setVisible(true);
		
		return project;
	}

	protected void saveProjectProperties()
	{
		try
		{
			project.setTestSuiteFolders(projectPropertiesTestSuiteFolders.saveChanges());
			project.setClassPathEntries(projectPropertiesClassPath.saveChanges());
		}catch(InvalidStateException ex)
		{
			JOptionPane.showMessageDialog(this, ex.getMessage());
			return;
		}

		project.save();
		ideContext.getProxy().projectStateChanged(project);
		
		dispose();
	}
}
