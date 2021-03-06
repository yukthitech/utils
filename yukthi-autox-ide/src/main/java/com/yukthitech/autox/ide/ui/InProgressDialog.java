package com.yukthitech.autox.ide.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.yukthitech.autox.ide.IdeUtils;

public class InProgressDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	private static InProgressDialog instance;
	
	private static Logger logger = LogManager.getLogger(InProgressDialog.class);
	
	private final JPanel contentPanel = new JPanel();
	private final JLabel lblPleaseWait = new JLabel("Please Wait! Work in Progress...");
	private final JLabel lblSubMessage = new JLabel("");

	/**
	 * Create the dialog.
	 */
	public InProgressDialog(Window window)
	{
		super(window);
//		super(window,ModalityType.APPLICATION_MODAL);
//		setModalityType(ModalityType.APPLICATION_MODAL);
		setUndecorated(true);
		setResizable(false);
		setBounds(100, 100, 531, 143);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		lblPleaseWait.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblPleaseWait.setHorizontalAlignment(SwingConstants.CENTER);
		
		contentPanel.add(lblPleaseWait, BorderLayout.CENTER);
		lblSubMessage.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblSubMessage.setHorizontalAlignment(SwingConstants.CENTER);
		
		contentPanel.add(lblSubMessage, BorderLayout.SOUTH);
	}
	
	public void setSubmessage(String mssg)
	{
		lblSubMessage.setText(mssg);
	}
	
	public synchronized void display(String message, final Runnable jobToExecute)
	{
		lblPleaseWait.setText(message);

		super.setVisible(true);
		IdeUtils.centerOnScreen(this);
		
		IdeUtils.execute(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					jobToExecute.run();
				}catch(Exception ex)
				{
					logger.error("An error occurred while executing in-progress action", ex);
				}
				
				InProgressDialog.this.setVisible(false);
			}
		}, 1);
	}
	
	public synchronized static InProgressDialog getInstance(){
		if(instance==null){
			instance=new InProgressDialog(IdeUtils.getCurrentWindow());
		}
		return instance;
	}
}
