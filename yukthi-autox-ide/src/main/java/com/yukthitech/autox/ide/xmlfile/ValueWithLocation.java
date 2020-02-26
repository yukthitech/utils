package com.yukthitech.autox.ide.xmlfile;

/**
 * Text value along with location.
 * @author akiran
 */
public class ValueWithLocation
{
	private String value;
	
	/**
	 * Location of name defining this value.
	 */
	private LocationRange nameLocation;
	
	/**
	 * Value location.
	 */
	private LocationRange location;

	public ValueWithLocation(String value, LocationRange nameLocation, LocationRange location)
	{
		this.value = value;
		this.nameLocation = nameLocation;
		this.location = location;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public LocationRange getNameLocation()
	{
		return nameLocation;
	}
	
	public LocationRange getLocation()
	{
		return location;
	}
}
