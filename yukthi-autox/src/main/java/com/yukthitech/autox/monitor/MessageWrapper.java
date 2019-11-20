package com.yukthitech.autox.monitor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Used to send meta information along with actual message.
 * @author akiran
 */
public class MessageWrapper implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Unique id used for message.
	 */
	private String id = UUID.randomUUID().toString();
	
	/**
	 * Actual message.
	 */
	private Serializable message;
	
	/**
	 * Flag indicating if confirmation is required for message.
	 */
	private boolean confirmationRequired;

	/**
	 * Instantiates a new message wrapper.
	 *
	 * @param message the message
	 * @param confirmationRequired the confirmation required
	 */
	public MessageWrapper(Serializable message, boolean confirmationRequired)
	{
		this.message = message;
		this.confirmationRequired = confirmationRequired;
	}
	
	/**
	 * Gets the unique id used for message.
	 *
	 * @return the unique id used for message
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Gets the actual message.
	 *
	 * @return the actual message
	 */
	public Serializable getMessage()
	{
		return message;
	}

	/**
	 * Gets the flag indicating if confirmation is required for message.
	 *
	 * @return the flag indicating if confirmation is required for message
	 */
	public boolean isConfirmationRequired()
	{
		return confirmationRequired;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Message: ").append(message);
		builder.append(",").append("Confirmation Req: ").append(confirmationRequired);

		builder.append("]");
		return builder.toString();
	}

}
