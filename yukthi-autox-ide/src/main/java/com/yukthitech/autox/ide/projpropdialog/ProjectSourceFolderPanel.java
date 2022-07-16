package com.yukthitech.autox.ide.projpropdialog;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.border.TitledBorder;

import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.model.Project;

import javax.swing.JList;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.BorderLayout;

@Component
public class ProjectSourceFolderPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private JList<String> sourceFolderList;
	private JList<String> resourceFolderList;
	
	private Project project;

	/**
	 * Create the panel.
	 */
	public ProjectSourceFolderPanel()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0};
		gridBagLayout.rowHeights = new int[] {0, 0};
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0};
		setLayout(gridBagLayout);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Source Folders", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0};
		gbl_panel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		panel.add(scrollPane, gbc_scrollPane);
		
		sourceFolderList = new JList<String>();
		scrollPane.setViewportView(sourceFolderList);
		
		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_2.gridx = 1;
		gbc_panel_2.gridy = 0;
		panel.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{0, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		JButton addSrcBut = new JButton("Add");
		GridBagConstraints gbc_addSrcBut = new GridBagConstraints();
		gbc_addSrcBut.fill = GridBagConstraints.HORIZONTAL;
		gbc_addSrcBut.insets = new Insets(0, 0, 5, 0);
		gbc_addSrcBut.gridx = 0;
		gbc_addSrcBut.gridy = 0;
		panel_2.add(addSrcBut, gbc_addSrcBut);
		
		JButton deleteSrcBut = new JButton("Delete");
		GridBagConstraints gbc_deleteSrcBut = new GridBagConstraints();
		gbc_deleteSrcBut.fill = GridBagConstraints.HORIZONTAL;
		gbc_deleteSrcBut.gridx = 0;
		gbc_deleteSrcBut.gridy = 1;
		panel_2.add(deleteSrcBut, gbc_deleteSrcBut);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Resource Folders", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 0;
		panel_1.add(scrollPane_1, gbc_scrollPane_1);
		
		resourceFolderList = new JList<String>();
		scrollPane_1.setViewportView(resourceFolderList);
		
		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_3.gridx = 1;
		gbc_panel_3.gridy = 0;
		panel_1.add(panel_3, gbc_panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[]{0, 0};
		gbl_panel_3.rowHeights = new int[]{0, 0, 0};
		gbl_panel_3.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel_3.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel_3.setLayout(gbl_panel_3);
		
		JButton addResBut = new JButton("Add");
		GridBagConstraints gbc_addResBut = new GridBagConstraints();
		gbc_addResBut.fill = GridBagConstraints.HORIZONTAL;
		gbc_addResBut.insets = new Insets(0, 0, 5, 0);
		gbc_addResBut.gridx = 0;
		gbc_addResBut.gridy = 0;
		panel_3.add(addResBut, gbc_addResBut);
		
		JButton delResBut = new JButton("Delete");
		GridBagConstraints gbc_delResBut = new GridBagConstraints();
		gbc_delResBut.fill = GridBagConstraints.HORIZONTAL;
		gbc_delResBut.gridx = 0;
		gbc_delResBut.gridy = 1;
		panel_3.add(delResBut, gbc_delResBut);
		
		JPanel panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.anchor = GridBagConstraints.EAST;
		gbc_panel_4.gridwidth = 2;
		gbc_panel_4.insets = new Insets(0, 0, 0, 5);
		gbc_panel_4.fill = GridBagConstraints.VERTICAL;
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 1;
		panel_1.add(panel_4, gbc_panel_4);
		
		JButton applyChanges = new JButton("Apply");
		panel_4.add(applyChanges);
	}
	
	public void setProject(Project project)
	{
		this.project = project;
	}
	
	private void addSourceFolder(ActionEvent e)
	{
		
	}
	
	private void remveSourceFolder(ActionEvent e)
	{
		
	}

	private void addResourceFolder(ActionEvent e)
	{
		
	}

	private void deleteResourceFolder(ActionEvent e)
	{
		
	}

	private void applyChanges(ActionEvent e)
	{
		
	}
}
