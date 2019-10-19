package com.yukthitech.test.persitence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;

@Table(name = "DATE_ENTITY")
public class DateDataEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "NAME")
	private String name;
	
	@Column(name = "FLD_DATE")
	@DataTypeMapping(type = DataType.DATE)
	private Date date;
	
	@Column(name = "FLD_DATE_TIME")
	private Date dateWithTime;
	
	public DateDataEntity()
	{}

	public DateDataEntity(String name, Date date, Date dateWithTime)
	{
		this.name = name;
		this.date = date;
		this.dateWithTime = dateWithTime;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public Date getDateWithTime()
	{
		return dateWithTime;
	}

	public void setDateWithTime(Date dateWithTime)
	{
		this.dateWithTime = dateWithTime;
	}
}
