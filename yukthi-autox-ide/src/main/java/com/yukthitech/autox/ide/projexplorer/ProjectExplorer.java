package com.yukthitech.autox.ide.projexplorer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
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

import com.yukthitech.autox.ide.FileDetails;
import com.yukthitech.autox.ide.FileParseCollector;
import com.yukthitech.autox.ide.IIdeFileManager;
import com.yukthitech.autox.ide.IdeFileManagerFactory;
import com.yukthitech.autox.ide.IdeIndex;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.layout.ActionCollection;
import com.yukthitech.autox.ide.layout.UiLayout;
import com.yukthitech.autox.ide.model.IdeState;
import com.yukthitech.autox.ide.model.Project;
import com.yukthitech.autox.ide.model.ProjectState;
import com.yukthitech.autox.ide.rest.RestRequest;
import com.yukthitech.autox.ide.ui.BaseTreeNode;
import com.yukthitech.autox.ide.ui.BaseTreeNodeRenderer;
import com.yukthitech.autox.ide.ui.TestSuiteFolderTreeNode;

@Component
public class ProjectExplorer extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(ProjectExplorer.class);
	
	private static ImageIcon EDITOR_LINK_ICON = IdeUtils.loadIconWithoutBorder("/ui/icons/toggle-button.png", 16);
	
	private static final String STATE_ATTR_EDITOR_LINK_BUTTON_STATE = "ProjectExplorer.editorLinkState";
	
	private JTree tree = new JTree();
	private JScrollPane treeScrollPane = new JScrollPane(tree);
	
	private ProjectsTreeModel projectTreeModel;

	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private TreeDragSource source;
	
	@Autowired
	private TreeDropTarget target;
	
	@Autowired
	private IdeFileManagerFactory ideFileManagerFactory;
	
	@Autowired
	private IdeIndex ideIndex;
	
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
	
	@Autowired
	private RestRequest restRequest;
	
	private JPanel iconPanel = new JPanel();
	
	private JToggleButton editorLinkButton = new JToggleButton();

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
		
		projectTreeModel = new ProjectsTreeModel();
		tree.setModel(projectTreeModel);
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
		
		//set tool bar panel
		super.add(iconPanel, BorderLayout.NORTH);
		iconPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 4, 4));
		
		editorLinkButton.setIcon(EDITOR_LINK_ICON);
		iconPanel.add(editorLinkButton);
		
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
					
					if(editorLinkButton.isSelected())
					{
						ideContext.getProxy().activeFileChanged(fileNode.getFile(), ProjectExplorer.this);
					}
				}
				else if(treeNode instanceof FolderTreeNode)
				{
					FolderTreeNode folderNode = (FolderTreeNode) treeNode;
					ideContext.setActiveDetails(folderNode.getProject(), folderNode.getFolder());
				}
			}
		});
		
		super.add(treeScrollPane, BorderLayout.CENTER);
		
		ideContext.addContextListener(new IContextListener()
		{
			@Override
			public void saveState(IdeState state)
			{
				for(Project proj : projects)
				{
					state.addOpenProject(proj);
				}
				
				state.setAtribute(STATE_ATTR_EDITOR_LINK_BUTTON_STATE, editorLinkButton.isSelected());
			}
			
			@Override
			public void loadState(IdeState state)
			{
				for(ProjectState project : state.getOpenProjects())
				{
					openProject(project.getPath());
				}
				
				if(Boolean.TRUE.equals(state.getAttribute(STATE_ATTR_EDITOR_LINK_BUTTON_STATE)))
				{
					editorLinkButton.setSelected(true);
				}
				
				loadFilesToIndex();
			}
			
			@Override
			public void projectStateChanged(Project project)
			{
				reloadProjectNode(project);
			}

			@Override
			public void fileSaved(File file)
			{
				FileTreeNode fileNode = getFileNode(file);
				
				if(fileNode == null)
				{
					return;
				}
				
				checkFile(fileNode);
			}
			
			@Override
			public void activeFileChanged(File file, Object source)
			{
				if(source == ProjectExplorer.this)
				{
					return;
				}
				
				setActiveFile(file);
			}
		});
	}
	
	private void setActiveFile(File file)
	{
		if(!editorLinkButton.isSelected())
		{
			return;
		}
		
		FileTreeNode node = getFileNode(file);
		
		if(node == null)
		{
			return;
		}
		
		TreePath treePath = new TreePath(node.getPath());
		
		tree.setSelectionPath(treePath);
		tree.scrollPathToVisible(treePath);
	}
	
	public FileTreeNode getFileNode(File file)
	{
		ProjectTreeNode projNode = projectTreeModel.getProjectNode(ideContext.getActiveProject());
		
		if(projNode == null)
		{
			return null;
		}
		
		return projNode.getFileNode(file);
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
		
		projectTreeModel.addProject(new ProjectTreeNode(this, project));
		
		projects.add(project);
		restRequest.addProject(project);
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
		ProjectTreeNode node = projectTreeModel.getProjectNode(project);
		
		if(node == null)
		{
			return;
		}
		
		node.reload(true);
		projectTreeModel.reload(node);
		
		loadFilesToIndex();
	}
	
	public void reloadActiveNode()
	{
		if(activeTreeNode == null)
		{
			logger.debug("No active node found for reload.");
			return;
		}

		activeTreeNode.reload(true);
		projectTreeModel.reload(activeTreeNode);
		
		loadFilesToIndex();
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
		projectTreeModel.reload(parentNode);
		
		loadFilesToIndex();
	}
	
	public void checkFile(FileTreeNode fileNode)
	{
		File file = fileNode.getFile();
		IIdeFileManager fileManager = ideFileManagerFactory.getFileManager(fileNode.getProject(), file);
		
		if(fileManager != null)
		{
			FileParseCollector collector = new FileParseCollector();
		
			try
			{
				fileManager.parseFile(fileNode.getProject(), file, collector);
				
				fileNode.setErrored(collector.getErrorCount() > 0);
				fileNode.setWarned(collector.getWarningCount() > 0);
			} catch(Exception ex)
			{
				logger.error("An error occurred while loading file: {}", file.getPath(), ex);
			}
		}
	}
	
	public ProjectsTreeModel getProjectTreeModel()
	{
		return projectTreeModel;
	}
	
	public void loadFilesToIndex()
	{
		ideIndex.cleanFileIndex();
		
		for(ProjectTreeNode projNode : projectTreeModel.getProjectNodes())
		{
			loadFilesFromNode(projNode, projNode);
		}
	}
	
	private void loadFilesFromNode(ProjectTreeNode projNode, BaseTreeNode node)
	{
		if(node instanceof FileTreeNode)
		{
			File file = ((FileTreeNode) node).getFile();
			ideIndex.addFile( new FileDetails(file, projNode.getProject()) );
			return;
		}
		
		Collection<BaseTreeNode> childNodes = node.getChildNodes();
		
		if(childNodes == null || childNodes.isEmpty())
		{
			return;
		}
		
		for(BaseTreeNode cnode : childNodes)
		{
			loadFilesFromNode(projNode, cnode);
		}
	}
}