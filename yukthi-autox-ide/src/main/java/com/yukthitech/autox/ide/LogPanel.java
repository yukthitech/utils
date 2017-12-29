package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

import com.yukthitech.autox.ide.engine.IdeEngine;
import com.yukthitech.autox.ide.engine.IdeEngineListener;
import com.yukthitech.autox.ide.engine.StepDetails;

public class LogPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private final JPanel panel = new JPanel();
	private final JButton btnClear = new JButton("Clear");
	private final JScrollPane scrollPane = new JScrollPane();
	
	private final JPanel mainLogPanel = new JPanel();
	
	private BoxLayout logPanelLayout;
	
	private RTFEditorKit rtfEditorKit = new RTFEditorKit();
	
	private HTMLEditorKit htmlEditorKit = new HTMLEditorKit();

	/**
	 * Create the panel.
	 */
	public LogPanel()
	{
		setLayout(new BorderLayout(0, 0));
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		add(panel, BorderLayout.NORTH);
		
		btnClear.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				clearLog();
			}
		});
		
		panel.add(btnClear);
		
		add(scrollPane, BorderLayout.CENTER);
		mainLogPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainLogPanel.setBackground(Color.WHITE);
		
		scrollPane.setViewportView(mainLogPanel);
		
		logPanelLayout = new BoxLayout(mainLogPanel, BoxLayout.Y_AXIS);
		
		mainLogPanel.setBackground(Color.gray);
		mainLogPanel.setLayout(logPanelLayout);
	}
	
	private void addNewStep(StepDetails step)
	{
		JTextPane textPane = new JTextPane();
		textPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		textPane.setEditable(false);
		textPane.setEditorKit(rtfEditorKit);
		
		try
		{
			rtfEditorKit.read(new ByteArrayInputStream(step.getRtfText().getBytes()), textPane.getDocument(), 0);
		}catch(Exception ex)
		{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "An error occurred while displaying step text: " + ex);
			return;
		}

		textPane.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
		mainLogPanel.add(textPane);
		
		mainLogPanel.updateUI();
		scrollPane.updateUI();
	}
	
	private void addLog(String output)
	{
		JTextPane textPane = new JTextPane();
		textPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		textPane.setEditable(false);
		textPane.setEditorKit(htmlEditorKit);
		
		try
		{
			htmlEditorKit.read(new ByteArrayInputStream(output.getBytes()), textPane.getDocument(), 0);
		}catch(Exception ex)
		{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "An error occurred while displaying log text: " + ex);
			return;
		}
		
		mainLogPanel.add(textPane);
		
		//mainLogPanel.setPreferredSize( new Dimension( scrollPane.getSize().width - 10, mainLogPanel.getPreferredSize().height ) );
		mainLogPanel.updateUI();
		scrollPane.updateUI();
	}
	
	public void setIdeEngine(IdeEngine ideEngine)
	{
		ideEngine.addIdeEngineListener(new IdeEngineListener()
		{
			@Override
			public void executingStep(StepDetails step)
			{
				addNewStep(step);
			}

			@Override
			public void sendOutput(String output)
			{
				addLog(output);
			}
		});
	}

	private void clearLog()
	{
		mainLogPanel.removeAll();

		mainLogPanel.updateUI();
		scrollPane.updateUI();
	}
}
