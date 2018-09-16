package com.yukthitech.autox.ide;

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
import com.yukthitech.autox.ide.ui.FileTreeNode;
import com.yukthitech.autox.ide.ui.FolderTreeNode;
import com.yukthitech.autox.ide.ui.ProjectTreeNode;
import com.yukthitech.autox.ide.ui.ProjectsTreeModel;

@Component
public class ProjectExplorer extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(ProjectExplorer.class);
	
	private JTree tree = new JTree();
	private ProjectsTreeModel treeModel;

	@Autowired
	private IdeContext ideContext;
	
	private JPopupMenu filePopup;
	
	private JPopupMenu folderPopup;
	
	private JPopupMenu projectExplorerPopup;
	
	private JPopupMenu projectPopup;
	
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
		tree.addMouseListener(listener);
		
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
		});
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
		
		if(clickedItem instanceof FileTreeNode)
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
		else if(clickedItem instanceof ProjectTreeNode)
		{
			ProjectTreeNode projTreeNode = (ProjectTreeNode) clickedItem;
			ideContext.setActiveDetails(projTreeNode.getProject(), null);
			
			projectPopup.show(tree, e.getX(), e.getY());
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
	
	public void reloadActiveNode()
	{
		if(activeTreeNode == null)
		{
			return;
		}
		
		activeTreeNode.reload();
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
		
		parentNode.reload();
		treeModel.reload(parentNode);
	}
}