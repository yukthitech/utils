package com.yukthitech.autox.ide.projpropdialog;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import com.yukthitech.autox.ide.model.Project;

public class ProjectPropertiesClassPath extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel panel_1;
	private JButton btnNewButton;
	private JButton btnAddClasspath;
	private JButton btnRemove;
	private JFileChooser addJarfileChooser;
	private JFileChooser addClassPathFileChooser;
	private Set<String> setOfEntries = new HashSet<>();
	private DefaultListModel<String> listModel = new DefaultListModel<>();

	private Project project = new Project();
	private JList<String> list;
	private JScrollPane scrollPane;

	/**
	 * Create the panel.
	 */
	public ProjectPropertiesClassPath()
	{
		setLayout(new BorderLayout(0, 0));
		// add(getList_1(), BorderLayout.CENTER);
		add(getScrollPane(), BorderLayout.CENTER);
		add(getPanel_1(), BorderLayout.EAST);

	}

	private JPanel getPanel_1()
	{
		if(panel_1 == null)
		{
			panel_1 = new JPanel();
			panel_1.setBorder(new EmptyBorder(5, 5, 5, 5));
			GridBagLayout gbl_panel_1 = new GridBagLayout();
			gbl_panel_1.columnWidths = new int[] { 101, 0 };
			gbl_panel_1.rowHeights = new int[] { 20, 20, 20, 20, 0 };
			gbl_panel_1.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
			gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
			panel_1.setLayout(gbl_panel_1);
			GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
			gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
			gbc_btnNewButton.gridx = 0;
			gbc_btnNewButton.gridy = 0;
			panel_1.add(getBtnNewButton(), gbc_btnNewButton);
			GridBagConstraints gbc_btnAddClasspath = new GridBagConstraints();
			gbc_btnAddClasspath.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnAddClasspath.insets = new Insets(0, 0, 5, 0);
			gbc_btnAddClasspath.gridx = 0;
			gbc_btnAddClasspath.gridy = 1;
			panel_1.add(getBtnAddClasspath(), gbc_btnAddClasspath);
			GridBagConstraints gbc_btnRemove = new GridBagConstraints();
			gbc_btnRemove.insets = new Insets(0, 0, 5, 0);
			gbc_btnRemove.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnRemove.gridx = 0;
			gbc_btnRemove.gridy = 2;
			panel_1.add(getBtnRemove(), gbc_btnRemove);
		}
		return panel_1;
	}

	private JButton getBtnNewButton()
	{
		if(btnNewButton == null)
		{
			btnNewButton = new JButton("Add Jar");
			btnNewButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					addJarfileChooser = new JFileChooser();
					addJarfileChooser.setAcceptAllFileFilterUsed(false);
					addJarfileChooser.setFileFilter(new FileFilter()
					{
						@Override
						public String getDescription()
						{
							return "*.jar *.zip";
						}

						@Override
						public boolean accept(File f)
						{
							return f.isDirectory() || f.getName().endsWith(".jar") || f.getName().endsWith(".zip");
						}
					});
					int result = addJarfileChooser.showOpenDialog(getParent());
					if(result == addJarfileChooser.APPROVE_OPTION)
					{
						String jarPath = addJarfileChooser.getSelectedFile().getAbsolutePath();
						if(!listModel.contains(jarPath))
						{
							listModel.addElement(jarPath);
							list.setModel(listModel);
						}
					}

				}
			});
		}
		return btnNewButton;
	}

	private JButton getBtnAddClasspath()
	{
		if(btnAddClasspath == null)
		{
			btnAddClasspath = new JButton("Add Directory");
			btnAddClasspath.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					addClassPathFileChooser = new JFileChooser();
					addClassPathFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int result = addClassPathFileChooser.showOpenDialog(getParent());
					if(result == addClassPathFileChooser.APPROVE_OPTION)
					{
						String jarPath = addClassPathFileChooser.getSelectedFile().getAbsolutePath();
						if(!listModel.contains(jarPath))
						{
							listModel.addElement(jarPath);
							list.setModel(listModel);
						}
					}
				}
			});
		}
		return btnAddClasspath;
	}

	private JButton getBtnRemove()
	{
		if(btnRemove == null)
		{
			btnRemove = new JButton("Remove");
			btnRemove.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					listModel.remove(list.getSelectedIndex());
					list.setModel(listModel);
				}
			});
		}
		return btnRemove;
	}

	public void setProject(Project project)
	{

		Set<String> set = project.getClassPathEntriesList();
		if(set != null && !set.isEmpty())
			for(String s : set)
			{
				listModel.addElement(s);
			}
		super.setVisible(true);
	}

	public Set<String> saveChanges()
	{
		for(int i = 0; i < listModel.size(); i++)
		{
			setOfEntries.add(listModel.getElementAt(i));
		}
		return setOfEntries;
	}

	private JList getList_1()
	{
		if(list == null)
		{
			list = new JList();
		}
		return list;
	}

	private JScrollPane getScrollPane()
	{
		if(scrollPane == null)
		{
			scrollPane = new JScrollPane(getList_1());
		}
		return scrollPane;
	}
}
