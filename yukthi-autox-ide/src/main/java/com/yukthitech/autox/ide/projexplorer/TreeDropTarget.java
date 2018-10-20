package com.yukthitech.autox.ide.projexplorer;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.actions.FileActions;
import com.yukthitech.autox.ide.model.Project;

@Component
public class TreeDropTarget implements DropTargetListener
{
	private static Logger logger = LogManager.getLogger(TreeDropTarget.class);

	private DropTarget target;

	private JTree targetTree;

	@Autowired
	private FileActions fileAction;

	private Project project;

	private File AtiveFile;

	public TreeDropTarget()
	{
		// TODO Auto-generated constructor stub
	}

	public Project getProject()
	{
		return project;
	}

	public File getAtiveFile()
	{
		return AtiveFile;
	}

	public void setAtiveFile(File ativeFile)
	{
		AtiveFile = ativeFile;
	}

	public void setProject(Project project)
	{
		this.project = project;
	}

	public JTree getTargetTree()
	{
		return targetTree;
	}

	public DropTarget getTarget()
	{
		return target;
	}

	public void setTarget(DropTarget target)
	{
		if(this.target == null)
		{
			target = new DropTarget(getTargetTree(), this);
		}
		this.target = target;
	}

	public void setTargetTree(JTree targetTree)
	{
		this.targetTree = targetTree;
		this.target = new DropTarget(targetTree, this);
	}

	private TreeNode getNodeForEvent(DropTargetDragEvent dtde)
	{
		Point p = dtde.getLocation();
		DropTargetContext dtc = dtde.getDropTargetContext();
		JTree tree = (JTree) dtc.getComponent();
		TreePath path = tree.getClosestPathForLocation(p.x, p.y);
		return (TreeNode) path.getLastPathComponent();

	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde)
	{
		TreeNode node = getNodeForEvent(dtde);
		// if(node.isLeaf())
		if(node instanceof FileTreeNode)
		{
			dtde.rejectDrag();
		}
		else
		{
			dtde.acceptDrag(dtde.getDropAction());
		}
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde)
	{
		// TODO Auto-generated method stub
		TreeNode node = getNodeForEvent(dtde);
		// if(node.isLeaf())
		if(node instanceof FileTreeNode)
		{
			dtde.rejectDrag();
		}
		else
		{
			dtde.acceptDrag(dtde.getDropAction());
		}
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dragExit(DropTargetEvent dte)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void drop(DropTargetDropEvent dtde)
	{
		Point p = dtde.getLocation();
		DropTargetContext dtc = dtde.getDropTargetContext();
		JTree tree = (JTree) dtc.getComponent();
		TreePath parentPath = tree.getClosestPathForLocation(p.x, p.y);
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
		// if(parent.isLeaf())
		if(parent instanceof FileTreeNode)
		{
			dtde.rejectDrop();
			return;
		}
		try
		{
			Transferable tr = dtde.getTransferable();
			DataFlavor[] flavors = tr.getTransferDataFlavors();
			for(int i = 0; i < flavors.length; i++)
			{
				if(tr.isDataFlavorSupported(flavors[i]))
				{
					dtde.acceptDrop(dtde.getDropAction());
					TreePath path = (TreePath) tr.getTransferData(flavors[i]);
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
					if(node instanceof FileTreeNode)
					{
						fileAction.copyFile(((FileTreeNode) node).getFile());
					}
					else if(node instanceof FolderTreeNode)
					{
						fileAction.copyFile(((FolderTreeNode) node).getFolder());
					}
					else
					{
						System.out.println("you can't move " + node.getClass());
						dtde.rejectDrop();
					}
					if(parent instanceof FolderTreeNode)
					{
						String source= ((FileTreeNode) node).getFile().getParent();
						String dest = ((FolderTreeNode) parent).getFolder().getAbsolutePath();
						System.out.println("source"+source);
						System.out.println("destination"+dest);
						if(source.equals(dest))
						{
							dtde.rejectDrop();
						}
						else{
							fileAction.pasteFile(((FolderTreeNode) parent).getFolder());
							// model.insertNodeInto(node, parent, 0);
							if(node instanceof FileTreeNode)
							{
								fileAction.deleteFile(((FileTreeNode) node).getFile());
							}
							else if(node instanceof FolderTreeNode)
							{
								fileAction.deleteFolder(((FolderTreeNode) node).getFolder());
							}
							else
							{
								dtde.rejectDrop();
								System.out.println("something went wrong");
							}
						}
					}
					else
					{
						System.out.println("you can not paste here");
						dtde.rejectDrop();
					}

					System.out.println("coping file" + node.getClass());
					fileAction.refreshFolder();
					dtde.dropComplete(true);
					return;
				}
			}
			dtde.rejectDrop();
		} catch(Exception ex)
		{
			logger.error("An error occoure during running of drop method ",ex);
			dtde.rejectDrop();
			return;
		}
	}

}
