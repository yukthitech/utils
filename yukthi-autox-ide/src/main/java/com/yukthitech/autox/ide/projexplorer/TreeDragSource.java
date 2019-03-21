package com.yukthitech.autox.ide.projexplorer;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.actions.TransferableFiles;
import com.yukthitech.utils.exceptions.InvalidStateException;

@Component
public class TreeDragSource implements DragSourceListener, DragGestureListener
{
	private DragSource source;

	private DefaultMutableTreeNode nodeBeingDragged;

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
		source.createDefaultDragGestureRecognizer(sourceTree, DnDConstants.ACTION_COPY_OR_MOVE, this);
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge)
	{
		TreePath path = sourceTree.getSelectionPath();

		if(path == null)
		{
			return;
		}

		nodeBeingDragged = (DefaultMutableTreeNode) path.getLastPathComponent();
		ArrayList<File> listOfFiles = new ArrayList<File>();
		
		try
		{
			if(nodeBeingDragged instanceof FileTreeNode)
			{
				listOfFiles.add( ((FileTreeNode) nodeBeingDragged).getFile().getCanonicalFile() );
			}
			else if(nodeBeingDragged instanceof FolderTreeNode)
			{
				listOfFiles.add( ((FolderTreeNode) nodeBeingDragged).getFolder().getCanonicalFile() );
			}
			else
			{
				return;
			}
		}catch(IOException ex)
		{
			throw new InvalidStateException("An error occurred while fetching cannoical path of file being dragged", ex);
		}
		
		TransferableFiles fileTransferable = new TransferableFiles(listOfFiles);
		source.startDrag(dge, DragSource.DefaultMoveDrop, fileTransferable, this);
	}

	@Override
	public void dragEnter(DragSourceDragEvent dsde)
	{
	}

	@Override
	public void dragOver(DragSourceDragEvent dsde)
	{
	}

	@Override
	public void dropActionChanged(DragSourceDragEvent dsde)
	{
	}

	@Override
	public void dragExit(DragSourceEvent dse)
	{
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde)
	{
	}
}