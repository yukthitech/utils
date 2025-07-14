/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.test.persitence.entity;

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
