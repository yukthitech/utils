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
package com.yukthitech.utils.cli;

/**
 * Test bean meant for testing command line arguments parsing.
 * @author akiran
 */
public class MappedBean2
{
	@CliArgument(name = "A", longName = "address", description = "Address for the bean")
	private String address;
	
	@CliArgument(name = "f", longName = "flag", description = "Flag for the bean", required = false)
	private boolean flag;

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public boolean isFlag()
	{
		return flag;
	}

	public void setFlag(boolean flag)
	{
		this.flag = flag;
	}
}
