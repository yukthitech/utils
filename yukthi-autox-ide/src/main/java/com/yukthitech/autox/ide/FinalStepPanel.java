package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.apache.commons.collections.CollectionUtils;

import com.yukthitech.autox.ide.engine.IdeEngine;
import com.yukthitech.autox.ide.engine.IdeEngineListener;
import com.yukthitech.autox.ide.model.ExecutedStep;

public class FinalStepPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final JPanel panel = new JPanel();
	private final JButton btnClear = new JButton("Export");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JPanel mainLogPanel = new JPanel();

	private IdeEngine ideEngine;

	private BoxLayout logPanelLayout;
	private final JButton btnReexecute = new JButton("Re-execute");

	/**
	 * Create the panel.
	 */
	public FinalStepPanel()
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
				export();
			}
		});

		panel.add(btnClear);
		btnReexecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reexecute();
			}
		});
		
		panel.add(btnReexecute);

		add(scrollPane, BorderLayout.CENTER);

		scrollPane.setViewportView(mainLogPanel);

		logPanelLayout = new BoxLayout(mainLogPanel, BoxLayout.Y_AXIS);
		mainLogPanel.setLayout(logPanelLayout);

	}

	private void addNewStep(ExecutedStep step)
	{
		ExecutedStepPanel stepPanel = new ExecutedStepPanel(step, ideEngine, this);
		mainLogPanel.add(stepPanel);
		
		refreshUi();
	}
	
	void refreshUi()
	{
		mainLogPanel.updateUI();
		scrollPane.updateUI();
	}

	public void setIdeEngine(IdeEngine ideEngine)
	{
		this.ideEngine = ideEngine;

		this.ideEngine.addIdeEngineListener(new IdeEngineListener()
		{
			@Override
			public void stepExecuted(ExecutedStep step)
			{
				addNewStep(step);
			}
		});
	}

	private void export()
	{
		if(CollectionUtils.isEmpty(ideEngine.getSteps()))
		{
			JOptionPane.showMessageDialog(this, "No steps found to export");
			return;
		}

		int res = IdeConstants.FILE_CHOOSER.showSaveDialog(this);

		if(res == JFileChooser.CANCEL_OPTION)
		{
			return;
		}

		File file = IdeConstants.FILE_CHOOSER.getSelectedFile();

		try
		{
			ideEngine.exportActionXml(file);
			JOptionPane.showMessageDialog(this, "Action xml is exported successfully!");
		} catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, "An error occurred while export action xml. Error: " + ex);
		}
	}
	
	private void reexecute()
	{
		int res = JOptionPane.showConfirmDialog(this, "Are you sure you want to re-execute from begining?\n"
				+ "This will recreate a new context.", "Re-execute", JOptionPane.YES_NO_OPTION);
		
		if(res == JOptionPane.NO_OPTION)
		{
			return;
		}
		
		ideEngine.reexecute();
	}
}
