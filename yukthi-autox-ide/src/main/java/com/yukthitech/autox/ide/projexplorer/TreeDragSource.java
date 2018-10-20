package com.yukthitech.autox.ide.projexplorer;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.springframework.stereotype.Component;

@Component
public class TreeDragSource implements DragSourceListener, DragGestureListener
{
	private DragSource source;

	private TransferableTreeNode transferableTreeNode;

	private DefaultMutableTreeNode oldNode;

	private JTree sourceTree;
	
	public TreeDragSource()
	{
		this.source = new DragSource();
	}
	

	public JTree getSourceTree()
	{
		return sourceTree;
	}


	public void setSourceTree(JTree sourceTree)
	{
		this.sourceTree = sourceTree;
		//recognizer = source.createDefaultDragGestureRecognizer(sourceTree, DnDConstants.ACTION_COPY_OR_MOVE, this);
	}


	@Override
	public void dragGestureRecognized(DragGestureEvent dge)
	{
		TreePath path = sourceTree.getSelectionPath();
		if(path == null)
		{
			System.out.println("we can't move the root node or empty selection");
			return;
		}
		
		oldNode = (DefaultMutableTreeNode)path.getLastPathComponent();
		transferableTreeNode = new TransferableTreeNode(path);
		System.out.println(oldNode.getClass()+"is ready to move");
		source.startDrag(dge, DragSource.DefaultMoveNoDrop, transferableTreeNode, this);
	}

	@Override
	public void dragEnter(DragSourceDragEvent dsde)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void dragOver(DragSourceDragEvent dsde)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dropActionChanged(DragSourceDragEvent dsde)
	{
		System.out.println("Action: " + dsde.getDropAction());
	    System.out.println("Target Action: " + dsde.getTargetActions());
	    System.out.println("User Action: " + dsde.getUserAction());
	}

	@Override
	public void dragExit(DragSourceEvent dse)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde)
	{
		System.out.println("Drop Action "+dsde.getDropAction());
		if(dsde.getDropSuccess()&&(dsde.getDropAction()==DnDConstants.ACTION_MOVE)){
			((DefaultTreeModel)sourceTree.getModel()).removeNodeFromParent(oldNode);
		}
	}

}
