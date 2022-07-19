package com.yukthitech.swing.test;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.yukthitech.swing.DropDownButton;
import com.yukthitech.swing.DropDownItem;
import com.yukthitech.swing.combo.SearchableComboBox;
import com.yukthitech.swing.combo.StringSearchableDataProvider;
import com.yukthitech.swing.common.SwingUtils;

import javax.swing.JButton;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Insets;

public class TestFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final JPanel panel = new JPanel();
	private final JButton btnNewButton = new JButton("Test");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					TestFrame frame = new TestFrame();
					frame.setVisible(true);
				} catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TestFrame()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		
		contentPane.add(panel, BorderLayout.NORTH);
		
		//panel.add(btnNewButton);
		
		DropDownButton ddBut = new DropDownButton("", SwingUtils.loadIconWithoutBorder("/swing-icons/checkbox-selected.svg", 16));
		panel.add(ddBut);

		addItem("test1", ddBut);
		addItem("test2", ddBut);
		addItem("test3", ddBut);
		addItem("test4", ddBut);
		addItem("test5", ddBut);
		addItem("test6", ddBut);
		
		ddBut.addStaticMenuItem(new JMenuItem("Run time Configs..."));

		StringSearchableDataProvider<String> dataProvider = new StringSearchableDataProvider<String>(Arrays.asList(
			"apple",
			"banana",
			"orange",
			"pine apple",
			"mango",
			"sweet lime",
			"lime",
			"goa"
		));
		
		SearchableComboBox<String> combo = new SearchableComboBox<String>(dataProvider);
		panel.add(combo);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0};
		gbl_panel_1.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel lblNewLabel = new JLabel("Test123");
		lblNewLabel.setOpaque(true);
		lblNewLabel.setBackground(Color.PINK);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel_1.add(lblNewLabel, gbc_lblNewLabel);
		
		JButton btnNewButton_1 = new JButton("New button");
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.gridx = 1;
		gbc_btnNewButton_1.gridy = 0;
		panel_1.add(btnNewButton_1, gbc_btnNewButton_1);
	}
	
	private void addItem(String text, DropDownButton ddBut)
	{
		DropDownItem mi = new DropDownItem(text);
		ddBut.addItem(mi);
		
		mi.addActionListener(e -> 
		{
			JOptionPane.showMessageDialog(TestFrame.this, text);
		});
	}

}
