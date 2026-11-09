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
 * Root wrapper so XML can load a DynamicNodeAcceptorBean via a typed setter.
 */
public class DynamicNodeAcceptorRoot
{
	private DynamicNodeAcceptorBean node;

	public DynamicNodeAcceptorBean getNode()
	{
		return node;
	}

	public void setNode(DynamicNodeAcceptorBean node)
	{
		this.node = node;
	}
}
