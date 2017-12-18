package com.yukthitech.autox.ide;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;

public class StepLogPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private final JPanel panel = new JPanel();
	private final JButton btnClear = new JButton("Clear");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JPanel panel_1 = new JPanel();

	/**
	 * Create the panel.
	 */
	public StepLogPanel()
	{
		setLayout(new BorderLayout(0, 0));
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		add(panel, BorderLayout.NORTH);
		
		panel.add(btnClear);
		
		add(scrollPane, BorderLayout.CENTER);
		
		scrollPane.setViewportView(panel_1);

	}

}
