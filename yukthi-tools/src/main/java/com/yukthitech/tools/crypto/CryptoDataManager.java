package com.yukthitech.tools.crypto;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

public class CryptoDataManager extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultTableModel tableModel;
	private JTextField searchField;
	private ArrayList<CryptoEntry> objectList;

	public CryptoDataManager()
	{
		objectList = new ArrayList<>();
		setTitle("Crypto Manager");
		
		super.setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLayout(new BorderLayout());

		// Table
		String[] columnNames = { "Title", "Data" };
		tableModel = new DefaultTableModel(columnNames, 0);
		table = new JTable(tableModel);
		table.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
		
		add(new JScrollPane(table), BorderLayout.CENTER);

		// Panel for buttons and search
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		topPanel.setBorder(new EtchedBorder());
		
		// Load button
		JButton loadButton = new JButton("Load File");
		loadButton.addActionListener(e -> loadFromFile());
		topPanel.add(loadButton);

		// Save button
		JButton saveButton = new JButton("Save File");
		saveButton.addActionListener(e -> saveToFile());
		topPanel.add(saveButton);
		
		topPanel.add(new JSeparator(JSeparator.VERTICAL));

		// Add button
		JButton addButton = new JButton("Add Entry");
		addButton.addActionListener(e -> addObject());
		topPanel.add(addButton);

		// Search field
		searchField = new JTextField(20);
		topPanel.add(searchField);

		// Search button
		JButton searchButton = new JButton("Search");
		searchButton.addActionListener(e -> searchObject());
		topPanel.add(searchButton);

		add(topPanel, BorderLayout.NORTH);
	}

	private void addObject()
	{
		JTextField titleField = new JTextField();
		JTextArea field1 = new JTextArea(10, 20);

		Object[] message = { "Title:", titleField, "Data:", field1};

		int option = JOptionPane.showConfirmDialog(null, message, "Add Entry", JOptionPane.OK_CANCEL_OPTION);
		
		if(option == JOptionPane.OK_OPTION)
		{
			String title = titleField.getText();
			String f1 = field1.getText();

			CryptoEntry obj = new CryptoEntry(title, f1);
			objectList.add(obj);
			tableModel.addRow(new Object[] { title, f1});
		}
	}

	private void searchObject()
	{
		String searchTerm = searchField.getText();
		tableModel.setRowCount(0); // Clear the table

		for(CryptoEntry obj : objectList)
		{
			if(obj.getTitle().contains(searchTerm))
			{
				tableModel.addRow(new Object[] { obj.getTitle(), obj.getData()});
			}
		}
	}

	private void saveToFile()
	{
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("objects.dat")))
		{
			oos.writeObject(objectList);
			JOptionPane.showMessageDialog(this, "Objects saved to file.");
		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void loadFromFile()
	{
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("objects.dat")))
		{
			objectList = (ArrayList<CryptoEntry>) ois.readObject();
			tableModel.setRowCount(0); // Clear the table

			for(CryptoEntry obj : objectList)
			{
				tableModel.addRow(new Object[] { obj.getTitle(), obj.getData()});
			}
			
			JOptionPane.showMessageDialog(this, "Objects loaded from file.");
		} catch(IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new CryptoDataManager().setVisible(true);
			}
		});
	}
}

class CryptoEntry implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String title;
	private String data;

	public CryptoEntry(String title, String field1)
	{
		this.title = title;
		this.data = field1;
	}

	public String getTitle()
	{
		return title;
	}

	public String getData()
	{
		return data;
	}
}
