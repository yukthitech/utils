package com.yukthitech.autox.ide.help;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.doc.DocGenerator;
import com.yukthitech.autox.doc.DocInformation;
import com.yukthitech.autox.doc.PluginInfo;
import com.yukthitech.autox.doc.StepInfo;
import com.yukthitech.autox.ide.views.IViewPanel;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

@Component
public class HelpPanel extends JPanel implements IViewPanel
{
	private static final long serialVersionUID = 1L;
	
	private JTabbedPane parentTabbedPane;
	
	private JSplitPane splitPane;
	private JScrollPane scrollPane;
	private JTree tree;
	private JScrollPane scrollPane_1;
	private JEditorPane editorPane;
	private JPanel panel;
	private JTextField textField;
	private JButton btnNewButton;
	
	private FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
	
	private String documentTemplate = null;

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
			documentTemplate = IOUtils.toString(HelpPanel.class.getResourceAsStream("/documentation.ftl"));
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
			HelpTreeModel model = new HelpTreeModel(docInformation);
			// model.addPlugins(docInformation.getPlugins());
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
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if(node == null)
					{
						return;
					}
					else
					{
						Map<String, Object> input = new HashMap<>();
						
						try
						{
							if(node instanceof PluginInfoTreeNode)
							{
								PluginInfoTreeNode pnode = (PluginInfoTreeNode) node;
								PluginInfo plugin = pnode.getPluginInfo();
								
								if(plugin != null)
								{
									input.put("type", "plugin");
									input.put("node", plugin);
									
									String output = freeMarkerEngine.processTemplate("documentation.ftl", documentTemplate, input);
									editorPane.setText(output);
								}
								else
								{
									editorPane.setText("");
								}
							}
							else if(node instanceof StepInforTreeNode)
							{
								StepInforTreeNode snode = (StepInforTreeNode) node;
								StepInfo step = snode.getStepInfo();
								input.put("type", "step");
								input.put("node", step);
								
								String output = freeMarkerEngine.processTemplate("documentation.ftl", documentTemplate, input);
								editorPane.setText(output);
							}
							else
							{
								editorPane.setText(" ");
							}
						} catch(Exception e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				}
			});
		}
		return tree;
	}

	private JScrollPane getScrollPane_1()
	{
		if(scrollPane_1 == null)
		{
			scrollPane_1 = new JScrollPane();
			scrollPane_1.setViewportView(getEditorPane());
		}
		return scrollPane_1;
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
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] { 364, 89, 0 };
			gbl_panel.rowHeights = new int[] { 23, 0 };
			gbl_panel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
			panel.setLayout(gbl_panel);
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.insets = new Insets(0, 0, 0, 5);
			gbc_textField.gridx = 0;
			gbc_textField.gridy = 0;
			panel.add(getTextField(), gbc_textField);
			GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
			gbc_btnNewButton.anchor = GridBagConstraints.NORTHWEST;
			gbc_btnNewButton.gridx = 1;
			gbc_btnNewButton.gridy = 0;
			panel.add(getBtnNewButton(), gbc_btnNewButton);
		}
		return panel;
	}

	private JTextField getTextField()
	{
		if(textField == null)
		{
			textField = new JTextField();
			textField.setColumns(10);
		}
		return textField;
	}

	private JButton getBtnNewButton()
	{
		if(btnNewButton == null)
		{
			btnNewButton = new JButton("Search");
		}
		return btnNewButton;
	}
}
