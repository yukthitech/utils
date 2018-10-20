package com.yukthitech.autox.ide.help;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.doc.DocGenerator;
import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.doc.PluginInfo;
import com.yukthitech.autox.doc.StepInfo;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.views.IViewPanel;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;
import com.yukthitech.utils.fmarker.FreeMarkerMethodDoc;

@Component
public class HelpPanel extends JPanel implements IViewPanel
{
	private static final long serialVersionUID = 1L;
	
	private JTabbedPane parentTabbedPane;
	
	private JSplitPane splitPane;
	private JScrollPane scrollPane;
	private JTree tree;
	private JScrollPane editorScrollPane;
	private JEditorPane editorPane;
	private JPanel panel;
	private JTextField searchField;
	
	private FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
	
	private String documentTemplate = null;
	
	private String fmMethodDocTemplate = null;
	
	private String currentSearchText = "";
	
	private HelpNodeData rootNodeData = new HelpNodeData("Autox Documentation", null);
	private final JLabel lblSearch = new JLabel("Search: ");

	/**
	 * Create the panel.
	 */
	public HelpPanel()
	{
		addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				System.out.println("key pressed" + e.getKeyCode());
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				System.out.println("key pressed" + e.getKeyCode());
			}
		});
		
		setLayout(new BorderLayout(0, 0));
		add(getPanel(), BorderLayout.NORTH);
		add(getSplitPane());
		
		try
		{
			documentTemplate = IOUtils.toString(HelpPanel.class.getResourceAsStream("/documentation.html"));
			fmMethodDocTemplate = IOUtils.toString(HelpPanel.class.getResourceAsStream("/fm-method-doc.html"));
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occured while loading documentation template", ex);
		}

	}

	@PostConstruct
	private void display()
	{
		String[] basepackage = { "com.yukthitech" };
		
		try
		{
			DocInformation docInformation = DocGenerator.buildDocInformation(basepackage);
			rootNodeData.addHelpNode(new HelpNodeData("Default Plugins", docInformation));

			for(PluginInfo pluginInfo : docInformation.getPlugins())
			{
				rootNodeData.addHelpNode(new HelpNodeData(pluginInfo, docInformation));
			}
			
			rootNodeData.addHelpNode( new HelpNodeData(docInformation.getFreeMarkerMethods(), docInformation) );
			
			HelpTreeModel model = new HelpTreeModel(rootNodeData);
			tree.setModel(model);
		} catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void setParent(JTabbedPane parentTabPane)
	{
		this.parentTabbedPane = parentTabPane;
	}

	private JSplitPane getSplitPane()
	{
		if(splitPane == null)
		{
			splitPane = new JSplitPane();
			splitPane.setLeftComponent(getScrollPane());
			splitPane.setRightComponent(getScrollPane_1());
		}
		return splitPane;
	}

	private JScrollPane getScrollPane()
	{
		if(scrollPane == null)
		{
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getTree());
		}
		return scrollPane;
	}

	private JTree getTree()
	{
		if(tree == null)
		{
			tree = new JTree();
			tree.addTreeSelectionListener(new TreeSelectionListener()
			{
				@Override
				public void valueChanged(TreeSelectionEvent e)
				{
					displayNodeContent();
				}
			});
		}
		return tree;
	}

	private JScrollPane getScrollPane_1()
	{
		if(editorScrollPane == null)
		{
			editorScrollPane = new JScrollPane();
			editorScrollPane.setViewportView(getEditorPane());
		}
		return editorScrollPane;
	}

	private JEditorPane getEditorPane()
	{
		if(editorPane == null)
		{
			editorPane = new JEditorPane("text/html", "");
			editorPane.setEditable(false);
		}
		return editorPane;
	}

	private JPanel getPanel()
	{
		if(panel == null)
		{
			panel = new JPanel();
			panel.setBorder(new EmptyBorder(5, 5, 5, 5));
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] {0, 0};
			gbl_panel.rowHeights = new int[] { 23, 0 };
			gbl_panel.columnWeights = new double[] { 0.0, 1.0 };
			gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
			panel.setLayout(gbl_panel);
			
			GridBagConstraints gbc_lblSearch = new GridBagConstraints();
			gbc_lblSearch.insets = new Insets(0, 0, 0, 5);
			gbc_lblSearch.anchor = GridBagConstraints.EAST;
			gbc_lblSearch.gridx = 0;
			gbc_lblSearch.gridy = 0;
			lblSearch.setFont(new Font("Tahoma", Font.BOLD, 12));
			panel.add(lblSearch, gbc_lblSearch);
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.insets = new Insets(0, 0, 0, 5);
			gbc_textField.gridx = 1;
			gbc_textField.gridy = 0;
			panel.add(getTextField(), gbc_textField);
		}
		return panel;
	}

	private JTextField getTextField()
	{
		if(searchField == null)
		{
			searchField = new JTextField();
			searchField.setColumns(10);
			
			final Runnable applyFilterRunnable = new Runnable()
			{
				@Override
				public void run()
				{
					applyFilter();
				}
			};
			
			searchField.getDocument().addDocumentListener(new DocumentListener()
			{
				@Override
				public void removeUpdate(DocumentEvent e)
				{
					IdeUtils.executeConsolidatedJob("applyFilter", applyFilterRunnable, 1000);
				}
				
				@Override
				public void insertUpdate(DocumentEvent e)
				{
					IdeUtils.executeConsolidatedJob("applyFilter", applyFilterRunnable, 1000);
				}
				
				@Override
				public void changedUpdate(DocumentEvent e)
				{
					IdeUtils.executeConsolidatedJob("applyFilter", applyFilterRunnable, 1000);
				}
			});
		}
		
		return searchField;
	}
	
	private void applyFilter()
	{
		String newText = searchField.getText().trim();
		
		if(newText.equals(currentSearchText))
		{
			return;
		}
		
		currentSearchText = newText;
		rootNodeData.filter(newText);
		
		HelpTreeModel model = new HelpTreeModel(rootNodeData);
		tree.setModel(model);
	}
	
	private void displayNodeContent()
	{
		Object treeNode = tree.getLastSelectedPathComponent();
		
		if(!(treeNode instanceof HelpTreeNode))
		{
			return;
		}
		
		HelpTreeNode node = (HelpTreeNode) tree.getLastSelectedPathComponent();

		if(node == null)
		{
			return;
		}
		
		Map<String, Object> input = new HashMap<>();
		Object nodeValue = node.getHelpNodeData().getNodeValue();
		
		try
		{
			if(nodeValue instanceof PluginInfo)
			{
				PluginInfo plugin = (PluginInfo) nodeValue;
				
				input.put("type", "plugin");
				input.put("node", plugin);
				
				String output = freeMarkerEngine.processTemplate("documentation.ftl", documentTemplate, input);
				editorPane.setText(output);
			}
			else if(nodeValue instanceof StepInfo)
			{
				StepInfo step = (StepInfo) nodeValue;
				input.put("type", "step");
				input.put("node", step);
				
				String output = freeMarkerEngine.processTemplate("documentation.html", documentTemplate, input);
				editorPane.setText(output);
			}
			else if(nodeValue instanceof FreeMarkerMethodDoc)
			{
				FreeMarkerMethodDoc method = (FreeMarkerMethodDoc) nodeValue;
				input.put("type", "method");
				input.put("node", method);
				
				String output = freeMarkerEngine.processTemplate("fm-method-doc.html", fmMethodDocTemplate, input);
				editorPane.setText(output);
			}
			else
			{
				editorPane.setText(" ");
			}

			IdeUtils.executeUiTask(()->
			{
				editorScrollPane.getVerticalScrollBar().setValue(0);
			});
		} catch(Exception e1)
		{
			e1.printStackTrace();
		}
	}
	
	public void activatePanel()
	{
		parentTabbedPane.setSelectedComponent(this);
		searchField.requestFocus();
	}
}
