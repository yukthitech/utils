package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.CardLayout;
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
import com.yukthitech.autox.ide.views.report.ReportPanel;

@ActionHolder
@SpringBootApplication
public class AutoxIDE extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private static final String PANEL_MAIN = "mainPanel";
	
	private static final String PANEL_MAXIMIZED = "maximizedPanel";
	
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
	
	@Autowired
	private ReportPanel reportPanel;
	
	/**
	 * Top panel to hold tool bar and env panel.
	 */
	private JPanel topPanel = new JPanel();
	
	/**
	 * Container to be used for maximized panes.
	 */
	private JPanel maximizedPanel = new JPanel();
	
	private JSplitPane verticalSplitPane;
	
	private JSplitPane horizontalSplitPane;
	
	private MaximizableTabbedPane leftTabbedPane;
	
	private MaximizableTabbedPane rightBottomTabbedPane;
	
	/**
	 * Status indicating if any tab is currently maximized.
	 */
	private boolean maximized = false;
	
	private CardLayout cardLayoutForMaximization;
	
	/**
	 * Panel containing the main tabbed panels and maximized panels as cards.
	 */
	private JPanel cardedMainPanel;
	
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

		JPanel mainPanel = createMainPanel();
		contentPane.add(mainPanel, BorderLayout.CENTER);

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
	
	private JPanel createMainPanel()
	{
		cardedMainPanel = new JPanel();
		
		cardLayoutForMaximization = new CardLayout();
		cardedMainPanel.setLayout(cardLayoutForMaximization);

		cardedMainPanel.add(createSplitPanes(), PANEL_MAIN);
		
		//create and maximized panel
		maximizedPanel.setLayout(new BorderLayout());
		cardedMainPanel.add(maximizedPanel, PANEL_MAXIMIZED);
		
		return cardedMainPanel;
	}
	
	private JSplitPane createSplitPanes()
	{
		IMaximizationListener maximizeListener = new IMaximizationListener()
		{
			@Override
			public void minimize(MaximizableTabbedPane tabPane)
			{
				minimizeTabPane(tabPane);
			}
			
			@Override
			public void flipMaximizationStatus(MaximizableTabbedPane tabPane)
			{
				flipMaximize(tabPane);
			}
		};
		
		verticalSplitPane = new JSplitPane();
		verticalSplitPane.setResizeWeight(0.2);

		leftTabbedPane = new MaximizableTabbedPane();
		verticalSplitPane.setLeftComponent(leftTabbedPane);
		leftTabbedPane.setParentDetails(maximizeListener, verticalSplitPane, true);

		leftTabbedPane.addTab("ProjectExplorer", null, projectExplorer, null);

		horizontalSplitPane = new JSplitPane();
		horizontalSplitPane.setResizeWeight(0.8);
		horizontalSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		verticalSplitPane.setRightComponent(horizontalSplitPane);

		// editor = new JTabbedPane(JTabbedPane.TOP);
		horizontalSplitPane.setLeftComponent(fileEditorTabbedPane);
		fileEditorTabbedPane.setParentDetails(maximizeListener, horizontalSplitPane, true);

		rightBottomTabbedPane = new MaximizableTabbedPane();
		horizontalSplitPane.setRightComponent(rightBottomTabbedPane);
		rightBottomTabbedPane.setParentDetails(maximizeListener, horizontalSplitPane, false);

		rightBottomTabbedPane.addTab("Report", null, reportPanel, null);

		rightBottomTabbedPane.addTab("Console", null, consolePanel, null);
		consolePanel.setParent(rightBottomTabbedPane);
		
		return verticalSplitPane;
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
	
	private synchronized void minimizeTabPane(MaximizableTabbedPane tabPane)
	{
		if(!maximized)
		{
			return;
		}

		if(tabPane.isLeftComponent())
		{
			tabPane.getParentSplitPane().setLeftComponent(tabPane);
		}
		else
		{
			tabPane.getParentSplitPane().setRightComponent(tabPane);
		}
		
		cardLayoutForMaximization.show(cardedMainPanel, PANEL_MAIN);
		
		maximized = false;
	}
	
	private synchronized void flipMaximize(MaximizableTabbedPane tabPane)
	{
		if(maximized)
		{
			minimizeTabPane(tabPane);
		}
		else
		{
			maximizedPanel.add(tabPane, BorderLayout.CENTER);
			cardLayoutForMaximization.show(cardedMainPanel, PANEL_MAXIMIZED);
			
			maximized = true;
		}
	}
}