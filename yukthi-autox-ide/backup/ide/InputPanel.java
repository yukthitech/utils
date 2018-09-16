package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.yukthitech.autox.ide.engine.IdeEngine;
import com.yukthitech.autox.ide.engine.StepDetails;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class InputPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private final JPanel panel = new JPanel();
	private final JButton button = new JButton(">>");
	private final RTextScrollPane textScrollPane = new RTextScrollPane();
	private final RSyntaxTextArea fldStepInput = new RSyntaxTextArea();

	private IdeEngine ideContext = new IdeEngine();
	
	private List<String> history = new LinkedList<>();
	private int historyIndex = -1;

	/**
	 * Create the panel.
	 */
	public InputPanel()
	{
		setLayout(new BorderLayout(0, 0));
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

		add(panel, BorderLayout.NORTH);
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				executeStep();
			}
		});
		
		button.setToolTipText("Execute Step (CTRL + ENTER)");

		panel.add(button);

		add(textScrollPane, BorderLayout.CENTER);
		fldStepInput.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				if(!e.isControlDown())
				{
					return;
				}
				
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					executeStep();
				}
				
				if(e.getKeyCode() == KeyEvent.VK_UP)
				{
					browseHistory(true);
				}
				
				if(e.getKeyCode() == KeyEvent.VK_DOWN)
				{
					browseHistory(false);
				}
			}
		});

		textScrollPane.setViewportView(fldStepInput);
		fldStepInput.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		fldStepInput.setCodeFoldingEnabled(true);
		fldStepInput.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
	}

	public void setIdeEngine(IdeEngine ideContext)
	{
		this.ideContext = ideContext;
	}
	
	private void browseHistory(boolean up)
	{
		//when history browsing is started
		if(historyIndex < 0)
		{
			//if down is pressed ignore
			if(!up)
			{
				return;
			}
			
			if(history.isEmpty())
			{
				return;
			}
			
			historyIndex = history.size();
		}
		
		if(up)
		{
			if(historyIndex <= 0)
			{
				return;
			}
			
			historyIndex --;
		}
		else
		{
			if(historyIndex >= history.size() - 1)
			{
				return;
			}
			
			historyIndex ++;
		}
		
		
		String histText = history.get(historyIndex);
		fldStepInput.setText(histText);
	}

	private void executeStep()
	{
		String text = fldStepInput.getText();

		if(text.length() == 0)
		{
			return;
		}
		
		try
		{
			String rtfText = IdeUtils.getRtfText(fldStepInput);
			StepDetails stepDetails = new StepDetails(text, rtfText);
			
			if(!ideContext.executeStep(stepDetails))
			{
				return;
			}
			
			fldStepInput.setText("");
			
			historyIndex = -1;
			history.add(text);
			
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while executing step", ex);
		}
	}
}
