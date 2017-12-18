package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

public class InteractiveEditor extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final JPanel panel = new JPanel();
	private final JButton btnNewButton = new JButton("New button");
	private final JSplitPane splitPane = new JSplitPane();
	private final JSplitPane splitPane_1 = new JSplitPane();
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private final JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
	private final IdeInputPanel ideInputPanel = new IdeInputPanel();
	private final StepLogPanel stepLogPanel = new StepLogPanel();

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
					InteractiveEditor frame = new InteractiveEditor();
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
	public InteractiveEditor()
	{
		setTitle("AutoX Interactive Editor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 940, 606);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		contentPane.add(panel, BorderLayout.NORTH);
		
		panel.add(btnNewButton);
		splitPane.setResizeWeight(0.8);
		
		contentPane.add(splitPane, BorderLayout.CENTER);
		splitPane_1.setResizeWeight(0.8);
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		splitPane.setLeftComponent(splitPane_1);
		
		splitPane_1.setLeftComponent(tabbedPane);
		
		tabbedPane.addTab("Step Logs", null, stepLogPanel, null);
		
		splitPane_1.setRightComponent(ideInputPanel);
		
		splitPane.setRightComponent(tabbedPane_1);
	}

}
