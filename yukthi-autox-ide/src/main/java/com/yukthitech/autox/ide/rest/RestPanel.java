package com.yukthitech.autox.ide.rest;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JSplitPane splitPane;
	
	@Autowired
	private RestRequest restRequest;
	
	@Autowired
	private RestResponse restResponse;

	/**
	 * Create the panel.
	 */
	public RestPanel()
	{

	}

	@PostConstruct
	public void init()
	{
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));
		add(getSplitPane());
	}

	private JSplitPane getSplitPane()
	{
		if(splitPane == null)
		{
			splitPane = new JSplitPane();
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane.setLeftComponent(restRequest);
			splitPane.setRightComponent(getPostmanResponse());
			splitPane.setMinimumSize(new Dimension(10, 10));
		}
		return splitPane;
	}

	private RestResponse getPostmanResponse()
	{
		return restResponse;
	}
}
