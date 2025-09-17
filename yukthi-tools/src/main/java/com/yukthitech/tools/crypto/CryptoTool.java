package com.yukthitech.tools.crypto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public class CryptoTool extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	public static class CryptoEntry implements Serializable
	{
		private static final long serialVersionUID = 1L;

		private String title;
		
		private String content;

		public CryptoEntry(String title, String content)
		{
			this.title = title;
			this.content = content;
		}
	}
	
	private static class TextDocListener implements DocumentListener
	{
		private JTextComponent component;
		
		private CryptoEntry entry;
		
		private boolean forTitle;
		
		public TextDocListener(JTextComponent component, CryptoEntry entry, boolean forTitle)
		{
			this.component = component;
			this.entry = entry;
			this.forTitle = forTitle;
		}
		
		private void update()
		{
			if(forTitle)
			{
				entry.title = component.getText();
			}
			else
			{
				entry.content = component.getText();
			}
		}

		@Override
		public void insertUpdate(DocumentEvent e)
		{
			update();
		}

		@Override
		public void removeUpdate(DocumentEvent e)
		{
			update();
		}

		@Override
		public void changedUpdate(DocumentEvent e)
		{
			update();
		}
	}
	
	private JPanel contentPane;
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnNewMenu = new JMenu("File");
	private final JMenuItem openMenuItem = new JMenuItem("Open");
	private final JMenuItem saveMenuItem_1 = new JMenuItem("Save");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JPanel topPanel = new JPanel();
	private final JLabel lblNewLabel = new JLabel("Title Filter:");
	private final JTextField filterFld = new JTextField();
	private final JMenuItem addEntryMenuItem = new JMenuItem("Add Entry");
	private final JPanel panel = new JPanel();
	private final JLabel fillerLbl = new JLabel("...");
	
	private List<CryptoEntry> entries = new LinkedList<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					CryptoTool frame = new CryptoTool();
					frame.setVisible(true);
				} catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CryptoTool()
	{
		filterFld.setFont(new Font("Tahoma", Font.BOLD, 14));
		filterFld.setColumns(30);
		setTitle("Crypto Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 766, 548);
		
		setJMenuBar(menuBar);
		
		menuBar.add(mnNewMenu);
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		
		mnNewMenu.add(openMenuItem);
		saveMenuItem_1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		openMenuItem.addActionListener(e -> openFile());
		
		mnNewMenu.add(saveMenuItem_1);
		addEntryMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		saveMenuItem_1.addActionListener(e -> saveFile());
		
		mnNewMenu.add(addEntryMenuItem);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		addEntryMenuItem.addActionListener(e -> addEmptyEntry());

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		contentPane.add(scrollPane, BorderLayout.CENTER);
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		scrollPane.setViewportView(panel);
		
		contentPane.add(topPanel, BorderLayout.NORTH);
		
		topPanel.add(lblNewLabel);
		
		filterFld.setFont(new Font("Tahoma", Font.BOLD, 16));
		filterFld.setColumns(30);
		filterFld.setBorder(BorderFactory.createCompoundBorder(
				filterFld.getBorder(), 
		        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		topPanel.add(filterFld);

		FlowLayout flowLayout = (FlowLayout) topPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);

		/*
		GridBagLayout gridLayout = new GridBagLayout();
		gridLayout.columnWidths = new int[] {0};
		gridLayout.rowHeights = new int[] {0, 0, 0, 0};
		gridLayout.columnWeights = new double[]{1.0};
		//gridLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0};
		panel.setLayout(gridLayout);
		*/
		
		panel.setLayout(null);
		addEmptyEntry();
	
	}
	
	private void addEmptyEntry()
	{
		addEntry(new CryptoEntry("", ""), entries.size() + 1);
	}

	private void addEntry(CryptoEntry entry, int index)
	{
		panel.remove(fillerLbl);
		
		JLabel entryTitleLabel = new JLabel("Entry# " + index);
		JTextField titleFld = new JTextField();
		JScrollPane contentScrollPane = new JScrollPane();
		JTextArea contentFld = new JTextArea();
		
		titleFld.setText(entry.title);

		titleFld.getDocument().addDocumentListener(new TextDocListener(titleFld, entry, true));
		contentFld.getDocument().addDocumentListener(new TextDocListener(contentFld, entry, false));

		titleFld.setForeground(Color.BLUE);
		titleFld.setFont(new Font("Tahoma", Font.BOLD, 18));
		titleFld.setColumns(10);
		titleFld.setBorder(BorderFactory.createCompoundBorder(
				titleFld.getBorder(), 
		        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		contentFld.setRows(10);
		contentFld.setText(entry.content);
		contentFld.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentFld.setBorder(BorderFactory.createCompoundBorder(
				contentFld.getBorder(), 
		        BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		int startIndex = (index - 1) * 3;
		int startY = 0;

		/*
		GridBagConstraints lblConstraints = new GridBagConstraints();
		lblConstraints.insets = new Insets(0, 0, 5, 0);
		lblConstraints.fill = GridBagConstraints.HORIZONTAL;
		lblConstraints.gridx = 0;
		lblConstraints.gridy = startIndex;
		panel.add(entryTitleLabel, lblConstraints);
		*/
		panel.add(entryTitleLabel);
		entryTitleLabel.setBounds(5, startY + 5, panel.getWidth(), 20);
		
		/*
		GridBagConstraints titleConstraints = new GridBagConstraints();
		titleConstraints.insets = new Insets(5, 10, 5, 10);
		titleConstraints.fill = GridBagConstraints.HORIZONTAL;
		titleConstraints.gridx = 0;
		titleConstraints.gridy = startIndex + 1;
		panel.add(titleFld, titleConstraints);
		*/
		panel.add(titleFld);
		
		/*
		GridBagConstraints contentConstraints = new GridBagConstraints();
		contentConstraints.insets = new Insets(5, 10, 5, 10);
		contentConstraints.fill = GridBagConstraints.HORIZONTAL;
		contentConstraints.gridx = 0;
		contentConstraints.gridy = startIndex + 2;
		panel.add(contentScrollPane, contentConstraints);
		*/
		panel.add(contentScrollPane);
		contentScrollPane.setViewportView(contentFld);
		
		/*
		GridBagConstraints filterConstraints = new GridBagConstraints();
		filterConstraints.gridx = 0;
		filterConstraints.gridy = startIndex + 3;
		filterConstraints.fill = GridBagConstraints.BOTH;
		panel.add(fillerLbl, filterConstraints);
		*/

		//panel.doLayout();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		titleFld.requestFocus();
		
		entries.add(entry);
	}
	
	private void openFile()
	{
		entries.clear();
		panel.removeAll();
		
		addEmptyEntry();
	}
	
	private void saveFile()
	{
		
	}
}
