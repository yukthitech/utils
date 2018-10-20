package com.yukthitech.autox.ide.projexplorer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.tree.TreePath;

public class TransferableTreeNode implements Transferable
{

	public static DataFlavor TREE_PATH_FLAVOR = new DataFlavor(TreePath.class,"Tree Path");
	
	DataFlavor flavors[] = {TREE_PATH_FLAVOR };
	
	TreePath path;
	public TransferableTreeNode(TreePath path)
	{
		this.path=path;
		
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		// TODO Auto-generated method stub
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		// TODO Auto-generated method stub
		return (flavor.getRepresentationClass()==TreePath.class);
	}

	@Override
	public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if(isDataFlavorSupported(flavor)){
			return (Object)path;
		}
		else{
		// TODO Auto-generated method stub
			throw new UnsupportedFlavorException(flavor);
		}
	}

}
