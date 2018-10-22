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
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.actions.FileActions;
import com.yukthitech.autox.ide.actions.TransferableFiles;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.model.Project;

@Component
public class TreeDragSource implements DragSourceListener, DragGestureListener
{
	private DragSource source;

	private DefaultMutableTreeNode nodeBeingDragged;

	private JTree sourceTree;
	
	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private FileActions fileActions;

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
		
		if(nodeBeingDragged instanceof FileTreeNode)
		{
			listOfFiles.add( ((FileTreeNode) nodeBeingDragged).getFile() );
		}
		else if(nodeBeingDragged instanceof FolderTreeNode)
		{
			listOfFiles.add( ((FolderTreeNode) nodeBeingDragged).getFolder() );
		}
		else
		{
			return;
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
		if(!dsde.getDropSuccess() || (dsde.getDropAction() != DnDConstants.ACTION_MOVE))
		{
			return;
		}
		
		Project project = null;
		File file = null;
		
		if(nodeBeingDragged instanceof FileTreeNode)
		{
			project = ((FileTreeNode) nodeBeingDragged).getProject();
			file = ((FileTreeNode) nodeBeingDragged).getFile();
		}
		else if(nodeBeingDragged instanceof FolderTreeNode)
		{
			project = ((FolderTreeNode) nodeBeingDragged).getProject();
			file = ((FolderTreeNode) nodeBeingDragged).getFolder();
		}
		else
		{
			return;
		}
		
		ideContext.setActiveDetails(project, file);
		fileActions.deleteFile(file);
	}

}