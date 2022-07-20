package com.yukthitech.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;

import com.yukthitech.swing.tree.LazyTreeNode;
import com.yukthitech.swing.tree.cbox.CboxNodeData;
import com.yukthitech.swing.tree.cbox.CheckBoxTree;

/**
 * Dialog that can be used to select one or more folders.
 * @author akranthikiran
 */
public class DirectoryChooserDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private final JScrollPane scrollPane = new JScrollPane();
	private final CheckBoxTree folderTree = new CheckBoxTree();
	
	private boolean cancelled = false;
	
	private LazyTreeNode<CboxNodeData> currentNode;

	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args)
	{
		try
		{
			DirectoryDialog dialog = new DirectoryDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			
			DirectoryDataProvider dataProvider = new DirectoryDataProvider(Arrays.asList(new File("E:\\Kranthi")));
			
			dataProvider.setSelectedFolders(Arrays.asList(new File("E:\\Kranthi\\MyFiles\\Books"), new File("E:\\Kranthi\\MyFiles\\Maya")));
			dataProvider.setReadOnlyFolders(Arrays.asList(new File("E:\\Kranthi\\MyFiles\\Books"), new File("E:\\Kranthi\\MyFiles\\Office")));
			
			List<File> selFolders = dialog.display(dataProvider);
			System.out.println(selFolders);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	*/

	/**
	 * Create the dialog.
	 */
	public DirectoryChooserDialog()
	{
		super.setModalityType(ModalityType.APPLICATION_MODAL);
		
		setTitle("Select Folders...");
		setBounds(100, 100, 413, 471);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		
		JPopupMenu popupMenu = new JPopupMenu();

		JMenuItem mntmNewMenuItem = new JMenuItem("New Folder");
		popupMenu.add(mntmNewMenuItem);
		mntmNewMenuItem.addActionListener(this::onNewFolder);

		JMenuItem refreshMenuItem = new JMenuItem("Refresh");
		popupMenu.add(refreshMenuItem);
		refreshMenuItem.addActionListener(this::onRefresh);

		addPopup(popupMenu);

		scrollPane.setViewportView(folderTree);
		
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(this::onOkay);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(this::onCancel);
			}
		}
		
		super.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				onCancel(null);
			}
		});
	}
	
	private void onRefresh(ActionEvent e)
	{
		folderTree.reload(currentNode);
	}
	
	private void onNewFolder(ActionEvent e)
	{
		if(currentNode == null)
		{
			return;
		}
		
		String name = JOptionPane.showInputDialog(this, "New folder name: ");
		
		if(name == null)
		{
			return;
		}
		
		CboxNodeData data = (CboxNodeData) currentNode.getData();
		File newFolder = new File((File) data.getUserData(), name);
		
		try
		{
			FileUtils.forceMkdir(newFolder);
		}catch(Exception ex)
		{
			JOptionPane.showInternalMessageDialog(this, 
					String.format("Failed to create new folder: %s\nError: %s", newFolder.getName(), ex.getMessage()), 
					"Error", 
					JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		folderTree.reload(currentNode);
	}
	
	private void onCancel(ActionEvent e)
	{
		cancelled = true;
		super.setVisible(false);
	}
	
	private void onOkay(ActionEvent e)
	{
		super.setVisible(false);
	}
	
	public List<File> display(DirectoryDataProvider dirDataProvider)
	{
		cancelled = false;
		
		folderTree.setDataProvider(dirDataProvider, dirDataProvider);
		
		super.setVisible(true);
		
		if(cancelled)
		{
			return null;
		}
		
		List<File> selFolders = dirDataProvider.getSelectedFolders();
		
		//Filter folders which may get renamed or deleted post selection 
		List<File> finalRes = new ArrayList<File>();
		
		for(File folder : selFolders)
		{
			if(folder.exists())
			{
				finalRes.add(folder);
			}
		}
		
		return finalRes;
	}

	private void addPopup(JPopupMenu popup)
	{
		folderTree.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				if(e.isPopupTrigger())
				{
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e)
			{
				if(e.isPopupTrigger())
				{
					showMenu(e);
				}
			}

			@SuppressWarnings("unchecked")
			private void showMenu(MouseEvent e)
			{
				TreePath treePath = folderTree.getPathForLocation(e.getX(), e.getY());
				
				if(treePath == null)
				{
					return;
				}
				
				currentNode = (LazyTreeNode<CboxNodeData>) treePath.getLastPathComponent();
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
