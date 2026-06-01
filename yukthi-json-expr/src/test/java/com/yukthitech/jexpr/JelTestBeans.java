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
package com.yukthitech.jexpr;

import java.util.ArrayList;
import java.util.List;

/**
 * List of test beans.
 * @author akiran
 */
public class JelTestBeans
{
	private List<JelTestBean> testBeans;
	
	public void addJelTestBean(JelTestBean bean)
	{
		if(testBeans == null)
		{
			this.testBeans = new ArrayList<JelTestBean>();
		}
		
		this.testBeans.add(bean);
	}
	
	public void setTestBeans(List<JelTestBean> testBeans)
	{
		this.testBeans = testBeans;
	}
	
	public List<JelTestBean> getTestBeans()
	{
		return testBeans;
	}
}
