package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.annotation.PostConstruct;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.editor.FileEditorTabbedPane;
import com.yukthitech.autox.ide.layout.Action;
import com.yukthitech.autox.ide.layout.ActionCollection;
import com.yukthitech.autox.ide.layout.ActionHolder;
import com.yukthitech.autox.ide.layout.UiLayout;
import com.yukthitech.autox.ide.model.IdeState;
import com.yukthitech.autox.ide.views.ConsolePanel;

@ActionHolder
@SpringBootApplication
public class AutoxIDE extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	@Autowired
	private ProjectExplorer projectExplorer;
	
	@Autowired
	private FileEditorTabbedPane fileEditorTabbedPane;
	
	@Autowired
	private UiLayout uiLayout;
	
	@Autowired
	private ActionCollection actionCollection;
	
	/**
	 * Ide context.
	 */
	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private ExeEnvironmentPanel exeEnvironmentPanel;
	
	@Autowired
	private ConsolePanel consolePanel;
	
	/**
	 * Top panel to hold tool bar and env panel.
	 */
	private JPanel topPanel = new JPanel();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		SpringApplication.run(AutoxIDE.class, args);
	}
	
	@PostConstruct
	private void display()
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					init();
					setVisible(true);
					loadState();
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
	private void init()
	{
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		super.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				closeIde();
			}
		});
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.2);
		contentPane.add(splitPane, BorderLayout.CENTER);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setLeftComponent(tabbedPane);

		tabbedPane.addTab("ProjectExplorer", null, projectExplorer, null);

		JSplitPane rightSplitPane = new JSplitPane();
		rightSplitPane.setResizeWeight(0.8);
		rightSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(rightSplitPane);

		// editor = new JTabbedPane(JTabbedPane.TOP);
		rightSplitPane.setLeftComponent(fileEditorTabbedPane);

		JTabbedPane bottomTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		rightSplitPane.setRightComponent(bottomTabbedPane);

		Report report = new Report();
		bottomTabbedPane.addTab("Report", null, report, null);

		bottomTabbedPane.addTab("Console", null, consolePanel, null);
		consolePanel.setParent(bottomTabbedPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		JLabel lblNewLabel = new JLabel("AutoxIDE successfully open");
		panel.add(lblNewLabel);
		
		topPanel.setLayout(new BorderLayout());
		topPanel.add(exeEnvironmentPanel, BorderLayout.EAST);
		contentPane.add(topPanel, BorderLayout.NORTH);
		
		loadLayout();
	}
	
	private void loadLayout()
	{
		JMenuBar menuBar = uiLayout.getMenuBar().toJMenuBar(actionCollection);
		super.setJMenuBar(menuBar);
		
		JToolBar toolBar = uiLayout.getToolBar().toJToolBar(actionCollection);
		topPanel.add(toolBar, BorderLayout.CENTER);
	}
	
	@Action
	public void closeIde()
	{
		IdeState ideState = new IdeState();
		ideContext.getProxy().saveState(ideState);
		ideState.save();
		
		System.exit(-1);
	}
	
	private void loadState()
	{
		IdeState ideState = IdeState.load();
		
		if(ideState == null)
		{
			return;
		}
		
		ideContext.getProxy().loadState(ideState);
	}
}