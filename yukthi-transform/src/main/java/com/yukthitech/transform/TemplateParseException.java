package com.yukthitech.transform;

import com.yukthitech.transform.template.Location;
import com.yukthitech.utils.exceptions.UtilsException;

public class TemplateParseException extends UtilsException
{
	private static final long serialVersionUID = 1L;
	
	private Location location;

	public TemplateParseException(Location location, String message, Object... args)
	{
		super(location + " " + message, args);
		this.location = location;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
}
