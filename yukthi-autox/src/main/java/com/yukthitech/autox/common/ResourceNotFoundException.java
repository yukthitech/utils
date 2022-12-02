package com.yukthitech.autox.common;

/**
 * Thrown when a resource request for loading is not found.
 * @author akranthikiran
 */
public class ResourceNotFoundException extends AutoxInfoException
{
	private static final long serialVersionUID = 1L;
	
	public static enum ResourceType
	{
		FILE("file"),
		
		RESOURCE("resource");
		
		private String rep;

		private ResourceType(String rep)
		{
			this.rep = rep;
		}
		
		public String getRep()
		{
			return rep;
		}
	}
	
	private String resourcePath;
	
	private ResourceType resourceType;

	public ResourceNotFoundException(ResourceType resType, String resourcePath)
	{
		super("Specified {} is not found: {}", resType.rep, resourcePath);
		
		this.resourcePath = resourcePath;
		this.resourceType = resType;
	}
	
	public String getResourcePath()
	{
		return resourcePath;
	}
	
	public ResourceType getResourceType()
	{
		return resourceType;
	}
}
