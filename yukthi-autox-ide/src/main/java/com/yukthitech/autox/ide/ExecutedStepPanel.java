package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import com.yukthitech.autox.ide.engine.IdeEngine;
import com.yukthitech.autox.ide.model.ExecutedStep;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;

public class ExecutedStepPanel extends JPanel implements Comparable<ExecutedStepPanel>
{
	private static final long serialVersionUID = 1L;

	private static RTFEditorKit rtfEditorKit = new RTFEditorKit();

	private static EditStepDialog editStepDialog;

	private final JPanel panel = new JPanel();
	private final JLabel lblTitle = new JLabel("Id: ");
	private final JButton btnX = new JButton("X");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JTextPane textPane = new JTextPane();

	private ExecutedStep step;

	private IdeEngine ideEngine;

	private FinalStepPanel finalStepPanel;
	private final JPopupMenu popupMenu = new JPopupMenu();
	private final JMenuItem menuItem = new JMenuItem("Edit");
	private final JMenuItem menuItem_1 = new JMenuItem("Move Up");
	private final JMenuItem menuItem_2 = new JMenuItem("Move Down");
	private final JMenuItem menuItem_3 = new JMenuItem("Move To Top");
	private final JMenuItem menuItem_4 = new JMenuItem("Move To Bottom");
	private final JMenuItem menuItem_5 = new JMenuItem("Remove");

	private boolean active = false;
	private final JMenuItem mntmExecute = new JMenuItem("Execute");

	/**
	 * Create the panel.
	 */
	public ExecutedStepPanel(ExecutedStep step, IdeEngine ideEngine, FinalStepPanel finalStepPanel)
	{
		this.step = step;
		this.ideEngine = ideEngine;
		this.finalStepPanel = finalStepPanel;

		setLayout(new BorderLayout(0, 0));
		panel.setBorder(null);

		add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		GridBagConstraints gbc_lblTitle = new GridBagConstraints();
		gbc_lblTitle.fill = GridBagConstraints.BOTH;
		gbc_lblTitle.gridx = 0;
		gbc_lblTitle.gridy = 0;
		lblTitle.setBorder(new EmptyBorder(0, 5, 0, 0));
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setOpaque(true);
		lblTitle.setBackground(Color.BLUE);
		lblTitle.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblTitle, gbc_lblTitle);

		GridBagConstraints gbc_btnX = new GridBagConstraints();
		gbc_btnX.fill = GridBagConstraints.VERTICAL;
		gbc_btnX.gridx = 1;
		gbc_btnX.gridy = 0;
		btnX.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnX.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				delete();
			}
		});
		btnX.setForeground(Color.WHITE);
		btnX.setBackground(Color.RED);
		panel.add(btnX, gbc_btnX);

		add(scrollPane, BorderLayout.CENTER);
		textPane.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(active)
				{
					handleKeyPress(e);
				}
			}
		});

		textPane.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				if(finalStepPanel.getActivePanel() != null)
				{
					finalStepPanel.getActivePanel().clearActive();
				}
				
				active = true;
				textPane.setBackground(Color.yellow);
				finalStepPanel.setActivePanel(ExecutedStepPanel.this);
			}
		});

		textPane.setEditable(false);

		scrollPane.setViewportView(textPane);
		init();
	}
	
	private void clearActive()
	{
		active = false;
		textPane.setBackground(Color.white);
	}

	private void init()
	{
		textPane.setEditorKit(rtfEditorKit);

		addPopup(textPane, popupMenu);
		menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				edit();
			}
		});
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));

		popupMenu.add(menuItem);

		menuItem_1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				moveUp();
			}
		});
		menuItem_1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK));

		popupMenu.add(menuItem_1);

		menuItem_2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				moveDown();
			}
		});
		menuItem_2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK));

		popupMenu.add(menuItem_2);

		menuItem_3.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				moveToTop();
			}
		});
		menuItem_3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, InputEvent.CTRL_MASK));

		menuItem_4.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				moveToBottom();
			}
		});
		popupMenu.add(menuItem_3);
		menuItem_4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.CTRL_MASK));

		popupMenu.add(menuItem_4);

		menuItem_2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				delete();
			}
		});
		mntmExecute.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				executeStep();
			}
		});
		mntmExecute.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK));

		popupMenu.add(mntmExecute);
		menuItem_5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_MASK));

		popupMenu.add(menuItem_5);

		// set font style
		StyledDocument doc = (StyledDocument) textPane.getDocument();
		Style style = doc.addStyle("StyleName", null);
		StyleConstants.setFontSize(style, 30);
		doc.addStyle("StyleName", style);

		try
		{
			rtfEditorKit.read(new ByteArrayInputStream(step.getRtfText().getBytes()), textPane.getDocument(), 0);
		} catch(Exception ex)
		{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "An error occurred while displaying step text: " + ex);
			return;
		}

		lblTitle.setText("Step: " + step.getId());
	}

	@Override
	public Dimension getPreferredSize()
	{
		Dimension dimension = super.getPreferredSize();

		if(super.getParent() != null)
		{
			dimension.width = super.getParent().getWidth();
		}

		return dimension;
	}

	private static void addPopup(Component component, final JPopupMenu popup)
	{
		component.addMouseListener(new MouseAdapter()
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

			private void showMenu(MouseEvent e)
			{
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	private void delete()
	{
		int res = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete step with id: " + step.getId(), "Delete", JOptionPane.YES_NO_OPTION);

		if(res == JOptionPane.NO_OPTION)
		{
			return;
		}

		ideEngine.removeStep(step);

		super.getParent().remove(this);
		finalStepPanel.refreshUi();
	}

	private void edit()
	{
		if(editStepDialog == null)
		{
			editStepDialog = new EditStepDialog();
		}

		if(editStepDialog.display(step))
		{
			textPane.setText("");

			try
			{
				rtfEditorKit.read(new ByteArrayInputStream(step.getRtfText().getBytes()), textPane.getDocument(), 0);
			} catch(Exception ex)
			{
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "An error occurred while displaying step text: " + ex);
				return;
			}
		}
	}

	public void requestActiveFocus()
	{
		IdeUtils.invokeAfter(100, () -> {
			textPane.requestFocus();
		});
	}

	private void moveUp()
	{
		int idx = ideEngine.getState().indexOf(step);

		if(idx <= 0)
		{
			return;
		}

		ideEngine.getState().moveStep(step, idx - 1);
		finalStepPanel.reorderSteps();
		requestActiveFocus();
	}

	private void moveDown()
	{
		int idx = ideEngine.getState().indexOf(step);
		int maxIdx = ideEngine.getState().getStepCount() - 1;

		if(idx >= maxIdx)
		{
			return;
		}

		ideEngine.getState().moveStep(step, idx + 1);
		finalStepPanel.reorderSteps();
		requestActiveFocus();
	}

	private void moveToTop()
	{
		int idx = ideEngine.getState().indexOf(step);

		if(idx <= 0)
		{
			return;
		}

		ideEngine.getState().moveStep(step, 0);
		finalStepPanel.reorderSteps();
		requestActiveFocus();
	}

	private void moveToBottom()
	{
		int idx = ideEngine.getState().indexOf(step);
		int maxIdx = ideEngine.getState().getStepCount() - 1;

		if(idx >= maxIdx)
		{
			return;
		}

		ideEngine.getState().moveStep(step, maxIdx);
		finalStepPanel.reorderSteps();
		requestActiveFocus();
	}

	private void moveFocus(int val)
	{
		Component components[] = super.getParent().getComponents();
		int index = -1;

		for(int i = 0; i < components.length; i++)
		{
			if(components[i] == this)
			{
				index = i;
				break;
			}
		}

		index += val;

		if(index < 0 || index >= components.length)
		{
			return;
		}

		((ExecutedStepPanel) components[index]).requestActiveFocus();
	}

	private void handleKeyPress(KeyEvent e)
	{
		if(!e.isControlDown())
		{
			if(e.getKeyCode() == KeyEvent.VK_F2)
			{
				edit();
				return;
			}

			if(e.getKeyCode() == KeyEvent.VK_UP)
			{
				moveFocus(-1);
				return;
			}

			if(e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				moveFocus(1);
				return;
			}

			return;
		}

		if(e.getKeyCode() == KeyEvent.VK_UP)
		{
			moveUp();
			return;
		}

		if(e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			moveDown();
			return;
		}

		if(e.getKeyCode() == KeyEvent.VK_PAGE_UP)
		{
			moveToTop();
			return;
		}

		if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
		{
			moveToBottom();
			return;
		}

		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			executeStep();
			return;
		}

		if(e.getKeyCode() == KeyEvent.VK_DELETE)
		{
			delete();
			return;
		}
	}

	@Override
	public int compareTo(ExecutedStepPanel o)
	{
		return ideEngine.getState().indexOf(step) - ideEngine.getState().indexOf(o.step);
	}

	public ExecutedStep getStep()
	{
		return step;
	}

	private void executeStep()
	{
		ideEngine.executeOnly(step);
		moveFocus(1);
	}
}
