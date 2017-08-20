package com.yukthitech.test.beans;

public class TestCaseBean
{
	public static class Value
	{
		private int paramValue;
		
		private int result;
		
		private boolean exception;

		public int getParamValue()
		{
			return paramValue;
		}

		public void setParamValue(int paramValue)
		{
			this.paramValue = paramValue;
		}

		public int getResult()
		{
			return result;
		}

		public void setResult(int result)
		{
			this.result = result;
		}

		public boolean isException()
		{
			return exception;
		}

		public void setException(boolean exception)
		{
			this.exception = exception;
		}
	}
	
	private String name;
	
	private Value value;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Value getValue()
	{
		return value;
	}

	public void setValue(Value value)
	{
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("[");

		builder.append("Name: ").append(name);
		builder.append(",").append("Value: ").append(value.paramValue);

		builder.append("]");
		return builder.toString();
	}

}
