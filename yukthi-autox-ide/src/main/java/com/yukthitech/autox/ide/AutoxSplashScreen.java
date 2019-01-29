package com.yukthitech.autox.ide;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AutoxSplashScreen extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private static AutoxSplashScreen splashScreen = new AutoxSplashScreen();
	
	private JPanel contentPane;
	private final JLabel label = new JLabel("");

	/**
	 * Launch the application.
	 */
	public static void display()
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					splashScreen.setVisible(true);
					IdeUtils.centerOnScreen(splashScreen);
				} catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void close()
	{
		splashScreen.setVisible(false);
	}

	/**
	 * Create the frame.
	 */
	public AutoxSplashScreen()
	{
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		super.setIconImage(IdeUtils.loadIconWithoutBorder("/ui/icons/autox-logo.png", 64).getImage());
		setBounds(100, 100, 817, 374);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		contentPane.add(label, BorderLayout.CENTER);
		
		label.setIcon(new ImageIcon(AutoxSplashScreen.class.getResource("/ui/autox-splash-screen.png")));
	}

}
