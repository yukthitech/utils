package com.yukthitech.swing.tree.cbox;

import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.yukthitech.swing.tree.ILazyTreeDataProvider;
import com.yukthitech.swing.tree.LazyTreeModel;
import com.yukthitech.swing.tree.LazyTreeNode;

public class CheckBoxTree extends JTree
{
	private static final long serialVersionUID = 1L;
	
	private LazyTreeModel<CboxNodeData> model;
	
	private ILazyTreeDataProvider<CboxNodeData> dataProvider;
	
	public CheckBoxTree()
	{
		super.setBorder(new EmptyBorder(5, 5, 5, 5));

		CboxTreeCellRenderer renderer = new CboxTreeCellRenderer();
		super.setCellRenderer(renderer);
		
		super.setEditable(true);
		super.setShowsRootHandles(true);
		super.setRootVisible(false);
		
		super.addTreeWillExpandListener(new TreeWillExpandListener()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
			{
				TreePath path = event.getPath();
		        
				if (path.getLastPathComponent() instanceof LazyTreeNode)
				{
					LazyTreeNode<CboxNodeData> node = (LazyTreeNode<CboxNodeData>) path.getLastPathComponent();
					node.loadChildren(model, false);
		        }
			}
			
			@Override
			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
			{
			}
		});
	}
	
	public void reload(LazyTreeNode<CboxNodeData> node)
	{
		node.loadChildren(model, true);
	}
	
	@SuppressWarnings({"unchecked" })
	public void setDataProvider(ILazyTreeDataProvider<CboxNodeData> dataProvider, ICboxTreeStateManager stateManager)
	{
		this.dataProvider = dataProvider;
		
		model = new LazyTreeModel<CboxNodeData>(dataProvider);
		super.setModel(model);
		
		CboxTreeCellEditor editor = new CboxTreeCellEditor(model, stateManager, this);
		super.setCellEditor(editor);

		LazyTreeNode<CboxNodeData> rootNode = (LazyTreeNode<CboxNodeData>) model.getRoot();
		rootNode.setChildren(dataProvider.getChildNodes(rootNode.getData()), model);
	}
	
	public ILazyTreeDataProvider<CboxNodeData> getDataProvider()
	{
		return dataProvider;
	}

	public boolean isPathEditable(TreePath path)
	{
		Object comp = path.getLastPathComponent();

		if(comp instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) comp;
			return !node.isRoot();
		}

		return false;
	}
}
