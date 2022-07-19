package com.yukthitech.swing;

import java.awt.event.MouseAdapter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

/**
 * Mouse listener for highlighting mouse border on mouse hover.
 * @author akiran
 */
public class BrdrHighligherMouseListener extends MouseAdapter
{
	/**
	 * Border thickness.
	 */
	private static final int BORDER_THICKNESS = 3;
	
	/**
	 * Border thickness for empty border.
	 */
	private static final int EMPTY_BORDER_THICKNESS = 5;

	/**
	 * Empty border.
	 */
	private static final Border EMPTY_BORDER = new EmptyBorder(EMPTY_BORDER_THICKNESS, EMPTY_BORDER_THICKNESS, 
			EMPTY_BORDER_THICKNESS, EMPTY_BORDER_THICKNESS);
	
	/**
	 * Etched border with empty boundaries.
	 */
	private static final Border ETCHED_BORDER = new CompoundBorder(
			new EtchedBorder(EtchedBorder.LOWERED, null, null), 
			new EmptyBorder(BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS));
	
	public static class ComponentAndBorder
	{
		private JComponent component;
		
		private Border emptyBorder;
		
		private Border highlightedBorder;

		public ComponentAndBorder(JComponent component, Border emptyBorder, Border highlightedBorder)
		{
			this.component = component;
			this.emptyBorder = emptyBorder;
			this.highlightedBorder = highlightedBorder;
			
			component.setBorder(emptyBorder);
		}

		public ComponentAndBorder(JComponent component, int leftWidth, int rightWidth)
		{
			this.component = component;
			this.emptyBorder = new EmptyBorder(EMPTY_BORDER_THICKNESS, leftWidth, EMPTY_BORDER_THICKNESS, rightWidth);
			this.highlightedBorder = new CompoundBorder(
					new EtchedBorder(EtchedBorder.LOWERED, null, null), 
					new EmptyBorder(BORDER_THICKNESS, leftWidth - 2, BORDER_THICKNESS, rightWidth - 2));
			
			component.setBorder(emptyBorder);
		}
	}
	
	private List<ComponentAndBorder> targets;
	
	private BrdrHighligherMouseListener(List<ComponentAndBorder> targets)
	{
		this.targets = targets;
		targets.forEach(comp -> comp.component.addMouseListener(BrdrHighligherMouseListener.this));
	}
	
	public static void applyTo(JComponent... sourceAndTarget)
	{
		List<ComponentAndBorder> targets = Arrays.asList(sourceAndTarget)
				.stream()
				.map(comp -> new ComponentAndBorder(comp, EMPTY_BORDER, ETCHED_BORDER))
				.collect(Collectors.toList());

		new BrdrHighligherMouseListener(targets);
	}

	public static void applyTo(ComponentAndBorder... sourceAndTarget)
	{
		new BrdrHighligherMouseListener(Arrays.asList(sourceAndTarget));
	}

	public void mouseEntered(java.awt.event.MouseEvent e) 
	{
		targets.forEach(comp -> comp.component.setBorder(comp.highlightedBorder));
	}
	
	public void mouseExited(java.awt.event.MouseEvent e) 
	{
		targets.forEach(comp -> comp.component.setBorder(comp.emptyBorder));
	}
}
