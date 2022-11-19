package com.yukthitech.autox.test.ssh.steps;

import com.yukthitech.autox.AbstractStep;
import com.yukthitech.autox.Executable;
import com.yukthitech.autox.Group;
import com.yukthitech.autox.Param;
import com.yukthitech.autox.context.AutomationContext;
import com.yukthitech.autox.exec.report.IExecutionLogger;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Closes the specified remote session.
 * 
 * @author akiran
 */
@Executable(name = "sshCloseSession", group = Group.Ssh, message = "Closes the specified remote session.")
public class CloseSessionStep extends AbstractStep
{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the session to be closed.
	 */
	@Param(description = "Name of the session to be closed.")
	private String session;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yukthitech.autox.IStep#execute(com.yukthitech.autox.
	 * AutomationContext, com.yukthitech.autox.ExecutionLogger)
	 */
	@Override
	public void execute(AutomationContext context, IExecutionLogger exeLogger)
	{
		exeLogger.debug("Closing the session - {}", session);
		
		RemoteSession remoteSession = (RemoteSession) context.getInternalAttribute(session);
		
		if(remoteSession == null)
		{
			throw new InvalidStateException("No ssh-session exists with specified name: {}", session);
		}
		
		remoteSession.close();
	}

	/**
	 * Sets the name of the session to be closed.
	 *
	 * @param session the new name of the session to be closed
	 */
	public void setSession(String session)
	{
		this.session = session;
	}
}
