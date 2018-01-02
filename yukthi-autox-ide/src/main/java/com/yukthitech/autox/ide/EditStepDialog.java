package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.yukthitech.autox.ide.model.ExecutedStep;

public class EditStepDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private final RTextScrollPane textScrollPane = new RTextScrollPane();
	private final RSyntaxTextArea syntaxTextArea = new RSyntaxTextArea();

	private boolean dlgState = false;

	/**
	 * Create the dialog.
	 */
	public EditStepDialog()
	{
		super(KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow(), ModalityType.APPLICATION_MODAL);
		setTitle("Edit Step");

		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));

		contentPanel.add(textScrollPane, BorderLayout.CENTER);

		textScrollPane.setViewportView(syntaxTextArea);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setMnemonic('O');
				okButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						acceptText();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setMnemonic('C');
				cancelButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						cancel();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}

		syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		syntaxTextArea.setCodeFoldingEnabled(true);
		syntaxTextArea.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
	}

	public boolean display(ExecutedStep step)
	{
		syntaxTextArea.setText(step.getText());

		super.setVisible(true);

		if(dlgState)
		{
			step.setText(syntaxTextArea.getText(), IdeUtils.getRtfText(syntaxTextArea));
			return true;
		}

		return false;
	}

	private void acceptText()
	{
		if(StringUtils.isBlank(syntaxTextArea.getText()))
		{
			JOptionPane.showMessageDialog(this, "Please provide some text!");
			return;
		}

		dlgState = true;
		super.setVisible(false);
	}

	public void cancel()
	{
		dlgState = false;
		super.setVisible(false);
	}
}
