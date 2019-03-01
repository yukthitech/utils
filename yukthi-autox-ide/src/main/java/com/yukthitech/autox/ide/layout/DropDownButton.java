package com.yukthitech.autox.ide.layout;

import javax.swing.JPanel;

import com.yukthitech.autox.ide.IdeUtils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

public class DropDownButton extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	public static interface DropDownItemProvider
	{
		public List<String> getItems();
	}

	private final JButton btnMain = new JButton("Main");
	private final JButton btnDropDown = new JButton("");

	/**
	 * Create the panel.
	 */
	public DropDownButton()
	{
		setLayout(new BorderLayout(0, 0));
		btnMain.setBorder(null);
		
		add(btnMain, BorderLayout.CENTER);
		btnDropDown.setBorder(new EmptyBorder(0, 5, 0, 5));
		
		btnDropDown.setIcon(IdeUtils.loadIconWithoutBorder("/ui/icons/drop-down-arrow.png", 16));
		add(btnDropDown, BorderLayout.EAST);

	}
	
	@Override
	public Dimension getPreferredSize()
	{
		Dimension mainButDim = btnMain.getPreferredSize();
		return new Dimension(mainButDim.width + btnDropDown.getPreferredSize().width, mainButDim.height);
	}
	
	public void addActionListener(ActionListener listener)
	{
		btnMain.addActionListener(listener);
	}

	public void setIcon(Icon icon)
	{
		btnMain.setIcon(icon);
	}
}
