package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	private final JButton btnStepExecute = new JButton("Step Execute");

	private boolean stepExecuteInProgress = false;
	
	private ExecutedStepPanel activePanel = null;

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
		btnReexecute.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				reexecute();
			}
		});

		panel.add(btnReexecute);
		btnStepExecute.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(stepExecuteInProgress)
				{
					stopStepExecute();
				}
				else
				{
					startStepExecute();
				}
			}
		});

		panel.add(btnStepExecute);

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

			@Override
			public void stateLoaded()
			{
				mainLogPanel.removeAll();

				if(CollectionUtils.isEmpty(ideEngine.getSteps()))
				{
					return;
				}

				for(ExecutedStep step : ideEngine.getSteps())
				{
					addNewStep(step);
				}
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
		int res = JOptionPane.showConfirmDialog(this, "Are you sure you want to re-execute from begining?\n" + "This will recreate a new context.", "Re-execute", JOptionPane.YES_NO_OPTION);

		if(res == JOptionPane.NO_OPTION)
		{
			return;
		}

		ideEngine.reexecute();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void reorderSteps()
	{
		List<ExecutedStepPanel> panels = (List) new ArrayList<>(Arrays.asList(mainLogPanel.getComponents()));
		Collections.sort(panels);

		// mainLogPanel.removeAll();

		int index = 0;

		for(ExecutedStepPanel panel : panels)
		{
			mainLogPanel.add(panel, index);
			index++;
			// mainLogPanel.add(panel);
		}

		refreshUi();
	}

	public void focus(ExecutedStepPanel panel)
	{
		int minY = scrollPane.getViewport().getViewPosition().y;
		int maxY = scrollPane.getSize().height;

		int panelY = panel.getLocation().y;

		if(panelY < minY || panelY > maxY)
		{
			scrollPane.getViewport().setViewPosition(new Point(0, panelY));
		}
		
		int panelMaxY = panel.getSize().height + panelY;
		
		if(panelMaxY > maxY)
		{
			scrollPane.getViewport().setViewPosition(new Point(0, panelY));
		}
	}

	private void startStepExecute()
	{
		stepExecuteInProgress = true;
		
		btnStepExecute.setText("Stop Step Execute");
		btnStepExecute.setBackground(Color.red);
		btnStepExecute.setForeground(Color.white);
		
		Thread exeThread = new Thread()
		{
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public void run()
			{
				List<ExecutedStepPanel> panels = (List) new ArrayList<>(Arrays.asList(mainLogPanel.getComponents()));
				ExecutedStepPanel actPanel = activePanel;
				
				if(actPanel == null)
				{
					actPanel = panels.get(0);
				}
				
				boolean actPanelFound = false;
				
				for(ExecutedStepPanel pnl : panels)
				{
					//skip panels prior to active panel
					if(!actPanelFound)
					{
						if(pnl != actPanel)
						{
							continue;
						}
						
						actPanelFound = true;
					}
					
					pnl.requestActiveFocus();
					
					ideEngine.executeOnly(pnl.getStep());
					
					try
					{
						Thread.sleep(2000);
					}catch(Exception ex)
					{}
					
					if(!stepExecuteInProgress)
					{
						break;
					}
				}
			}
		};
		
		exeThread.start();
	}

	private void stopStepExecute()
	{
		stepExecuteInProgress = false;
		
		btnStepExecute.setText("Step Execute");
		btnStepExecute.setBackground(btnReexecute.getBackground());
		btnStepExecute.setForeground(Color.black);
	}
	
	public void setActivePanel(ExecutedStepPanel activePanel)
	{
		this.activePanel = activePanel;
		focus(activePanel);
	}
	
	public ExecutedStepPanel getActivePanel()
	{
		return activePanel;
	}
}
