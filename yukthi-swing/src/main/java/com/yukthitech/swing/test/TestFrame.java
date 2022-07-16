package com.yukthitech.swing.test;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.yukthitech.swing.DropDownButton;
import com.yukthitech.swing.common.SwingUtils;

import javax.swing.JButton;
import java.awt.FlowLayout;

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
		
		DropDownButton ddBut = new DropDownButton("", SwingUtils.loadIconWithoutBorder("/swing-icons/run.svg", 16));
		panel.add(ddBut);

		addItem("test1", ddBut);
		addItem("test2", ddBut);
		addItem("test3", ddBut);
		addItem("test4", ddBut);
		addItem("test5", ddBut);
		addItem("test6", ddBut);
		addItem("test7", ddBut);
		addItem("test8", ddBut);
		addItem("test9", ddBut);
		addItem("test10", ddBut);
		addItem("test11", ddBut);
		addItem("test12", ddBut);
		
		ddBut.addStaticMenuItem(new JMenuItem("Run time Configs..."));
		
	}
	
	private void addItem(String text, DropDownButton ddBut)
	{
		JMenuItem mi = new JMenuItem(text);
		ddBut.addItem(mi);
		
		mi.addActionListener(e -> 
		{
			JOptionPane.showMessageDialog(TestFrame.this, text);
		});
	}

}
