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
package com.yukthitech.utils.test;

/**
 * This is mainly meant to enumerate the test case groups. But made as common code to make it available
 * for all probjects.
 * @author akiran
 */
public interface ITestGroups
{
	/**
	 * Unit test cases. Which are expected to be very fast. 
	 * And generally executed during checkin.
	 */
	public String UNIT_TESTS = "unit_test_cases";
	
	/**
	 * Function test cases. These test-cases are expected to be executed once per day
	 * to ensure no functionality is broken
	 */
	public String FUNCTIONAL_TESTS = "functional_test_cases";
}
