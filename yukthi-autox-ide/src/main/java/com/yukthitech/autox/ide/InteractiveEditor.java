package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;

import com.yukthitech.autox.ide.engine.IdeEngine;
import com.yukthitech.autox.ide.engine.IdeEngineListener;
import com.yukthitech.autox.ide.model.ExecutedStep;
import com.yukthitech.autox.ide.model.IdeState;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import java.awt.FlowLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class InteractiveEditor extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final JPanel panel = new JPanel();
	private final JButton btnSave = new JButton("Save");
	private final JSplitPane splitPane = new JSplitPane();
	private final JSplitPane splitPane_1 = new JSplitPane();
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private final JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
	private final InputPanel ideInputPanel = new InputPanel();
	private final LogPanel stepLogPanel = new LogPanel();
	private final ActionStepPanel finalStepPanel = new ActionStepPanel();

	private IdeEngine ideEngine = new IdeEngine();

	private IdeState ideState = IdeState.load();

	private SettingsDialog settingsDialog;
	private final ContextAttributePanel contextAttributePanel = new ContextAttributePanel();
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnFile = new JMenu("File");
	private final JMenuItem mntmSave = new JMenuItem("Save");
	private final JButton btnSettings = new JButton("Settings");
	private final JButton btnReload = new JButton("Reload");

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
					frame.display();
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

		setJMenuBar(menuBar);

		menuBar.add(mnFile);
		mntmSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				saveState();
			}
		});
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));

		mnFile.add(mntmSave);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

		contentPane.add(panel, BorderLayout.NORTH);
		btnSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				saveState();
			}
		});

		panel.add(btnSave);
		btnSettings.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				displaySettings();
			}
		});

		panel.add(btnSettings);
		btnReload.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				reload();
			}
		});

		panel.add(btnReload);
		splitPane.setResizeWeight(0.9);

		contentPane.add(splitPane, BorderLayout.CENTER);
		splitPane_1.setResizeWeight(0.8);
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);

		splitPane.setLeftComponent(splitPane_1);

		splitPane_1.setLeftComponent(tabbedPane);

		tabbedPane.addTab("Step Logs", null, stepLogPanel, null);

		splitPane_1.setRightComponent(ideInputPanel);

		splitPane.setRightComponent(tabbedPane_1);

		tabbedPane_1.addTab("Steps", null, finalStepPanel, null);

		tabbedPane_1.addTab("Context Attr", null, contextAttributePanel, null);

		init();

		settingsDialog = new SettingsDialog(this);
	}

	private void init()
	{
		ideInputPanel.setIdeEngine(ideEngine);
		stepLogPanel.setIdeEngine(ideEngine);
		finalStepPanel.setIdeEngine(ideEngine);
		contextAttributePanel.setIdeEngine(ideEngine);

		ideEngine.addIdeEngineListener(new IdeEngineListener()
		{
			@Override
			public void stepExecuted(ExecutedStep step)
			{
				setSaveEnabled(true);
			}

			@Override
			public void stepRemoved(ExecutedStep step)
			{
				setSaveEnabled(true);
			}
		});
	}

	private void setSaveEnabled(boolean enabled)
	{
		btnSave.setEnabled(enabled);
		mntmSave.setEnabled(enabled);
	}

	private void display()
	{
		if(ideState == null)
		{
			ideState = new IdeState();
			settingsDialog.display(ideState);
		}

		ideEngine.init(ideState);
		setVisible(true);
	}

	private void saveState()
	{
		ideEngine.getState().save();
		setSaveEnabled(false);
	}

	private void displaySettings()
	{
		settingsDialog.display(ideState);
	}

	private void reload()
	{
		ideEngine.reload();
	}
}
