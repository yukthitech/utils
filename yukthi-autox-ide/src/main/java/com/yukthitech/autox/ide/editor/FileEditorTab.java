package com.yukthitech.autox.ide.editor;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;

import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.layout.ActionCollection;
import com.yukthitech.autox.ide.layout.UiLayout;
import com.yukthitech.autox.ide.model.Project;

/**
 * Tab component used by file editor.
 * @author akiran
 */
public class FileEditorTab extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static final Color INACTIVE_COLOR = new Color(128, 128, 128);
	
	private static final Color ACTIVE_COLOR = Color.red;
	
	private static MouseListener CLOSE_BUTTON_MOUSE_LIST = new MouseAdapter()
	{
		public void mouseEntered(MouseEvent e) 
		{
			JButton button = (JButton) e.getComponent();
			button.setForeground(ACTIVE_COLOR);
		}
		
		public void mouseExited(MouseEvent e) 
		{
			JButton button = (JButton) e.getComponent();
			button.setForeground(INACTIVE_COLOR);
		}
	};

	private JLabel changeLabel = new JLabel();
	
	private JLabel label = new JLabel();
	
	private JButton closeButton = new JButton("\u2718");
	
	@Autowired
	private UiLayout uiLayout;
	
	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private ActionCollection actionCollection;
	
	private Project project;
	
	private File file;
	
	private JPopupMenu popupMenu;
	
	private FileEditor fileEditor;
	
	public FileEditorTab(Project project, File file, FileEditor fileEditor)
	{
		this.project = project;
		this.file = file;
		this.fileEditor = fileEditor;
		
		super.setOpaque(false);
		
		label.setText(file.getName());
		label.setBackground(null);
		
		closeButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		closeButton.setBackground(null);
		closeButton.setContentAreaFilled(false);
		closeButton.setFocusable(false);
		closeButton.setBorderPainted(false);
		closeButton.setRolloverEnabled(true);
		closeButton.setForeground(INACTIVE_COLOR);
		closeButton.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
		closeButton.addMouseListener(CLOSE_BUTTON_MOUSE_LIST);
		closeButton.setToolTipText("Close");

		super.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		super.add(changeLabel);
		super.add(label);
		super.add(closeButton);
	}
	
	@PostConstruct
	private void init()
	{
		ideContext.addContextListener(new IContextListener()
		{
			@Override
			public void fileChanged(File file)
			{
				fileContentChanged(file);
			}
			
			@Override
			public void fileSaved(File file)
			{
				fileContentSaved(file);
			}
		});
		
		closeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ideContext.setActiveDetails(project, file);
				actionCollection.invokeAction("closeFile");
			}
		});
		
		super.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				ideContext.setActiveDetails(project, file);
				actionCollection.invokeAction("openFile");
			}
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				if(!e.isPopupTrigger())
				{
					return;
				}
				
				displayPopup(e);
			}
		});
	}
	
	public File getFile()
	{
		return file;
	}
	
	public void setFile(File file)
	{
		label.setText(file.getName());
		this.file = file;
	}
	
	public FileEditor getFileEditor()
	{
		return fileEditor;
	}
	
	public Project getProject()
	{
		return project;
	}
	
	private void fileContentChanged(File file)
	{
		if(!this.file.equals(file))
		{
			return;
		}
		
		changeLabel.setText("*");
	}
	
	private void fileContentSaved(File file)
	{
		if(!this.file.equals(file))
		{
			return;
		}
		
		changeLabel.setText("");
	}
	
	private void displayPopup(MouseEvent e)
	{
		if(popupMenu == null)
		{
			 popupMenu = uiLayout.getPopupMenu("fileTabPopup").toPopupMenu(actionCollection);
		}
		
		ideContext.setActiveDetails(project, file);
		popupMenu.show(this, e.getX(), e.getY());
	}
	
	public boolean isFileChanged()
	{
		return changeLabel.getText().length() > 0;
	}
}
