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
package com.yukthi.utils.fmarker;

import java.util.Collections;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthitech.utils.CommonUtils;
import com.yukthitech.utils.fmarker.FreeMarkerEngine;

public class TDefaultMethods
{
	private FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
	
	private Object eval(String expr, Object... args)
	{
		Map<String, Object> context = null;
		
		if(args.length > 1)
		{
			context = CommonUtils.toMap(args);
		}
		else
		{
			context = Collections.emptyMap();
		}
		
		return freeMarkerEngine.fetchValue("test", expr, context);
	}
	
	@Test
	public void testIfNull()
	{
		Assert.assertEquals(eval("ifNull(var, true, false)", "var", "abc"), false);
		Assert.assertEquals(eval("ifNull(var, true, false)", "var", null), true);
		
		Assert.assertEquals(eval("ifNull(var)", "var", "abc"), false);
		Assert.assertEquals(eval("ifNull(var)", "var", null), true);

		Assert.assertEquals(eval("ifNull(var, 'a', 'b')", "var", "abc"), "b");
		Assert.assertEquals(eval("ifNull(var, 'a', 'b')", "var", null), "a");
	}

	@Test
	public void testIfNotNull()
	{
		Assert.assertEquals(eval("ifNotNull(var, true, false)", "var", "abc"), true);
		Assert.assertEquals(eval("ifNotNull(var, true, false)", "var", null), false);
		
		Assert.assertEquals(eval("ifNotNull(var)", "var", "abc"), true);
		Assert.assertEquals(eval("ifNotNull(var)", "var", null), false);

		Assert.assertEquals(eval("ifNotNull(var, 'a', 'b')", "var", "abc"), "a");
		Assert.assertEquals(eval("ifNotNull(var, 'a', 'b')", "var", null), "b");
	}
}
