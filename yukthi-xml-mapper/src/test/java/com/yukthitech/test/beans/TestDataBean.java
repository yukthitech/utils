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
package com.yukthitech.test.beans;

/**
 * @author akiran
 *
 */
public class TestDataBean
{
	private int intVal;
	
	public TestDataBean()
	{}
	
	public TestDataBean(int intVal)
	{
		this.intVal = intVal;
	}

	public int getIntVal()
	{
		return intVal;
	}

	public void setIntVal(int intVal)
	{
		this.intVal = intVal;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}

		if(!(obj instanceof TestDataBean))
		{
			return false;
		}

		TestDataBean other = (TestDataBean) obj;
		return (intVal == other.intVal);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return intVal;
	}
}
