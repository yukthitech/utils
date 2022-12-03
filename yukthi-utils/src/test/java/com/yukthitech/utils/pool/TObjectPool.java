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
package com.yukthitech.utils.pool;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TObjectPool
{
	@Test
	public void testObjectPoolLimits()
	{
		ObjectPool<String> pool = new ObjectPool<String>(String.class, 3);
		
		String s = null;
		
		//make sure objects are available till limit
		Assert.assertNotNull(s = pool.getObject());
		Assert.assertNotNull(pool.getObject());
		Assert.assertNotNull(pool.getObject());
		
		//once limit is crossed, object should not be available
		Assert.assertNull(pool.getObject());
		
		//free the object and then try
		pool.freeObject(s);
		Assert.assertNotNull(pool.getObject());
	}
}
