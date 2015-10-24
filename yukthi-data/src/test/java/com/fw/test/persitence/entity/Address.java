package com.fw.test.persitence.entity;

/**
 * Sub bean for employee1
 * @author akiran
 *
 */
public class Address
{
	private String city;
	private String state;
	
	public Address()
	{}

	public Address(String city, String state)
	{
		super();
		this.city = city;
		this.state = state;
	}

	/**
	 * @return the {@link #city city}
	 */
	public String getCity()
	{
		return city;
	}

	/**
	 * @param city the {@link #city city} to set
	 */
	public void setCity(String city)
	{
		this.city = city;
	}

	/**
	 * @return the {@link #state state}
	 */
	public String getState()
	{
		return state;
	}

	/**
	 * @param state the {@link #state state} to set
	 */
	public void setState(String state)
	{
		this.state = state;
	}
}
