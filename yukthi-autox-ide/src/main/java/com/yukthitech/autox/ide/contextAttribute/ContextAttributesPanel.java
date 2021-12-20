package com.yukthitech.autox.ide.contextAttribute;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.exeenv.EnvironmentEvent;
import com.yukthitech.autox.ide.exeenv.EnvironmentEventType;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;
import com.yukthitech.autox.ide.layout.ActionCollection;
import com.yukthitech.autox.ide.layout.UiLayout;
import com.yukthitech.autox.ide.views.IViewPanel;
import com.yukthitech.autox.monitor.ienv.ContextAttributeDetails;

@Component
public class ContextAttributesPanel extends JPanel implements IViewPanel
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Create the panel.
	 */
	@Autowired
	private IdeContext IdeContext;

	private ExecutionEnvironment activeEnvironment;

	@Autowired
	private UiLayout uiLayout;

	@Autowired
	private ActionCollection actionCollection;

	private ContextAttributeValueDlg ctxValDlg;

	private JScrollPane scrollPane;

	private JTable table;

	private ContextAttributeTableModel model;

	private JPopupMenu ctxAttributePopup;

	public ContextAttributesPanel()
	{
		setLayout(new BorderLayout(0, 0));
		add(getScrollPane());
	}

	@PostConstruct
	public void init()
	{
		IdeContext.addContextListener(new IContextListener()
		{
			@Override
			public void activeEnvironmentChanged(ExecutionEnvironment activeEnvironment)
			{
				ContextAttributesPanel.this.activeEnvironment = activeEnvironment;
				model.reload(activeEnvironment);
			}

			@Override
			public void environmentChanged(EnvironmentEvent event)
			{
				if(activeEnvironment != event.getEnvironment() || event.getEventType() != EnvironmentEventType.CONTEXT_ATTRIBUTE_CHANGED)
				{
					return;
				}
				
				contextAttrChanged(event.getNewContextAttribute());
			}
		});
	}

	@Override
	public void setParent(JTabbedPane parentTabPane)
	{
	}

	private JScrollPane getScrollPane()
	{
		if(scrollPane == null)
		{
			scrollPane = new JScrollPane(getTable());
		}
		return scrollPane;
	}

	private JTable getTable()
	{
		if(table == null)
		{
			table = new JTable();
			table.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseClicked(MouseEvent e)
				{
					if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && table.getSelectedColumn() == 1)
					{
						if(ctxValDlg == null)
						{
							ctxValDlg = new ContextAttributeValueDlg(IdeUtils.getCurrentWindow());
						}
						
						ctxValDlg.display(
							model.getValueAt(table.getSelectedRow(), 0).toString(),
							model.getValueAt(table.getSelectedRow(), table.getSelectedColumn()).toString()
						);
					}
					else if(SwingUtilities.isRightMouseButton(e))
					{
						ctxAttributePopup = uiLayout.getPopupMenu("contextAttributePopup").toPopupMenu(actionCollection);
						table.setComponentPopupMenu(ctxAttributePopup);
					}

				}
			});
			
			model = new ContextAttributeTableModel();
			table.setModel(model);
		}
		return table;
	}

	private void contextAttrChanged(ContextAttributeDetails attr)
	{
		model.addContextAttribute(attr);
	}
}
