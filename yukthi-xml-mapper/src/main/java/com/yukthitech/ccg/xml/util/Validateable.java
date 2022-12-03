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
package com.yukthitech.ccg.xml.util;

/**
 * <BR><BR>
  * <P>
 * This interface is meant for those beans which are expected to be loaded by engines/builders 
 * from different sources.
 * </P>
 * <P>
 * Beans implementing this interface are capable of validating themselves. Bean loading 
 * engines are expected to call validate() method of these beans once the bean is loaded
 * completely.
 * </P>
 * <BR>
 * @author A. Kranthi Kiran
*/
public interface Validateable
{
	/**
	 * Validates this bean.
	 * @throws ValidateException When validation fails.
	 */
	public void validate() throws ValidateException;
}
