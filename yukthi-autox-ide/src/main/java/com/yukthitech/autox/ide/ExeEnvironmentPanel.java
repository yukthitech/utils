package com.yukthitech.autox.ide;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.autox.ide.context.IContextListener;
import com.yukthitech.autox.ide.context.IdeContext;
import com.yukthitech.autox.ide.exeenv.ExecutionEnvironment;

@Component
public class ExeEnvironmentPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private final JComboBox<ExecutionEnvironment> envComboBox = new JComboBox<ExecutionEnvironment>();
	private final JButton stopBut = new JButton("");
	private final JButton clearBut = new JButton("");
	private final JButton clearAllBut = new JButton("");
	private final JLabel lblEnvironments = new JLabel("Environments: ");

	@Autowired
	private IdeContext ideContext;
	
	/**
	 * Create the panel.
	 */
	public ExeEnvironmentPanel()
	{
		setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), new EmptyBorder(5, 0, 5, 0)));
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		
		envComboBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				changeEnvironment();
			}
		});
		
		add(lblEnvironments);

		add(envComboBox);
		stopBut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				stopEnvironment();
			}
		});
		stopBut.setToolTipText("Stop");
		stopBut.setBorder(null);
		stopBut.setIcon(IdeUtils.loadIcon("/ui/icons/kill.png", 16));
		add(stopBut);
		
		clearBut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				clearEnvironment();
			}
		});
		clearBut.setToolTipText("Clear Environment");

		clearBut.setBorder(null);
		clearBut.setIcon(IdeUtils.loadIcon("/ui/icons/clear.png", 16));
		add(clearBut);
		clearAllBut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				clearAllEnvironments();
			}
		});
		clearAllBut.setToolTipText("Clear All Environments");

		clearAllBut.setBorder(null);
		clearAllBut.setIcon(IdeUtils.loadIcon("/ui/icons/clearAll.png", 16));
		add(clearAllBut);
	}
	
	@PostConstruct
	private void init()
	{
		ideContext.addContextListener(new IContextListener()
		{
			@Override
			public void newEnvironmentStarted(ExecutionEnvironment environment)
			{
				newEnvironmentAdded(environment);
			}
		});
	}

	private void stopEnvironment()
	{

	}

	private void clearEnvironment()
	{

	}

	private void clearAllEnvironments()
	{

	}

	private void changeEnvironment()
	{
		ExecutionEnvironment env = (ExecutionEnvironment) envComboBox.getSelectedItem();
		ideContext.getProxy().environmentChanged(env);
	}
	
	private void newEnvironmentAdded(ExecutionEnvironment environment)
	{
		envComboBox.addItem(environment);
		envComboBox.setSelectedItem(environment);
		
		ideContext.getProxy().environmentChanged(environment);
	}
}
