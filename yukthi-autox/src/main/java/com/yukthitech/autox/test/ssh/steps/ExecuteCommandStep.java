package com.yukthitech.autox.test.ssh.steps;

import java.io.InputStream;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.AutomationContext;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.ExecutionLogger;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Executes specified command on specified session and stores the output on
 * context.
 * 
 * @author akiran
 */
@Executable(name = "sshExecuteCommand", group = Group.Ssh, message = "Executes specified command on specified session and stores the output on context.")
public class ExecuteCommandStep extends AbstractStep
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the session on which command needs to be executed.
	 */
	@Param(description = "Name of the session on which command needs to be executed.")
	private String session;

	/**
	 * Command to be executed.
	 */
	@Param(description = "Command to be executed.")
	private String command;

	/**
	 * Name of the output context attribute on which output needs to be set.
	 * Default: output.
	 */
	@Param(description = "Name of the output context attribute on which output needs to be set. Default: output", required = false,
			attrName = true, defaultValue = "output")
	private String outputVar = "output";
	
	/**
	 * Name of the context attribute on which exit status to be set. Default: exitStatus.
	 */
	@Param(description = "Name of the context attribute on which exit status to be set. Default: exitStatus", required = false,
			attrName = true, defaultValue = "exitStatus")
	private String exitStatusVar = "exitStatus";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.
	 * AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public boolean execute(AutomationContext context, ExecutionLogger exeLogger)
	{
		exeLogger.debug("On session '{}' executing command: {}", session, command);
		
		RemoteSession remoteSession = (RemoteSession) context.getInternalAttribute(session);

		if(remoteSession == null)
		{
			throw new InvalidStateException("No ssh-session exists with specified name: {}", session);
		}

		try
		{
			Session sshSession = remoteSession.getSession();
			ChannelExec channelExec = (ChannelExec) sshSession.openChannel("exec");

			channelExec.setCommand(command);

			SynchronizedOutputStream out = new SynchronizedOutputStream();

			channelExec.setErrStream(out);

			InputStream in = channelExec.getInputStream();
			channelExec.connect();

			byte[] buff = new byte[1024];
			
			while(true)
			{
				while(in.available() > 0)
				{
					int len = in.read(buff, 0, 1024);
					
					if(len < 0)
					{
						break;
					}
					
					out.write(buff, 0, len);
				}
				
				if(channelExec.isClosed())
				{
					if(in.available() > 0)
					{
						continue;
					}
					
					exeLogger.debug("Command exited with status: {}", channelExec.getExitStatus());
					break;
				}

				try
				{
					Thread.sleep(1000);
				} catch(Exception ee)
				{
				}
			}
			
			int exitStatus = channelExec.getExitStatus();
			String output = new String(out.toByteArray());

			exeLogger.debug("Got exit status as '{}' and output as:\n{}", exitStatus, output);
			exeLogger.debug("Setting exit status and output on context with names '{}' and '{}' respectively", exitStatusVar, outputVar);
			
			context.setAttribute(outputVar, output);
			context.setAttribute(exitStatusVar, exitStatusVar);
			
			channelExec.disconnect();

		} catch(Exception e)
		{
			throw new InvalidStateException("An error occurred while executing command on session: {}", session, e);
		}

		return true;
	}

	/**
	 * Sets the name of the session on which command needs to be executed.
	 *
	 * @param session
	 *            the new name of the session on which command needs to be
	 *            executed
	 */
	public void setSession(String session)
	{
		this.session = session;
	}

	/**
	 * Sets the command to be executed.
	 *
	 * @param command
	 *            the new command to be executed
	 */
	public void setCommand(String command)
	{
		this.command = command;
	}

	/**
	 * Sets the name of the output context attribute on which output needs to be
	 * set. Default: output.
	 *
	 * @param outputVar
	 *            the new name of the output context attribute on which output
	 *            needs to be set
	 */
	public void setOutputVar(String outputVar)
	{
		this.outputVar = outputVar;
	}
	
	/**
	 * Sets the name of the context attribute on which exit status to be set. Default: exitStatus.
	 *
	 * @param exitStatusVar the new name of the context attribute on which exit status to be set
	 */
	public void setExitStatusVar(String exitStatusVar)
	{
		this.exitStatusVar = exitStatusVar;
	}
}
