package com.yukthitech.autox.ide.help;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.common.AutomationUtils;
import com.yukthitech.autox.doc.DocGenerator;
import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.doc.StepInfo;
import com.yukthitech.autox.doc.ValidationInfo;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.views.IViewPanel;
import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;
import com.yukthitech.utils.fmarker.FreeMarkerMethodDoc;

@Component
public class HelpPanel extends JPanel implements IViewPanel
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(HelpPanel.class);

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

	private final JLabel lblSearch = new JLabel("Search: ");

	private HelpNodeData rootNode;
	
	private Directory indexDirectory;
	
	private StandardAnalyzer indexAnalyzer = new StandardAnalyzer();
	
	private IndexSearcher indexSearcher;
	
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
		} catch(Exception ex)
		{
			throw new IllegalStateException("An error occured while loading documentation template", ex);
		}
	}
	
	private void initIndex()
	{
		File folder = new File("./autox-work/help-index");

		try
		{
			if(folder.exists())
			{
				AutomationUtils.deleteFolder(folder);
			}
			
			FileUtils.forceMkdir(folder);
			indexDirectory = FSDirectory.open(folder.toPath());
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while creating index folder: " + folder.getPath(), ex);
		}
	}

	@PostConstruct
	private void display()
	{
		initIndex();
		
		String[] basepackage = { "com.yukthitech" };

		try
		{
			DocInformation docInformation = DocGenerator.buildDocInformation(basepackage);
			Map<String, Object> context = new HashMap<>();
			
			rootNode = new HelpNodeData("root", "root", "", null);
			
			HelpNodeData stepRootNode = new HelpNodeData("steps", "Steps", "", null);
			rootNode.addHelpNode(stepRootNode);
			
			for(StepInfo step : docInformation.getSteps())
			{
				context.put("type", "step");
				context.put("node", step);
				stepRootNode.addHelpNode(new HelpNodeData("step:" + step.getName(), step.getName(), buildDoc(documentTemplate, context), step));
			}
			
			HelpNodeData validationNode = new HelpNodeData("validations", "Validations", "", null);
			rootNode.addHelpNode(validationNode);

			for(ValidationInfo step : docInformation.getValidations())
			{
				context.put("type", "step");
				context.put("node", step);
				validationNode.addHelpNode(new HelpNodeData("validation:" + step.getName(), step.getName(), buildDoc(documentTemplate, context), step));
			}
			
			HelpNodeData methodNode = new HelpNodeData("methods", "Free Marker Methods", "", null);
			rootNode.addHelpNode(methodNode);

			for(FreeMarkerMethodDoc method : docInformation.getFreeMarkerMethods())
			{
				context.put("type", "method");
				context.put("node", method);
				methodNode.addHelpNode(new HelpNodeData("method:" + method.getName(), method.getName(), buildDoc(fmMethodDocTemplate, context), method));
			}
			
			//create and open index
			IndexWriterConfig config = new IndexWriterConfig(indexAnalyzer);
			IndexWriter writer = new IndexWriter(indexDirectory, config);
			rootNode.index(writer);
			writer.close();
			
			IndexReader indexReader = DirectoryReader.open(indexDirectory);
			indexSearcher = new IndexSearcher(indexReader);

			//create final tree
			HelpTreeModel model = new HelpTreeModel(rootNode);
			tree.setModel(model);
		} catch(Exception ex)
		{
			logger.error("An error occurred while initializing help panel", ex);
			throw new InvalidStateException("An error occurred while initializing help panel", ex);
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
			tree.setRootVisible(false);
			tree.setShowsRootHandles(true);
			
			tree.addTreeSelectionListener(new TreeSelectionListener()
			{
				@Override
				public void valueChanged(TreeSelectionEvent e)
				{
					displayNodeContent();
				}
			});
			tree.setCellRenderer(new DefaultTreeCellRenderer() 
			{
				private static final long serialVersionUID = 1L;

				@Override
				public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
				{
					JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
					if(!searchField.getText().isEmpty()&&label.getText().contains(searchField.getText()))
					
					{
						label.setForeground(Color.GRAY);
						
						return label;
					}
					return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
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
			gbl_panel.columnWidths = new int[] { 0, 0 };
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
			searchField.addKeyListener(new KeyAdapter()
			{
				@Override
				public void keyPressed(KeyEvent e)
				{
					if(e.getKeyCode() == KeyEvent.VK_UP)
					{
						tree.requestFocus();
						transferFocus();
					}
					if(e.getKeyCode() == KeyEvent.VK_DOWN)
					{
						tree.requestFocus();
						transferFocus();
					}
				}
			});
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
	
	private Set<String> performSearch(String newText, Set<String> filteredIds)
	{
		if(StringUtils.isBlank(newText))
		{
			return filteredIds;
		}
		
		try
		{
			TopScoreDocCollector collector = TopScoreDocCollector.create(1000);
			Query q = new QueryParser("doc", indexAnalyzer).parse(newText);
			indexSearcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			Set<String> filteredDocIds = new HashSet<>();
			
			if(hits != null)
			{
				for(ScoreDoc doc : hits)
				{
					Document filteredDoc = indexSearcher.doc(doc.doc);
					filteredDocIds.add(filteredDoc.get("id"));
				}
			}
			
			if(filteredIds == null)
			{
				return filteredDocIds;
			}
			
			filteredIds.retainAll(filteredDocIds);
			return filteredIds;
		} catch(Exception ex)
		{
			logger.error("An error occurred while performing search operation with string: {}", newText, ex);
			JOptionPane.showMessageDialog(this, "An error occurred while performing search operation. Search string: " + newText + "\nError: " + ex);
			return null;
		}
	}

	private void applyFilter()
	{
		String newText = searchField.getText().trim();

		if(newText.equals(currentSearchText))
		{
			return;
		}

		//do the lucene search
		Set<String> filteredDocIds = null;
		
		if(StringUtils.isNotBlank(newText))
		{
			String searchQueries[] = newText.split("\\|");
			
			for(String query : searchQueries)
			{
				filteredDocIds = performSearch(query, filteredDocIds);
				
				//when error occurs
				if(filteredDocIds == null)
				{
					return;
				}
				
				//if empty results are encountered
				if(filteredDocIds.isEmpty())
				{
					break;
				}
			}
		}
		
		//filter the tree in the ui
		currentSearchText = newText;
		rootNode.filter(filteredDocIds);
		
		HelpTreeModel model = new HelpTreeModel(rootNode);
		tree.setModel(model);
		selectNode((HelpTreeNode) model.getRoot());
	}

	private void selectNode(HelpTreeNode select)
	{
		if(select.getChildCount() > 0)
		{
			select = (HelpTreeNode) select.getFirstChild();
			selectNode(select);
		}
		else
		{
			TreeNode[] treeNodes = ((DefaultTreeModel) tree.getModel()).getPathToRoot((TreeNode) select);
			TreePath path = new TreePath(treeNodes);
			tree.scrollPathToVisible(path);
			tree.setSelectionPath(path);
		}
	}
	
	private String buildDoc(String template, Map<String, Object> context)
	{
		return freeMarkerEngine.processTemplate("documentation.ftl", template, context);
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

		HelpNodeData nodeValue = node.getHelpNodeData();
		editorPane.setText(nodeValue.getDocumentation());

		IdeUtils.executeUiTask(() -> {
			editorScrollPane.getVerticalScrollBar().setValue(0);
		});
	}

	public void activatePanel()
	{
		parentTabbedPane.setSelectedComponent(this);
		searchField.requestFocus();
		
		if(searchField.getText().length() > 0)
		{
			searchField.select(0, searchField.getText().length());
		}
	}
}
