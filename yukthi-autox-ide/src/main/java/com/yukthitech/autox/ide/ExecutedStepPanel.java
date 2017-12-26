package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.rtf.RTFEditorKit;

import com.yukthitech.autox.ide.engine.IdeEngine;
import com.yukthitech.autox.ide.model.ExecutedStep;

public class ExecutedStepPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static RTFEditorKit rtfEditorKit = new RTFEditorKit();
	
	private final JPanel panel = new JPanel();
	private final JLabel lblTitle = new JLabel("Id: ");
	private final JButton btnX = new JButton("X");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JTextPane textPane = new JTextPane();

	private ExecutedStep step;
	
	private IdeEngine ideEngine;
	
	private FinalStepPanel finalStepPanel;
	
	/**
	 * Create the panel.
	 */
	public ExecutedStepPanel(ExecutedStep step, IdeEngine ideEngine, FinalStepPanel finalStepPanel)
	{
		this.step = step;
		this.ideEngine = ideEngine;
		this.finalStepPanel = finalStepPanel;
		
		setLayout(new BorderLayout(0, 0));
		panel.setBorder(null);

		add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		GridBagConstraints gbc_lblTitle = new GridBagConstraints();
		gbc_lblTitle.fill = GridBagConstraints.BOTH;
		gbc_lblTitle.gridx = 0;
		gbc_lblTitle.gridy = 0;
		lblTitle.setBorder(new EmptyBorder(0, 5, 0, 0));
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setOpaque(true);
		lblTitle.setBackground(Color.BLUE);
		lblTitle.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblTitle, gbc_lblTitle);

		GridBagConstraints gbc_btnX = new GridBagConstraints();
		gbc_btnX.fill = GridBagConstraints.VERTICAL;
		gbc_btnX.gridx = 1;
		gbc_btnX.gridy = 0;
		btnX.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnX.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				delete();
			}
		});
		btnX.setForeground(Color.WHITE);
		btnX.setBackground(Color.RED);
		panel.add(btnX, gbc_btnX);

		add(scrollPane, BorderLayout.CENTER);
		textPane.setEditable(false);
		textPane.setFont(new Font(Font.DIALOG, Font.BOLD, 14));

		scrollPane.setViewportView(textPane);
		init();
	}
	
	private void init()
	{
		textPane.setEditorKit(rtfEditorKit);

		try
		{
			rtfEditorKit.read(new ByteArrayInputStream(step.getRtfText().getBytes()), textPane.getDocument(), 0);
		} catch(Exception ex)
		{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "An error occurred while displaying step text: " + ex);
			return;
		}
		
		lblTitle.setText("Step: " + step.getId());
	}

	private void delete()
	{
		int res = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete step with id: " + step.getId(), "Delete", JOptionPane.YES_NO_OPTION);
		
		if(res == JOptionPane.NO_OPTION)
		{
			return;
		}
		
		ideEngine.removeStep(step);
		
		super.getParent().remove(this);
		finalStepPanel.refreshUi();
	}
}
