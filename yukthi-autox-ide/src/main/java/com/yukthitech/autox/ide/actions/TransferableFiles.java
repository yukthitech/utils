package com.yukthitech.autox.ide.actions;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Trasferable wrapper for fils which is used to copy file paths to clipboard.
 * @author akiran
 */
public class TransferableFiles implements Transferable
{
	private List<File> listOfFiles;

	public TransferableFiles(List<File> listOfFiles)
	{
		this.listOfFiles = listOfFiles;
	}

	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[] { DataFlavor.javaFileListFlavor };
	}

	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return DataFlavor.javaFileListFlavor.equals(flavor);
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		return listOfFiles;
	}

}
