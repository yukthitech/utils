/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.autox.ide.views.console;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.IViewPanel;
import com.yukthitech.autox.ide.IdeUtils;
import com.yukthitech.autox.ide.actions.FileActions;
import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.exeenv.EnvironmentEvent;
import com.yukthitech.autox.ide.exeenv.EnvironmentEventType;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;
import com.yukthitech.swing.HyperLinkEvent;
import com.yukthitech.swing.YukthiHtmlPane;

@Component
public class ConsolePanel extends JPanel implements IViewPanel
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger(ConsolePanel.class);
	
	private static Pattern LOC_PATTERN = Pattern.compile("([\\w\\-\\.]+\\.xml)\\:(\\d+)", Pattern.CASE_INSENSITIVE);

	private final JPanel panel = new JPanel();
	private final JLabel lblEnvironment = new JLabel("Environment: ");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JPanel panel_1 = new JPanel();
	private final JButton btnClear = new JButton("");
	private final YukthiHtmlPane consoleDisplayArea = new YukthiHtmlPane();

	@Autowired
	private IdeContext ideContext;
	
	@Autowired
	private FileActions fileAction;

	private JTabbedPane parentTabbedPane;

	private ExecutionEnvironment activeEnvironment;
	private final JButton btnOpenReport = new JButton("Open Report");

	/**
	 * Create the panel.
	 */
	public ConsolePanel()
	{
		setBorder(new EmptyBorder(3, 3, 3, 3));
		setLayout(new BorderLayout(0, 0));

		add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 444, 0 };
		gbl_panel.rowHeights = new int[] { 21, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.anchor = GridBagConstraints.EAST;
		gbc_panel_1.fill = GridBagConstraints.VERTICAL;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		panel.add(panel_1, gbc_panel_1);
		btnClear.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		btnClear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				clearConsole();
			}
		});
		btnClear.setToolTipText("Clear");
		btnClear.setIcon(IdeUtils.loadIcon("/ui/icons/clear-console.svg", 16));

		panel_1.add(btnClear);
		btnOpenReport.addActionListener(this::openReport);
		btnOpenReport.setEnabled(false);
		btnOpenReport.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		panel_1.add(btnOpenReport);
		lblEnvironment.setBorder(new EmptyBorder(3, 3, 3, 3));
		lblEnvironment.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblEnvironment.setOpaque(true);
		lblEnvironment.setBackground(UIManager.getColor("info"));
		lblEnvironment.setVisible(false);

		GridBagConstraints gbc_lblEnvironment = new GridBagConstraints();
		gbc_lblEnvironment.fill = GridBagConstraints.BOTH;
		gbc_lblEnvironment.gridx = 0;
		gbc_lblEnvironment.gridy = 0;
		panel.add(lblEnvironment, gbc_lblEnvironment);

		add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(consoleDisplayArea);
		
		consoleDisplayArea.addHyperLinkListener(this::onHyperLinkClick);
	}

	@PostConstruct
	private void init()
	{
		ideContext.addContextListener(new IContextListener()
		{
			@Override
			public void activeEnvironmentChanged(ExecutionEnvironment activeEnvironment)
			{
				if(activeEnvironment != null)
				{
					lblEnvironment.setVisible(true);
					lblEnvironment.setText("Environment: " + activeEnvironment.getName());
				}
				else
				{
					lblEnvironment.setVisible(false);
				}

				ConsolePanel.this.activeEnvironment = activeEnvironment;
				refreshConsoleText();
			}

			@Override
			public void environmentChanged(EnvironmentEvent event)
			{
				if(activeEnvironment != event.getEnvironment() || event.getEventType() != EnvironmentEventType.CONSOLE_CHANGED)
				{
					return;
				}

				appendNewContent(event.getNewMessage());
				
				boolean repFile = (event.getEnvironment().isReportFileAvailable());
				btnOpenReport.setEnabled(repFile);
			}
			
			@Override
			public void environmentTerminated(ExecutionEnvironment environment)
			{
				boolean repFile = (environment.isReportFileAvailable());
				btnOpenReport.setEnabled(repFile);
			}
		});
	}

	@Override
	public void setParent(JTabbedPane parentTabPane)
	{
		this.parentTabbedPane = parentTabPane;
	}

	private void refreshConsoleText()
	{
		if(activeEnvironment == null)
		{
			consoleDisplayArea.setText("");
			return;
		}

		consoleDisplayArea.setText("<html><body id=\"body\">" + injectLinks(activeEnvironment.getConsoleHtml()) + "</body></html>");
		moveToEnd();
	}
	
	private String injectLinks(CharSequence html)
	{
		Matcher matcher = LOC_PATTERN.matcher(html);
		StringBuffer buff = new StringBuffer();
		
		while(matcher.find())
		{
			matcher.appendReplacement(buff, "<a href=\"" + matcher.group(0) + "\">" + matcher.group(0) + "</a>");
		}
		
		matcher.appendTail(buff);
		return buff.toString();
	}
	
	private void moveToEnd()
	{
		EventQueue.invokeLater(() -> {
			// move scroll pane to the end
			JScrollBar vertical = scrollPane.getVerticalScrollBar();
			vertical.setValue(vertical.getMaximum());
			
			JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
			horizontal.setValue(0);
		});
	}

	private void appendNewContent(String content)
	{
		HTMLDocument htmlDoc = (HTMLDocument) consoleDisplayArea.getDocument();
		Element element = htmlDoc.getElement("body");

		try
		{
			htmlDoc.insertBeforeEnd(element, injectLinks(content));

			moveToEnd();
			
			if(parentTabbedPane != null)
			{
				parentTabbedPane.setSelectedComponent(this);
			}
		} catch(Exception ex)
		{
			logger.error("An error occurred while adding html content", ex);
		}
	}
	
	private void clearConsole()
	{
		if(activeEnvironment == null)
		{
			return;
		}
		
		activeEnvironment.clearConsole();
		refreshConsoleText();
	}
	
	private void onHyperLinkClick(HyperLinkEvent event)
	{
		Matcher matcher = LOC_PATTERN.matcher(event.getHref());
		
		if(!matcher.matches())
		{
			return;
		}
		
		String file = matcher.group(1);
		int lineNo = Integer.parseInt(matcher.group(2));
		
		fileAction.gotoFile(activeEnvironment.getProject(), file, lineNo, true);
	}
	
	private void openReport(ActionEvent e)
	{
		File file = activeEnvironment.getReportFile();
		
		if(file.exists())
		{
			try
			{
				Desktop.getDesktop().open(file);
			}catch(Exception ex)
			{
				logger.error("An error occurred while opening file: " + file.getPath(), ex);
				JOptionPane.showMessageDialog(this, "An error occurred while opening file: " + file.getPath() + "\nError: " + ex);
			}
		}
	}
}
