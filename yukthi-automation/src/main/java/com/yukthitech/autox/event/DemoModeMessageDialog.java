package com.yukthitech.autox.event;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import com.yukthitech.autox.test.TestCase;
import com.yukthitech.autox.test.TestSuite;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class DemoModeMessageDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	private JLabel mssgLbl;
	
	private JLabel lblTestSuiteName;
	
	private JLabel lblTestCaseName;
	
	private JTextPane textPane;

	/**
	 * Create the frame.
	 */
	public DemoModeMessageDialog()
	{
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new EmptyBorder(10, 10, 10, 10)));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		mssgLbl = new JLabel("Some Message Goes here....");
		mssgLbl.setBorder(new EmptyBorder(5, 5, 20, 5));
		mssgLbl.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_mssgLbl = new GridBagConstraints();
		gbc_mssgLbl.fill = GridBagConstraints.VERTICAL;
		gbc_mssgLbl.anchor = GridBagConstraints.WEST;
		gbc_mssgLbl.gridwidth = 2;
		gbc_mssgLbl.insets = new Insets(5, 5, 5, 0);
		gbc_mssgLbl.gridx = 0;
		gbc_mssgLbl.gridy = 0;
		contentPane.add(mssgLbl, gbc_mssgLbl);
		
		JLabel lblTestSuite = new JLabel("Test Suite: ");
		GridBagConstraints gbc_lblTestSuite = new GridBagConstraints();
		gbc_lblTestSuite.anchor = GridBagConstraints.WEST;
		gbc_lblTestSuite.insets = new Insets(0, 0, 5, 5);
		gbc_lblTestSuite.gridx = 0;
		gbc_lblTestSuite.gridy = 2;
		contentPane.add(lblTestSuite, gbc_lblTestSuite);
		
		lblTestSuiteName = new JLabel("test suite name");
		lblTestSuiteName.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_lblTestSuiteName = new GridBagConstraints();
		gbc_lblTestSuiteName.anchor = GridBagConstraints.WEST;
		gbc_lblTestSuiteName.insets = new Insets(0, 10, 5, 0);
		gbc_lblTestSuiteName.gridx = 1;
		gbc_lblTestSuiteName.gridy = 2;
		contentPane.add(lblTestSuiteName, gbc_lblTestSuiteName);
		
		JLabel lblTestCase = new JLabel("Test Case: ");
		GridBagConstraints gbc_lblTestCase = new GridBagConstraints();
		gbc_lblTestCase.insets = new Insets(0, 0, 5, 5);
		gbc_lblTestCase.gridx = 0;
		gbc_lblTestCase.gridy = 3;
		contentPane.add(lblTestCase, gbc_lblTestCase);
		
		lblTestCaseName = new JLabel("test case name");
		lblTestCaseName.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_lblTestCaseName = new GridBagConstraints();
		gbc_lblTestCaseName.anchor = GridBagConstraints.WEST;
		gbc_lblTestCaseName.insets = new Insets(0, 10, 5, 0);
		gbc_lblTestCaseName.gridx = 1;
		gbc_lblTestCaseName.gridy = 3;
		contentPane.add(lblTestCaseName, gbc_lblTestCaseName);
		
		JLabel lblDescription = new JLabel("Description:");
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.insets = new Insets(0, 0, 5, 5);
		gbc_lblDescription.gridx = 0;
		gbc_lblDescription.gridy = 4;
		contentPane.add(lblDescription, gbc_lblDescription);
		
		textPane = new JTextPane();
		textPane.setBackground(SystemColor.inactiveCaptionBorder);
		textPane.setFont(new Font("Tahoma", Font.BOLD, 13));
		GridBagConstraints gbc_textPane = new GridBagConstraints();
		gbc_textPane.fill = GridBagConstraints.BOTH;
		gbc_textPane.gridx = 1;
		gbc_textPane.gridy = 5;
		contentPane.add(textPane, gbc_textPane);
	}
	
	public void display(String mssg, TestSuite testSuite, TestCase testCase, Boolean state)
	{
		if(state == null)
		{
			mssgLbl.setForeground(Color.black);
			mssgLbl.setBackground(null);
			mssgLbl.setOpaque(false);
		}
		else
		{
			mssgLbl.setOpaque(true);
			mssgLbl.setForeground(Color.white);
			mssgLbl.setBackground(state ? Color.GREEN : Color.RED);
		}
		
		mssgLbl.setText(mssg);
		lblTestSuiteName.setText(testSuite.getName());
		lblTestCaseName.setText(testCase.getName());
		textPane.setText(testCase.getDescription());
		
		setSize(450, 300);		
		setVisible(true);
		setLocationRelativeTo(null);
	}
	
	public void close()
	{
		setVisible(false);
	}
}
