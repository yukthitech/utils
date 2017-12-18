package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.apache.commons.io.IOUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.yukthitech.autox.ide.engine.IdeContext;
import com.yukthitech.autox.ide.engine.StepDetails;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class IdeInputPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private final DataFlavor RTF_FLAVOR;

	private final JPanel panel = new JPanel();
	private final JButton button = new JButton(">>");
	private final RTextScrollPane textScrollPane = new RTextScrollPane();
	private final RSyntaxTextArea fldStepInput = new RSyntaxTextArea();

	private IdeContext ideContext = new IdeContext();

	/**
	 * Create the panel.
	 */
	public IdeInputPanel()
	{
		try
		{
			RTF_FLAVOR = new DataFlavor("text/rtf");
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while initializing rtf flavor", ex);
		}
				
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
				
				if(e.getKeyCode() != KeyEvent.VK_ENTER)
				{
					return;
				}
				
				executeStep();
			}
		});

		textScrollPane.setViewportView(fldStepInput);
		fldStepInput.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		fldStepInput.setCodeFoldingEnabled(true);
	}

	public void setIdeContext(IdeContext ideContext)
	{
		this.ideContext = ideContext;
	}

	private void executeStep()
	{
		String text = fldStepInput.getText();

		if(text.length() == 0)
		{
			return;
		}

		fldStepInput.selectAll();
		fldStepInput.copyAsRtf();

		try
		{
			ByteArrayInputStream bis = (ByteArrayInputStream) Toolkit.getDefaultToolkit().getSystemClipboard().getData(RTF_FLAVOR);
			String rtfText = IOUtils.toString(bis);

			StepDetails stepDetails = new StepDetails(text, rtfText);
			ideContext.executeStep(stepDetails);

			System.out.println("RTF: " + rtfText);
			
			fldStepInput.setText("");
		} catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while executing step", ex);
		}
	}
}
