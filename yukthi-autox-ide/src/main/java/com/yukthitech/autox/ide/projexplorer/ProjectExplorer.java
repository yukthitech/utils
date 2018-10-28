package com.yukthitech.autox.ide.projexplorer;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.layout.ActionCollection;
import com.yukthitech.autox.ide.layout.UiLayout;
import com.yukthitech.autox.ide.model.IdeState;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.model.ProjectState;
import com.yukthitech.autox.ide.ui.BaseTreeNode;
import com.yukthitech.autox.ide.ui.BaseTreeNodeRenderer;
import com.yukthitech.autox.ide.ui.TestSuiteFolderTreeNode;

@Component
public class ProjectExplorer extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(ProjectExplorer.class);
	
	private JTree tree = new JTree();
	private ProjectsTreeModel treeModel;

	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private TreeDragSource source;
	
	@Autowired
	private TreeDropTarget target;
	
	private JPopupMenu filePopup;
	
	private JPopupMenu folderPopup;
	
	private JPopupMenu projectExplorerPopup;
	
	private JPopupMenu projectPopup;
	
	private JPopupMenu testSuitePopup;
	
	@Autowired
	private UiLayout uiLayout;
	
	@Autowired
	private ActionCollection actionCollection;
	
	private Set<Project> projects = new HashSet<>();
	
	private BaseTreeNode activeTreeNode;
	
	/**
	 * Create the panel.
	 */
	@PostConstruct
	private void init()
	{
		MouseListener listener = new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				handleMouseClick(e);
			}
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				handleMouseClick(e);
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() < 2 || e.getButton() != MouseEvent.BUTTON1)
				{
					return;
				}
				
				handleOpenEvent(e);
			}
		};
		
		addMouseListener(listener);
		
		treeModel = new ProjectsTreeModel();
		tree.setModel(treeModel);
		tree.setRootVisible(false);
		tree.setCellRenderer(new BaseTreeNodeRenderer());

		setLayout(new BorderLayout(0, 0));
		tree.setShowsRootHandles(true);
		tree.addMouseListener(listener);
		source.setSourceTree(tree);
		target.setTargetTree(tree);
		
		tree.getInputMap().put(KeyStroke.getKeyStroke("ctrl C"), "dummy");
		tree.getInputMap().put(KeyStroke.getKeyStroke("ctrl V"), "dummy");
		tree.getInputMap().put(KeyStroke.getKeyStroke("ctrl X"), "dummy");
		tree.getInputMap().put(KeyStroke.getKeyStroke("F2"), "dummy");
		
		tree.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				TreePath path = e.getPath();
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				
				if(treeNode instanceof FileTreeNode)
				{
					FileTreeNode fileNode = (FileTreeNode) treeNode;
					ideContext.setActiveDetails(fileNode.getProject(), fileNode.getFile());
				}
				else if(treeNode instanceof FolderTreeNode)
				{
					FolderTreeNode folderNode = (FolderTreeNode) treeNode;
					ideContext.setActiveDetails(folderNode.getProject(), folderNode.getFolder());
				}
			}
		});
		
		super.add(tree);
		
		ideContext.addContextListener(new IContextListener()
		{
			@Override
			public void saveState(IdeState state)
			{
				for(Project proj : projects)
				{
					state.addOpenProject(proj);
				}
			}
			
			@Override
			public void loadState(IdeState state)
			{
				for(ProjectState project : state.getOpenProjects())
				{
					openProject(project.getPath());
				}
			}
			
			@Override
			public void projectStateChanged(Project project)
			{
				reloadProjectNode(project);
			}
		});
	}
	
	public void setActiveTreeNode(BaseTreeNode activeTreeNode)
	{
		this.activeTreeNode = activeTreeNode;
	}

	/**
	 * Opens the project from specified base folder path.
	 * @param path base folder path of project to open
	 */
	public Project openProject(String path)
	{
		logger.debug("Loading project at path: {}", path);
		
		Project project = Project.load(path);
		
		if(project == null)
		{
			logger.debug("Failed to load project from path: " + path);
			return null;
		}
		
		openProject(project);
		return project;
	}

	/**
	 * Called when an existing project object needs to be opened.
	 * @param project
	 */
	private void openProject(Project project)
	{
		if(projects.contains(project))
		{
			return;
		}
		
		treeModel.addProject(new ProjectTreeNode(project));
		
		projects.add(project);
		logger.debug("Adding project {} to project tree", project.getName());
	}
	
	private void initMenus()
	{
		if(projectExplorerPopup != null)
		{
			return;
		}
		
		filePopup = uiLayout.getPopupMenu("filePopup").toPopupMenu(actionCollection);
		folderPopup = uiLayout.getPopupMenu("folderPopup").toPopupMenu(actionCollection);
		projectPopup = uiLayout.getPopupMenu("projectPopup").toPopupMenu(actionCollection);
		projectExplorerPopup = uiLayout.getPopupMenu("projectExplorerPopup").toPopupMenu(actionCollection);
		testSuitePopup = uiLayout.getPopupMenu("testStuitePopup").toPopupMenu(actionCollection);
	}
	
	private void handleMouseClick(MouseEvent e)
	{
		if(!e.isPopupTrigger())
		{
			return;
		}
		
		initMenus();
		
		int clickedRow = tree.getRowForLocation(e.getX(), e.getY());
		
		if(clickedRow < 0)
		{
			projectExplorerPopup.show(this, e.getX(), e.getY());
			return;
		}
		
		TreePath treePath = tree.getPathForLocation(e.getX(), e.getY());
		Object clickedItem = treePath.getLastPathComponent();
		
		activeTreeNode = (BaseTreeNode) clickedItem;
		
		if(clickedItem instanceof ProjectTreeNode)
		{
			ProjectTreeNode projTreeNode = (ProjectTreeNode) clickedItem;
			ideContext.setActiveDetails(projTreeNode.getProject(), null);
			
			projectPopup.show(tree, e.getX(), e.getY());
		}
		else if(clickedItem instanceof TestSuiteFolderTreeNode) 
		{
			TestSuiteFolderTreeNode testSuiteFolderTreeNode = (TestSuiteFolderTreeNode) clickedItem;
			ideContext.setActiveDetails(testSuiteFolderTreeNode.getProject(), null);
			testSuitePopup.show(tree, e.getX(), e.getY());
		}
		else if(clickedItem instanceof FileTreeNode)
		{
			FileTreeNode fileTreeNode = (FileTreeNode) clickedItem;
			ideContext.setActiveDetails(fileTreeNode.getProject(), fileTreeNode.getFile());
			
			filePopup.show(tree, e.getX(), e.getY());
		}
		else if(clickedItem instanceof FolderTreeNode)
		{
			FolderTreeNode folderTreeNode = (FolderTreeNode) clickedItem;
			ideContext.setActiveDetails(folderTreeNode.getProject(), folderTreeNode.getFolder());
	
			folderPopup.show(tree, e.getX(), e.getY());
		}
	}
	
	private void handleOpenEvent(MouseEvent e)
	{
		int clickedRow = tree.getRowForLocation(e.getX(), e.getY());
		
		if(clickedRow < 0)
		{
			return;
		}
		
		TreePath treePath = tree.getPathForLocation(e.getX(), e.getY());
		Object clickedItem = treePath.getLastPathComponent();
		
		if(clickedItem instanceof FileTreeNode)
		{
			FileTreeNode fileTreeNode = (FileTreeNode) clickedItem;
			ideContext.setActiveDetails(fileTreeNode.getProject(), fileTreeNode.getFile());
			actionCollection.invokeAction("openFile");
		}
	}
	
	public void reloadProjectNode(Project project)
	{
		ProjectTreeNode node = treeModel.getProjectNode(project);
		
		if(node == null)
		{
			return;
		}
		
		node.reload(true);
		treeModel.reload(node);
	}
	
	public void reloadActiveNode()
	{
		if(activeTreeNode == null)
		{
			logger.debug("No active node found for reload.");
			return;
		}

		activeTreeNode.reload(true);
		treeModel.reload(activeTreeNode);
	}

	
	public void reloadActiveNodeParent()
	{
		if(activeTreeNode == null)
		{
			return;
		}
		
		BaseTreeNode parentNode = (BaseTreeNode) activeTreeNode.getParent();
		
		if(parentNode == null)
		{
			return;
		}
		
		parentNode.reload(true);
		treeModel.reload(parentNode);
	}
}