package com.yukthitech.test.persitence.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Sub bean for employee1
 * @author akiran
 *
 */
@Table(name = "CUSTOMER_ADDRESS")
public class CustomerAddress
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "PROP_ID", unique = true)
	private String propertyId;

	@Column(name = "CITY")
	private String city;

	@Column(name = "STATE")
	private String state;
	
	@OneToOne(mappedBy = "address")
	private Customer customer;
	
	public CustomerAddress()
	{}

	public CustomerAddress(String propId, String city, String state)
	{
		this.propertyId = propId;
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

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Customer getCustomer()
	{
		return customer;
	}

	public void setCustomer(Customer customer)
	{
		this.customer = customer;
	}

	public String getPropertyId()
	{
		return propertyId;
	}

	public void setPropertyId(String propertyId)
	{
		this.propertyId = propertyId;
	}
}
