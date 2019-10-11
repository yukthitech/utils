package com.yukthitech.autox.ide.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import org.fife.ui.rsyntaxtextarea.SquiggleUnderlineHighlightPainter;

public class RectangleHighlighter extends SquiggleUnderlineHighlightPainter
{
	private static final long serialVersionUID = 1L;

	private Color fillColor;
	
	public RectangleHighlighter(Color color)
	{
		super(color);
		this.fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 50);
	}
	
	@Override
	protected void paintSquiggle(Graphics g, Rectangle r)
	{
		g.setColor(fillColor);
		g.fillRect(r.x, r.y, r.width, r.height);
		
		super.paintSquiggle(g, r);
	}
}
