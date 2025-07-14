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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.Index;
import com.yukthitech.persistence.annotations.Indexed;
import com.yukthitech.persistence.annotations.Indexes;
import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.conversion.impl.JsonConverter;

@Table(name = "EMPLOYEE1")
@Indexes({
	@Index(name = "E1_PH_NAME", fields={"phoneNo", "name"})
})
public class Employee1
{
	public static final String ERROR_MESSAGE_DUPLICATE_EMAIL = "Specified email-id already exists: ${emailId}";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@UniqueConstraint(name="EmpNo", finalName = false)
	@Column(name = "EMP_NO")
	private String employeeNo;
	
	@UniqueConstraint(name = "EmailId1", message = ERROR_MESSAGE_DUPLICATE_EMAIL)
	private String emailId;
	
	@Indexed
	@Column(name = "ENAME")
	private String name;
	
	private String phoneNo;
	
	private int age;
	
	@Column(name = "ADDRESS", length = 2000)
	@DataTypeMapping(type = DataType.STRING, converterType = JsonConverter.class)
	private Address address;
	
	@Column(name = "IS_MARRIED")
	private boolean married;
	
	@Column(name = "DOB")
	private Date dob;
	
	public Employee1()
	{}
	
	public Employee1(String employeeNo, String emailId, String name, String phoneNo, int age)
	{
		this.employeeNo = employeeNo;
		this.emailId = emailId;
		this.name = name;
		this.phoneNo = phoneNo;
		this.age = age;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getEmployeeNo()
	{
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo)
	{
		this.employeeNo = employeeNo;
	}

	public String getEmailId()
	{
		return emailId;
	}

	public void setEmailId(String emailId)
	{
		this.emailId = emailId;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPhoneNo()
	{
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo)
	{
		this.phoneNo = phoneNo;
	}

	/**
	 * @return the {@link #age age}
	 */
	public int getAge()
	{
		return age;
	}

	/**
	 * @param age the {@link #age age} to set
	 */
	public void setAge(int age)
	{
		this.age = age;
	}

	/**
	 * @return the {@link #address address}
	 */
	public Address getAddress()
	{
		return address;
	}

	/**
	 * @param address the {@link #address address} to set
	 */
	public void setAddress(Address address)
	{
		this.address = address;
	}

	public boolean isMarried()
	{
		return married;
	}

	public void setMarried(boolean married)
	{
		this.married = married;
	}

	public Date getDob()
	{
		return dob;
	}

	public void setDob(Date dob)
	{
		this.dob = dob;
	}
}
