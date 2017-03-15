package com.fw.test.persitence.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yukthi.persistence.annotations.DataType;
import com.yukthi.persistence.annotations.DataTypeMapping;
import com.yukthi.persistence.annotations.Index;
import com.yukthi.persistence.annotations.Indexed;
import com.yukthi.persistence.annotations.Indexes;
import com.yukthi.persistence.annotations.UniqueConstraint;
import com.yukthi.persistence.conversion.impl.JsonConverter;

@Table(name = "EMPLOYEE1")
@Indexes({
	@Index(fields={"phoneNo", "name"})
})
public class Employee1
{
	public static final String ERROR_MESSAGE_DUPLICATE_EMAIL = "Specified email-id already exists: ${emailId}";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@UniqueConstraint(name="EmpNo")
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

	
	
}
